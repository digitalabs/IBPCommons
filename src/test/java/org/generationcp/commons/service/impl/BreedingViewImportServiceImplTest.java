
package org.generationcp.commons.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DMSVariableType;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.rits.cloning.Cloner;

public class BreedingViewImportServiceImplTest {

	private final int STUDY_ID = 1;
	private final String STUDY_NAME = "TEST STUDY";

	private final int MEASUREMENT_DATASET_ID = 2;
	private final int EXISTING_MEANS_DATASET_ID = 3;
	private final int NEW_MEANS_DATASET_ID = 4;
	private final int TRIAL_DATASET_ID = 1;
	private static final int DATASET_TITLE_STANDARD_VAR_ID = 8155;
	private static final int DATASET_STANDARD_VAR_ID = 8160;
	private static final int STUDY_STANDARD_VAR_ID = 8150;

	private static final String PROGRAM_UUID = "12345678";

	private final String EMPTY_VALUE = "";

	private final String LS_MEAN = "LS MEAN";
	private final int LS_MEAN_ID = 97393;

	List<DMSVariableType> factorVariableTypes = new ArrayList<DMSVariableType>();
	List<DMSVariableType> variateVariableTypes = new ArrayList<DMSVariableType>();
	List<DMSVariableType> meansVariateVariableTypes = new ArrayList<DMSVariableType>();

	Map<String, String> localNameToAliasMapping;

	@Mock
	StudyDataManager studyDataManager;
	@Mock
	OntologyDataManager ontologyDataManager;
	@Mock
	WorkbenchDataManager workbenchDataManager;
	@Mock
	Map<String, String> params;

	@Mock
	TrialEnvironments trialEnvironments;
	@Mock
	TrialEnvironment trialEnvironment;

	@Mock
	Stocks stocks;

	@Mock
	private Study study;

	@Mock
	private DmsProject dmsProject;

	@Mock
	private DataSet newMeansDataSet;

	Map<String, ArrayList<String>> meansInput;
	Map<String, Map<String, ArrayList<String>>> summaryStatisticsInput;

	@InjectMocks
	BreedingViewImportServiceImpl service = Mockito.spy(new BreedingViewImportServiceImpl());

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.doReturn(this.STUDY_ID).when(this.study).getId();
		Mockito.doReturn(this.STUDY_NAME).when(this.study).getName();

		Mockito.doReturn(PROGRAM_UUID).when(this.dmsProject).getProgramUUID();

		this.localNameToAliasMapping = new HashMap<>();
		this.localNameToAliasMapping.put("TRIAL_INSTANCE", "TRIAL_INSTANCE");
		this.localNameToAliasMapping.put("ENTRY_NO", "ENTRY_NO");
		this.localNameToAliasMapping.put("GID", "GID");
		this.localNameToAliasMapping.put("ASI", "ASI");
		this.localNameToAliasMapping.put("Aphid1_5", "Aphid1_5");
		this.localNameToAliasMapping.put("EPH", "EPH");
		this.localNameToAliasMapping.put("FMSROT", "FMSROT");

		this.factorVariableTypes.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("ENTRY_NO"));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("GID"));

		this.variateVariableTypes.add(this.createVariateVariableType("ASI"));
		this.variateVariableTypes.add(this.createVariateVariableType("Aphid1_5"));
		this.variateVariableTypes.add(this.createVariateVariableType("EPH"));
		this.variateVariableTypes.add(this.createVariateVariableType("FMSROT"));

		this.meansVariateVariableTypes.add(this.createVariateVariableType("ASI_Means", "", "", "LS MEAN"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("ASI_UnitErrors", "", "", "ERROR ESTIMATE"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("Aphid1_5_Means", "", "", "LS MEAN"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("Aphid1_5_UnitErrors", "", "", "ERROR ESTIMATE"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("EPH_Means", "", "", "LS MEAN"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("EPH_UnitErrors", "", "", "ERROR ESTIMATE"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("FMSROT_Means", "", "", "LS MEAN"));
		this.meansVariateVariableTypes.add(this.createVariateVariableType("FMSROT_UnitErrors", "", "", "ERROR ESTIMATE"));

		Mockito.doReturn(this.createMeasurementDataSet()).when(this.service).getPlotDataSet(this.STUDY_ID);
		Mockito.doReturn(this.localNameToAliasMapping).when(this.service).generateNameToAliasMap(this.STUDY_ID);

		this.service.setCloner(new Cloner());
	}

	@Test
	public void testImportMeansData() throws Exception {

		StandardVariable studyStdVar = new StandardVariable();
		studyStdVar.setId(BreedingViewImportServiceImplTest.STUDY_STANDARD_VAR_ID);
		studyStdVar.setPhenotypicType(PhenotypicType.STUDY);

		StandardVariable dataSetStdVar = new StandardVariable();
		dataSetStdVar.setId(BreedingViewImportServiceImplTest.DATASET_STANDARD_VAR_ID);
		dataSetStdVar.setPhenotypicType(PhenotypicType.DATASET);

		StandardVariable titleStdVar = new StandardVariable();
		titleStdVar.setId(BreedingViewImportServiceImplTest.DATASET_TITLE_STANDARD_VAR_ID);

		Mockito.when(this.studyDataManager.getDataSetsByType(Matchers.anyInt(), (DataSetType) Matchers.anyObject())).thenReturn(
				new ArrayList<DataSet>());
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);
		Mockito.when(this.studyDataManager.getDataSet(this.MEASUREMENT_DATASET_ID)).thenReturn(this.createMeasurementDataSet());
		Mockito.when(this.studyDataManager.getDataSet(this.EXISTING_MEANS_DATASET_ID)).thenReturn(null);
		Mockito.when(this.studyDataManager.getDataSet(this.NEW_MEANS_DATASET_ID)).thenReturn(this.createNewMeansDataSet());
		Mockito.when(this.studyDataManager.getStudy(Matchers.anyInt())).thenReturn(this.study);
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(this.dmsProject);
		Mockito.when(
				this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
						(DatasetValues) Matchers.anyObject(), Matchers.anyString())).thenReturn(
				new DatasetReference(this.NEW_MEANS_DATASET_ID, this.EMPTY_VALUE));
		Mockito.when(this.studyDataManager.getStocksInDataset(Matchers.anyInt())).thenReturn(this.stocks);

		Mockito.when(this.ontologyDataManager.addMethod(Matchers.anyString(), Matchers.anyString())).thenReturn(
				this.createTerm(this.LS_MEAN_ID, this.LS_MEAN));
		Mockito.when(
				this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyInt())).thenReturn(null);
		Mockito.when(this.ontologyDataManager.getStandardVariable(8150, PROGRAM_UUID)).thenReturn(studyStdVar);
		Mockito.when(this.ontologyDataManager.getStandardVariable(8155, PROGRAM_UUID)).thenReturn(titleStdVar);
		Mockito.when(this.ontologyDataManager.getStandardVariable(8160, PROGRAM_UUID)).thenReturn(dataSetStdVar);

		Mockito.when(this.stocks.findOnlyOneByLocalName(Matchers.anyString(), Matchers.anyString())).thenReturn(Mockito.mock(Stock.class));
		Mockito.when(this.stocks.findOnlyOneByLocalName(Matchers.anyString(), Matchers.anyString()).getId()).thenReturn(1);

		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	@Test
	public void testImportMeansDataWithExistingMeansDataSet() throws Exception {

		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(this.createExistingMeansDataSet());
		Mockito.when(this.studyDataManager.getDataSetsByType(Matchers.anyInt(), (DataSetType) Matchers.anyObject())).thenReturn(dataSets);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);
		Mockito.when(this.studyDataManager.getDataSet(this.MEASUREMENT_DATASET_ID)).thenReturn(this.createMeasurementDataSet());
		Mockito.when(this.studyDataManager.getDataSet(this.EXISTING_MEANS_DATASET_ID)).thenReturn(this.createExistingMeansDataSet());
		Mockito.when(this.studyDataManager.getStudy(Matchers.anyInt())).thenReturn(this.study);
		Mockito.when(
				this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
						(DatasetValues) Matchers.anyObject(), Matchers.anyString())).thenReturn(
				new DatasetReference(this.NEW_MEANS_DATASET_ID, this.EMPTY_VALUE));
		Mockito.when(this.studyDataManager.getStocksInDataset(Matchers.anyInt())).thenReturn(this.stocks);

		Mockito.when(this.ontologyDataManager.addMethod(Matchers.anyString(), Matchers.anyString())).thenReturn(
				this.createTerm(this.LS_MEAN_ID, this.LS_MEAN));
		Mockito.when(this.ontologyDataManager.getStandardVariable(Matchers.anyInt(), Matchers.anyString())).thenReturn(
				Mockito.mock(StandardVariable.class));
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(this.dmsProject);


		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.service).appendVariableTypesToExistingMeans((String[]) Matchers.anyObject(), Matchers.any(DataSet.class),
				Matchers.any(DataSet.class), Matchers.anyString());
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	@Test
	public void testImportMeansDataWithExistingMeansDataAndAdditionalTraits() throws Exception {

		this.variateVariableTypes.add(this.createVariateVariableType("EXTRAIT"));
		DataSet measurementDataSet = this.createMeasurementDataSet();

		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(this.createExistingMeansDataSet());
		Mockito.when(this.studyDataManager.getDataSetsByType(Matchers.anyInt(), (DataSetType) Matchers.anyObject())).thenReturn(dataSets);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);
		Mockito.when(this.service.getPlotDataSet(this.STUDY_ID)).thenReturn(measurementDataSet);
		Mockito.when(this.studyDataManager.getDataSet(this.EXISTING_MEANS_DATASET_ID)).thenReturn(this.createExistingMeansDataSet());
		Mockito.when(this.studyDataManager.getStudy(Matchers.anyInt())).thenReturn(this.study);
		Mockito.when(
				this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
						(DatasetValues) Matchers.anyObject(), Matchers.anyString())).thenReturn(
				new DatasetReference(this.NEW_MEANS_DATASET_ID, this.EMPTY_VALUE));
		Mockito.when(this.studyDataManager.getStocksInDataset(Matchers.anyInt())).thenReturn(this.stocks);

		Mockito.when(this.ontologyDataManager.addMethod(Matchers.anyString(), Matchers.anyString())).thenReturn(
				this.createTerm(this.LS_MEAN_ID, this.LS_MEAN));
		Mockito.when(
				this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyInt())).thenReturn(null);
		Mockito.when(this.ontologyDataManager.getStandardVariable(Matchers.anyInt(), Matchers.anyString())).thenReturn(
				Mockito.mock(StandardVariable.class));
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(this.dmsProject);


		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithAdditionalTraits.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.service).appendVariableTypesToExistingMeans((String[]) Matchers.anyObject(), Matchers.any(DataSet.class),
				Matchers.any(DataSet.class), Matchers.anyString());
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	@Test
	public void testImportOutlierData() throws Exception {

		List<Object[]> phenotypeIds = new ArrayList<>();
		phenotypeIds.add(new Object[] {"76373", "9999", "1"});

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());
		Mockito.when(
				this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyList())).thenReturn(phenotypeIds);

		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		this.service.importOutlierData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).saveOrUpdatePhenotypeOutliers(Matchers.anyList());
	}

	@Test
	public void testImportSummaryStatsData() throws Exception {

		Mockito.doReturn(this.createTrialDataSet()).when(this.service).getTrialDataSet(this.TRIAL_DATASET_ID);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());

		Mockito.when(this.ontologyDataManager.findMethodByName(Matchers.anyString())).thenReturn(this.createTerm(888, "DUMMYTERM"));
		Mockito.when(
				this.ontologyDataManager.getStandardVariableIdByPropertyIdScaleIdMethodId(Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyInt())).thenReturn(null);

		File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		this.service.importSummaryStatsData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).saveTrialDatasetSummary(Matchers.any(DmsProject.class), Matchers.any(VariableTypeList.class),
				Matchers.anyList(), Matchers.anyList());

	}

	private Term createTerm(int id, String name) {
		return new Term(id, name, "");
	}

	private DataSet createTrialDataSet() {

		DataSet dataSet = new DataSet();
		dataSet.setId(this.TRIAL_DATASET_ID);

		VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (DMSVariableType variate : this.variateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createMeasurementDataSet() {

		DataSet dataSet = new DataSet();
		dataSet.setId(this.MEASUREMENT_DATASET_ID);

		VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (DMSVariableType variate : this.variateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createNewMeansDataSet() {

		DataSet dataSet = new DataSet();
		dataSet.setId(this.NEW_MEANS_DATASET_ID);

		VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (DMSVariableType variate : this.meansVariateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createExistingMeansDataSet() {

		DataSet dataSet = new DataSet();
		dataSet.setId(this.EXISTING_MEANS_DATASET_ID);

		VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (DMSVariableType variate : this.meansVariateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DMSVariableType createVariateVariableType(String localName) {
		DMSVariableType variate = new DMSVariableType();
		StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);

		Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);

		Term property = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(33333);

		variateStandardVar.setId(1234);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setDataType(dataType);
		variateStandardVar.setName(localName);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);
		variate.setRole(PhenotypicType.VARIATE);

		return variate;
	}

	private DMSVariableType createVariateVariableType(String localName, String propertyName, String scaleName, String methodName) {
		DMSVariableType variate = new DMSVariableType();
		StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);
		method.setName(methodName);

		Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);

		Term property = new Term();
		property.setDefinition(this.EMPTY_VALUE);
		property.setId(33333);
		property.setName(propertyName);

		variateStandardVar.setId(1234);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setDataType(dataType);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);

		return variate;
	}

	private DMSVariableType createTrialEnvironmentVariableType(String localName) {

		DMSVariableType factor = new DMSVariableType();
		StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setId(TermId.LOCATION_ID.getId());
		factorStandardVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		factorStandardVar.setName(localName);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);

		return factor;
	}

	private DMSVariableType createGermplasmFactorVariableType(String localName) {
		DMSVariableType factor = new DMSVariableType();
		StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.GERMPLASM);

		Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_DBID_VARIABLE.getId());

		Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);

		Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);

		Term property = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(33333);

		factorStandardVar.setId(1234);
		factorStandardVar.setProperty(property);
		factorStandardVar.setScale(scale);
		factorStandardVar.setMethod(method);
		factorStandardVar.setDataType(dataType);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);

		return factor;
	}

	private TrialEnvironments createTrialEnvironments() {

		TrialEnvironments trialEnvs = new TrialEnvironments();
		trialEnvs.add(this.createTrialEnvironment());
		return trialEnvs;

	}

	private TrialEnvironment createTrialEnvironment() {

		LocationDto location = new LocationDto(1, "CIMMYT");
		VariableList variableList = new VariableList();

		this.addVariables(variableList);

		TrialEnvironment trialEnv = new TrialEnvironment(1, variableList);
		trialEnv.setLocation(location);

		return trialEnv;

	}

	private void addVariables(VariableList list) {
		for (DMSVariableType f : this.factorVariableTypes) {
			list.add(this.createVariable(f));
		}
		for (DMSVariableType v : this.variateVariableTypes) {
			list.add(this.createVariable(v));
		}
	}

	private Variable createVariable(DMSVariableType variableType) {
		Variable v = new Variable();
		if (variableType.getLocalName().equals("TRIAL_INSTANCE")) {
			v.setValue("1");
		} else {
			v.setValue("2222");
		}

		v.setVariableType(variableType);
		return v;
	}

}
