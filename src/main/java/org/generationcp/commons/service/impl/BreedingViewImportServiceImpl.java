
package org.generationcp.commons.service.impl;

import au.com.bytecode.opencsv.CSVReader;
import com.rits.cloning.Cloner;
import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.commons.util.DatasetUtil;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

@Configurable
public class BreedingViewImportServiceImpl implements BreedingViewImportService {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingViewImportServiceImpl.class);

	private static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";
	private static final String UNIT_ERRORS_SUFFIX = "_UnitErrors";
	private static final String MEANS_SUFFIX = "_Means";
	private static final String LS_MEAN = "LS MEAN";
	private static final String ERROR_ESTIMATE = "error estimate";

	private StudyDataManager studyDataManager;

	private OntologyDataManager ontologyDataManager;

	private DataSet plotDataSet;

	private Map<String, String> localNameToAliasMap = null;

	@Autowired
	private Cloner cloner;

	public BreedingViewImportServiceImpl() {

	}

	public BreedingViewImportServiceImpl(StudyDataManager studyDataManager, OntologyDataManager ontologyDataManager) {
		this.studyDataManager = studyDataManager;
		this.ontologyDataManager = ontologyDataManager;
	}

	public BreedingViewImportServiceImpl(Project project, ManagerFactoryProvider managerFactoryProvider) {
		ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
		this.studyDataManager = managerFactory.getNewStudyDataManager();
		this.ontologyDataManager = managerFactory.getNewOntologyDataManager();
	}

	@Override
	public void importMeansData(File file, int studyId) throws BreedingViewImportException {

		Boolean meansDataSetExists = false;
		List<ExperimentValues> experimentValuesList = new ArrayList<>();

		try {
			
			DmsProject study = studyDataManager.getProject(studyId);
			String programUUID = study.getProgramUUID();
			
			Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			Map<String, ArrayList<String>> traitsAndMeans = new MeansCSV(file, nameToAliasMap).csvToMap();
			Map<String, Integer> ndGeolocationIds = new HashMap<String, Integer>();

			if (!traitsAndMeans.isEmpty()) {

				String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);

				DataSet meansDataSet = null;

				List<DataSet> ds = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);

				if (ds != null) {
					if (!ds.isEmpty()) {
						meansDataSet = ds.get(0);
					}
					if (meansDataSet != null) {
						meansDataSet = this.appendVariableTypesToExistingMeans(csvHeader, 
								this.getPlotDataSet(studyId), meansDataSet, programUUID);
						meansDataSetExists = true;
					}
				}

				TrialEnvironments trialEnvironments =
						this.studyDataManager.getTrialEnvironmentsInDataset(this.getPlotDataSet(studyId).getId());
				for (TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()) {
					ndGeolocationIds.put(trialEnv.getVariables().findByLocalName(csvHeader[0]).getValue(), trialEnv.getId());
				}

				Stocks stocks = this.studyDataManager.getStocksInDataset(this.getPlotDataSet(studyId).getId());
				DataSet dataSet = this.getPlotDataSet(studyId);

				VariableTypeList meansVariatesList = this.getMeansVariableTypeList();

				// Get only the trial environment and germplasm factors

				for (DMSVariableType factorFromDataSet : dataSet.getVariableTypes().getFactors().getVariableTypes()) {
					if (factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
							|| factorFromDataSet.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						meansVariatesList.makeRoom(1);
						factorFromDataSet.setRank(1);
						meansVariatesList.add(factorFromDataSet);
					}
				}
				// get variates only
				VariableTypeList allVariatesList = dataSet.getVariableTypes().getVariates();

				Integer numOfFactorsAndVariates =
						meansVariatesList.getFactors().getVariableTypes().size()
								+ meansVariatesList.getVariates().getVariableTypes().size() + 1;

				for (int i = 2; i < csvHeader.length; i++) {
					this.createMeansVariableType(numOfFactorsAndVariates, csvHeader[i], 
							allVariatesList, meansVariatesList, programUUID);
				}

				// please make sure that the study name is unique and does not exist in the db.
				VariableList variableList = new VariableList();
				Variable variable = this.createVariable(TermId.DATASET_NAME.getId(), study.getName() + "-MEANS", 1, programUUID, PhenotypicType.DATASET);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());

				// name of dataset [STUDY NAME]-MEANS
				this.updateVariableType(variable.getVariableType(), study.getName() + "-MEANS", "Dataset name (local)");
				variableList.add(variable);

				variable = this.createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2, programUUID, PhenotypicType.DATASET);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());
				this.updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
				variableList.add(variable);

				variable = this.createVariable(TermId.DATASET_TYPE.getId(), "10070", 3, programUUID, PhenotypicType.DATASET);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());
				this.updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
				variableList.add(variable);
				DatasetValues datasetValues = new DatasetValues();
				datasetValues.setVariables(variableList);

				DatasetReference datasetReference = null;
				if (meansDataSet == null) {
					// save data
					// get dataset using new datasetid
					datasetReference = this.studyDataManager.addDataSet(studyId, meansVariatesList, datasetValues, "");
					meansDataSet = this.studyDataManager.getDataSet(datasetReference.getId());
				}

				experimentValuesList = new ArrayList<ExperimentValues>();
				List<String> environments = traitsAndMeans.get(csvHeader[0]);
				for (int i = 0; i < environments.size(); i++) {

					String envName = traitsAndMeans.get(csvHeader[0]).get(i).replace(";", ",");

					Stock stock = stocks.findOnlyOneByLocalName(csvHeader[1], traitsAndMeans.get(csvHeader[1]).get(i));
					if (stock != null) {
						ExperimentValues experimentRow = new ExperimentValues();
						experimentRow.setGermplasmId(stock.getId());
						Integer ndLocationId = ndGeolocationIds.get(envName);
						experimentRow.setLocationId(ndLocationId);

						List<Variable> list = new ArrayList<Variable>();

						for (int j = 2; j < csvHeader.length; j++) {
							if (meansDataSetExists) {
								if (meansDataSet.getVariableTypes().getVariates().findByLocalName(csvHeader[j]) == null) {
									continue;
								}
							}

							String variableValue = traitsAndMeans.get(csvHeader[j]).get(i).trim();
							if (!variableValue.trim().isEmpty()) {
								Variable var = new Variable(meansDataSet.getVariableTypes().findByLocalName(csvHeader[j]), variableValue);
								list.add(var);
							}

						}
						VariableList variableList1 = new VariableList();
						variableList1.setVariables(list);
						experimentRow.setVariableList(variableList1);
						experimentValuesList.add(experimentRow);

					}

				}

				this.studyDataManager.addOrUpdateExperiment(meansDataSet.getId(), ExperimentType.AVERAGE, experimentValuesList);

			}
		} catch (Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}
	}

	@Override
	public void importMeansData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importMeansData(file, studyId);
	}

	@Override
	public void importSummaryStatsData(File file, int studyId) throws BreedingViewImportException {

		try {
			
			Map<String, String> nameToAliasMap = this.generateNameToAliasMap(studyId);
			SummaryStatsCSV summaryStatsCSV = new SummaryStatsCSV(file, nameToAliasMap);

			Map<String, Map<String, ArrayList<String>>> summaryStatsData = summaryStatsCSV.getData();

			DataSet trialDataSet = this.getTrialDataSet(studyId);

			// used in getting the new project properties
			VariableTypeList variableTypeListVariates = this.getPlotDataSet(studyId).getVariableTypes().getVariates();

			// list that will contain all summary stats project properties
			VariableTypeList variableTypeListSummaryStats = new VariableTypeList();

			List<String> summaryStatsList = summaryStatsCSV.getHeaderStats();
			String trialLocalName = summaryStatsCSV.getTrialHeader();

			for (String summaryStatName : summaryStatsList) {
				Term termSummaryStat = this.ontologyDataManager.findMethodByName(summaryStatName);
				if (termSummaryStat == null) {
					termSummaryStat = this.ontologyDataManager.addMethod(summaryStatName, summaryStatName + "  (system generated method)");
				}
			}

			BreedingViewImportServiceImpl.LOG.info("prepare the summary stats project properties if necessary");
			int lastRank = trialDataSet.getVariableTypes().size();

			List<StandardVariable> list = new ArrayList<StandardVariable>();

			for (String summaryStatName : summaryStatsList) {

				for (DMSVariableType variate : variableTypeListVariates.getVariableTypes()) {

					if (nameToAliasMap.containsValue(variate.getLocalName())) {

						DMSVariableType originalVariableType = null;
						DMSVariableType summaryStatVariableType = null;
						Term termSummaryStat = this.ontologyDataManager.findMethodByName(summaryStatName);
						Term termIsASummaryStat = this.ontologyDataManager.getTermById(TermId.SUMMARY_STATISTIC.getId());

						// check if the summary stat trait is already existing
						String trait = variate.getLocalName();
						String localName = trait + "_" + summaryStatName;
						summaryStatVariableType = trialDataSet.findVariableTypeByLocalName(localName);
						// this means we need to append the traits in the dataset project properties
						if (summaryStatVariableType == null) {
							BreedingViewImportServiceImpl.LOG.info(localName + " project property not found.. need to add " + localName);
							originalVariableType = variableTypeListVariates.findByLocalName(trait);
							summaryStatVariableType = this.cloner.deepClone(originalVariableType);
							summaryStatVariableType.setLocalName(localName);

							Integer stdVariableId =
									this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(summaryStatVariableType
											.getStandardVariable().getProperty().getId(), summaryStatVariableType.getStandardVariable()
											.getScale().getId(), termSummaryStat.getId());

							if (stdVariableId == null) {

								StandardVariable stdVariable = new StandardVariable();
								stdVariable = this.cloner.deepClone(summaryStatVariableType.getStandardVariable());
								stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "", ""));
								stdVariable.setEnumerations(null);
								stdVariable.setConstraints(null);
								stdVariable.setId(0);
								stdVariable.setName(summaryStatVariableType.getLocalName());
								stdVariable.setMethod(termSummaryStat);
								stdVariable.setPhenotypicType(PhenotypicType.VARIATE);

								if (termIsASummaryStat != null) {
									stdVariable.setIsA(termIsASummaryStat);
								}

								// check if localname is already used
								Term existingStdVar = this.ontologyDataManager.findTermByName(stdVariable.getName(), CvId.VARIABLES);
								if (existingStdVar != null) {
									// rename
									stdVariable.setName(stdVariable.getName() + "_1");
								}

								list.add(stdVariable);
								summaryStatVariableType.setStandardVariable(stdVariable);
								BreedingViewImportServiceImpl.LOG.info("added standard variable "
										+ summaryStatVariableType.getStandardVariable().getName());
							} else {							
								StandardVariable stdVar = this.ontologyDataManager.
										getStandardVariable(stdVariableId,
												studyDataManager.getProject(studyId).getProgramUUID());
								stdVar.setPhenotypicType(PhenotypicType.VARIATE);
								
								if (stdVar.getEnumerations() != null) {
									for (Enumeration enumeration : stdVar.getEnumerations()) {
										this.ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
									}
								}
								stdVar.setEnumerations(null);
								stdVar.setConstraints(null);
								this.ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
								summaryStatVariableType.setStandardVariable(stdVar);
								BreedingViewImportServiceImpl.LOG.info("reused standard variable "
										+ summaryStatVariableType.getStandardVariable().getName());
							}

							summaryStatVariableType.setRank(++lastRank);
							variableTypeListSummaryStats.add(summaryStatVariableType);
							trialDataSet.getVariableTypes().add(summaryStatVariableType);
						}
					}
				}
			}

			this.ontologyDataManager.addStandardVariable(list);

			Set<String> environments = summaryStatsData.keySet();
			List<ExperimentValues> summaryStatsExperimentValuesList = new ArrayList<ExperimentValues>();
			List<Integer> locationIds = new ArrayList<Integer>();

			TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.getPlotDataSet(studyId).getId());

			for (String summaryStatName : summaryStatsList) {

				DMSVariableType summaryStatVariableType = null;

				for (String env : environments) {

					BreedingViewImportServiceImpl.LOG.info("prepare experiment values per location, " + trialLocalName + "=" + env);
					// --------- prepare experiment values per location ------------------------------------------------------//
					TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(trialLocalName, env.replace(";", ","));
					if (trialEnv == null) {
						trialEnv = trialEnvironments.findOnlyOneByLocalName(trialLocalName, env);
					}
					int ndLocationId = trialEnv.getId();
					BreedingViewImportServiceImpl.LOG.info("ndLocationId =" + ndLocationId);
					locationIds.add(ndLocationId);
					List<Variable> traits = new ArrayList<Variable>();
					VariableList variableList = new VariableList();
					variableList.setVariables(traits);
					ExperimentValues e = new ExperimentValues();
					e.setVariableList(variableList);
					e.setLocationId(ndLocationId);
					summaryStatsExperimentValuesList.add(e);

					Map<String, ArrayList<String>> traitSummaryStats = summaryStatsData.get(env);
					for (Entry<String, ArrayList<String>> traitSummaryStat : traitSummaryStats.entrySet()) {
						String trait = traitSummaryStat.getKey();

						String summaryStatValue = traitSummaryStat.getValue().get(summaryStatsList.indexOf(summaryStatName));
						String localName = trait + "_" + summaryStatName;

						// get summary stat trait
						summaryStatVariableType = trialDataSet.findVariableTypeByLocalName(localName);

						// ---------- prepare experiments -------------------------------------//
						if (summaryStatVariableType != null) {
							Variable var = new Variable(summaryStatVariableType, summaryStatValue);
							e.getVariableList().getVariables().add(var);
							BreedingViewImportServiceImpl.LOG.info("preparing experiment variable "
									+ summaryStatVariableType.getLocalName() + " with value " + summaryStatValue);
						}
					}
				}

			}

			// ------------ save project properties and experiments ----------------------------------//
			DmsProject project = new DmsProject();
			project.setProjectId(trialDataSet.getId());
			this.studyDataManager.saveTrialDatasetSummary(project, variableTypeListSummaryStats, summaryStatsExperimentValuesList,
					locationIds);

		} catch (Exception e) {
			throw new BreedingViewImportException(e.getMessage(), e);
		}

	}

	@Override
	public void importSummaryStatsData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		this.importSummaryStatsData(file, studyId);
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
		if (this.plotDataSet != null && this.plotDataSet.getId() == studyId) {
			return this.plotDataSet;
		} else {
			this.plotDataSet = this.studyDataManager.getDataSet(DatasetUtil.getPlotDataSetId(this.studyDataManager, studyId));
		}
		return this.plotDataSet;
	}

	protected DataSet getTrialDataSet(int studyId) throws MiddlewareException {
		return DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);
	}

	protected DataSet appendVariableTypesToExistingMeans(String[] csvHeader, 
			DataSet inputDataSet, DataSet meansDataSet, String programUUID)
			throws MiddlewareException {

		List<Integer> numericTypes = new ArrayList<Integer>();
		numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
		numericTypes.add(TermId.MIN_VALUE.getId());
		numericTypes.add(TermId.MAX_VALUE.getId());
		numericTypes.add(TermId.DATE_VARIABLE.getId());
		numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());

		List<Integer> standardVariableIdTracker = new ArrayList<Integer>();

		int rank =
				meansDataSet.getVariableTypes().getVariableTypes().get(meansDataSet.getVariableTypes().getVariableTypes().size() - 1)
						.getRank() + 1;

		List<String> inputDataSetVariateNames = new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(csvHeader, 3, csvHeader.length)));
		List<String> meansDataSetVariateNames = new ArrayList<String>();

		Iterator<String> iterator = inputDataSetVariateNames.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().contains(BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX)) {
				iterator.remove();
			}
		}

		for (DMSVariableType var : meansDataSet.getVariableTypes().getVariates().getVariableTypes()) {
			standardVariableIdTracker.add(var.getStandardVariable().getId());
			if (!var.getStandardVariable().getMethod().getName().equalsIgnoreCase(BreedingViewImportServiceImpl.ERROR_ESTIMATE)) {
				meansDataSetVariateNames.add(var.getLocalName().trim());
			}

		}

		if (meansDataSetVariateNames.size() < inputDataSetVariateNames.size()) {

			inputDataSetVariateNames.removeAll(meansDataSetVariateNames);

			for (String variateName : inputDataSetVariateNames) {
				String root = variateName.substring(0, variateName.lastIndexOf("_"));
				if (!"".equals(root)) {

					DMSVariableType meansVariableType = this.cloner.deepClone(inputDataSet.getVariableTypes().findByLocalName(root));
					meansVariableType.setLocalName(root + BreedingViewImportServiceImpl.MEANS_SUFFIX);

					Term termLSMean = this.ontologyDataManager.findMethodByName(BreedingViewImportServiceImpl.LS_MEAN);
					Term termTreatmentMean = this.ontologyDataManager.getTermById(TermId.TREATMENT_MEAN.getId());

					if (termLSMean == null) {
						String definitionMeans = meansVariableType.getStandardVariable().getMethod().getDefinition();
						termLSMean = this.ontologyDataManager.addMethod(BreedingViewImportServiceImpl.LS_MEAN, definitionMeans);
					}

					Integer stdVariableId =
							this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(meansVariableType.getStandardVariable()
									.getProperty().getId(), meansVariableType.getStandardVariable().getScale().getId(), termLSMean.getId());

					// check if the stdVariableId already exists in the standardVariableIdTracker
					for (Integer vt : standardVariableIdTracker) {
						if (stdVariableId != null && vt.intValue() == stdVariableId.intValue()) {

							termLSMean = this.ontologyDataManager.findMethodByName("LS MEAN (" + root + ")");

							if (termLSMean == null) {
								String definitionMeans = meansVariableType.getStandardVariable().getMethod().getDefinition();
								termLSMean = this.ontologyDataManager.addMethod("LS MEAN (" + root + ")", definitionMeans);
							}

							stdVariableId =
									this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(meansVariableType
											.getStandardVariable().getProperty().getId(), meansVariableType.getStandardVariable()
											.getScale().getId(), termLSMean.getId());
							break;
						}
					}

					if (stdVariableId == null) {
						StandardVariable stdVariable = new StandardVariable();
						stdVariable = this.cloner.deepClone(meansVariableType.getStandardVariable());
						stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "", ""));
						stdVariable.setEnumerations(null);
						stdVariable.setConstraints(null);
						stdVariable.setId(0);
						stdVariable.setName(meansVariableType.getLocalName());
						stdVariable.setMethod(termLSMean);
						stdVariable.setPhenotypicType(PhenotypicType.VARIATE);

						if (termTreatmentMean != null) {
							stdVariable.setIsA(termTreatmentMean);
						}

						// check if name is already used
						Term existingStdVar = this.ontologyDataManager.findTermByName(stdVariable.getName(), CvId.VARIABLES);
						if (existingStdVar != null) {
							// rename
							stdVariable.setName(stdVariable.getName() + "_1");
						}
						this.ontologyDataManager.addStandardVariable(stdVariable,programUUID);
						meansVariableType.setStandardVariable(stdVariable);
						standardVariableIdTracker.add(stdVariable.getId());
					} else {
						StandardVariable stdVar = this.ontologyDataManager.getStandardVariable(stdVariableId,programUUID);
						stdVar.setPhenotypicType(PhenotypicType.VARIATE);
						if (stdVar.getEnumerations() != null) {
							for (Enumeration enumeration : stdVar.getEnumerations()) {
								this.ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
							}
						}
						stdVar.setEnumerations(null);
						this.ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
						meansVariableType.setStandardVariable(stdVar);
						standardVariableIdTracker.add(stdVariableId);
					}

					meansVariableType.setRank(rank);
					try {
						this.studyDataManager.addDataSetVariableType(meansDataSet.getId(), meansVariableType);
						rank++;
					} catch (MiddlewareQueryException e) {
						BreedingViewImportServiceImpl.LOG.info("INFO: ", e);
					}

					stdVariableId = null;
					// Unit Errors
					DMSVariableType unitErrorsVariableType = this.cloner.deepClone(inputDataSet.getVariableTypes().findByLocalName(root));
					unitErrorsVariableType.setLocalName(root + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX);

					Term termErrorEstimate = this.ontologyDataManager.findMethodByName("ERROR ESTIMATE");
					Term termSummaryStatistic = this.ontologyDataManager.getTermById(TermId.SUMMARY_STATISTIC.getId());

					if (termErrorEstimate == null) {
						String definitionUErrors = unitErrorsVariableType.getStandardVariable().getMethod().getDefinition();
						termErrorEstimate = this.ontologyDataManager.addMethod("ERROR ESTIMATE", definitionUErrors);
					}

					stdVariableId =
							this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(unitErrorsVariableType
									.getStandardVariable().getProperty().getId(), unitErrorsVariableType.getStandardVariable().getScale()
									.getId(), termErrorEstimate.getId());

					// check if the stdVariableId already exists in the variableTypeList
					for (Integer vt : standardVariableIdTracker) {
						if (stdVariableId != null && vt.intValue() == stdVariableId.intValue()) {

							termErrorEstimate = this.ontologyDataManager.findMethodByName("ERROR ESTIMATE (" + root + ")");
							if (termErrorEstimate == null) {
								String definitionUErrors = unitErrorsVariableType.getStandardVariable().getMethod().getDefinition();
								termErrorEstimate = this.ontologyDataManager.addMethod("ERROR ESTIMATE (" + root + ")", definitionUErrors);
							}

							stdVariableId =
									this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(unitErrorsVariableType
											.getStandardVariable().getProperty().getId(), unitErrorsVariableType.getStandardVariable()
											.getScale().getId(), termErrorEstimate.getId());
							break;
						}
					}

					if (stdVariableId == null) {
						StandardVariable stdVariable = new StandardVariable();
						stdVariable = this.cloner.deepClone(unitErrorsVariableType.getStandardVariable());
						stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "", ""));
						stdVariable.setEnumerations(null);
						stdVariable.setConstraints(null);
						stdVariable.setId(0);
						stdVariable.setName(unitErrorsVariableType.getLocalName());
						stdVariable.setMethod(termErrorEstimate);
						stdVariable.setPhenotypicType(PhenotypicType.VARIATE);
						if (termSummaryStatistic != null) {
							stdVariable.setIsA(termSummaryStatistic);
						}

						// check if name is already used
						Term existingStdVar = this.ontologyDataManager.findTermByName(stdVariable.getName(), CvId.VARIABLES);
						if (existingStdVar != null) {
							// rename
							stdVariable.setName(stdVariable.getName() + "_1");
						}
						this.ontologyDataManager.addStandardVariable(stdVariable,programUUID);
						unitErrorsVariableType.setStandardVariable(stdVariable);
						standardVariableIdTracker.add(stdVariable.getId());
					} else {
						StandardVariable stdVar = this.ontologyDataManager.getStandardVariable(stdVariableId,programUUID);
						stdVar.setPhenotypicType(PhenotypicType.VARIATE);
						
						if (stdVar.getEnumerations() != null) {
							for (Enumeration enumeration : stdVar.getEnumerations()) {
								this.ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
							}
						}
						stdVar.setEnumerations(null);
						this.ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
						unitErrorsVariableType.setStandardVariable(stdVar);
						standardVariableIdTracker.add(stdVariableId);
					}

					unitErrorsVariableType.setRank(rank);
					try {
						this.studyDataManager.addDataSetVariableType(meansDataSet.getId(), unitErrorsVariableType);
						rank++;
					} catch (MiddlewareQueryException e) {
						BreedingViewImportServiceImpl.LOG.info("INFO: ", e);
					}
				}

			}

			return this.studyDataManager.getDataSet(meansDataSet.getId());
		}

		return meansDataSet;

	}

	protected void createMeansVariableType(Integer numOfFactorsAndVariates, String headerName, VariableTypeList allVariatesList,
			VariableTypeList meansVariateList, String programUUID) throws MiddlewareException {

		String traitName = "", localName = "", methodName = "";
		Term isATerm = null;

		traitName = headerName != null && headerName.lastIndexOf("_") != -1 ? headerName.substring(0, headerName.lastIndexOf("_")) : "";
				if (headerName.endsWith(BreedingViewImportServiceImpl.MEANS_SUFFIX)) {
					localName = BreedingViewImportServiceImpl.MEANS_SUFFIX;
					methodName = BreedingViewImportServiceImpl.LS_MEAN;
					isATerm = this.ontologyDataManager.getTermById(TermId.TREATMENT_MEAN.getId());
				} else if (headerName.endsWith(BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX)) {
					localName = BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX;
					methodName = "ERROR ESTIMATE";
					isATerm = this.ontologyDataManager.getTermById(TermId.SUMMARY_STATISTIC.getId());
				} else {
					return;
				}

		DMSVariableType originalVariableType = null;
		DMSVariableType newVariableType = null;

		originalVariableType = allVariatesList.findByLocalName(traitName);
				newVariableType = this.cloner.deepClone(originalVariableType);

		newVariableType.setLocalName(traitName + localName);

		Term termMethod = this.ontologyDataManager.findMethodByName(methodName);
				if (termMethod == null) {
					String definitionMeans = newVariableType.getStandardVariable().getMethod().getDefinition();
					termMethod = this.ontologyDataManager.addMethod(methodName, definitionMeans);
				}

				Integer stdVariableId =
				this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(newVariableType.getStandardVariable().getProperty()
						.getId(), newVariableType.getStandardVariable().getScale().getId(), termMethod.getId());

		// check if the stdVariableId already exists in the variableTypeList
		for (DMSVariableType vt : meansVariateList.getVariableTypes()) {
					if (stdVariableId != null && vt.getStandardVariable().getId() == stdVariableId.intValue()) {

				termMethod = this.ontologyDataManager.findMethodByName(methodName + " (" + traitName + ")");

				if (termMethod == null) {
							String definitionMeans = newVariableType.getStandardVariable().getMethod().getDefinition();
							termMethod = this.ontologyDataManager.addMethod(methodName + " (" + traitName + ")", definitionMeans);
						}

						stdVariableId =
						this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(newVariableType.getStandardVariable()
								.getProperty().getId(), newVariableType.getStandardVariable().getScale().getId(), termMethod.getId());
						break;
					}
				}

		if (stdVariableId == null) {
					StandardVariable stdVariable = new StandardVariable();
					stdVariable = this.cloner.deepClone(newVariableType.getStandardVariable());
					stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "", ""));
					stdVariable.setEnumerations(null);
					stdVariable.setConstraints(null);
					stdVariable.setId(0);
					stdVariable.setName(newVariableType.getLocalName());
					stdVariable.setMethod(termMethod);
					stdVariable.setPhenotypicType(PhenotypicType.VARIATE);

			if (isATerm != null) {
						stdVariable.setIsA(isATerm);
					}

			// check if name is already used
					Term existingStdVar = this.ontologyDataManager.findTermByName(stdVariable.getName(), CvId.VARIABLES);
					if (existingStdVar != null) {
						// rename
				stdVariable.setName(stdVariable.getName() + "_1");
					}
					this.ontologyDataManager.addStandardVariable(stdVariable,programUUID);
					newVariableType.setStandardVariable(stdVariable);

				} else {
					StandardVariable stdVar = this.ontologyDataManager.getStandardVariable(
							stdVariableId, programUUID);
					stdVar.setPhenotypicType(PhenotypicType.VARIATE);
			if (stdVar.getEnumerations() != null) {
				for (Enumeration enumeration : stdVar.getEnumerations()) {
					this.ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
				}
			}
			stdVar.setEnumerations(null);
			this.ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
					newVariableType.setStandardVariable(stdVar);
				}

				meansVariateList.makeRoom(numOfFactorsAndVariates);
				newVariableType.setRank(numOfFactorsAndVariates);
				meansVariateList.add(newVariableType);

	}

	protected Variable createVariable(int termId, String value, int rank, String programUUID, PhenotypicType phenotypicType) throws MiddlewareException {
		StandardVariable stVar = this.ontologyDataManager.getStandardVariable(termId,programUUID);
		stVar.setPhenotypicType(phenotypicType);
		
		DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		vtype.setRole(phenotypicType);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	protected void updateVariableType(DMSVariableType type, String name, String description) {
		type.setLocalName(name);
		type.setLocalDescription(description);
	}

	protected VariableTypeList getMeansVariableTypeList() {
		return new VariableTypeList();
	}

	protected Map<String, String> generateNameToAliasMap(int studyId) throws MiddlewareException {

		if (this.localNameToAliasMap != null) {
			return this.localNameToAliasMap;
		} else {
			List<DMSVariableType> variateList = this.getPlotDataSet(studyId).getVariableTypes().getVariableTypes();

			Map<String, String> nameAliasMap = new HashMap<>();

			for (Iterator<DMSVariableType> i = variateList.iterator(); i.hasNext(); ) {
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
			String[] header = new String[] {};

			try {
				reader = new CSVReader(new FileReader(this.file));
				header = reader.readNext();
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
			if (actualLocalName == null){
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
