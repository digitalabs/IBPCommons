
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
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
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

	private Map<String, String> localNameToAliasMap = null;

	public BreedingViewImportServiceImpl() {

	}

	public BreedingViewImportServiceImpl(final StudyDataManager studyDataManager,
			final OntologyVariableDataManager ontologyVariableDataManager, final OntologyMethodDataManager methodDataManager,
			final OntologyDaoFactory ontologyDaoFactory, final StandardVariableTransformer standardVariableTransformer) {
		this.studyDataManager = studyDataManager;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.methodDataManager = methodDataManager;
		this.ontologyDaoFactory = ontologyDaoFactory;
		this.standardVariableTransformer = standardVariableTransformer;
	}

	@Override
	public void importMeansData(final File file, final int studyId) throws BreedingViewImportException {

		boolean meansDataSetExists = false;
		final CVTerm lsMean =
				this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(BreedingViewImportServiceImpl.LS_MEAN, CvId.METHODS.getId());
		final CVTerm errorEstimate =
				this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(BreedingViewImportServiceImpl.ERROR_ESTIMATE, CvId.METHODS.getId());

		try {

			final DmsProject study = this.studyDataManager.getProject(studyId);
			final String programUUID = study.getProgramUUID();

			final Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			final Map<String, ArrayList<String>> traitsAndMeans = new MeansCSV(file, nameToAliasMap).csvToMap();

			if (!traitsAndMeans.isEmpty()) {

				final String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);

				DataSet meansDataSet = this.getMeansDataSet(studyId);
				final DataSet plotDataSet = this.getPlotDataSet(studyId);
				final DataSet trialDataSet = this.getTrialDataSet(studyId);

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
		} catch (final Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}
	}

	private void createOrAppendMeansExperiments(final DataSet meansDataSet, final Map<String, ArrayList<String>> traitsAndMeans,
			final boolean meansDataSetExists, final int plotDatasetId, final int trialDatasetId) {
		final List<ExperimentValues> experimentValuesList = new ArrayList<>();
		final String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);
		final String envHeader = csvHeader[0];
		final String entryNoHeader = csvHeader[1];
		final Map<String, Integer> envNameToNdGeolocationIdMap = this.getEnvNameToNdGeolocationIdMap(envHeader, trialDatasetId);
		final Map<String, Integer> entroNoToStockIdMap = this.getEntryNoToStockIdMap(entryNoHeader, plotDatasetId);

		final List<String> environments = traitsAndMeans.get(envHeader);
		for (int i = 0; i < environments.size(); i++) {
			// Unfortunately, Breeding View cannot handle double quotes in CSV. Because of that, variables in the CSV file with comma are
			// replaced with semicolon
			// So in comparison, we need to replace semicolon with comma again
			final String envName = environments.get(i).replace(";", ",");
			final Integer ndGeolocationId = envNameToNdGeolocationIdMap.get(envName);
			final String entryNo = traitsAndMeans.get(entryNoHeader).get(i);
			final Integer stockId = entroNoToStockIdMap.get(entryNo);

			final ExperimentValues experimentRow = new ExperimentValues();
			experimentRow.setGermplasmId(stockId);
			experimentRow.setLocationId(ndGeolocationId);

			final List<Variable> list = new ArrayList<Variable>();

			for (int j = 2; j < csvHeader.length; j++) {
				final String meansVariable = csvHeader[j];
				if (meansDataSetExists && meansDataSet.getVariableTypes().getVariates().findByLocalName(meansVariable) == null) {
					continue;
				}

				final String variableValue = traitsAndMeans.get(meansVariable).get(i).trim();
				if (!variableValue.trim().isEmpty()) {
					final Variable var = new Variable(meansDataSet.getVariableTypes().findByLocalName(meansVariable), variableValue);
					list.add(var);
				}

			}

			final VariableList variableList1 = new VariableList();
			variableList1.setVariables(list);
			experimentRow.setVariableList(variableList1);
			experimentValuesList.add(experimentRow);
		}

		this.studyDataManager.addOrUpdateExperiment(meansDataSet.getId(), ExperimentType.AVERAGE, experimentValuesList);
	}

	private Map<String, Integer> getEntryNoToStockIdMap(final String entryNoHeader, final int plotDatasetId) {
		final Stocks stocks = this.studyDataManager.getStocksInDataset(plotDatasetId);
		return stocks.getStockMap(entryNoHeader);
	}

	private Map<String, Integer> getEnvNameToNdGeolocationIdMap(final String envFactor, final int trialDatasetId) {
		final Map<String, Integer> envNameToGeolocationIdMap = new HashMap<String, Integer>();
		final TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(trialDatasetId);
		for (final TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()) {
			envNameToGeolocationIdMap.put(trialEnv.getVariables().findByLocalName(envFactor).getValue(), trialEnv.getId());
		}
		return envNameToGeolocationIdMap;
	}

	private DataSet createMeansDataset(final int studyId, final String datasetName, final String[] csvHeader, final DataSet plotDataSet,
			final String programUUID, final CVTerm lSMean, final CVTerm errorEstimate) {

		final VariableTypeList meansVariableTypeList = new VariableTypeList();
		final VariableList meansVariableList = new VariableList();
		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setVariables(meansVariableList);

		this.addMeansVariableToLists(this.createMeansVariable(TermId.DATASET_NAME.getId(), datasetName, "Dataset name (local)",
				datasetName, 1, programUUID, PhenotypicType.DATASET), meansVariableList, meansVariableTypeList);

		this.addMeansVariableToLists(this.createMeansVariable(TermId.DATASET_TITLE.getId(), "DATASET_TITLE", "Dataset title (local)",
				"My Dataset Description", 2, programUUID, PhenotypicType.DATASET), meansVariableList, meansVariableTypeList);

		this.addMeansVariableToLists(
				this.createMeansVariable(TermId.DATASET_TYPE.getId(), "DATASET_TYPE", "Dataset type (local)",
						String.valueOf(DataSetType.MEANS_DATA.getId()), 3, programUUID, PhenotypicType.DATASET), meansVariableList,
						meansVariableTypeList);

		this.createMeansVariablesFromPlotDatasetAndAddToList(plotDataSet, meansVariableTypeList, 4);

		this.createMeansVariablesFromImportFileAndAddToList(csvHeader, plotDataSet.getVariableTypes().getVariates(), meansVariableTypeList,
				programUUID, lSMean, errorEstimate);

		final DatasetReference datasetReference = this.studyDataManager.addDataSet(studyId, meansVariableTypeList, datasetValues, "");
		return this.studyDataManager.getDataSet(datasetReference.getId());

	}

	private void createMeansVariablesFromImportFileAndAddToList(final String[] csvHeader, final VariableTypeList plotVariates,
			final VariableTypeList meansVariableTypeList, final String programUUID, final CVTerm lsMean, final CVTerm errorEstimate) {
		final int numberOfMeansVariables = meansVariableTypeList.getVariableTypes().size();
		int rank = meansVariableTypeList.getVariableTypes().get(numberOfMeansVariables - 1).getRank() + 1;
		final Set<String> inputDataSetVariateNames = this.getAllNewVariatesToProcess(csvHeader, null);
		final Term lsMeanTerm = new Term(lsMean.getCvTermId(), lsMean.getName(), lsMean.getDefinition());
		final Term errorEstimateTerm = new Term(errorEstimate.getCvTermId(), errorEstimate.getName(), errorEstimate.getDefinition());

		for (final String variateName : inputDataSetVariateNames) {
			final DMSVariableType variate = plotVariates.findByLocalName(variateName);
			meansVariableTypeList.add(this.createAnalysisVariable(variate, variateName + BreedingViewImportServiceImpl.MEANS_SUFFIX,
					lsMeanTerm, programUUID, rank++));
			meansVariableTypeList.add(this.createAnalysisVariable(variate, variateName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX,
					errorEstimateTerm, programUUID, rank++));
		}
	}

	private void createMeansVariablesFromPlotDatasetAndAddToList(final DataSet plotDataSet, final VariableTypeList meansVariableTypeList,
			final int lastRank) {
		int rank = lastRank;
		for (final DMSVariableType factorFromDataSet : plotDataSet.getVariableTypes().getFactors().getVariableTypes()) {
			if (factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
					|| factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
				factorFromDataSet.setRank(++rank);
				meansVariableTypeList.add(factorFromDataSet);
			}
		}
	}

	private void addMeansVariableToLists(final Variable variable, final VariableList meansVariableList,
			final VariableTypeList meansVariableTypeList) {
		meansVariableList.add(variable);
		meansVariableTypeList.add(variable.getVariableType());
	}

	private Variable createMeansVariable(final int ontologyVariableId, final String name, final String definition, final String value,
			final int rank, final String programUUID, final PhenotypicType phenotypicType) {
		final Variable variable = this.createVariable(ontologyVariableId, value, rank, programUUID, phenotypicType);
		final VariableType variableType = new StandardVariableBuilder(null).mapPhenotypicTypeToDefaultVariableType(phenotypicType, true);
		this.updateDMSVariableType(variable.getVariableType(), name, definition, variableType);
		return variable;
	}

	private DataSet getMeansDataSet(final int studyId) {
		final List<DataSet> ds = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);
		if (ds != null && !ds.isEmpty()) {
			// return the 1st one as we're sure that we can only have one means dataset per study
			return ds.get(0);
		}
		return null;
	}

	@Override
	public void importMeansData(final File file, final int studyId, final Map<String, String> localNameToAliasMap)
			throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importMeansData(file, studyId);
	}

	private Map<String, Integer> findOrSaveMethodsIfNotExisting(final List<String> methodNameList) {
		final Map<String, Integer> methodNameToIdMap = new HashMap<>();
		for (final String methodName : methodNameList) {
			final Integer methodId = this.findOrSaveMethod(methodName, methodName + "  (system generated method)");
			methodNameToIdMap.put(methodName, methodId);
		}
		return methodNameToIdMap;
	}

	private Integer findOrSaveMethod(final String methodName, final String methodDefinition) {
		Integer methodId = null;
		final CVTerm cvterm = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(methodName, CvId.METHODS.getId());
		if (cvterm == null) {
			methodId = this.saveMethod(methodName, methodDefinition);
		} else {
			methodId = cvterm.getCvTermId();
		}
		return methodId;
	}

	private Integer saveMethod(final String methodName, final String methodDefinition) {
		final Method method = new Method();
		method.setName(methodName);
		method.setDefinition(methodDefinition);
		this.methodDataManager.addMethod(method);
		return method.getId();
	}

	private boolean isVariableExisting(final String variableName) {
		final CVTerm cvterm = this.ontologyDaoFactory.getCvTermDao().getByNameAndCvId(variableName, CvId.VARIABLES.getId());
		if (cvterm != null) {
			return true;
		}
		return false;
	}

	private Integer saveAnalysisVariable(final String name, final String description, final int methodId, final int propertyId,
			final int scaleId, final String programUUID) {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
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
	public void importSummaryStatsData(final File file, final int studyId, final Map<String, String> localNameToAliasMap)
			throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importSummaryStatsData(file, studyId);
	}

	@Override
	public void importSummaryStatsData(final File file, final int studyId) throws BreedingViewImportException {

		try {

			final Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			final SummaryStatsCSV summaryStatsCSV = new SummaryStatsCSV(file, nameToAliasMap);

			final Map<String, Map<String, ArrayList<String>>> summaryStatsData = summaryStatsCSV.getData();

			final DmsProject study = this.studyDataManager.getProject(studyId);
			final String programUUID = study.getProgramUUID();
			final DataSet trialDataSet = this.getTrialDataSet(studyId);

			// used in getting the new project properties
			final VariableTypeList variableTypeListVariates = this.getPlotDataSet(studyId).getVariableTypes().getVariates();

			final VariableTypeList summaryStatsVariableTypeList =
					this.createSummaryStatsVariableTypes(summaryStatsCSV, trialDataSet, variableTypeListVariates, nameToAliasMap,
							programUUID);

			final Map<String, Integer> envFactorTolocationIdMap =
					this.retrieveAllLocationsOfStudy(summaryStatsData.keySet(), studyId, summaryStatsCSV.getTrialHeader());
			final List<ExperimentValues> summaryStatsExperimentValuesList =
					this.createSummaryStatsExperimentValuesList(trialDataSet, envFactorTolocationIdMap, summaryStatsCSV.getHeaderStats(),
							summaryStatsCSV.getData());

			// save project properties and experiments
			final DmsProject project = new DmsProject();
			project.setProjectId(trialDataSet.getId());
			this.studyDataManager.saveTrialDatasetSummary(project, summaryStatsVariableTypeList, summaryStatsExperimentValuesList,
					new ArrayList<>(envFactorTolocationIdMap.values()));

		} catch (final Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}

	}

	private List<ExperimentValues> createSummaryStatsExperimentValuesList(final DataSet trialDataSet,
			final Map<String, Integer> envFactorTolocationIdMap, final List<String> summaryStatsList,
			final Map<String, Map<String, ArrayList<String>>> summaryStatsData) {

		final List<ExperimentValues> summaryStatsExperimentValuesList = new ArrayList<ExperimentValues>();
		for (final String envFactorValue : envFactorTolocationIdMap.keySet()) {
			for (final String summaryStatName : summaryStatsList) {
				for (final Entry<String, ArrayList<String>> traitSummaryStat : summaryStatsData.get(envFactorValue).entrySet()) {

					final VariableList variableList = new VariableList();
					variableList.setVariables(new ArrayList<Variable>());
					final ExperimentValues experimentValues = new ExperimentValues();
					experimentValues.setVariableList(variableList);
					experimentValues.setLocationId(envFactorTolocationIdMap.get(envFactorValue));

					final DMSVariableType summaryStatVariableType =
							trialDataSet.findVariableTypeByLocalName(traitSummaryStat.getKey() + "_" + summaryStatName);

					if (summaryStatVariableType != null) {
						final String summaryStatValue = traitSummaryStat.getValue().get(summaryStatsList.indexOf(summaryStatName));
						final Variable var = new Variable(summaryStatVariableType, summaryStatValue);
						experimentValues.getVariableList().getVariables().add(var);
						summaryStatsExperimentValuesList.add(experimentValues);
					}
				}
			}
		}
		return summaryStatsExperimentValuesList;
	}

	private Map<String, Integer> retrieveAllLocationsOfStudy(final Set<String> environments, final int studyId, final String envFactorName) {
		final Map<String, Integer> envFactorTolocationIdMap = new LinkedHashMap<>();
		final TrialEnvironments trialEnvironments =
				this.studyDataManager.getTrialEnvironmentsInDataset(this.getTrialDataSet(studyId).getId());
		for (final String env : environments) {
			// Unfortunately, Breeding View cannot handle double quotes in CSV. Because of that, variables in the CSV file with comma are
			// replaced with semicolon
			// So in comparison, we need to replace semicolon with comma again
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

	protected VariableTypeList createSummaryStatsVariableTypes(final SummaryStatsCSV summaryStatsCSV, final DataSet trialDataSet,
			final VariableTypeList plotVariates, final Map<String, String> nameToAliasMap, final String programUUID) throws IOException {
		final VariableTypeList summaryStatsVariableTypeList = new VariableTypeList();

		summaryStatsCSV.getData();
		final List<String> summaryStatsList = summaryStatsCSV.getHeaderStats();
		summaryStatsCSV.getTrialHeader();

		final Map<String, Integer> summaryStatNameToIdMap = this.findOrSaveMethodsIfNotExisting(summaryStatsList);

		final int numberOfTrialDatasetVariables = trialDataSet.getVariableTypes().size();
		int rank = trialDataSet.getVariableTypes().getVariableTypes().get(numberOfTrialDatasetVariables - 1).getRank() + 1;

		for (final String summaryStatName : summaryStatsList) {
			for (final DMSVariableType variate : plotVariates.getVariableTypes()) {
				if (nameToAliasMap.containsValue(variate.getLocalName())) {
					final String trait = variate.getLocalName();
					final String localName = trait + "_" + summaryStatName;
					if (trialDataSet.findVariableTypeByLocalName(localName) == null) {
						final DMSVariableType originalVariableType = plotVariates.findByLocalName(trait);
						final Term summaryStatMethod =
								new Term(summaryStatNameToIdMap.get(summaryStatName), summaryStatName, summaryStatName);
						final DMSVariableType summaryStatVariableType =
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
	public void importOutlierData(final File file, final int studyId) throws BreedingViewImportException {

		try {

			final Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			final OutlierCSV outlierCSV = new OutlierCSV(file, nameToAliasMap);
			final Map<String, Map<String, ArrayList<String>>> outlierData = outlierCSV.getData();
			final Map<String, Integer> ndGeolocationIds = new HashMap<String, Integer>();

			final Map<Integer, Integer> stdVariableIds = new HashMap<Integer, Integer>();
			final VariableTypeList plotVariableTypeList = this.getPlotDataSet(studyId).getVariableTypes();

			Integer i = 0;
			for (final String l : outlierCSV.getHeaderTraits()) {
				final Integer traitId = plotVariableTypeList.findByLocalName(l).getId();
				stdVariableIds.put(i, traitId);
				i++;
			}

			final TrialEnvironments trialEnvironments =
					this.studyDataManager.getTrialEnvironmentsInDataset(this.getPlotDataSet(studyId).getId());
			for (final TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()) {
				ndGeolocationIds.put(trialEnv.getVariables().findByLocalName(outlierCSV.getTrialHeader()).getValue(), trialEnv.getId());
			}

			final Set<String> environments = outlierData.keySet();
			for (final String env : environments) {

				final List<PhenotypeOutlier> outliers = new ArrayList<PhenotypeOutlier>();
				final Integer ndGeolocationId = ndGeolocationIds.get(env);

				for (final Entry<String, ArrayList<String>> plot : outlierData.get(env).entrySet()) {

					final List<Integer> cvTermIds = new ArrayList<Integer>();
					final Integer plotNo = Integer.valueOf(plot.getKey());
					final Map<Integer, String> plotMap = new HashMap<Integer, String>();

					for (int x = 0; x < plot.getValue().size(); x++) {
						final String traitValue = plot.getValue().get(x);
						if (traitValue.isEmpty()) {
							cvTermIds.add(stdVariableIds.get(x));
							plotMap.put(stdVariableIds.get(x), traitValue);
						}

					}

					final List<Object[]> list =
							this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(this.getPlotDataSet(studyId).getId(), ndGeolocationId,
									plotNo, cvTermIds);
					for (final Object[] object : list) {
						final PhenotypeOutlier outlier = new PhenotypeOutlier();
						outlier.setPhenotypeId(Integer.valueOf(object[2].toString()));
						outlier.setValue(plotMap.get(Integer.valueOf(object[1].toString())));
						outliers.add(outlier);
					}

				}

				this.studyDataManager.saveOrUpdatePhenotypeOutliers(outliers);
			}
		} catch (final Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}

	}

	@Override
	public void importOutlierData(final File file, final int studyId, final Map<String, String> localNameToAliasMap)
			throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importOutlierData(file, studyId);
	}

	protected DataSet getPlotDataSet(final int studyId) {
		return DatasetUtil.getPlotDataSet(this.studyDataManager, studyId);
	}

	protected DataSet getTrialDataSet(final int studyId) {
		return DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);
	}

	protected DataSet appendVariableTypesToExistingMeans(final String[] csvHeader, final DataSet inputDataSet, final DataSet meansDataSet,
			final String programUUID, final CVTerm lsMean, final CVTerm errorEstimate) {
		final int numberOfMeansVariables = meansDataSet.getVariableTypes().getVariableTypes().size();
		int rank = meansDataSet.getVariableTypes().getVariableTypes().get(numberOfMeansVariables - 1).getRank() + 1;
		final Set<String> inputDataSetVariateNames =
				this.getAllNewVariatesToProcess(csvHeader, meansDataSet.getVariableTypes().getVariates().getVariableTypes());
		final Term lsMeanTerm = new Term(lsMean.getCvTermId(), lsMean.getName(), lsMean.getDefinition());
		final Term errorEstimateTerm = new Term(errorEstimate.getCvTermId(), errorEstimate.getName(), errorEstimate.getDefinition());
		for (final String variateName : inputDataSetVariateNames) {
			final DMSVariableType variate = inputDataSet.getVariableTypes().findByLocalName(variateName);
			// add means of the variate to the means dataset
			this.addVariableToDataset(meansDataSet, this.createAnalysisVariable(variate, variateName
					+ BreedingViewImportServiceImpl.MEANS_SUFFIX, lsMeanTerm, programUUID, rank++));
			// add unit errors of the variate to the means dataset
			this.addVariableToDataset(meansDataSet, this.createAnalysisVariable(variate, variateName
					+ BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, errorEstimateTerm, programUUID, rank++));
		}

		return meansDataSet;
	}

	private void addVariableToDataset(final DataSet dataSet, final DMSVariableType meansVariableType) {
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
	private DMSVariableType createAnalysisVariable(final DMSVariableType originalVariableType, final String name, final Term method,
			final String programUUID, final int rank) {
		final DMSVariableType analysisVariableType = this.cloner.deepClone(originalVariableType);
		analysisVariableType.setLocalName(name);
		final StandardVariable standardVariable = analysisVariableType.getStandardVariable();
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
	private Set<String> getAllNewVariatesToProcess(final String[] csvHeader, final List<DMSVariableType> existingMeansVariables) {
		final Set<String> newVariateNames = new LinkedHashSet<>();
		final List<String> inputDataSetVariateNames =
				new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(csvHeader, 3, csvHeader.length)));

		for (final String csvHeaderNames : inputDataSetVariateNames) {
			final String variateName = csvHeaderNames.substring(0, csvHeaderNames.lastIndexOf("_"));
			newVariateNames.add(variateName);
		}

		// only process the new traits that were not part of the previous analysis
		if (existingMeansVariables != null) {
			for (final DMSVariableType var : existingMeansVariables) {
				String variateName = var.getLocalName().trim();
				variateName = variateName.substring(0, variateName.lastIndexOf("_"));
				newVariateNames.remove(variateName);
			}
		}

		return newVariateNames;
	}

	private Integer findOntologyVariableId(final int propertyId, final int scaleId, final Integer methodId, final String programUUID) {
		Integer ontologyVariableId = null;

		final VariableFilter filterOpts = new VariableFilter();
		filterOpts.setProgramUuid(programUUID);
		filterOpts.addPropertyId(propertyId);
		filterOpts.addMethodId(methodId);
		filterOpts.addScaleId(scaleId);

		final List<org.generationcp.middleware.domain.ontology.Variable> variableList =
				this.ontologyVariableDataManager.getWithFilter(filterOpts);
		if (variableList != null && !variableList.isEmpty()) {
			final org.generationcp.middleware.domain.ontology.Variable variable = variableList.get(0);
			ontologyVariableId = variable.getId();
		}
		return ontologyVariableId;
	}

	protected StandardVariable createStandardardVariable(final int termId, final String programUUID, final PhenotypicType phenotypicType) {
		final org.generationcp.middleware.domain.ontology.Variable ontologyVariable =
				this.ontologyVariableDataManager.getVariable(programUUID, termId, false, false);
		final StandardVariable standardVariable = this.standardVariableTransformer.transformVariable(ontologyVariable);
		standardVariable.setPhenotypicType(phenotypicType);
		return standardVariable;
	}

	protected Variable createVariable(final int termId, final String value, final int rank, final String programUUID,
			final PhenotypicType phenotypicType) {

		final StandardVariable stVar = this.createStandardardVariable(termId, programUUID, phenotypicType);

		final DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		vtype.setRole(phenotypicType);
		final Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	protected void updateDMSVariableType(final DMSVariableType type, final String name, final String description,
			final VariableType variableType) {
		type.setLocalName(name);
		type.setLocalDescription(description);
		type.setVariableType(variableType);
	}

	protected Map<String, String> generateNameToAliasMap(final int studyId) {

		if (this.localNameToAliasMap != null) {
			return this.localNameToAliasMap;
		} else {
			final List<DMSVariableType> variateList = this.getPlotDataSet(studyId).getVariableTypes().getVariableTypes();

			final Map<String, String> nameAliasMap = new HashMap<>();

			for (final Iterator<DMSVariableType> i = variateList.iterator(); i.hasNext();) {
				final DMSVariableType k = i.next();
				final String nameSanitized =
						k.getLocalName().replaceAll(BreedingViewImportServiceImpl.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				nameAliasMap.put(nameSanitized, k.getLocalName());
			}

			return nameAliasMap;
		}
	}

	public class MeansCSV {

		private final Map<String, String> nameToAliasMapping;
		private final File file;

		public MeansCSV(final File file, final Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public Map<String, ArrayList<String>> csvToMap() throws IOException {

			final CSVReader reader = new CSVReader(new FileReader(this.file));
			final Map<String, ArrayList<String>> csvMap = new LinkedHashMap<String, ArrayList<String>>();
			final String[] header = reader.readNext();

			for (final String headerCol : header) {
				final String aliasLocalName =
						headerCol.trim().replace(BreedingViewImportServiceImpl.MEANS_SUFFIX, "")
						.replace(BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, "");
				String actualLocalName = null;

				actualLocalName = this.nameToAliasMapping.get(aliasLocalName);
				if (actualLocalName == null) {
					actualLocalName = aliasLocalName;
				}
				csvMap.put(headerCol.trim().replace(aliasLocalName, actualLocalName), new ArrayList<String>());

			}
			final String[] trimHeader = csvMap.keySet().toArray(new String[0]);
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
			} catch (final Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the MEANS data file", e);
			}

			for (final String s : header) {
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

		public OutlierCSV(final File file, final Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException {

			this.data = this.getData();

			return Arrays.asList(this.header);
		}

		public List<String> getHeaderTraits() throws IOException {

			this.data = this.getData();

			final List<String> list = new ArrayList<String>(Arrays.asList(this.header));
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

			final CSVReader reader = new CSVReader(new FileReader(this.file));
			this.data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();

			final List<String> list = new ArrayList<String>();
			for (final String aliasLocalName : reader.readNext()) {
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
				final String environment = nextLine[0].trim();
				final String trait = nextLine[1].trim();

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
			} catch (final Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Outlier data file", e);
			}
		}

	}

	public class SummaryStatsCSV {

		private final File file;
		private Map<String, Map<String, ArrayList<String>>> data;
		private final Map<String, String> nameToAliasMapping;
		private String[] header;

		public SummaryStatsCSV(final File file, final Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException {

			this.data = this.getData();

			return Arrays.asList(this.header);
		}

		public List<String> getHeaderStats() throws IOException {

			this.data = this.getData();

			final List<String> list = new ArrayList<String>(Arrays.asList(this.header));
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

			final CSVReader reader = new CSVReader(new FileReader(this.file));
			this.data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();
			this.header = reader.readNext();
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {

				final String environment = nextLine[0].trim();
				String trait = null;

				final String traitString = this.nameToAliasMapping.get(nextLine[1]);
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
			String[] fileHeaders = new String[] {};

			try {
				reader = new CSVReader(new FileReader(this.file));
				fileHeaders = reader.readNext();
				reader.close();
			} catch (final Exception e) {
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Summary Statistics data file", e);
			}

			final List<String> headerList = Arrays.asList(fileHeaders);

			if (!headerList
					.containsAll(Arrays
							.asList("Trait,NumValues,NumMissing,Mean,Variance,SD,Min,Max,Range,Median,LowerQuartile,UpperQuartile,MeanRep,MinRep,MaxRep,MeanSED,MinSED,MaxSED,MeanLSD,MinLSD,MaxLSD,CV,Heritability,WaldStatistic,WaldDF,Pvalue"
									.split(",")))) {
				throw new BreedingViewInvalidFormatException("Cannot parse the file because the format is invalid for Summary Statistics.");
			}
		}

	}

	protected void setCloner(final Cloner cloner) {
		this.cloner = cloner;
	}

}
