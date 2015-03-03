package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.commons.util.DatasetUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stock;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
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

import au.com.bytecode.opencsv.CSVReader;

import com.rits.cloning.Cloner;

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

	public BreedingViewImportServiceImpl(){
		
	}
	
	public BreedingViewImportServiceImpl(StudyDataManager studyDataManager, OntologyDataManager ontologyDataManager){
    	this.studyDataManager = studyDataManager;
    	this.ontologyDataManager = ontologyDataManager;
    }
    
    public BreedingViewImportServiceImpl(Project project, ManagerFactoryProvider managerFactoryProvider){
    	ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
    	this.studyDataManager = managerFactory.getNewStudyDataManager();
    	this.ontologyDataManager = managerFactory.getNewOntologyDataManager();
    }
	
	@Override
	public void importMeansData(File file, int studyId) throws BreedingViewImportException {
		
		Boolean meansDataSetExists = false;
		List<ExperimentValues> experimentValuesList = new ArrayList<>();
		
		try{
			Map<String, String> nameToAliasMap = generateNameToAliasMap(studyId);
			Map<String, ArrayList<String>> traitsAndMeans = new MeansCSV(file, nameToAliasMap).csvToMap();
			Map<String, Integer> ndGeolocationIds = new HashMap<String, Integer>();
			
			if(!traitsAndMeans.isEmpty()) {

				String[] csvHeader = traitsAndMeans.keySet().toArray(new String[0]);
				
				DataSet meansDataSet = null;
			
				List<DataSet> ds = studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);
				
				if (ds != null){
					if (!ds.isEmpty()){
						meansDataSet = ds.get(0);
					}
					if (meansDataSet != null) {
						meansDataSet = appendVariableTypesToExistingMeans(csvHeader , getPlotDataSet(studyId) , meansDataSet);
						meansDataSetExists = true;
					}
				}

				TrialEnvironments trialEnvironments = 
						studyDataManager.getTrialEnvironmentsInDataset(getPlotDataSet(studyId).getId());
				for (TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()){
					ndGeolocationIds.put(trialEnv.getVariables()
							.findByLocalName(csvHeader[0]).getValue(), trialEnv.getId());
				}

				Stocks stocks = studyDataManager.getStocksInDataset(getPlotDataSet(studyId).getId());
				DataSet dataSet = getPlotDataSet(studyId);
				
				VariableTypeList meansVariatesList = getMeansVariableTypeList();
				
				//Get only the trial environment and germplasm factors

				for (VariableType factorFromDataSet : dataSet.getVariableTypes().getFactors().getVariableTypes()){
					if (factorFromDataSet.getStandardVariable().getPhenotypicType() 
							== PhenotypicType.TRIAL_ENVIRONMENT
							|| factorFromDataSet.getStandardVariable().getPhenotypicType() 
							== PhenotypicType.GERMPLASM) {
						meansVariatesList.makeRoom(1);
						factorFromDataSet.setRank(1);
						meansVariatesList.add(factorFromDataSet);
					}
				}
				//get variates only
				VariableTypeList allVariatesList = dataSet.getVariableTypes().getVariates();
				

				Integer numOfFactorsAndVariates = 
						meansVariatesList.getFactors().getVariableTypes().size()
						+ meansVariatesList.getVariates().getVariableTypes().size() + 1;
				
				for(int i = 2; i < csvHeader.length; i++) {
					createMeansVariableType(numOfFactorsAndVariates, csvHeader[i], allVariatesList, meansVariatesList);		
				}


				//please make sure that the study name is unique and does not exist in the db.
				VariableList variableList = new VariableList();
				Study study = studyDataManager.getStudy(studyId);
				Variable variable = createVariable(TermId.DATASET_NAME.getId()
						, study.getName() + "-MEANS"  , 1);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());

				//name of dataset [STUDY NAME]-MEANS
				updateVariableType(variable.getVariableType(), study.getName() + "-MEANS", "Dataset name (local)");
				variableList.add(variable);

				variable = createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());
				updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
				variableList.add(variable);

				variable = createVariable(TermId.DATASET_TYPE.getId(), "10070", 3);
				meansVariatesList.makeRoom(1);
				variable.getVariableType().setRank(1);
				meansVariatesList.add(variable.getVariableType());
				updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
				variableList.add(variable);
				DatasetValues datasetValues = new DatasetValues();
				datasetValues.setVariables(variableList);

				DatasetReference datasetReference = null;
				if (meansDataSet == null){
					//save data
					//get dataset using new datasetid
					datasetReference = studyDataManager.addDataSet(studyId, meansVariatesList, datasetValues, "");
					meansDataSet = studyDataManager.getDataSet(datasetReference.getId());
				}

				experimentValuesList = new ArrayList<ExperimentValues>();
				List<String> environments = traitsAndMeans.get(csvHeader[0]);
				for(int i = 0; i < environments.size(); i++) {

					String envName = traitsAndMeans.get(csvHeader[0]).get(i).replace(";", ",");

					Stock stock = stocks.findOnlyOneByLocalName(
							csvHeader[1], traitsAndMeans.get(csvHeader[1]).get(i));
					if (stock != null){
						ExperimentValues experimentRow = new ExperimentValues();
						experimentRow.setGermplasmId(stock.getId());
						Integer ndLocationId = ndGeolocationIds.get(envName);
						experimentRow.setLocationId(ndLocationId);

						List<Variable> list = new ArrayList<Variable>();

						for(int j = 2; j < csvHeader.length; j++) {
							if (meansDataSetExists){
								if (meansDataSet.getVariableTypes().getVariates()
										.findByLocalName(csvHeader[j]) == null){
									continue;
								}
							}

							String variableValue = traitsAndMeans.get(csvHeader[j]).get(i).trim();
							if (!variableValue.trim().isEmpty()) {
								Variable var = new Variable(meansDataSet.getVariableTypes()
										.findByLocalName(csvHeader[j]), variableValue);
								list.add(var);
							}

						}
						VariableList variableList1 = new VariableList();
						variableList1.setVariables(list);
						experimentRow.setVariableList(variableList1);
						experimentValuesList.add(experimentRow);


					}


				}

				studyDataManager.addOrUpdateExperiment(meansDataSet.getId(), ExperimentType.AVERAGE, experimentValuesList);
			
			}
		}catch(Exception e){
			throw new BreedingViewImportException(e.getMessage(), e);
		}
	}

	@Override
	public void importMeansData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		importMeansData(file, studyId);
	}
	
	@Override
	public void importSummaryStatsData(File file, int studyId) throws BreedingViewImportException {
		
		try {
			
			Map<String, String> nameToAliasMap = generateNameToAliasMap(studyId);
			SummaryStatsCSV summaryStatsCSV = new SummaryStatsCSV(file, nameToAliasMap);

			Map<String, Map<String, ArrayList<String>>> summaryStatsData = 
					summaryStatsCSV.getData();

			DataSet trialDataSet = getTrialDataSet(studyId);

			//used in getting the new project properties
			VariableTypeList variableTypeListVariates = getPlotDataSet(studyId).getVariableTypes().getVariates();

			//list that will contain all summary stats project properties
			VariableTypeList variableTypeListSummaryStats = new VariableTypeList();

			List<String> summaryStatsList = summaryStatsCSV.getHeaderStats();
			String trialLocalName =  summaryStatsCSV.getTrialHeader();

			for (String summaryStatName : summaryStatsList){
				Term termSummaryStat = ontologyDataManager.findMethodByName(summaryStatName);
				if(termSummaryStat == null) {
					termSummaryStat = ontologyDataManager.addMethod(summaryStatName, summaryStatName + "  (system generated method)");
				}
			}


			LOG.info("prepare the summary stats project properties if necessary");
			int lastRank = trialDataSet.getVariableTypes().size();

			List<StandardVariable> list = new ArrayList<StandardVariable>();

			for (String summaryStatName : summaryStatsList){

				for(VariableType variate : variableTypeListVariates.getVariableTypes()) {
					
					if (nameToAliasMap.containsValue(variate.getLocalName())){

						VariableType originalVariableType = null;
						VariableType summaryStatVariableType = null;	
						Term termSummaryStat = ontologyDataManager.findMethodByName(summaryStatName);

						//check if the summary stat trait is already existing
						String trait = variate.getLocalName();
						String localName = trait + "_" + summaryStatName;
						summaryStatVariableType = trialDataSet.findVariableTypeByLocalName(localName);
						//this means we need to append the traits in the dataset project properties
						if(summaryStatVariableType == null) {
							LOG.info(localName + " project property not found.. need to add "+localName);
							originalVariableType = variableTypeListVariates.findByLocalName(trait);
							summaryStatVariableType = cloner.deepClone(originalVariableType);
							summaryStatVariableType.setLocalName(localName);

							Integer stdVariableId = ontologyDataManager
									.getStandardVariableIdByPropertyScaleMethodRole(
											summaryStatVariableType.getStandardVariable().getProperty().getId(),
											summaryStatVariableType.getStandardVariable().getScale().getId(),
											termSummaryStat.getId(),
											PhenotypicType.VARIATE);

							if (stdVariableId == null){
								StandardVariable stdVariable = new StandardVariable();
								stdVariable = cloner.deepClone(summaryStatVariableType.getStandardVariable());
								stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(),"",""));
								stdVariable.setEnumerations(null);
								stdVariable.setConstraints(null);
								stdVariable.setId(0);
								stdVariable.setName(summaryStatVariableType.getLocalName());
								stdVariable.setMethod(termSummaryStat);

								//check if localname is already used
								Term existingStdVar = ontologyDataManager
										.findTermByName(stdVariable.getName(), CvId.VARIABLES);
								if (existingStdVar != null){
									//rename 
									stdVariable.setName(stdVariable.getName()+"_1");
								}

								list.add(stdVariable);
								summaryStatVariableType.setStandardVariable(stdVariable);
								LOG.info("added standard variable "+summaryStatVariableType
										.getStandardVariable().getName());
							}else{
								StandardVariable stdVar = ontologyDataManager
								.getStandardVariable(stdVariableId);
									if (stdVar.getEnumerations() != null){
										for (Enumeration enumeration : stdVar.getEnumerations()){
											ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
										}
									}
								stdVar.setEnumerations(null);
								stdVar.setConstraints(null);
								ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
								summaryStatVariableType.setStandardVariable(stdVar);
								LOG.info("reused standard variable "
										+ summaryStatVariableType.getStandardVariable().getName());	    	            	
							}

							summaryStatVariableType.setRank(++lastRank);
							variableTypeListSummaryStats.add(summaryStatVariableType);
							trialDataSet.getVariableTypes()
							.add(summaryStatVariableType);
						}
					}
				}
			}

			ontologyDataManager.addStandardVariable(list);

			Set<String> environments = summaryStatsData.keySet();
			List<ExperimentValues> summaryStatsExperimentValuesList = new ArrayList<ExperimentValues>();
			List<Integer> locationIds = new ArrayList<Integer>();
			
			TrialEnvironments trialEnvironments = studyDataManager.getTrialEnvironmentsInDataset(getPlotDataSet(studyId).getId());

			for (String summaryStatName : summaryStatsList){

				VariableType summaryStatVariableType = null;

				for(String env : environments) {

					LOG.info("prepare experiment values per location, "+trialLocalName+"="+env);
					//--------- prepare experiment values per location ------------------------------------------------------//
					TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(trialLocalName, env.replace(";", ","));
					if (trialEnv == null) {
						trialEnv = trialEnvironments.findOnlyOneByLocalName(trialLocalName, env);
					}
					int ndLocationId = trialEnv.getId();
					LOG.info("ndLocationId ="+ndLocationId);
					locationIds.add(ndLocationId);
					List<Variable> traits = new ArrayList<Variable>();
					VariableList variableList = new VariableList();
					variableList.setVariables(traits);
					ExperimentValues e = new ExperimentValues();
					e.setVariableList(variableList);
					e.setLocationId(ndLocationId);
					summaryStatsExperimentValuesList.add(e);

					Map<String, ArrayList<String>> traitSummaryStats = summaryStatsData.get(env);
					for(Entry<String, ArrayList<String>> traitSummaryStat : traitSummaryStats.entrySet()) {
						String trait = traitSummaryStat.getKey();

						String summaryStatValue = traitSummaryStat.getValue().get(summaryStatsList.indexOf(summaryStatName));
						String localName = trait + "_" + summaryStatName;

						//get summary stat trait
						summaryStatVariableType = trialDataSet.findVariableTypeByLocalName(localName);

						//---------- prepare experiments -------------------------------------//
						if(summaryStatVariableType!=null) {
							Variable var = new Variable(summaryStatVariableType,summaryStatValue);
							e.getVariableList().getVariables().add(var);
							LOG.info("preparing experiment variable "+summaryStatVariableType.getLocalName()+ 
									" with value "+summaryStatValue);
						}
					}
				}



			}


			//------------ save project properties and experiments ----------------------------------//
			DmsProject project = new DmsProject();
			project.setProjectId(trialDataSet.getId());
			studyDataManager.saveTrialDatasetSummary(project,variableTypeListSummaryStats, summaryStatsExperimentValuesList, locationIds);
			
		}catch(Exception e){
			throw new BreedingViewImportException(e.getMessage(), e);
		}
		
		
	}

	@Override
	public void importSummaryStatsData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		importSummaryStatsData(file, studyId);
	}
	
	@Override
	public void importOutlierData(File file, int studyId) throws BreedingViewImportException {
		
		try {
			
			Map<String, String> nameToAliasMap = generateNameToAliasMap(studyId);
			OutlierCSV outlierCSV = new OutlierCSV(file, nameToAliasMap);
			Map<String, Map<String, ArrayList<String>>> outlierData = outlierCSV.getData();
			Map<String, Integer> ndGeolocationIds = new HashMap<String, Integer>();

			Map<Integer, Integer> stdVariableIds = new HashMap<Integer, Integer>();
			VariableTypeList plotVariableTypeList = getPlotDataSet(studyId).getVariableTypes();
			
			Integer i = 0;
			for (String l : outlierCSV.getHeaderTraits()){
				Integer traitId = plotVariableTypeList.findByLocalName(l).getId();
				stdVariableIds.put(i, traitId);
				i++;
			}
			
			TrialEnvironments trialEnvironments = studyDataManager.getTrialEnvironmentsInDataset(getPlotDataSet(studyId).getId());
			for (TrialEnvironment trialEnv : trialEnvironments.getTrialEnvironments()){
				ndGeolocationIds.put(trialEnv.getVariables()
						.findByLocalName(outlierCSV.getTrialHeader()).getValue(), trialEnv.getId());
			}

			Set<String> environments = outlierData.keySet();
			for(String env : environments) {

				List<PhenotypeOutlier> outliers = new ArrayList<PhenotypeOutlier>();
				Integer ndGeolocationId = ndGeolocationIds.get(env);

				for (Entry<String, ArrayList<String>> plot : outlierData.get(env).entrySet()){

					List<Integer> cvTermIds = new ArrayList<Integer>();
					Integer plotNo = Integer.valueOf(plot.getKey());
					Map<Integer, String> plotMap = new HashMap<Integer, String>();

					for (int x = 0; x < plot.getValue().size(); x++){
						String traitValue = plot.getValue().get(x);
						if (traitValue.isEmpty()){
							cvTermIds.add(stdVariableIds.get(x));
							plotMap.put(stdVariableIds.get(x), traitValue);
						}

					}

					List<Object[]> list = studyDataManager.getPhenotypeIdsByLocationAndPlotNo(getPlotDataSet(studyId).getId(), ndGeolocationId, plotNo, cvTermIds);
					for (Object[] object : list){
						PhenotypeOutlier outlier = new PhenotypeOutlier();
						outlier.setPhenotypeId(Integer.valueOf(object[2].toString()));
						outlier.setValue(plotMap.get(Integer.valueOf(object[1].toString())));
						outliers.add(outlier);
					}

				}

				studyDataManager.saveOrUpdatePhenotypeOutliers(outliers);
			}
		}catch(Exception e){
			throw new BreedingViewImportException(e.getMessage(), e);
		}
	
	}
	
	@Override
	public void importOutlierData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException {
		this.localNameToAliasMap = localNameToAliasMap;
		importOutlierData(file, studyId);
	}
	
	protected DataSet getPlotDataSet(int studyId) throws MiddlewareQueryException {
		if (plotDataSet != null && plotDataSet.getId() == studyId){
			return plotDataSet;
		}else{
			plotDataSet = studyDataManager.getDataSet(DatasetUtil.getPlotDataSetId(studyDataManager, studyId));
		}
		return plotDataSet;
	}
	
	protected DataSet getTrialDataSet(int studyId) throws MiddlewareQueryException {
		return DatasetUtil.getTrialDataSet(studyDataManager, studyId);
	}
	
	protected DataSet appendVariableTypesToExistingMeans(
			String[] csvHeader,DataSet inputDataSet,DataSet meansDataSet) 
					throws MiddlewareQueryException{

		List<Integer> numericTypes = new ArrayList<Integer>();
		numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
		numericTypes.add(TermId.MIN_VALUE.getId());
		numericTypes.add(TermId.MAX_VALUE.getId());
		numericTypes.add(TermId.DATE_VARIABLE.getId());
		numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());
		
		List<Integer> standardVariableIdTracker = new ArrayList<Integer>();

		int rank = meansDataSet.getVariableTypes().getVariableTypes()
				.get(meansDataSet.getVariableTypes().getVariableTypes().size()-1).getRank()+1;

		List<String> inputDataSetVariateNames = new ArrayList<String>( 
				Arrays.asList(Arrays.copyOfRange(csvHeader, 3, csvHeader.length)));
		List<String> meansDataSetVariateNames = new ArrayList<String>();

		Iterator<String> iterator = inputDataSetVariateNames.iterator();
		while (iterator.hasNext()){
			if (iterator.next().contains(UNIT_ERRORS_SUFFIX)) {
				iterator.remove();
			}
		}

		for (VariableType var : meansDataSet.getVariableTypes().getVariates().getVariableTypes()){
			standardVariableIdTracker.add(var.getStandardVariable().getId());
			if (!var.getStandardVariable().getMethod().getName().equalsIgnoreCase(ERROR_ESTIMATE)){
				meansDataSetVariateNames.add(var.getLocalName().trim());
			}
				
		}

		if (meansDataSetVariateNames.size() < inputDataSetVariateNames.size()){

			inputDataSetVariateNames.removeAll(meansDataSetVariateNames);

			for (String variateName : inputDataSetVariateNames){
				String root = variateName.substring(0, variateName.lastIndexOf("_"));
				if(!"".equals(root)) {

					VariableType meansVariableType = cloner.deepClone(
							inputDataSet.getVariableTypes().findByLocalName(root));
					meansVariableType.setLocalName(root + MEANS_SUFFIX);
					Term termLSMean = ontologyDataManager.findMethodByName(LS_MEAN);
					if(termLSMean == null) {
						String definitionMeans = meansVariableType.getStandardVariable()
								.getMethod().getDefinition();
						termLSMean = ontologyDataManager.addMethod(LS_MEAN, definitionMeans);
					}

					Integer stdVariableId = ontologyDataManager
							.getStandardVariableIdByPropertyScaleMethodRole(
									meansVariableType.getStandardVariable().getProperty().getId()
									,meansVariableType.getStandardVariable().getScale().getId()
									,termLSMean.getId()
									,PhenotypicType.VARIATE
									);
					
					//check if the stdVariableId already exists in the standardVariableIdTracker
					for (Integer vt : standardVariableIdTracker){
						if (stdVariableId != null && vt.intValue() == stdVariableId.intValue()){
							
							termLSMean = ontologyDataManager.findMethodByName("LS MEAN (" + root + ")");
							
							if(termLSMean == null) {
								String definitionMeans = 
										meansVariableType.getStandardVariable().getMethod().getDefinition();
								termLSMean = ontologyDataManager.addMethod("LS MEAN (" + root + ")" , definitionMeans);
							}

							stdVariableId = 
									ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(
											meansVariableType.getStandardVariable().getProperty().getId()
											,meansVariableType.getStandardVariable().getScale().getId()
											,termLSMean.getId()
											,PhenotypicType.VARIATE
											);
							break;
						}
					}

					if (stdVariableId == null){
						StandardVariable stdVariable = new StandardVariable();
						stdVariable = cloner.deepClone(meansVariableType.getStandardVariable());
						stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(),"",""));
						stdVariable.setEnumerations(null);
						stdVariable.setConstraints(null);
						stdVariable.setId(0);
						stdVariable.setName(meansVariableType.getLocalName());
						stdVariable.setMethod(termLSMean);
						//check if name is already used
						Term existingStdVar = ontologyDataManager
								.findTermByName(stdVariable.getName(), CvId.VARIABLES);
						if (existingStdVar != null){
							//rename 
							stdVariable.setName(stdVariable.getName()+"_1");
						}
						ontologyDataManager.addStandardVariable(stdVariable);
						meansVariableType.setStandardVariable(stdVariable);
						standardVariableIdTracker.add(stdVariable.getId());
					}else{
						StandardVariable stdVar = ontologyDataManager
								.getStandardVariable(stdVariableId);
									if (stdVar.getEnumerations() != null){
										for (Enumeration enumeration : stdVar.getEnumerations()){
											ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
										}
									}
								stdVar.setEnumerations(null);
								ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
						meansVariableType.setStandardVariable(stdVar);
						standardVariableIdTracker.add(stdVariableId);
					}


					meansVariableType.setRank(rank);
					try{ 
						studyDataManager.addDataSetVariableType(meansDataSet.getId(), meansVariableType); 
						rank++;
					} catch(MiddlewareQueryException e ) {  
						LOG.info("INFO: ",e);
					}

					stdVariableId = null;
					//Unit Errors
					VariableType unitErrorsVariableType = cloner.deepClone(
							inputDataSet.getVariableTypes().findByLocalName(root));
					unitErrorsVariableType.setLocalName(root + UNIT_ERRORS_SUFFIX);
					Term termErrorEstimate = ontologyDataManager
							.findMethodByName("ERROR ESTIMATE");
					if(termErrorEstimate == null) {
						String definitionUErrors = unitErrorsVariableType
								.getStandardVariable().getMethod().getDefinition();
						termErrorEstimate = ontologyDataManager
								.addMethod("ERROR ESTIMATE", definitionUErrors);
					}

					stdVariableId = ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(
							unitErrorsVariableType.getStandardVariable().getProperty().getId()
							,unitErrorsVariableType.getStandardVariable().getScale().getId()
							,termErrorEstimate.getId()
							,PhenotypicType.VARIATE
							);
					
					//check if the stdVariableId already exists in the variableTypeList
					for (Integer vt : standardVariableIdTracker){
						if (stdVariableId != null && vt.intValue() == stdVariableId.intValue()){
							
							termErrorEstimate = ontologyDataManager.findMethodByName("ERROR ESTIMATE (" + root + ")");
							if(termErrorEstimate == null) {
								String definitionUErrors = 
										unitErrorsVariableType.getStandardVariable().getMethod().getDefinition();
								termErrorEstimate = ontologyDataManager
										.addMethod("ERROR ESTIMATE (" + root + ")", definitionUErrors);
							}

							stdVariableId = ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(
									unitErrorsVariableType.getStandardVariable().getProperty().getId()
									,unitErrorsVariableType.getStandardVariable().getScale().getId()
									,termErrorEstimate.getId()
									,PhenotypicType.VARIATE
									);
							break;
						}
					}
					
					

					if (stdVariableId == null){
						StandardVariable stdVariable = new StandardVariable();
						stdVariable = cloner.deepClone(unitErrorsVariableType.getStandardVariable());
						stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(),"",""));
						stdVariable.setEnumerations(null);
						stdVariable.setConstraints(null);
						stdVariable.setId(0);
						stdVariable.setName(unitErrorsVariableType.getLocalName());
						stdVariable.setMethod(termErrorEstimate);
						//check if name is already used
						Term existingStdVar = ontologyDataManager
								.findTermByName(stdVariable.getName(), CvId.VARIABLES);
						if (existingStdVar != null){
							//rename 
							stdVariable.setName(stdVariable.getName()+"_1");
						}
						ontologyDataManager.addStandardVariable(stdVariable);
						unitErrorsVariableType.setStandardVariable(stdVariable);
						standardVariableIdTracker.add(stdVariable.getId());
					}else{
						StandardVariable stdVar = ontologyDataManager
								.getStandardVariable(stdVariableId);
									if (stdVar.getEnumerations() != null){
										for (Enumeration enumeration : stdVar.getEnumerations()){
											ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
										}
									}
								stdVar.setEnumerations(null);
								ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
						unitErrorsVariableType.setStandardVariable(stdVar);
						standardVariableIdTracker.add(stdVariableId);
					}


					unitErrorsVariableType.setRank(rank);
					try {
						studyDataManager.addDataSetVariableType(
								meansDataSet.getId(), unitErrorsVariableType);
						rank++;
					} catch (MiddlewareQueryException e) {
						LOG.info("INFO: ", e);
					}                     
				}

			}

			return studyDataManager.getDataSet(meansDataSet.getId());
		}

		return meansDataSet;


	}
	
	protected void createMeansVariableType(Integer numOfFactorsAndVariates, String headerName, VariableTypeList allVariatesList,VariableTypeList meansVariateList) throws MiddlewareQueryException {
		
		String traitName = "", localName = "", methodName = "";
		
		traitName = (headerName != null && headerName.lastIndexOf("_") != -1)
				? headerName.substring(0, headerName.lastIndexOf("_")) : "";
		if (headerName.endsWith(MEANS_SUFFIX)){
			localName = MEANS_SUFFIX;
			methodName = LS_MEAN;
		} else if (headerName.endsWith(UNIT_ERRORS_SUFFIX)) {
			localName = UNIT_ERRORS_SUFFIX;
			methodName = "ERROR ESTIMATE";
		} else {
			return;
		}
		
		VariableType originalVariableType = null;
		VariableType newVariableType = null;
		
		originalVariableType = allVariatesList.findByLocalName(traitName);
		newVariableType = cloner.deepClone(originalVariableType);
		
		newVariableType.setLocalName(traitName + localName);
		
		Term termMethod = ontologyDataManager.findMethodByName(methodName);
		if(termMethod == null) {
			String definitionMeans = 
					newVariableType.getStandardVariable().getMethod().getDefinition();
			termMethod = ontologyDataManager.addMethod(methodName, definitionMeans);
		}

		Integer stdVariableId = 
				ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(
						newVariableType.getStandardVariable().getProperty().getId()
						,newVariableType.getStandardVariable().getScale().getId()
						,termMethod.getId()
						,PhenotypicType.VARIATE
						);
		
		//check if the stdVariableId already exists in the variableTypeList
		for (VariableType vt : meansVariateList.getVariableTypes()){
			if (stdVariableId != null && vt.getStandardVariable().getId() == stdVariableId.intValue()){
				
				termMethod = ontologyDataManager.findMethodByName(methodName+ " (" + traitName + ")");
				
				if(termMethod == null) {
					String definitionMeans = 
							newVariableType.getStandardVariable().getMethod().getDefinition();
					termMethod = ontologyDataManager.addMethod(methodName + " (" + traitName + ")" , definitionMeans);
				}

				stdVariableId = 
						ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(
								newVariableType.getStandardVariable().getProperty().getId()
								,newVariableType.getStandardVariable().getScale().getId()
								,termMethod.getId()
								,PhenotypicType.VARIATE
								);
				break;
			}
		}
		

		if (stdVariableId == null){
			StandardVariable stdVariable = new StandardVariable();
			stdVariable = cloner.deepClone(newVariableType.getStandardVariable());
			stdVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(),"",""));
			stdVariable.setEnumerations(null);
			stdVariable.setConstraints(null);
			stdVariable.setId(0);
			stdVariable.setName(newVariableType.getLocalName());
			stdVariable.setMethod(termMethod);
			//check if name is already used
			Term existingStdVar = ontologyDataManager
					.findTermByName(stdVariable.getName(), CvId.VARIABLES);
			if (existingStdVar != null){
				//rename 
				stdVariable.setName(stdVariable.getName()+"_1");
			}
			ontologyDataManager.addStandardVariable(stdVariable);
			newVariableType.setStandardVariable(stdVariable);

		}else{
			StandardVariable stdVar = ontologyDataManager
					.getStandardVariable(stdVariableId);
						if (stdVar.getEnumerations() != null){
							for (Enumeration enumeration : stdVar.getEnumerations()){
								ontologyDataManager.deleteStandardVariableEnumeration(stdVariableId, enumeration.getId());
							}
						}
					stdVar.setEnumerations(null);
					ontologyDataManager.deleteStandardVariableLocalConstraints(stdVariableId);
			newVariableType.setStandardVariable(stdVar);
		}

		meansVariateList.makeRoom(numOfFactorsAndVariates);
		newVariableType.setRank(numOfFactorsAndVariates);
		meansVariateList.add(newVariableType);
		
	}

	protected Variable createVariable(int termId, String value, int rank) throws MiddlewareQueryException {
		StandardVariable stVar = ontologyDataManager.getStandardVariable(termId);

		VariableType vtype = new VariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	protected void updateVariableType(VariableType type, String name, String description) {
		type.setLocalName(name);
		type.setLocalDescription(description);
	}

	protected VariableTypeList getMeansVariableTypeList() {
		return new VariableTypeList();
	}
	
	protected Map<String, String> generateNameToAliasMap(int studyId) throws MiddlewareQueryException{
		
		if (this.localNameToAliasMap != null){
			return this.localNameToAliasMap;
		}else{
			List<VariableType> variateList = getPlotDataSet(studyId).getVariableTypes().getVariableTypes();
			
			Map<String, String> nameAliasMap = new HashMap<>();
			
			for (Iterator<VariableType> i = variateList.iterator(); i.hasNext();) {
				VariableType k = i.next();
				String nameSanitized = k.getLocalName().replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				nameAliasMap.put(nameSanitized, k.getLocalName());	
			}
			
			return nameAliasMap;
		}
	}
	
	public class MeansCSV {

		private Map<String, String> nameToAliasMapping;
		private File file;

		public MeansCSV(File file, Map<String, String> nameToAliasMapping) {
			this.file = file;
			this.nameToAliasMapping = nameToAliasMapping;
		}

		public Map<String, ArrayList<String>> csvToMap() throws Exception {

			CSVReader reader = new CSVReader(new FileReader(file));
			Map<String, ArrayList<String>> csvMap = new LinkedHashMap<String, ArrayList<String>>();
			String[] header = reader.readNext();
			
			for(String headerCol : header) {
				String aliasLocalName = headerCol.trim().replace(MEANS_SUFFIX, "").replace(UNIT_ERRORS_SUFFIX, "");
				String actualLocalName = null;
				
				actualLocalName = nameToAliasMapping.get(aliasLocalName);
					if (actualLocalName == null) {
						actualLocalName = aliasLocalName;
					}
				csvMap.put(headerCol.trim().replace(aliasLocalName, actualLocalName), new ArrayList<String>());
				
			}
			String[] trimHeader = csvMap.keySet().toArray(new String[0]);
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					for(int i = 0; i < header.length; i++) {
						csvMap.get(trimHeader[i]).add(nextLine[i].trim());
					}
				}
			}
			
			reader.close();
			
			return csvMap;
		}
		
		public void validate() throws BreedingViewInvalidFormatException {
			
			int meansCounter=0;
			int unitErrorsCounter=0;
			
			CSVReader reader;
			String[] header = new String[]{};
			
			try{
				reader = new CSVReader(new FileReader(file));
				header = reader.readNext();
				reader.close();
			}catch(Exception e){
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the MEANS data file", e);
			}
			 
			for (String s : header){
				if (s.contains(MEANS_SUFFIX)){
					meansCounter++;
				}
				if (s.contains(UNIT_ERRORS_SUFFIX)){
					unitErrorsCounter++;
				}
			}
			
			if (meansCounter != unitErrorsCounter || (meansCounter == 0 && unitErrorsCounter == 0)){
				throw new BreedingViewInvalidFormatException("Cannot parse the file because the format is invalid for MEANS data.");
			}
		}
	}
	
	public class OutlierCSV {
		
	    private File file;
	    private Map<String, Map<String, ArrayList<String> >> data;
	    private Map<String, String> nameToAliasMapping;
	    private String[] header;
	    
	    
	    public OutlierCSV(File file,
				Map<String, String> nameToAliasMapping) {
	    	this.file = file;
	    	this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException {
	    	
	    	data = getData();
	    	
	    	return Arrays.asList(header);
	    }
	    
	    public List<String> getHeaderTraits() throws IOException {
	    	
	    	data = getData();
	    	
	    	List<String> list = new ArrayList<String>(Arrays.asList(header));
	    	list.remove(0);
	    	list.remove(0);
	    	return list;
	    }
	    
	    public String getTrialHeader() throws IOException {
	    	
	    	return getHeader().get(0);
	    }
	    
	    public Map<String, Map<String, ArrayList<String> >> getData() throws IOException {
	    	
	    	if (data != null) {
	    		return data;
	    	}
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	        data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();
	        
	        List<String> list = new ArrayList<String>();
	        for (String aliasLocalName : reader.readNext()){
	        	String actualLocalName = null;
	        	actualLocalName = nameToAliasMapping.get(aliasLocalName);
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
	            

	        	if(!data.containsKey(environment)) {
	        		data.put(environment, new LinkedHashMap<String, ArrayList<String>>());
	        	}
	        	
	        	if(!data.get(environment).containsKey(trait)){
	    			data.get(environment).put(trait,  new ArrayList<String>());
	    		}
	        	 for(int i = 2; i < header.length; i++) {
	        		 data.get(environment).get(trait).add(nextLine[i].trim());
	             }
	            
	        }
	        
	        reader.close();
	        return data;
	    }
	    
	    public void validate() throws BreedingViewInvalidFormatException {
			
			CSVReader reader;
			String[] header = new String[]{};
			
			try{
				reader = new CSVReader(new FileReader(file));
				header = reader.readNext();
				reader.close();
			}catch(Exception e){
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Outlier data file", e);
			}
		}
	    
	}
	
	public class SummaryStatsCSV {

	    private File file;
	    private Map<String, Map<String, ArrayList<String> >> data;
	    private Map<String, String> nameToAliasMapping;
	    private String[] header;
	    
	    public SummaryStatsCSV(File file,
				Map<String, String> nameToAliasMapping) {
	    	this.file = file;
	    	this.nameToAliasMapping = nameToAliasMapping;
		}

		public List<String> getHeader() throws IOException{
	    	
	    	data = getData();
	    	
	    	return Arrays.asList(header);
	    }
	    
	    public List<String> getHeaderStats() throws IOException{
	    	
	    	data = getData();
	    	
	    	List<String> list = new ArrayList<String>(Arrays.asList(header));
	    	list.remove(0);
	    	list.remove(0);
	    	return list;
	    }
	    
	    public String getTrialHeader() throws IOException{
	    	
	    	return nameToAliasMapping.get(getHeader().get(0));
	    }
	    
	    public Map<String, Map<String, ArrayList<String> >> getData() throws IOException {
	    	
	    	if (data != null) {
	    		return data;
	    	}
	    	
	    	CSVReader reader = new CSVReader(new FileReader(file));
	        data = new LinkedHashMap<String, Map<String, ArrayList<String>>>();
	        this.header = reader.readNext();
	        String[] nextLine;
	        while ((nextLine = reader.readNext()) != null) {
	            
	        	String environment = nextLine[0].trim();
	            String trait = null;
				
				trait = nameToAliasMapping.get(nextLine[1]).trim();
				if (trait == null) {
					trait = nextLine[1].trim();
				}
				
	        	if(!data.containsKey(environment)) {
	        		data.put(environment, new LinkedHashMap<String, ArrayList<String>>());
	        	}
	        	
	        	if(!data.get(environment).containsKey(trait)){
	    			data.get(environment).put(trait,  new ArrayList<String>());
	    		}
	        	 for(int i = 2; i < header.length; i++) {
	        		 data.get(environment).get(trait).add(nextLine[i].trim());
	             }
	            
	        }
	        
	        reader.close();
	        return data;
	    }
	    
	    public void validate() throws BreedingViewInvalidFormatException {
			
			CSVReader reader;
			String[] header = new String[]{};
			
			try{
				reader = new CSVReader(new FileReader(file));
				header = reader.readNext();
				reader.close();
			}catch(Exception e){
				throw new BreedingViewInvalidFormatException("A problem occurred while reading the Summary Statistics data file", e);
			}
			 	
			List<String> headerList = Arrays.asList(header);
			
			if (!headerList.containsAll(Arrays.asList("Trait,NumValues,NumMissing,Mean,Variance,SD,Min,Max,Range,Median,LowerQuartile,UpperQuartile,MeanRep,MinRep,MaxRep,MeanSED,MinSED,MaxSED,MeanLSD,MinLSD,MaxLSD,CV,Heritability,WaldStatistic,WaldDF,Pvalue".split(",")))){
				throw new BreedingViewInvalidFormatException("Cannot parse the file because the format is invalid for Summary Statistics.");
			}
		}
	    
	}

	protected void setCloner(Cloner cloner) {
		this.cloner = cloner;
	}
	
}
