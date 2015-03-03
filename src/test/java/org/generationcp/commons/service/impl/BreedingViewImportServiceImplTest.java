package org.generationcp.commons.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.LocationDto;
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
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rits.cloning.Cloner;

public class BreedingViewImportServiceImplTest {
	
	private int STUDY_ID = 1;
	private String STUDY_NAME = "TEST STUDY";
	
	private int MEASUREMENT_DATASET_ID = 2;
	private int EXISTING_MEANS_DATASET_ID = 3;
	private int NEW_MEANS_DATASET_ID = 4;
	private int TRIAL_DATASET_ID = 1;
	private static final int DATASET_TITLE_STANDARD_VAR_ID = 8155;
	private static final int DATASET_STANDARD_VAR_ID = 8160;
	private static final int STUDY_STANDARD_VAR_ID = 8150;
	
	private String EMPTY_VALUE = "";
	
	private String LS_MEAN = "LS MEAN";
	private int LS_MEAN_ID = 97393;
	
	List<VariableType> factorVariableTypes = new ArrayList<VariableType>();
	List<VariableType> variateVariableTypes = new ArrayList<VariableType>();
	List<VariableType> meansVariateVariableTypes = new ArrayList<VariableType>();
	
	Map<String, String> localNameToAliasMapping;
	
	@Mock StudyDataManager studyDataManager;
	@Mock OntologyDataManager ontologyDataManager;
	@Mock WorkbenchDataManager workbenchDataManager;
	@Mock Map<String, String> params;
	
	@Mock TrialEnvironments trialEnvironments;
	@Mock TrialEnvironment trialEnvironment;
	
	@Mock Stocks stocks;
	
	@Mock
	private Study study;
	
	@Mock
	private DataSet newMeansDataSet;

	Map<String,ArrayList<String>> meansInput;
	Map<String,Map<String,ArrayList<String>>> summaryStatisticsInput;
	
	@InjectMocks
	BreedingViewImportServiceImpl service = spy(new BreedingViewImportServiceImpl());
		
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		
		doReturn(STUDY_ID).when(study).getId();
		doReturn(STUDY_NAME).when(study).getName();
		
		localNameToAliasMapping = new HashMap<>();
		localNameToAliasMapping.put("TRIAL_INSTANCE", "TRIAL_INSTANCE");
		localNameToAliasMapping.put("ENTRY_NO", "ENTRY_NO");
		localNameToAliasMapping.put("GID", "GID");
		localNameToAliasMapping.put("ASI", "ASI");
		localNameToAliasMapping.put("Aphid1_5", "Aphid1_5");
		localNameToAliasMapping.put("EPH", "EPH");
		localNameToAliasMapping.put("FMSROT", "FMSROT");
		
		factorVariableTypes.add(createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		factorVariableTypes.add(createGermplasmFactorVariableType("ENTRY_NO"));
		factorVariableTypes.add(createGermplasmFactorVariableType("GID"));
		
		variateVariableTypes.add(createVariateVariableType("ASI"));
		variateVariableTypes.add(createVariateVariableType("Aphid1_5"));
		variateVariableTypes.add(createVariateVariableType("EPH"));
		variateVariableTypes.add(createVariateVariableType("FMSROT"));	
		
		meansVariateVariableTypes.add(createVariateVariableType("ASI_Means","", "","LS MEAN"));
		meansVariateVariableTypes.add(createVariateVariableType("ASI_UnitErrors","", "","ERROR ESTIMATE"));
		meansVariateVariableTypes.add(createVariateVariableType("Aphid1_5_Means","", "","LS MEAN"));
		meansVariateVariableTypes.add(createVariateVariableType("Aphid1_5_UnitErrors","", "","ERROR ESTIMATE"));
		meansVariateVariableTypes.add(createVariateVariableType("EPH_Means","", "","LS MEAN"));
		meansVariateVariableTypes.add(createVariateVariableType("EPH_UnitErrors","", "","ERROR ESTIMATE"));
		meansVariateVariableTypes.add(createVariateVariableType("FMSROT_Means","", "","LS MEAN"));
		meansVariateVariableTypes.add(createVariateVariableType("FMSROT_UnitErrors","", "","ERROR ESTIMATE"));
		
		doReturn(createMeasurementDataSet()).when(service).getPlotDataSet(STUDY_ID);
		doReturn(localNameToAliasMapping).when(service).generateNameToAliasMap(STUDY_ID);
		
		service.setCloner(new Cloner());
	}
	
	@Test
	public void testImportMeansData() throws Exception {
		
		StandardVariable studyStdVar = new StandardVariable();
		studyStdVar.setId(STUDY_STANDARD_VAR_ID);
		studyStdVar.setPhenotypicType(PhenotypicType.STUDY);
		
		StandardVariable dataSetStdVar = new StandardVariable();
		dataSetStdVar.setId(DATASET_STANDARD_VAR_ID);
		dataSetStdVar.setPhenotypicType(PhenotypicType.DATASET);
		
		StandardVariable titleStdVar = new StandardVariable();
		titleStdVar.setId(DATASET_TITLE_STANDARD_VAR_ID);
		
		when(studyDataManager.getDataSetsByType(anyInt(), (DataSetType)anyObject())).thenReturn(new ArrayList<DataSet>());
		when(studyDataManager.getTrialEnvironmentsInDataset(anyInt())).thenReturn(trialEnvironments);
		when(studyDataManager.getDataSet(MEASUREMENT_DATASET_ID)).thenReturn(createMeasurementDataSet());
		when(studyDataManager.getDataSet(EXISTING_MEANS_DATASET_ID)).thenReturn(null);
		when(studyDataManager.getDataSet(NEW_MEANS_DATASET_ID)).thenReturn(createNewMeansDataSet());
		when(studyDataManager.getStudy(anyInt())).thenReturn(study);
		when(studyDataManager.addDataSet(anyInt(), (VariableTypeList) anyObject(), (DatasetValues) anyObject(), anyString()))
			.thenReturn(new DatasetReference(NEW_MEANS_DATASET_ID, EMPTY_VALUE));
		when(studyDataManager.getStocksInDataset(anyInt())).thenReturn(stocks);
		
		when(ontologyDataManager.addMethod(anyString(), anyString())).thenReturn(createTerm(LS_MEAN_ID,LS_MEAN));
		when(ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(anyInt(),anyInt(),anyInt(),(PhenotypicType) anyObject())).thenReturn(null);
		when(ontologyDataManager.getStandardVariable(8150)).thenReturn(studyStdVar);
		when(ontologyDataManager.getStandardVariable(8155)).thenReturn(titleStdVar);
		when(ontologyDataManager.getStandardVariable(8160)).thenReturn(dataSetStdVar);
		
		when(stocks.findOnlyOneByLocalName(anyString(),anyString())).thenReturn(mock(Stock.class));
		when(stocks.findOnlyOneByLocalName(anyString(),anyString()).getId()).thenReturn(1);
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());
		
		service.importMeansData(file, STUDY_ID);
		
		verify(studyDataManager).addOrUpdateExperiment(anyInt(), any(ExperimentType.class), anyList());
		
	}
	
	@Test
	public void testImportMeansDataWithExistingMeansDataSet() throws Exception {
		
		
		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(createExistingMeansDataSet());
		when(studyDataManager.getDataSetsByType(anyInt(), (DataSetType)anyObject())).thenReturn(dataSets);
		when(studyDataManager.getTrialEnvironmentsInDataset(anyInt())).thenReturn(trialEnvironments);
		when(studyDataManager.getDataSet(MEASUREMENT_DATASET_ID)).thenReturn(createMeasurementDataSet());
		when(studyDataManager.getDataSet(EXISTING_MEANS_DATASET_ID)).thenReturn(createExistingMeansDataSet());
		when(studyDataManager.getStudy(anyInt())).thenReturn(study);
		when(studyDataManager.addDataSet(anyInt(), (VariableTypeList) anyObject(), (DatasetValues) anyObject(), anyString()))
			.thenReturn(new DatasetReference(NEW_MEANS_DATASET_ID, EMPTY_VALUE));
		when(studyDataManager.getStocksInDataset(anyInt())).thenReturn(stocks);
		
		when(ontologyDataManager.addMethod(anyString(), anyString())).thenReturn(createTerm(LS_MEAN_ID, LS_MEAN));
		when(ontologyDataManager.getStandardVariable(anyInt())).thenReturn(mock(StandardVariable.class));
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());
		
		service.importMeansData(file, STUDY_ID);
		
		verify(service).appendVariableTypesToExistingMeans((String[])anyObject(), any(DataSet.class), any(DataSet.class));
		verify(studyDataManager).addOrUpdateExperiment(anyInt(), any(ExperimentType.class), anyList());
		
	}
	
	@Test
	public void testImportMeansDataWithExistingMeansDataAndAdditionalTraits() throws Exception {
		
		variateVariableTypes.add(createVariateVariableType("EXTRAIT"));
		DataSet measurementDataSet = createMeasurementDataSet();
		
		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(createExistingMeansDataSet());
		when(studyDataManager.getDataSetsByType(anyInt(), (DataSetType)anyObject())).thenReturn(dataSets);
		when(studyDataManager.getTrialEnvironmentsInDataset(anyInt())).thenReturn(trialEnvironments);
		when(service.getPlotDataSet(STUDY_ID)).thenReturn(measurementDataSet);
		when(studyDataManager.getDataSet(EXISTING_MEANS_DATASET_ID)).thenReturn(createExistingMeansDataSet());
		when(studyDataManager.getStudy(anyInt())).thenReturn(study);
		when(studyDataManager.addDataSet(anyInt(), (VariableTypeList) anyObject(), (DatasetValues) anyObject(), anyString()))
			.thenReturn(new DatasetReference(NEW_MEANS_DATASET_ID, EMPTY_VALUE));
		when(studyDataManager.getStocksInDataset(anyInt())).thenReturn(stocks);
		
		when(ontologyDataManager.addMethod(anyString(), anyString())).thenReturn(createTerm(LS_MEAN_ID, LS_MEAN));
		when(ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(anyInt(),anyInt(),anyInt(),(PhenotypicType) anyObject())).thenReturn(null);
		when(ontologyDataManager.getStandardVariable(anyInt())).thenReturn(mock(StandardVariable.class));
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithAdditionalTraits.csv").toURI());
		
		service.importMeansData(file, STUDY_ID);
		
		verify(service).appendVariableTypesToExistingMeans((String[])anyObject(), any(DataSet.class), any(DataSet.class));
		verify(studyDataManager).addOrUpdateExperiment(anyInt(), any(ExperimentType.class), anyList());
		
	}
	
	@Test
	public void testImportOutlierData() throws Exception {
		
		List<Object[]> phenotypeIds = new ArrayList<>();
		phenotypeIds.add(new Object[]{"76373","9999","1"});

		when(studyDataManager.getTrialEnvironmentsInDataset(anyInt())).thenReturn(createTrialEnvironments());
		when(studyDataManager.getPhenotypeIdsByLocationAndPlotNo(anyInt(), anyInt(), anyInt(), anyList())).thenReturn(phenotypeIds);
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		service.importOutlierData(file, STUDY_ID);
		
		verify(studyDataManager).saveOrUpdatePhenotypeOutliers(anyList());
	}
	
	@Test
	public void testImportSummaryStatsData() throws Exception {
		
		doReturn(createTrialDataSet()).when(service).getTrialDataSet(TRIAL_DATASET_ID);
		
		when(studyDataManager.getTrialEnvironmentsInDataset(anyInt())).thenReturn(createTrialEnvironments());
		
		when(ontologyDataManager.findMethodByName(anyString())).thenReturn(createTerm(888, "DUMMYTERM"));
		when(ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(anyInt(),anyInt(),anyInt(),(PhenotypicType) anyObject())).thenReturn(null);
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		service.importSummaryStatsData(file, STUDY_ID);
		
		verify(studyDataManager).saveTrialDatasetSummary(any(DmsProject.class), any(VariableTypeList.class), anyList(), anyList());
		
	}
	

	private Term createTerm(int id, String name){
		return new Term(id, name, "");
	}
	
	private DataSet createTrialDataSet(){
		
		DataSet dataSet = new DataSet();
		dataSet.setId(TRIAL_DATASET_ID);
		
		VariableTypeList variableTypes = new VariableTypeList();
		
		dataSet.setVariableTypes(variableTypes);
		for(VariableType factor : factorVariableTypes){
			dataSet.getVariableTypes().add(factor);
		}
		for(VariableType variate : variateVariableTypes){
			dataSet.getVariableTypes().add(variate);
		}
		
		return dataSet;
	}
	
	private DataSet createMeasurementDataSet(){
		
		DataSet dataSet = new DataSet();
		dataSet.setId(MEASUREMENT_DATASET_ID);
		
		VariableTypeList variableTypes = new VariableTypeList();
		
		dataSet.setVariableTypes(variableTypes);
		for(VariableType factor : factorVariableTypes){
			dataSet.getVariableTypes().add(factor);
		}
		for(VariableType variate : variateVariableTypes){
			dataSet.getVariableTypes().add(variate);
		}
		
		return dataSet;
	}
	
	private DataSet createNewMeansDataSet(){
		
		DataSet dataSet = new DataSet();
		dataSet.setId(NEW_MEANS_DATASET_ID);
		
		VariableTypeList variableTypes = new VariableTypeList();
		
		dataSet.setVariableTypes(variableTypes);
		for(VariableType factor : factorVariableTypes){
			dataSet.getVariableTypes().add(factor);
		}
		for(VariableType variate : meansVariateVariableTypes){
			dataSet.getVariableTypes().add(variate);
		}
		
		return dataSet;
	}
	
	private DataSet createExistingMeansDataSet(){
		
		DataSet dataSet = new DataSet();
		dataSet.setId(EXISTING_MEANS_DATASET_ID);
		
		VariableTypeList variableTypes = new VariableTypeList();
		
		dataSet.setVariableTypes(variableTypes);
		for(VariableType factor : factorVariableTypes){
			dataSet.getVariableTypes().add(factor);
		}
		for(VariableType variate : meansVariateVariableTypes){
			dataSet.getVariableTypes().add(variate);
		}
		
		return dataSet;
	}
	
	private VariableType createVariateVariableType(String localName){
		VariableType variate = new VariableType();
		StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);
		
		Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());
		
		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());
		
		Term method = new Term();
		method.setId(1111);
		method.setDefinition(EMPTY_VALUE);
		
		Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);
		
		Term property = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(33333);

		variateStandardVar.setId(1234);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setStoredIn(storedIn);
		variateStandardVar.setDataType(dataType);
		variateStandardVar.setName(localName);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);
		
		return variate;
	}
	
	private VariableType createVariateVariableType(String localName, String propertyName, String scaleName, String methodName){
		VariableType variate = new VariableType();
		StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);
		
		Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());
		
		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());
		
		Term method = new Term();
		method.setId(1111);
		method.setDefinition(EMPTY_VALUE);
		method.setName(methodName);
		
		Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);
		
		Term property = new Term();
		property.setDefinition(EMPTY_VALUE);
		property.setId(33333);
		property.setName(propertyName);
		
		variateStandardVar.setId(1234);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setStoredIn(storedIn);
		variateStandardVar.setDataType(dataType);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);
		
		return variate;
	}
	
	private VariableType createTrialEnvironmentVariableType(String localName){
		
		VariableType factor = new VariableType();
		StandardVariable factorStandardVar = new StandardVariable();
		Term storedInLoc = new Term();
		storedInLoc.setId(TermId.LOCATION_ID.getId());
		factorStandardVar.setStoredIn(storedInLoc);
		factorStandardVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		factorStandardVar.setName(localName);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		
		return factor;
	}
	
	private VariableType createGermplasmFactorVariableType(String localName){
		VariableType factor = new VariableType();
		StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.GERMPLASM);
		
		Term storedIn = new Term();
		storedIn.setId(TermId.GERMPLASM_ENTRY_STORAGE.getId());
		
		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_DBID_VARIABLE.getId());
		
		Term method = new Term();
		method.setId(1111);
		method.setDefinition(EMPTY_VALUE);
		
		Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);
		
		Term property = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(33333);
		
		factorStandardVar.setId(1234);
		factorStandardVar.setProperty(property);
		factorStandardVar.setScale(scale);
		factorStandardVar.setMethod(method);
		factorStandardVar.setStoredIn(storedIn);
		factorStandardVar.setDataType(dataType);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		
		return factor;
	}

	private TrialEnvironments createTrialEnvironments(){
		
		TrialEnvironments trialEnvs = new TrialEnvironments();
		trialEnvs.add(createTrialEnvironment());
		return trialEnvs;
		
	}
	
	private TrialEnvironment createTrialEnvironment(){
		
		LocationDto location = new LocationDto(1, "CIMMYT");
		VariableList variableList = new VariableList();
		
		addVariables(variableList);
		
		TrialEnvironment trialEnv = new TrialEnvironment(1, variableList);
		trialEnv.setLocation(location);
		
		return trialEnv;
		
		
	}
	
	private void addVariables(VariableList list){
		for (VariableType f : factorVariableTypes){
			list.add(createVariable(f));
		}
		for (VariableType v : variateVariableTypes){
			list.add(createVariable(v));
		}
	}
	
	private Variable createVariable(VariableType variableType){
		Variable v = new Variable();
		if (variableType.getLocalName().equals("TRIAL_INSTANCE")){
			v.setValue("1");
		}else{
			v.setValue("2222");
		}
		
		v.setVariableType(variableType);
		return v;
	}
	
}
