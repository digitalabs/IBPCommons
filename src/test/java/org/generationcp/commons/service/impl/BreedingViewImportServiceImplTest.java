
package org.generationcp.commons.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.LocationDto;
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
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
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
	private static final String PROGRAM_UUID = "12345678";

	private final String EMPTY_VALUE = "";

	private final String LS_MEAN = "LS MEAN";
	private final int LS_MEAN_ID = 16090;
	private final String ERROR_ESTIMATE = "ERROR ESTIMATE";
	private final int ERROR_ESTIMATE_ID = 16095;

	List<DMSVariableType> factorVariableTypes = new ArrayList<DMSVariableType>();
	List<DMSVariableType> variateVariableTypes = new ArrayList<DMSVariableType>();
	List<DMSVariableType> meansVariateVariableTypes = new ArrayList<DMSVariableType>();

	@Mock
	StudyDataManager studyDataManager;
	@Mock
	OntologyDaoFactory ontologyDaoFactory;
	@Mock
	OntologyVariableDataManager ontologyVariableDataManager;
	@Mock
	OntologyMethodDataManager methodDataManager;
	@Mock
	StandardVariableTransformer standardVariableTransformer;
	@Mock
	WorkbenchDataManager workbenchDataManager;
	@Mock
	Map<String, String> params;

	@Mock
	TrialEnvironments trialEnvironments;
	@Mock
	TrialEnvironment trialEnvironment;

	Map<String, ArrayList<String>> meansInput;
	Map<String, Map<String, ArrayList<String>>> summaryStatisticsInput;

	@InjectMocks
	BreedingViewImportServiceImpl service = new BreedingViewImportServiceImpl();

	private Stocks stocks;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();
		Mockito.doReturn(this.createCVTerm(this.LS_MEAN_ID, this.LS_MEAN)).when(cvTermDao)
				.getByNameAndCvId(this.LS_MEAN, CvId.METHODS.getId());
		Mockito.doReturn(this.createCVTerm(this.ERROR_ESTIMATE_ID, this.ERROR_ESTIMATE)).when(cvTermDao)
				.getByNameAndCvId(this.ERROR_ESTIMATE, CvId.METHODS.getId());

		Mockito.doReturn(this.createDmsProject(this.STUDY_ID, this.STUDY_NAME, BreedingViewImportServiceImplTest.PROGRAM_UUID))
				.when(this.studyDataManager).getProject(this.STUDY_ID);

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

		final List<DataSet> plotDatasets = new ArrayList<>();
		plotDatasets.add(this.createMeasurementDataSet());
		Mockito.doReturn(plotDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.PLOT_DATA);

		this.stocks = new Stocks();
		Mockito.when(this.studyDataManager.getStocksInDataset(Matchers.anyInt())).thenReturn(this.stocks);

		this.service.setCloner(new Cloner());

		Mockito.doReturn(new StandardVariable()).when(this.standardVariableTransformer)
				.transformVariable(Matchers.any(org.generationcp.middleware.domain.ontology.Variable.class));
	}

	private DmsProject createDmsProject(final int id, final String name, final String programUUID) {
		final DmsProject study = new DmsProject();
		study.setProjectId(id);
		study.setName(name);
		study.setProgramUUID(programUUID);
		return study;
	}

	@Test
	public void testImportMeansData() throws Exception {
		// import with no existing means data
		Mockito.doReturn(null).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.when(
				this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
						(DatasetValues) Matchers.anyObject(), Matchers.anyString())).thenReturn(
				new DatasetReference(this.NEW_MEANS_DATASET_ID, this.EMPTY_VALUE));

		Mockito.when(this.studyDataManager.getDataSet(this.NEW_MEANS_DATASET_ID)).thenReturn(this.createNewMeansDataSet());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString());

		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	private CVTerm createCVTerm(final int cvTermId, final String name) {
		final CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(cvTermId);
		cvTerm.setName(name);
		return cvTerm;
	}

	@Test
	public void testImportMeansDataWithExistingMeansDataSet() throws Exception {

		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(0)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	@Test
	public void testImportMeansDataWithExistingMeansDataAndAdditionalTraits() throws Exception {

		this.variateVariableTypes.add(this.createVariateVariableType("EXTRAIT"));
		final DataSet measurementDataSet = this.createMeasurementDataSet();
		final List<DataSet> plotDataDatasets = new ArrayList<>();
		plotDataDatasets.add(measurementDataSet);
		Mockito.doReturn(plotDataDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.PLOT_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.SUMMARY_DATA);

		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.MEANS_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithAdditionalTraits.csv").toURI());

		this.service.importMeansData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(2)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyList());

	}

	@Test
	public void testImportOutlierData() throws Exception {

		final List<Object[]> phenotypeIds = new ArrayList<>();
		phenotypeIds.add(new Object[] {"76373", "9999", "1"});

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());
		Mockito.when(
				this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyList())).thenReturn(phenotypeIds);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		this.service.importOutlierData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).saveOrUpdatePhenotypeOutliers(Matchers.anyList());
	}

	@Test
	public void testImportSummaryStatsData() throws Exception {

		final List<DataSet> summaryDataDatasets = new ArrayList<>();
		summaryDataDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDataDatasets).when(this.studyDataManager).getDataSetsByType(this.STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());

		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();
		Mockito.doReturn(this.createCVTerm(888, "DUMMYTERM")).when(cvTermDao).getByNameAndCvId("DUMMYTERM", CvId.METHODS.getId());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		this.service.importSummaryStatsData(file, this.STUDY_ID);

		Mockito.verify(this.studyDataManager).saveTrialDatasetSummary(Matchers.any(DmsProject.class), Matchers.any(VariableTypeList.class),
				Matchers.anyList(), Matchers.anyList());

	}

	private DataSet createTrialDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(this.TRIAL_DATASET_ID);

		final VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (final DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (final DMSVariableType variate : this.variateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createMeasurementDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(this.MEASUREMENT_DATASET_ID);

		final VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (final DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (final DMSVariableType variate : this.variateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createNewMeansDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(this.NEW_MEANS_DATASET_ID);

		final VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (final DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (final DMSVariableType variate : this.meansVariateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DataSet createExistingMeansDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(this.EXISTING_MEANS_DATASET_ID);

		final VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (final DMSVariableType factor : this.factorVariableTypes) {
			dataSet.getVariableTypes().add(factor);
		}
		for (final DMSVariableType variate : this.meansVariateVariableTypes) {
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DMSVariableType createVariateVariableType(final String localName) {
		final DMSVariableType variate = new DMSVariableType();
		final StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		final Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
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
		variate.setRole(variateStandardVar.getPhenotypicType());

		return variate;
	}

	private DMSVariableType createVariateVariableType(final String localName, final String propertyName, final String scaleName,
			final String methodName) {
		final DMSVariableType variate = new DMSVariableType();
		final StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		final Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);
		method.setName(methodName);

		final Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);

		final Term property = new Term();
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
		variate.setRole(variateStandardVar.getPhenotypicType());

		return variate;
	}

	private DMSVariableType createTrialEnvironmentVariableType(final String localName) {

		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setId(TermId.LOCATION_ID.getId());
		factorStandardVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		factorStandardVar.setName(localName);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		factor.setRole(factorStandardVar.getPhenotypicType());
		return factor;
	}

	private DMSVariableType createGermplasmFactorVariableType(final String localName) {
		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.GERMPLASM);

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_DBID_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(this.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(this.EMPTY_VALUE);
		scale.setId(33333);

		factorStandardVar.setId(1234);
		factorStandardVar.setProperty(property);
		factorStandardVar.setScale(scale);
		factorStandardVar.setMethod(method);
		factorStandardVar.setDataType(dataType);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		factor.setRole(factorStandardVar.getPhenotypicType());

		return factor;
	}

	private TrialEnvironments createTrialEnvironments() {

		final TrialEnvironments trialEnvs = new TrialEnvironments();
		trialEnvs.add(this.createTrialEnvironment());
		return trialEnvs;

	}

	private TrialEnvironment createTrialEnvironment() {

		final LocationDto location = new LocationDto(1, "CIMMYT");
		final VariableList variableList = new VariableList();

		this.addVariables(variableList);

		final TrialEnvironment trialEnv = new TrialEnvironment(1, variableList);
		trialEnv.setLocation(location);

		return trialEnv;

	}

	private void addVariables(final VariableList list) {
		for (final DMSVariableType f : this.factorVariableTypes) {
			list.add(this.createVariable(f));
		}
		for (final DMSVariableType v : this.variateVariableTypes) {
			list.add(this.createVariable(v));
		}
	}

	private Variable createVariable(final DMSVariableType variableType) {
		final Variable v = new Variable();
		if (variableType.getLocalName().equals("TRIAL_INSTANCE")) {
			v.setValue("1");
		} else {
			v.setValue("2222");
		}

		v.setVariableType(variableType);
		return v;
	}

}
