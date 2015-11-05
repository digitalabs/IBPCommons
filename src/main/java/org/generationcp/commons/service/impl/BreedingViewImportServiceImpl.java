
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.DatasetUtil;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVReader;

import com.rits.cloning.Cloner;

public class BreedingViewImportServiceImpl implements BreedingViewImportService {

	private static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";
	private static final String UNIT_ERRORS_SUFFIX = "_UnitErrors";
	private static final String MEANS_SUFFIX = "_Means";
	private static final String LS_MEAN = "LS MEAN";
	private static final String ERROR_ESTIMATE = "ERROR ESTIMATE";

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyDaoFactory ontologyDaoFactory;

	@Autowired
	private Cloner cloner;

	@Autowired
	private OntologyMethodDataManager methodDataManager;

	@Autowired
	private StandardVariableTransformer standardVariableTransformer;

	private DataSet plotDataSet;

	private Map<String, String> localNameToAliasMap = null;

	public BreedingViewImportServiceImpl() {

	}

	public BreedingViewImportServiceImpl(StudyDataManager studyDataManager, OntologyVariableDataManager ontologyVariableDataManager,
			OntologyMethodDataManager methodDataManager, OntologyDaoFactory ontologyDaoFactory,
			StandardVariableTransformer standardVariableTransformer) {
		this.studyDataManager = studyDataManager;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.methodDataManager = methodDataManager;
		this.ontologyDaoFactory = ontologyDaoFactory;
		this.standardVariableTransformer = standardVariableTransformer;
	}

	@Override
	public void importMeansData(File file, int studyId) throws BreedingViewImportException {

		boolean meansDataSetExists = false;
		CVTerm lsMean = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(LS_MEAN, CvId.METHODS.getId());
		CVTerm errorEstimate = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(ERROR_ESTIMATE, CvId.METHODS.getId());

		try {

			DmsProject study = this.studyDataManager.getProject(studyId);
			String programUUID = study.getProgramUUID();

			Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			Map<String, ArrayList<String>> traitsAndMeans = new MeansCSV(file, nameToAliasMap).csvToMap();

			if (!traitsAndMeans.isEmpty()) {

				String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);

				DataSet meansDataSet = this.getMeansDataSet(studyId);
				DataSet plotDataSet = this.getPlotDataSet(studyId);
				DataSet trialDataSet = this.getTrialDataSet(studyId);

				if (meansDataSet != null) {
					meansDataSet =
							this.appendVariableTypesToExistingMeans(csvHeader, plotDataSet, meansDataSet, programUUID, lsMean,
									errorEstimate);
					meansDataSetExists = true;
				} else {
					meansDataSet =
							this.createMeansDataset(study.getProjectId(), study.getName() + "-MEANS", csvHeader, plotDataSet, programUUID,
									lsMean, errorEstimate);
				}

				this.createOrAppendMeansExperiments(meansDataSet, traitsAndMeans, meansDataSetExists, plotDataSet.getId(),
						trialDataSet.getId());

			}
		} catch (Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}
	}

	private void createOrAppendMeansExperiments(DataSet meansDataSet, Map<String, ArrayList<String>> traitsAndMeans,
			boolean meansDataSetExists, int plotDatasetId, int trialDatasetId) {
		List<ExperimentValues> experimentValuesList = new ArrayList<>();
		String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);
		String envHeader = csvHeader[0];
		String entryNoHeader = csvHeader[1];
		Map<String, Integer> envNameToNdGeolocationIdMap = this.getEnvNameToNdGeolocationIdMap(envHeader, trialDatasetId);
		Map<String, Integer> entroNoToNdStockIdMap = this.getEntroNoToNdStockIdMap(entryNoHeader, plotDatasetId);

		List<String> environments = traitsAndMeans.get(envHeader);
		for (int i = 0; i < environments.size(); i++) {
			String envName = environments.get(i).replace(";", ",");
			Integer ndGeolocationId = envNameToNdGeolocationIdMap.get(envName);
			String entryNo = traitsAndMeans.get(entryNoHeader).get(i);
			Integer ndStockId = entroNoToNdStockIdMap.get(entryNo);

			ExperimentValues experimentRow = new ExperimentValues();
			experimentRow.setGermplasmId(ndStockId);
			experimentRow.setLocationId(ndGeolocationId);

			List<Variable> list = new ArrayList<Variable>();

			for (int j = 2; j < csvHeader.length; j++) {
				String meansVariable = csvHeader[j];
				if (meansDataSetExists) {
					if (meansDataSet.getVariableTypes().getVariates().findByLocalName(meansVariable) == null) {
						continue;
					}
				}

				String variableValue = traitsAndMeans.get(meansVariable).get(i).trim();
				if (!variableValue.trim().isEmpty()) {
					Variable var = new Variable(meansDataSet.getVariableTypes().findByLocalName(meansVariable), variableValue);
					list.add(var);
				}

			}

			VariableList variableList1 = new VariableList();
			variableList1.setVariables(list);
			experimentRow.setVariableList(variableList1);
			experimentValuesList.add(experimentRow);
		}

		this.studyDataManager.addOrUpdateExperiment(meansDataSet.getId(), ExperimentType.AVERAGE, experimentValuesList);
	}

	private Map<String, Integer> getEntroNoToNdStockIdMap(String entryNoHeader, int plotDatasetId) {
		Stocks stocks = this.studyDataManager.getStocksInDataset(plotDatasetId);
		return stocks.getStockMap(entryNoHeader);
	}

	private Map<String, Integer> getEnvNameToNdGeolocationIdMap(String envFactor, int trialDatasetId) {
		Map<String, Integer> envNameToGeolocationIdMap = new HashMap<String, Integer>();
		TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(trialDatasetId);
		for (TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()) {
			envNameToGeolocationIdMap.put(trialEnv.getVariables().findByLocalName(envFactor).getValue(), trialEnv.getId());
		}
		return envNameToGeolocationIdMap;
	}

	private DataSet createMeansDataset(int studyId, String datasetName, String[] csvHeader, DataSet plotDataSet, String programUUID,
			CVTerm lSMean, CVTerm errorEstimate) {

		VariableTypeList meansVariableTypeList = new VariableTypeList();
		VariableList meansVariableList = new VariableList();
		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setVariables(meansVariableList);

		this.createMeansVariableAndAddToLists(meansVariableList, meansVariableTypeList, TermId.DATASET_NAME.getId(), datasetName,
				"Dataset name (local)", datasetName, 1, programUUID, PhenotypicType.DATASET);

		this.createMeansVariableAndAddToLists(meansVariableList, meansVariableTypeList, TermId.DATASET_TITLE.getId(), "DATASET_TITLE",
				"Dataset title (local)", "My Dataset Description", 2, programUUID, PhenotypicType.DATASET);

		this.createMeansVariableAndAddToLists(meansVariableList, meansVariableTypeList, TermId.DATASET_TYPE.getId(), "DATASET_TYPE",
				"Dataset type (local)", "10070", 3, programUUID, PhenotypicType.DATASET);

		this.createMeansVariablesFromPlotDatasetAndAddToList(plotDataSet, meansVariableTypeList, 4);

		this.createMeansVariablesFromImportFileAndAddToList(csvHeader, plotDataSet.getVariableTypes().getVariates(), meansVariableTypeList,
				programUUID, lSMean, errorEstimate);

		DatasetReference datasetReference = this.studyDataManager.addDataSet(studyId, meansVariableTypeList, datasetValues, "");
		return this.studyDataManager.getDataSet(datasetReference.getId());

	}

	private void createMeansVariablesFromImportFileAndAddToList(String[] csvHeader, VariableTypeList plotVariates,
			VariableTypeList meansVariableTypeList, String programUUID, CVTerm lsMean, CVTerm errorEstimate) {
		int numberOfMeansVariables = meansVariableTypeList.getVariableTypes().size();
		int rank = meansVariableTypeList.getVariableTypes().get(numberOfMeansVariables - 1).getRank() + 1;
		Set<String> inputDataSetVariateNames = this.getAllNewVariatesToProcess(csvHeader, null);
		Term lsMeanTerm = new Term(lsMean.getCvTermId(), lsMean.getName(), lsMean.getDefinition());
		Term errorEstimateTerm = new Term(errorEstimate.getCvTermId(), errorEstimate.getName(), errorEstimate.getDefinition());

		for (String variateName : inputDataSetVariateNames) {
			DMSVariableType variate = plotVariates.findByLocalName(variateName);
			meansVariableTypeList.add(this.createAnalysisVariable(variate, variateName + BreedingViewImportServiceImpl.MEANS_SUFFIX,
					lsMeanTerm, programUUID, rank++));
			meansVariableTypeList.add(this.createAnalysisVariable(variate, variateName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX,
					errorEstimateTerm, programUUID, rank++));
		}
	}

	private void createMeansVariablesFromPlotDatasetAndAddToList(DataSet plotDataSet, VariableTypeList meansVariableTypeList, int lastRank) {
		int rank = lastRank;
		for (DMSVariableType factorFromDataSet : plotDataSet.getVariableTypes().getFactors().getVariableTypes()) {
			if (factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
					|| factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
				factorFromDataSet.setRank(++rank);
				meansVariableTypeList.add(factorFromDataSet);
			}
		}
	}

	private void createMeansVariableAndAddToLists(VariableList meansVariableList, VariableTypeList meansVariableTypeList,
			int ontologyVariableId, String name, String definition, String value, int rank, String programUUID,
			PhenotypicType phenotypicType) {

		Variable variable = this.createVariable(ontologyVariableId, value, rank, programUUID, phenotypicType);
		meansVariableList.add(variable);

		VariableType variableType = this.getVariableTypeByPhenotypicType(phenotypicType);
		this.updateVariableType(variable.getVariableType(), name, definition, variableType);
		meansVariableTypeList.add(variable.getVariableType());
	}

	private VariableType getVariableTypeByPhenotypicType(PhenotypicType phenotypicType) {

		if (PhenotypicType.DATASET == phenotypicType) {
			return VariableType.STUDY_DETAIL;
		} else if (PhenotypicType.GERMPLASM == phenotypicType) {
			return VariableType.GERMPLASM_DESCRIPTOR;
		} else {
			return VariableType.ANALYSIS;
		}

	}

	private DataSet getMeansDataSet(int studyId) {
		List<DataSet> ds = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);
		if (ds != null && !ds.isEmpty()) {
			// return the 1st one as we're sure that we can only have one means dataset per study
			return ds.get(0);
		}
		return null;
	}

	@Override
	public void importMeansData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importMeansData(file, studyId);
	}

	private Map<String, Integer> findOrSaveMethodsIfNotExisting(List<String> methodNameList) {
		Map<String, Integer> methodNameToIdMap = new HashMap<>();
		for (String methodName : methodNameList) {
			Integer methodId = this.findOrSaveMethod(methodName, methodName + "  (system generated method)");
			methodNameToIdMap.put(methodName, methodId);
		}
		return methodNameToIdMap;
	}

	private Integer findOrSaveMethod(String methodName, String methodDefinition) {
		Integer methodId = null;
		CVTerm cvterm = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(methodName, CvId.METHODS.getId());
		if (cvterm == null) {
			methodId = this.saveMethod(methodName, methodDefinition);
		} else {
			methodId = cvterm.getCvTermId();
		}
		return methodId;
	}

	private Integer saveMethod(String methodName, String methodDefinition) {
		Method method = new Method();
		method.setName(methodName);
		method.setDefinition(methodDefinition);
		this.methodDataManager.addMethod(method);
		return method.getId();
	}

	private boolean isVariableExisting(String variableName) {
		CVTerm cvterm = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(variableName, CvId.VARIABLES.getId());
		if (cvterm != null) {
			return true;
		}
		return false;
	}

	private Integer saveAnalysisVariable(String name, String description, int methodId, int propertyId, int scaleId, String programUUID) {
		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(name);
		variableInfo.setDescription(description);
		variableInfo.setMethodId(methodId);
		variableInfo.setPropertyId(propertyId);
		variableInfo.setScaleId(scaleId);
		variableInfo.setProgramUuid(programUUID);
		variableInfo.addVariableType(VariableType.ANALYSIS);
		this.ontologyVariableDataManager.addVariable(variableInfo);
		return variableInfo.getId();
	}

	@Override
	public void importSummaryStatsData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importSummaryStatsData(file, studyId);
	}

	@Override
	public void importSummaryStatsData(File file, int studyId) throws BreedingViewImportException {

		try {

			Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			SummaryStatsCSV summaryStatsCSV = new SummaryStatsCSV(file, nameToAliasMap);

			Map<String, Map<String, ArrayList<String>>> summaryStatsData = summaryStatsCSV.getData();

			DmsProject study = this.studyDataManager.getProject(studyId);
			String programUUID = study.getProgramUUID();
			DataSet trialDataSet = this.getTrialDataSet(studyId);

			// used in getting the new project properties
			VariableTypeList variableTypeListVariates = this.getPlotDataSet(studyId).getVariableTypes().getVariates();

			VariableTypeList summaryStatsVariableTypeList =
					this.createSummaryStatsVariableTypes(summaryStatsCSV, trialDataSet, variableTypeListVariates, nameToAliasMap,
							programUUID);

			Map<String, Integer> envFactorTolocationIdMap =
					this.retrieveAllLocationsOfStudy(summaryStatsData.keySet(), studyId, summaryStatsCSV.getTrialHeader());
			List<ExperimentValues> summaryStatsExperimentValuesList =
					this.createSummaryStatsExperimentValuesList(trialDataSet, envFactorTolocationIdMap, summaryStatsCSV.getHeaderStats(),
							summaryStatsCSV.getData());

			// save project properties and experiments
			DmsProject project = new DmsProject();
			project.setProjectId(trialDataSet.getId());
			this.studyDataManager.saveTrialDatasetSummary(project, summaryStatsVariableTypeList, summaryStatsExperimentValuesList,
					new ArrayList<>(envFactorTolocationIdMap.values()));

		} catch (Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}

	}

	private List<ExperimentValues> createSummaryStatsExperimentValuesList(DataSet trialDataSet,
			Map<String, Integer> envFactorTolocationIdMap, List<String> summaryStatsList,
			Map<String, Map<String, ArrayList<String>>> summaryStatsData) {

		List<ExperimentValues> summaryStatsExperimentValuesList = new ArrayList<ExperimentValues>();
		for (String envFactorValue : envFactorTolocationIdMap.keySet()) {
			for (String summaryStatName : summaryStatsList) {
				for (Entry<String, ArrayList<String>> traitSummaryStat : summaryStatsData.get(envFactorValue).entrySet()) {

					VariableList variableList = new VariableList();
					variableList.setVariables(new ArrayList<Variable>());
					ExperimentValues experimentValues = new ExperimentValues();
					experimentValues.setVariableList(variableList);
					experimentValues.setLocationId(envFactorTolocationIdMap.get(envFactorValue));

					DMSVariableType summaryStatVariableType =
							trialDataSet.findVariableTypeByLocalName(traitSummaryStat.getKey() + "_" + summaryStatName);

					if (summaryStatVariableType != null) {
						String summaryStatValue = traitSummaryStat.getValue().get(summaryStatsList.indexOf(summaryStatName));
						Variable var = new Variable(summaryStatVariableType, summaryStatValue);
						experimentValues.getVariableList().getVariables().add(var);
						summaryStatsExperimentValuesList.add(experimentValues);
					}
				}
			}
		}
		return summaryStatsExperimentValuesList;
	}

	private Map<String, Integer> retrieveAllLocationsOfStudy(Set<String> environments, int studyId, String envFactorName) {
		Map<String, Integer> envFactorTolocationIdMap = new LinkedHashMap<>();
		TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.getTrialDataSet(studyId).getId());
		for (String env : environments) {
			String envFactor = env.replace(";", ",");
			TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(envFactorName, envFactor);
			if (trialEnv == null) {
				envFactor = env;
				trialEnv = trialEnvironments.findOnlyOneByLocalName(envFactorName, envFactor);
			}
			envFactorTolocationIdMap.put(env, trialEnv.getId());
		}
		return envFactorTolocationIdMap;
	}

	protected VariableTypeList createSummaryStatsVariableTypes(SummaryStatsCSV summaryStatsCSV, DataSet trialDataSet,
			VariableTypeList plotVariates, Map<String, String> nameToAliasMap, String programUUID) throws IOException {
		VariableTypeList summaryStatsVariableTypeList = new VariableTypeList();

		summaryStatsCSV.getData();
		List<String> summaryStatsList = summaryStatsCSV.getHeaderStats();
		summaryStatsCSV.getTrialHeader();

		Map<String, Integer> summaryStatNameToIdMap = this.findOrSaveMethodsIfNotExisting(summaryStatsList);

		int numberOfTrialDatasetVariables = trialDataSet.getVariableTypes().size();
		int rank = trialDataSet.getVariableTypes().getVariableTypes().get(numberOfTrialDatasetVariables - 1).getRank() + 1;

		for (String summaryStatName : summaryStatsList) {
			for (DMSVariableType variate : plotVariates.getVariableTypes()) {
				if (nameToAliasMap.containsValue(variate.getLocalName())) {
					String trait = variate.getLocalName();
					String localName = trait + "_" + summaryStatName;
					if (trialDataSet.findVariableTypeByLocalName(localName) == null) {
						DMSVariableType originalVariableType = plotVariates.findByLocalName(trait);
						Term summaryStatMethod = new Term(summaryStatNameToIdMap.get(summaryStatName), summaryStatName, summaryStatName);
						DMSVariableType summaryStatVariableType =
								this.createAnalysisVariable(originalVariableType, localName, summaryStatMethod, programUUID, rank++);
						summaryStatVariableType.setVariableType(VariableType.ANALYSIS);
						summaryStatsVariableTypeList.add(summaryStatVariableType);
						trialDataSet.getVariableTypes().add(summaryStatVariableType);
					}

				}
			}
		}

		return summaryStatsVariableTypeList;
	}

	@Override
	public void importOutlierData(File file, int studyId) throws BreedingViewImportException {

		try {

			Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			OutlierCSV outlierCSV = new OutlierCSV(file, nameToAliasMap);
			Map<String, Map<String, ArrayList<String>>> outlierData = outlierCSV.getData();
			Map<String, Integer> ndGeolocationIds = new HashMap<String, Integer>();

			Map<Integer, Integer> stdVariableIds = new HashMap<Integer, Integer>();
			VariableTypeList plotVariableTypeList = this.getPlotDataSet(studyId).getVariableTypes();

			Integer i = 0;
			for (String l : outlierCSV.getHeaderTraits()) {
				Integer traitId = plotVariableTypeList.findByLocalName(l).getId();
				stdVariableIds.put(i, traitId);
				i++;
			}

			TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.getPlotDataSet(studyId).getId());
			for (TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()) {
				ndGeolocationIds.put(trialEnv.getVariables().findByLocalName(outlierCSV.getTrialHeader()).getValue(), trialEnv.getId());
			}

			Set<String> environments = outlierData.keySet();
			for (String env : environments) {

				List<PhenotypeOutlier> outliers = new ArrayList<PhenotypeOutlier>();
				Integer ndGeolocationId = ndGeolocationIds.get(env);

				for (Entry<String, ArrayList<String>> plot : outlierData.get(env).entrySet()) {

					List<Integer> cvTermIds = new ArrayList<Integer>();
					Integer plotNo = Integer.valueOf(plot.getKey());
					Map<Integer, String> plotMap = new HashMap<Integer, String>();

					for (int x = 0; x < plot.getValue().size(); x++) {
						String traitValue = plot.getValue().get(x);
						if (traitValue.isEmpty()) {
							cvTermIds.add(stdVariableIds.get(x));
							plotMap.put(stdVariableIds.get(x), traitValue);
						}

					}

					List<Object[]> list =
							this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(this.getPlotDataSet(studyId).getId(), ndGeolocationId,
									plotNo, cvTermIds);
					for (Object[] object : list) {
						PhenotypeOutlier outlier = new PhenotypeOutlier();
						outlier.setPhenotypeId(Integer.valueOf(object[2].toString()));
						outlier.setValue(plotMap.get(Integer.valueOf(object[1].toString())));
						outliers.add(outlier);
					}

				}

				this.studyDataManager.saveOrUpdatePhenotypeOutliers(outliers);
			}
		} catch (Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}

	}

	@Override
	public void importOutlierData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importOutlierData(file, studyId);
	}

	protected DataSet getPlotDataSet(int studyId) throws MiddlewareException {
		if (this.plotDataSet != null) {
			return this.plotDataSet;
		} else {
			this.plotDataSet = DatasetUtil.getPlotDataSet(this.studyDataManager, studyId);
		}
		return this.plotDataSet;
	}

	protected DataSet getTrialDataSet(int studyId) throws MiddlewareException {
		return DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);
	}

	protected DataSet appendVariableTypesToExistingMeans(String[] csvHeader, DataSet inputDataSet, DataSet meansDataSet,
			String programUUID, CVTerm lsMean, CVTerm errorEstimate) throws MiddlewareException {
		int numberOfMeansVariables = meansDataSet.getVariableTypes().getVariableTypes().size();
		int rank = meansDataSet.getVariableTypes().getVariableTypes().get(numberOfMeansVariables - 1).getRank() + 1;
		Set<String> inputDataSetVariateNames =
				this.getAllNewVariatesToProcess(csvHeader, meansDataSet.getVariableTypes().getVariates().getVariableTypes());
		Term lsMeanTerm = new Term(lsMean.getCvTermId(), lsMean.getName(), lsMean.getDefinition());
		Term errorEstimateTerm = new Term(errorEstimate.getCvTermId(), errorEstimate.getName(), errorEstimate.getDefinition());
		for (String variateName : inputDataSetVariateNames) {
			DMSVariableType variate = inputDataSet.getVariableTypes().findByLocalName(variateName);
			// add means of the variate to the means dataset
			this.addVariableToDataset(meansDataSet, this.createAnalysisVariable(variate, variateName
					+ BreedingViewImportServiceImpl.MEANS_SUFFIX, lsMeanTerm, programUUID, rank++));
			// add unit errors of the variate to the means dataset
			this.addVariableToDataset(meansDataSet, this.createAnalysisVariable(variate, variateName
					+ BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, errorEstimateTerm, programUUID, rank++));
		}

		return meansDataSet;
	}

	private void addVariableToDataset(DataSet dataSet, DMSVariableType meansVariableType) {
		this.studyDataManager.addDataSetVariableType(dataSet.getId(), meansVariableType);
		dataSet.getVariableTypes().add(meansVariableType);
	}

	/***
	 * 
	 * This method creates the analysis variable based from the variates in the plot dataset. Basically, the difference between the original
	 * variate and the new means variable is their name and the ontology variable where they are associated, having a different method and
	 * having no specific variable value constraints. This method also creates the ontology variable if it is still not existing.
	 * 
	 * @param originalVariableType - the variate where the analysis variable will be based
	 * @param name - the name of the analysis variable
	 * @param method - the method of the analysis variable
	 * @param programUUID - the program where the analysis belongs
	 * @param rank - the rank of the analysis variable from the list
	 * @return DMSVariableType - the new analysis variable
	 */
	private DMSVariableType createAnalysisVariable(DMSVariableType originalVariableType, String name, Term method, String programUUID,
			int rank) {
		DMSVariableType analysisVariableType = this.cloner.deepClone(originalVariableType);
		analysisVariableType.setLocalName(name);
		StandardVariable standardVariable = analysisVariableType.getStandardVariable();
		standardVariable.setMethod(method);

		Integer ontologyVariableId =
				this.findOntologyVariableId(standardVariable.getProperty().getId(), standardVariable.getScale().getId(), standardVariable
						.getMethod().getId(), programUUID);

		if (ontologyVariableId == null) {

			String variableName = name;
			if (this.isVariableExisting(variableName)) {
				variableName = variableName + "_1";
			}

			ontologyVariableId =
					this.saveAnalysisVariable(variableName, standardVariable.getDescription(), standardVariable.getMethod().getId(),
							standardVariable.getProperty().getId(), standardVariable.getScale().getId(), programUUID);

			standardVariable.setId(ontologyVariableId);
			standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		} else {

			analysisVariableType.setStandardVariable(this
					.createStandardardVariable(ontologyVariableId, programUUID, PhenotypicType.VARIATE));
		}

		analysisVariableType.setRank(rank);
		return analysisVariableType;
	}

	/***
	 * 
	 * This method processes the headers from the CSV file which are list of means variable names. The variate names are extracted from the
	 * headers and added to the list. Existing variates are removed from the list. The list with only the new variates is returned.
	 * 
	 * @param csvHeader - the array of headers from the CSV file
	 * @param existingMeansVariables - existing means variables in the means dataset of the study
	 * @return Set<String> - unique list of new variates
	 */
	private Set<String> getAllNewVariatesToProcess(String[] csvHeader, List<DMSVariableType> existingMeansVariables) {
		Set<String> newVariateNames = new LinkedHashSet<>();
		List<String> inputDataSetVariateNames = new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(csvHeader, 3, csvHeader.length)));

		for (String csvHeaderNames : inputDataSetVariateNames) {
			String variateName = csvHeaderNames.substring(0, csvHeaderNames.lastIndexOf("_"));
			newVariateNames.add(variateName);
		}

		// only process the new traits that were not part of the previous analysis
		if (existingMeansVariables != null) {
			for (DMSVariableType var : existingMeansVariables) {
				String variateName = var.getLocalName().trim();
				variateName = variateName.substring(0, variateName.lastIndexOf("_"));
				newVariateNames.remove(variateName);
			}
		}

		return newVariateNames;
	}

	private Integer findOntologyVariableId(int propertyId, int scaleId, Integer methodId, String programUUID) {
		Integer ontologyVariableId = null;

		VariableFilter filterOpts = new VariableFilter();
		filterOpts.setProgramUuid(programUUID);
		filterOpts.addPropertyId(propertyId);
		filterOpts.addMethodId(methodId);
		filterOpts.addScaleId(scaleId);

		List<org.generationcp.middleware.domain.ontology.Variable> variableList =
				this.ontologyVariableDataManager.getWithFilter(filterOpts);
		if (variableList != null && !variableList.isEmpty()) {
			org.generationcp.middleware.domain.ontology.Variable variable = variableList.get(0);
			ontologyVariableId = variable.getId();
		}
		return ontologyVariableId;
	}

	protected StandardVariable createStandardardVariable(int termId, String programUUID, PhenotypicType phenotypicType) {
		org.generationcp.middleware.domain.ontology.Variable ontologyVariable =
				this.ontologyVariableDataManager.getVariable(programUUID, termId, false, false);
		StandardVariable standardVariable = this.standardVariableTransformer.transformVariable(ontologyVariable);
		standardVariable.setPhenotypicType(phenotypicType);
		return standardVariable;
	}

	protected Variable createVariable(int termId, String value, int rank, String programUUID, PhenotypicType phenotypicType)
			throws MiddlewareException {

		StandardVariable stVar = this.createStandardardVariable(termId, programUUID, phenotypicType);

		DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		vtype.setRole(phenotypicType);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	protected void updateVariableType(DMSVariableType type, String name, String description, VariableType variableType) {
		type.setLocalName(name);
		type.setLocalDescription(description);
		type.setVariableType(variableType);
	}

	protected Map<String, String> generateNameToAliasMap(int studyId) throws MiddlewareException {

		if (this.localNameToAliasMap != null) {
			return this.localNameToAliasMap;
		} else {
			List<DMSVariableType> variateList = this.getPlotDataSet(studyId).getVariableTypes().getVariableTypes();

			Map<String, String> nameAliasMap = new HashMap<>();

			for (Iterator<DMSVariableType> i = variateList.iterator(); i.hasNext();) {
				DMSVariableType k = i.next();
				String nameSanitized = k.getLocalName().replaceAll(BreedingViewImportServiceImpl.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				nameAliasMap.put(nameSanitized, k.getLocalName());
			}

			return nameAliasMap;
		}
	}

	public class MeansCSV {

		private final Map<String, String> nameToAliasMapping;
		private final File file;

		public MeansCSV(File file, Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public Map<String, ArrayList<String>> csvToMap() throws IOException {

			CSVReader reader = new CSVReader(new FileReader(this.file));
			Map<String, ArrayList<String>> csvMap = new LinkedHashMap<String, ArrayList<String>>();
			String[] header = reader.readNext();

			for (String headerCol : header) {
				String aliasLocalName =
						headerCol.trim().replace(BreedingViewImportServiceImpl.MEANS_SUFFIX, "")
								.replace(BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, "");
				String actualLocalName = null;

				actualLocalName = this.nameToAliasMapping.get(aliasLocalName);
				if (actualLocalName == null) {
					actualLocalName = aliasLocalName;
				}
				csvMap.put(headerCol.trim().replace(aliasLocalName, actualLocalName), new ArrayList<String>());

			}
			String[] trimHeader = csvMap.keySet().toArray(new String[0]);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					for (int i = 0; i < header.length; i++) {
						csvMap.get(trimHeader[i]).add(nextLine[i].trim());
					}
				}
			}

			reader.close();

			return csvMap;
		}

		public void validate() throws BreedingViewInvalidFormatException {

			int meansCounter = 0;
			int unitErrorsCounter = 0;

			CSVReader reader;
			String[] header = new String[] {};

			try {
				reader = new CSVReader(new FileReader(this.file));
				header = reader.readNext();
				reader.close();
			} catch (Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the MEANS data file", e);
			}

			for (String s : header) {
				if (s.contains(BreedingViewImportServiceImpl.MEANS_SUFFIX)) {
					meansCounter++;
				}
				if (s.contains(BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX)) {
					unitErrorsCounter++;
				}
			}

			if (meansCounter != unitErrorsCounter || meansCounter == 0 && unitErrorsCounter == 0) {
				throw new BreedingViewInvalidFormatException("Cannot parse the file because the format is invalid for MEANS data.");
			}
		}
	}

	public class OutlierCSV {

		private final File file;
		private Map<String, Map<String, ArrayList<String>>> data;
		private final Map<String, String> nameToAliasMapping;
		private String[] header;

		public OutlierCSV(File file, Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException {

			this.data = this.getData();

			return Arrays.asList(this.header);
		}

		public List<String> getHeaderTraits() throws IOException {

			this.data = this.getData();

			List<String> list = new ArrayList<String>(Arrays.asList(this.header));
			list.remove(0);
			list.remove(0);
			return list;
		}

		public String getTrialHeader() throws IOException {

			return this.getHeader().get(0);
		}

		public Map<String, Map<String, ArrayList<String>>> getData() throws IOException {

			if (this.data != null) {
				return this.data;
			}

			CSVReader reader = new CSVReader(new FileReader(this.file));
			this.data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();

			List<String> list = new ArrayList<String>();
			for (String aliasLocalName : reader.readNext()) {
				String actualLocalName = null;
				actualLocalName = this.nameToAliasMapping.get(aliasLocalName);
				if (actualLocalName == null) {
					actualLocalName = aliasLocalName;
				}
				list.add(actualLocalName);
			}

			this.header = list.toArray(new String[0]);

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				String environment = nextLine[0].trim();
				String trait = nextLine[1].trim();

				if (!this.data.containsKey(environment)) {
					this.data.put(environment, new LinkedHashMap<String, ArrayList<String>>());
				}

				if (!this.data.get(environment).containsKey(trait)) {
					this.data.get(environment).put(trait, new ArrayList<String>());
				}
				for (int i = 2; i < this.header.length; i++) {
					this.data.get(environment).get(trait).add(nextLine[i].trim());
				}

			}

			reader.close();
			return this.data;
		}

		public void validate() throws BreedingViewInvalidFormatException {

			CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(this.file));
				reader.readNext();
				reader.close();
			} catch (Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Outlier data file", e);
			}
		}

	}

	public class SummaryStatsCSV {

		private final File file;
		private Map<String, Map<String, ArrayList<String>>> data;
		private final Map<String, String> nameToAliasMapping;
		private String[] header;

		public SummaryStatsCSV(File file, Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException {

			this.data = this.getData();

			return Arrays.asList(this.header);
		}

		public List<String> getHeaderStats() throws IOException {

			this.data = this.getData();

			List<String> list = new ArrayList<String>(Arrays.asList(this.header));
			list.remove(0);
			list.remove(0);
			return list;
		}

		public String getTrialHeader() throws IOException {

			String actualLocalName = this.nameToAliasMapping.get(this.getHeader().get(0));
			if (actualLocalName == null) {
				actualLocalName = this.getHeader().get(0);
			}
			return actualLocalName;
		}

		public Map<String, Map<String, ArrayList<String>>> getData() throws IOException {

			if (this.data != null) {
				return this.data;
			}

			CSVReader reader = new CSVReader(new FileReader(this.file));
			this.data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();
			this.header = reader.readNext();
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {

				String environment = nextLine[0].trim();
				String trait = null;

				String traitString = this.nameToAliasMapping.get(nextLine[1]);
				if (traitString != null) {
					trait = traitString.trim();
				}

				if (trait == null) {
					trait = nextLine[1].trim();
				}

				if (!this.data.containsKey(environment)) {
					this.data.put(environment, new LinkedHashMap<String, ArrayList<String>>());
				}

				if (!this.data.get(environment).containsKey(trait)) {
					this.data.get(environment).put(trait, new ArrayList<String>());
				}
				for (int i = 2; i < this.header.length; i++) {
					this.data.get(environment).get(trait).add(nextLine[i].trim());
				}

			}

			reader.close();
			return this.data;
		}

		public void validate() throws BreedingViewInvalidFormatException {

			CSVReader reader;
			String[] header = new String[] {};

			try {
				reader = new CSVReader(new FileReader(this.file));
				header = reader.readNext();
				reader.close();
			} catch (Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Summary Statistics data file", e);
			}

			List<String> headerList = Arrays.asList(header);

			if (!headerList
					.containsAll(Arrays
							.asList("Trait,NumValues,NumMissing,Mean,Variance,SD,Min,Max,Range,Median,LowerQuartile,UpperQuartile,MeanRep,MinRep,MaxRep,MeanSED,MinSED,MaxSED,MeanLSD,MinLSD,MaxLSD,CV,Heritability,WaldStatistic,WaldDF,Pvalue"
									.split(",")))) {
				throw new BreedingViewInvalidFormatException("Cannot parse the file because the format is invalid for Summary Statistics.");
			}
		}

	}

	protected void setCloner(Cloner cloner) {
		this.cloner = cloner;
	}

}
