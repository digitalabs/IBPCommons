
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.service.impl.BreedingViewImportServiceImpl.SummaryStatsCSV;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
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
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.rits.cloning.Cloner;

import junit.framework.Assert;

public class BreedingViewImportServiceImplTest {

	private static final String TRAIT_ASI = "ASI";
	private static final String TRAIT_APHID = "Aphid1_5";
	private static final String TRAIT_EPH = "EPH";
	private static final String TRAIT_FMSROT = "FMSROT";
	private static final String[] TRAITS = {TRAIT_ASI, TRAIT_APHID, TRAIT_EPH, TRAIT_FMSROT};
	private static final String ANALYSIS_VAR_NAME = TRAIT_ASI + BreedingViewImportServiceImpl.MEANS_SUFFIX;
	private static final String[] SUMMARY_STATS_HEADERS = {"NumValues",	"NumMissing", "Variance", "SD",	"Min", "Max", "Range"};
	private static final int STUDY_ID = 1;
	private static final String STUDY_NAME = "TEST STUDY";

	private static final int MEASUREMENT_DATASET_ID = 2;
	private static final int EXISTING_MEANS_DATASET_ID = 3;
	private static final int NEW_MEANS_DATASET_ID = 4;
	private static final int TRIAL_DATASET_ID = 1;
	private static final String PROGRAM_UUID = "12345678";
    private static final Integer TEST_ANALYSIS_VARIABLE_TERM_ID = 65000;

	private static final String EMPTY_VALUE = "";

	private static final String LS_MEAN = "LS MEAN";
	private static final Integer LS_MEAN_ID = 16090;
	private static final String ERROR_ESTIMATE = "ERROR ESTIMATE";
	private static final int ERROR_ESTIMATE_ID = 16095;
	

	private List<DMSVariableType> factorVariableTypes = new ArrayList<DMSVariableType>();
	private List<DMSVariableType> variateVariableTypes = new ArrayList<DMSVariableType>();
	private List<DMSVariableType> meansVariateVariableTypes = new ArrayList<DMSVariableType>();

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private OntologyDaoFactory ontologyDaoFactory;
	
	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;
	
	@Mock
	private OntologyMethodDataManager methodDataManager;
	
	@Mock
	private StandardVariableTransformer standardVariableTransformer;
	
	@Mock
	private WorkbenchDataManager workbenchDataManager;
    
	@Mock
    private OntologyDataManager ontologyDataManager;

	@Mock
	private TrialEnvironments trialEnvironments;
	
	@Mock
	private TrialEnvironment trialEnvironment;
	
	@Mock
	private ContextUtil contextUtil;

	private CVTerm meansCVTerm;
	private CVTerm errorEstimateCVTerm;
	

	@InjectMocks
	private BreedingViewImportServiceImpl bvImportService = new BreedingViewImportServiceImpl();

	private Stocks stocks;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();
		
		this.meansCVTerm = this.createCVTerm(LS_MEAN_ID, LS_MEAN);
		Mockito.doReturn(this.meansCVTerm).when(cvTermDao)
				.getByNameAndCvId(LS_MEAN, CvId.METHODS.getId());
		this.errorEstimateCVTerm = this.createCVTerm(ERROR_ESTIMATE_ID, ERROR_ESTIMATE);
		Mockito.doReturn(this.errorEstimateCVTerm).when(cvTermDao)
				.getByNameAndCvId(ERROR_ESTIMATE, CvId.METHODS.getId());

		Mockito.doReturn(this.createDmsProject(STUDY_ID, STUDY_NAME, BreedingViewImportServiceImplTest.PROGRAM_UUID))
				.when(this.studyDataManager).getProject(STUDY_ID);

		this.factorVariableTypes.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("ENTRY_NO", TermId.ENTRY_NO.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("GID", TermId.GID.getId()));

		for (final String traitName : TRAITS){
			this.variateVariableTypes.add(this.createVariateVariableType(traitName));
			this.meansVariateVariableTypes.add(this.createVariateVariableType(traitName + BreedingViewImportServiceImpl.MEANS_SUFFIX, "", "", "LS MEAN"));
			this.meansVariateVariableTypes.add(this.createVariateVariableType(traitName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, "", "", "ERROR ESTIMATE"));
		}

		final List<DataSet> plotDatasets = new ArrayList<>();
		plotDatasets.add(this.createMeasurementDataSet());
		Mockito.doReturn(plotDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.PLOT_DATA);

		this.stocks = new Stocks();
		Mockito.when(this.studyDataManager.getStocksInDataset(Matchers.anyInt())).thenReturn(this.stocks);

		this.bvImportService.setCloner(new Cloner());

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
		Project p = this.createProjectWithCrop();

		// import with no existing means data
		Mockito.doReturn(null).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager
				.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(), (DatasetValues) Matchers.anyObject(),
						Matchers.anyString())).thenReturn(new DatasetReference(NEW_MEANS_DATASET_ID, EMPTY_VALUE));

		Mockito.when(this.studyDataManager.getDataSet(NEW_MEANS_DATASET_ID)).thenReturn(this.createNewMeansDataSet());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.bvImportService.importMeansData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager).addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString());

		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	private CVTerm createCVTerm(final int cvTermId, final String name) {
		final CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(cvTermId);
		cvTerm.setName(name);
		return cvTerm;
	}

	@Test
	public void testImportMeansDataWithExistingMeansDataSet() throws Exception {
		Project p = createProjectWithCrop();
		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.bvImportService.importMeansData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(0)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	@Test
	public void testImportMeansDataWithExistingMeansDataAndAdditionalTraits() throws Exception {
		Project p = this.createProjectWithCrop();

		this.variateVariableTypes.add(this.createVariateVariableType("EXTRAIT"));
		final DataSet measurementDataSet = this.createMeasurementDataSet();
		final List<DataSet> plotDataDatasets = new ArrayList<>();
		plotDataDatasets.add(measurementDataSet);
		Mockito.doReturn(plotDataDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.PLOT_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);

		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.MEANS_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithAdditionalTraits.csv").toURI());

		this.bvImportService.importMeansData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(2)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	@Test
	public void testImportOutlierData() throws Exception {

		final List<Object[]> phenotypeIds = new ArrayList<>();
		phenotypeIds.add(new Object[] {"76373", "9999", "1"});

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());
		Mockito.when(
				this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyListOf(Integer.class))).thenReturn(phenotypeIds);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		this.bvImportService.importOutlierData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager).saveOrUpdatePhenotypeOutliers(Matchers.anyListOf(PhenotypeOutlier.class));
	}

	@Test
	public void testImportSummaryStatsData() throws Exception {

		final List<DataSet> summaryDataDatasets = new ArrayList<>();
		summaryDataDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDataDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());

		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();
		Mockito.doReturn(this.createCVTerm(888, "DUMMYTERM")).when(cvTermDao).getByNameAndCvId("DUMMYTERM", CvId.METHODS.getId());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		this.bvImportService.importSummaryStatsData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager).saveTrialDatasetSummary(Matchers.any(DmsProject.class), Matchers.any(VariableTypeList.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyListOf(Integer.class));

	}

	private DataSet createTrialDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(TRIAL_DATASET_ID);

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
		dataSet.setId(MEASUREMENT_DATASET_ID);

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
		dataSet.setId(NEW_MEANS_DATASET_ID);

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
		dataSet.setId(EXISTING_MEANS_DATASET_ID);

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
		method.setDefinition(EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(EMPTY_VALUE);
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
		method.setDefinition(EMPTY_VALUE);
		method.setName(methodName);

		final Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);

		final Term property = new Term();
		property.setDefinition(EMPTY_VALUE);
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

	private DMSVariableType createGermplasmFactorVariableType(final String localName, final int termId) {
		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.GERMPLASM);

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_DBID_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(EMPTY_VALUE);
		scale.setId(33333);

		factorStandardVar.setId(termId);
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

	@Test
	public void testImportMeansDataWithDupeEntryNo() throws Exception {
		Project p = createProjectWithCrop();
		// import with no existing means data
		Mockito.doReturn(null).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);

		Mockito.when(
				this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
						(DatasetValues) Matchers.anyObject(), Matchers.anyString())).thenReturn(
				new DatasetReference(NEW_MEANS_DATASET_ID, EMPTY_VALUE));

		Mockito.when(this.studyDataManager.getDataSet(NEW_MEANS_DATASET_ID)).thenReturn(this.createNewMeansDataSet());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputDupeEntryNo.csv").toURI());

		this.bvImportService.importMeansData(file, STUDY_ID);

		Mockito.verify(this.studyDataManager).addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString());

		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	private Project createProjectWithCrop() {
		Project p = new Project();
		CropType c = new CropType();
		c.setPlotCodePrefix("CODE");
		p.setCropType(c);
		return p;
	}

	@Test
    public void testCreateAnalysisVariableNonExisting() {
		final boolean isSummaryVariable = false;

		DMSVariableType originalVariableType = this.createVariateVariableType(TRAIT_ASI);
        Term meansMethod = new Term();
        meansMethod.setId(LS_MEAN_ID);
        Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(), LS_MEAN_ID)).thenReturn(null);

        this.bvImportService.createAnalysisVariable(originalVariableType, ANALYSIS_VAR_NAME, meansMethod, PROGRAM_UUID, 1, isSummaryVariable);
        
        // Verify saving actions in Middleware
        ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
        Mockito.verify(ontologyVariableDataManager).addVariable(infoArgument.capture());
        // anyInt is used as the 2nd argument since this represents the dynamic term ID that will be generated after saving the new variable into the DB
        Mockito.verify(ontologyDataManager).addCvTermRelationship(Mockito.eq(originalVariableType.getId()), Mockito.anyInt(), Mockito.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));
        OntologyVariableInfo argument = infoArgument.getValue();

        Assert.assertEquals("Unable to properly add a new analysis variable with the proper method ID", LS_MEAN_ID, argument.getMethodId());
        Assert.assertEquals("Unable to properly add a new analysis variable with the proper name", ANALYSIS_VAR_NAME, argument.getName() );
        Assert.assertNotNull(argument.getVariableTypes());
        Assert.assertEquals("Expecting only one variable type for new analysis variable but had more than one.", 1, argument.getVariableTypes().size() );
        Assert.assertEquals("Expecting analysis variable to have variable type 'Analysis' but did not.", VariableType.ANALYSIS, argument.getVariableTypes().iterator().next());
    }
	

    @Test
    public void testCreateAnalysisVariableExisting() {
    	final boolean isSummaryVariable = false;
        DMSVariableType originalVariableType = this.createVariateVariableType(TRAIT_ASI);
        Term meansMethod = new Term();
        meansMethod.setId(LS_MEAN_ID);

        Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(), LS_MEAN_ID)).thenReturn(TEST_ANALYSIS_VARIABLE_TERM_ID);

        bvImportService.createAnalysisVariable(originalVariableType, ANALYSIS_VAR_NAME, meansMethod, PROGRAM_UUID, 1, isSummaryVariable);

        Mockito.verify(ontologyVariableDataManager, Mockito.never()).addVariable(Mockito.any(OntologyVariableInfo.class));
        Mockito.verify(ontologyDataManager, Mockito.never()).addCvTermRelationship(Mockito.eq(originalVariableType.getId()), Mockito.anyInt(), Mockito.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));

    }
    
    @Test
    public void testCreateAnalysisSummaryVariableNonExisting() {
        final boolean isSummaryVariable = true;
		final String analysisSummaryVariableName = TRAIT_ASI + "_NumMissing";
		final Integer methodId = 4130;
		
		DMSVariableType originalVariableType = this.createVariateVariableType(TRAIT_ASI);
        Term meansMethod = new Term();
        meansMethod.setId(methodId);
        Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(), methodId)).thenReturn(null);

        this.bvImportService.createAnalysisVariable(originalVariableType, analysisSummaryVariableName, meansMethod, PROGRAM_UUID, 1, isSummaryVariable);
        
        // Verify saving actions in Middleware
        ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
        Mockito.verify(ontologyVariableDataManager).addVariable(infoArgument.capture());
        // anyInt is used as the 2nd argument since this represents the dynamic term ID that will be generated after saving the new variable into the DB
        Mockito.verify(ontologyDataManager).addCvTermRelationship(Mockito.eq(originalVariableType.getId()), Mockito.anyInt(), Mockito.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));
        OntologyVariableInfo argument = infoArgument.getValue();

        Assert.assertEquals("Unable to properly add a new analysis summary variable with the proper method ID", methodId, argument.getMethodId());
        Assert.assertEquals("Unable to properly add a new analysis summary variable with the proper name", analysisSummaryVariableName, argument.getName() );
        Assert.assertNotNull(argument.getVariableTypes());
        Assert.assertEquals("Expecting only one variable type for new analysis summary variable but had more than one.", 1, argument.getVariableTypes().size() );
        Assert.assertEquals("Expecting analysis summary variable to have variable type 'Analysis Summary' but did not.", VariableType.ANALYSIS_SUMMARY, argument.getVariableTypes().iterator().next());     
    }
    
    @Test
    public void testCreateMeansVariablesFromImportFile() {
    	// Setup test data from file  - 4 traits with 2 analysis variables each (_Means and _ErrorEstimate suffixes)
    	final VariableTypeList meansVariableList = new VariableTypeList();
    	final List<String> csvHeaders = new ArrayList<String>();
    	for (final DMSVariableType factor : this.factorVariableTypes) {
    		meansVariableList.add(factor);
    		csvHeaders.add(factor.getLocalName());
    	}
    	final int oldVariableListSize = meansVariableList.size();
    	final VariableTypeList plotVariateList = new VariableTypeList();
    	for (final DMSVariableType trait : this.variateVariableTypes) {
    		plotVariateList.add(trait);
    	}
    	for (final DMSVariableType analysisVar : this.meansVariateVariableTypes){
    		csvHeaders.add(analysisVar.getLocalName());
    	}
    	Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);

    	
    	// Method to test
		final String[] csvHeadersArray = csvHeaders.toArray(new String[csvHeaders.size()]);
		this.bvImportService.createMeansVariablesFromImportFileAndAddToList(csvHeadersArray,
				plotVariateList, meansVariableList, PROGRAM_UUID, this.meansCVTerm, this.errorEstimateCVTerm, false);
		
		// Expecting 2 analysis variables for each trait: <trait name>_Means and <trait name>_ErrorEstimate
		final int newVariablesSize = TRAITS.length * 2;
		Assert.assertEquals("Expecting " + newVariablesSize + " analysis variables to be added to means dataset variables.",
				oldVariableListSize + newVariablesSize, meansVariableList.size());
		for (final String traitName : TRAITS){
			boolean isMeansVarFound = false;
			boolean isErrorEstimateVarFound = false;
			for (final DMSVariableType trait : meansVariableList.getVariates().getVariableTypes()) {
				final String meansVariableName = traitName + BreedingViewImportServiceImpl.MEANS_SUFFIX;
				final String errorEstimateVariableName = traitName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX;
				if (meansVariableName.equals(trait.getLocalName())) {
					isMeansVarFound = true;
					continue;
				} else if (errorEstimateVariableName.equals(trait.getLocalName())) {
					isErrorEstimateVarFound = true;
					continue;
				}
			}
			Assert.assertTrue("Expecting means analysis variable for " + traitName + " was added but was not.", isMeansVarFound);
			Assert.assertTrue("Expecting unit errors analysis variable for " + traitName + " was added but was not.", isErrorEstimateVarFound);
		}
		// Check that new variables were added to have "Analysis" variable type
		ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
        Mockito.verify(ontologyVariableDataManager, Mockito.times(newVariablesSize)).addVariable(infoArgument.capture());
        for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()){
        	Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
        	Assert.assertEquals("Expecting 'Analysis' as sole variable type for new variable.", VariableType.ANALYSIS, variableInfo.getVariableTypes().iterator().next());
        }
    }
    
    @Test
    public void testAppendVariableTypesToExistingMeans() {
    	// Setup test data from file  - 4 traits with 2 analysis variables each (_Means and _ErrorEstimate suffixes)
    	final String[] prevAnalyzedTraits = {TRAIT_ASI, TRAIT_EPH};
    	final VariableTypeList meansVariableList = new VariableTypeList();
    	final List<String> csvHeaders = new ArrayList<String>();
    	for (final DMSVariableType factor : this.factorVariableTypes) {
    		meansVariableList.add(factor);
    		csvHeaders.add(factor.getLocalName());
    	}
    	// Assuming there is existing means dataset with 2 traits previously analyzed
    	for (final String traitName : prevAnalyzedTraits){
    		meansVariableList.add(this.createVariateVariableType(traitName + BreedingViewImportServiceImpl.MEANS_SUFFIX, "", "", "LS MEAN"));
    		meansVariableList.add(this.createVariateVariableType(traitName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX, "", "", "ERROR ESTIMATE"));
    	}
    	final int oldVariableListSize = meansVariableList.size();

    	final DataSet plotDataSet = new DataSet();
    	plotDataSet.setId(MEASUREMENT_DATASET_ID);
    	final VariableTypeList plotVariateList = new VariableTypeList();
    	for (final DMSVariableType trait : this.variateVariableTypes) {
    		plotVariateList.add(trait);
    	}
    	plotDataSet.setVariableTypes(plotVariateList);
    	
    	final DataSet meansDataSet = new DataSet();
    	meansDataSet.setId(EXISTING_MEANS_DATASET_ID);
    	for (final DMSVariableType analysisVar : this.meansVariateVariableTypes){
    		csvHeaders.add(analysisVar.getLocalName());
    	}
    	meansDataSet.setVariableTypes(meansVariableList);
    	Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
    	
    	// Method to test
		final String[] csvHeadersArray = csvHeaders.toArray(new String[csvHeaders.size()]);
		this.bvImportService.appendVariableTypesToExistingMeans(csvHeadersArray, plotDataSet, meansDataSet, PROGRAM_UUID, meansCVTerm, errorEstimateCVTerm, false);
		
		
		// Expecting 2 analysis variables for each unanalyzed trait: <trait name>_Means and <trait name>_ErrorEstimate	
		final int newVariablesSize = (TRAITS.length - prevAnalyzedTraits.length) * 2;
		Assert.assertEquals("Expecting " + newVariablesSize + " analysis variables to be added to means dataset variables.",
				oldVariableListSize + newVariablesSize, meansVariableList.size());
		for (final String traitName : TRAITS){
			boolean isMeansVarFound = false;
			boolean isErrorEstimateVarFound = false;
			for (final DMSVariableType trait : meansVariableList.getVariates().getVariableTypes()) {
				final String meansVariableName = traitName + BreedingViewImportServiceImpl.MEANS_SUFFIX;
				final String errorEstimateVariableName = traitName + BreedingViewImportServiceImpl.UNIT_ERRORS_SUFFIX;
				if (meansVariableName.equals(trait.getLocalName())) {
					isMeansVarFound = true;
					continue;
				} else if (errorEstimateVariableName.equals(trait.getLocalName())) {
					isErrorEstimateVarFound = true;
					continue;
				}
			}
			Assert.assertTrue("Expecting means analysis variable for " + traitName + " but was not found.", isMeansVarFound);
			Assert.assertTrue("Expecting unit errors analysis variable for " + traitName + " but was not found.", isErrorEstimateVarFound);
		}
                                                                                                                                         		ArgumentCaptor<Integer> datasetIdArgument = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(this.studyDataManager, Mockito.times(newVariablesSize)).addDataSetVariableType(datasetIdArgument.capture(), Matchers.any(DMSVariableType.class));
	  	Assert.assertEquals("Expecting correct ID to be used for adding new variables to means dataset.", EXISTING_MEANS_DATASET_ID, datasetIdArgument.getValue().intValue());
	 
	  	// Check that new variables were added to have "Analysis" variable type
	  	ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
        Mockito.verify(ontologyVariableDataManager, Mockito.times(newVariablesSize)).addVariable(infoArgument.capture());
        final List<String> prevAnalyzedTraitsList = Arrays.asList(prevAnalyzedTraits);
        for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()){
        	Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
        	Assert.assertEquals("Expecting 'Analysis' as sole variable type for new variable.", VariableType.ANALYSIS, variableInfo.getVariableTypes().iterator().next());
        	final String analysisVariableName = variableInfo.getName();
        	final String sourceTraitName = analysisVariableName.substring(0, analysisVariableName.lastIndexOf("_"));
        	Assert.assertFalse("Expecting analysis variables are not added for previously analyzed traits.", prevAnalyzedTraitsList.contains(sourceTraitName));
        }
    }
    
    @Test
    public void testCreateSummaryStatsVariableTypes() throws IOException {
    	final SummaryStatsCSV summaryStatsCSV = Mockito.mock(SummaryStatsCSV.class);
    	Mockito.doReturn(Arrays.asList(SUMMARY_STATS_HEADERS)).when(summaryStatsCSV).getHeaderStats();
    	
    	final DataSet trialDataSet = new DataSet();
    	trialDataSet.setId(MEASUREMENT_DATASET_ID);
    	final VariableTypeList trialVariablesList = new VariableTypeList();
		trialVariablesList.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
    	trialDataSet.setVariableTypes(trialVariablesList);
    	
    	final VariableTypeList plotVariateList = new VariableTypeList();
    	final Map<String, String> traitAliasMap = new HashMap<>();
    	for (final DMSVariableType trait : this.variateVariableTypes) {
    		plotVariateList.add(trait);
    		traitAliasMap.put(trait.getLocalName(), trait.getLocalName());
    	}
    	Mockito.when(ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
    	
    	
    	// Method to test
    	final VariableTypeList summaryStatVariables = this.bvImportService.createSummaryStatsVariableTypes(summaryStatsCSV, trialDataSet, plotVariateList, traitAliasMap, PROGRAM_UUID);
    	
    	// Expecting one analysis summary variable per summary statistic method for each trait
    	final List<String> expectedSummaryVariableNames = new ArrayList<>();
    	for (String trait : TRAITS){
    		for (String method : SUMMARY_STATS_HEADERS) {
    			expectedSummaryVariableNames.add(trait + "_" + method);
    		}
    	}
    	Assert.assertEquals("Expecting " + summaryStatsCSV + " summary statistics variables per trait.", expectedSummaryVariableNames.size(), summaryStatVariables.size());
    	
    	// Check that variable type "Analysis Summary" was used for new summary statistic variables
    	for (DMSVariableType variable : summaryStatVariables.getVariableTypes()){
    		Assert.assertEquals("Expecting 'Analysis Summary' variable type for added variable.", VariableType.ANALYSIS_SUMMARY, variable.getVariableType());
        	
    	}
    	ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
        Mockito.verify(ontologyVariableDataManager, Mockito.times(expectedSummaryVariableNames.size())).addVariable(infoArgument.capture());
        for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()){
        	Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
        	Assert.assertEquals("Expecting 'Analysis Summary' as sole variable type for new variable.", VariableType.ANALYSIS_SUMMARY, variableInfo.getVariableTypes().iterator().next());
        }
    	
    }
    
    

}
