
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.parsing.MeansCSV;
import org.generationcp.commons.breedingview.parsing.SummaryStatsCSV;
import org.generationcp.commons.data.initializer.SummaryStatsTestDataInitializer;
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
import org.mockito.Captor;
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
	private static final String[] TRAITS = {BreedingViewImportServiceImplTest.TRAIT_ASI, BreedingViewImportServiceImplTest.TRAIT_APHID,
			BreedingViewImportServiceImplTest.TRAIT_EPH, BreedingViewImportServiceImplTest.TRAIT_FMSROT};
	private static final String ANALYSIS_VAR_NAME = BreedingViewImportServiceImplTest.TRAIT_ASI + MeansCSV.MEANS_SUFFIX;
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

	private final List<DMSVariableType> factorVariableTypes = new ArrayList<>();
	private final List<DMSVariableType> variateVariableTypes = new ArrayList<>();
	private final List<DMSVariableType> meansVariateVariableTypes = new ArrayList<>();
	private final List<DMSVariableType> summaryVariableTypes = new ArrayList<>();

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

	@Mock
	private SummaryStatsCSV summaryStatsCSV;

	@Captor
	private ArgumentCaptor<List<ExperimentValues>> experimentValuesCaptor;

	private CVTerm meansCVTerm;

	@InjectMocks
	private final BreedingViewImportServiceImpl bvImportService = new BreedingViewImportServiceImpl();

	private Stocks stocks;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();

		this.meansCVTerm = this.createCVTerm(BreedingViewImportServiceImplTest.LS_MEAN_ID, BreedingViewImportServiceImplTest.LS_MEAN);
		Mockito.doReturn(this.meansCVTerm).when(cvTermDao).getByNameAndCvId(BreedingViewImportServiceImplTest.LS_MEAN,
				CvId.METHODS.getId());

		Mockito.doReturn(this.createDmsProject(BreedingViewImportServiceImplTest.STUDY_ID, BreedingViewImportServiceImplTest.STUDY_NAME,
				BreedingViewImportServiceImplTest.PROGRAM_UUID)).when(this.studyDataManager)
				.getProject(BreedingViewImportServiceImplTest.STUDY_ID);

		this.factorVariableTypes.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("ENTRY_NO", TermId.ENTRY_NO.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("GID", TermId.GID.getId()));

		for (final String traitName : BreedingViewImportServiceImplTest.TRAITS) {
			this.variateVariableTypes.add(this.createVariateVariableType(traitName));
			this.meansVariateVariableTypes.add(this.createVariateVariableType(traitName + MeansCSV.MEANS_SUFFIX, "", "", "LS MEAN"));
			this.meansVariateVariableTypes
					.add(this.createVariateVariableType(traitName + MeansCSV.UNIT_ERRORS_SUFFIX, "", "", "ERROR ESTIMATE"));
		}

		final List<DataSet> plotDatasets = new ArrayList<>();
		plotDatasets.add(this.createMeasurementDataSet());
		Mockito.doReturn(plotDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.PLOT_DATA);

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
		final Project p = this.createProjectWithCrop();

		// import with no existing means data
		Mockito.doReturn(null).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString()))
				.thenReturn(new DatasetReference(BreedingViewImportServiceImplTest.NEW_MEANS_DATASET_ID,
						BreedingViewImportServiceImplTest.EMPTY_VALUE));

		Mockito.when(this.studyDataManager.getDataSet(BreedingViewImportServiceImplTest.NEW_MEANS_DATASET_ID))
				.thenReturn(this.createNewMeansDataSet());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.bvImportService.importMeansData(file, BreedingViewImportServiceImplTest.STUDY_ID);

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
		final Project p = this.createProjectWithCrop();
		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.SUMMARY_DATA);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());

		this.bvImportService.importMeansData(file, BreedingViewImportServiceImplTest.STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(0)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	@Test
	public void testImportMeansDataWithExistingMeansDataAndAdditionalTraits() throws Exception {
		final Project p = this.createProjectWithCrop();

		this.variateVariableTypes.add(this.createVariateVariableType("EXTRAIT"));
		final DataSet measurementDataSet = this.createMeasurementDataSet();
		final List<DataSet> plotDataDatasets = new ArrayList<>();
		plotDataDatasets.add(measurementDataSet);
		Mockito.doReturn(plotDataDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.PLOT_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.SUMMARY_DATA);

		// import with existing means data
		final List<DataSet> meansDataSets = new ArrayList<>();
		meansDataSets.add(this.createExistingMeansDataSet());
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.MEANS_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		Mockito.doNothing().when(this.studyDataManager).addDataSetVariableType(Matchers.anyInt(), Matchers.any(DMSVariableType.class));

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithAdditionalTraits.csv").toURI());

		this.bvImportService.importMeansData(file, BreedingViewImportServiceImplTest.STUDY_ID);

		Mockito.verify(this.studyDataManager, Mockito.times(1)).addDataSetVariableType(Matchers.anyInt(),
				Matchers.any(DMSVariableType.class));
		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	@Test
	public void testImportOutlierData() throws Exception {

		final List<Object[]> phenotypeIds = new ArrayList<>();
		phenotypeIds.add(new Object[] {"76373", "9999", "1"});

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());
		Mockito.when(this.studyDataManager.getPhenotypeIdsByLocationAndPlotNo(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(),
				Matchers.anyListOf(Integer.class))).thenReturn(phenotypeIds);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		this.bvImportService.importOutlierData(file, BreedingViewImportServiceImplTest.STUDY_ID);

		Mockito.verify(this.studyDataManager).saveOrUpdatePhenotypeOutliers(Matchers.anyListOf(PhenotypeOutlier.class));
	}

	@Test
	public void testImportSummaryStatsData() throws Exception {

		final List<DataSet> summaryDataDatasets = new ArrayList<>();
		summaryDataDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDataDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.createTrialEnvironments());

		final CVTermDao cvTermDao = Mockito.mock(CVTermDao.class);
		Mockito.doReturn(cvTermDao).when(this.ontologyDaoFactory).getCvTermDao();
		Mockito.doReturn(this.createCVTerm(888, "DUMMYTERM")).when(cvTermDao).getByNameAndCvId("DUMMYTERM", CvId.METHODS.getId());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		this.bvImportService.importSummaryStatsData(file, BreedingViewImportServiceImplTest.STUDY_ID);

		final ArgumentCaptor<DmsProject> datasetCaptor = ArgumentCaptor.forClass(DmsProject.class);
		final ArgumentCaptor<VariableTypeList> variableTypeListCaptor = ArgumentCaptor.forClass(VariableTypeList.class);
		Mockito.verify(this.studyDataManager).saveTrialDatasetSummary(datasetCaptor.capture(), variableTypeListCaptor.capture(),
				this.experimentValuesCaptor.capture(), Matchers.anyListOf(Integer.class));
		// Check arguments used when updating trial dataset
		Assert.assertEquals(Integer.valueOf(summaryDataDatasets.get(0).getId()), datasetCaptor.getValue().getProjectId());
		final int numberOfSummaryVariables = BreedingViewImportServiceImplTest.TRAITS.length * SummaryStatsCSV.SUMMARY_STATS_METHODS.length;
		Assert.assertEquals(numberOfSummaryVariables, variableTypeListCaptor.getValue().getVariableTypes().size());
		Assert.assertEquals(numberOfSummaryVariables, this.experimentValuesCaptor.getValue().size());

	}

	private DataSet createTrialDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(BreedingViewImportServiceImplTest.TRIAL_DATASET_ID);

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
		dataSet.setId(BreedingViewImportServiceImplTest.MEASUREMENT_DATASET_ID);

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
		dataSet.setId(BreedingViewImportServiceImplTest.NEW_MEANS_DATASET_ID);

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
		dataSet.setId(BreedingViewImportServiceImplTest.EXISTING_MEANS_DATASET_ID);

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
		method.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
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
		method.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
		method.setName(methodName);

		final Term scale = new Term();
		scale.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);

		final Term property = new Term();
		property.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
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

	private DMSVariableType createAnalysisSummaryVariableType(final String localName) {
		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.VARIATE);
		factorStandardVar.setName(localName);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		factor.setRole(factorStandardVar.getPhenotypicType());
		factor.setVariableType(VariableType.ANALYSIS_SUMMARY);
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
		method.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(BreedingViewImportServiceImplTest.EMPTY_VALUE);
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
		final Project p = this.createProjectWithCrop();
		// import with no existing means data
		Mockito.doReturn(null).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.MEANS_DATA);

		final List<DataSet> summaryDatasets = new ArrayList<>();
		summaryDatasets.add(this.createTrialDataSet());
		Mockito.doReturn(summaryDatasets).when(this.studyDataManager).getDataSetsByType(BreedingViewImportServiceImplTest.STUDY_ID,
				DataSetType.SUMMARY_DATA);

		Mockito.when(this.studyDataManager.addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString()))
				.thenReturn(new DatasetReference(BreedingViewImportServiceImplTest.NEW_MEANS_DATASET_ID,
						BreedingViewImportServiceImplTest.EMPTY_VALUE));

		Mockito.when(this.studyDataManager.getDataSet(BreedingViewImportServiceImplTest.NEW_MEANS_DATASET_ID))
				.thenReturn(this.createNewMeansDataSet());

		Mockito.doReturn(null).when(this.ontologyVariableDataManager).getWithFilter(Matchers.any(VariableFilter.class));
		Mockito.doNothing().when(this.ontologyVariableDataManager).addVariable(Matchers.any(OntologyVariableInfo.class));
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(p);

		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(this.trialEnvironments);

		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputDupeEntryNo.csv").toURI());

		this.bvImportService.importMeansData(file, BreedingViewImportServiceImplTest.STUDY_ID);

		Mockito.verify(this.studyDataManager).addDataSet(Matchers.anyInt(), (VariableTypeList) Matchers.anyObject(),
				(DatasetValues) Matchers.anyObject(), Matchers.anyString());

		Mockito.verify(this.studyDataManager).addOrUpdateExperiment(Matchers.anyInt(), Matchers.any(ExperimentType.class),
				Matchers.anyListOf(ExperimentValues.class), Matchers.anyString());

	}

	private Project createProjectWithCrop() {
		final Project p = new Project();
		final CropType c = new CropType();
		c.setPlotCodePrefix("CODE");
		p.setCropType(c);
		return p;
	}

	@Test
	public void testCreateAnalysisVariableNonExisting() {
		final boolean isSummaryVariable = false;

		final DMSVariableType originalVariableType = this.createVariateVariableType(BreedingViewImportServiceImplTest.TRAIT_ASI);
		final Term meansMethod = new Term();
		meansMethod.setId(BreedingViewImportServiceImplTest.LS_MEAN_ID);
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(),
				BreedingViewImportServiceImplTest.LS_MEAN_ID)).thenReturn(null);

		this.bvImportService.createAnalysisVariable(originalVariableType, BreedingViewImportServiceImplTest.ANALYSIS_VAR_NAME, meansMethod,
				BreedingViewImportServiceImplTest.PROGRAM_UUID, 1, isSummaryVariable);

		// Verify saving actions in Middleware
		final ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
		Mockito.verify(this.ontologyVariableDataManager).addVariable(infoArgument.capture());
		// anyInt is used as the 2nd argument since this represents the dynamic term ID that will be generated after saving the new variable
		// into the DB
		Mockito.verify(this.ontologyDataManager).addCvTermRelationship(Matchers.eq(originalVariableType.getId()), Matchers.anyInt(),
				Matchers.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));
		final OntologyVariableInfo argument = infoArgument.getValue();

		Assert.assertEquals("Unable to properly add a new analysis variable with the proper method ID",
				BreedingViewImportServiceImplTest.LS_MEAN_ID, argument.getMethodId());
		Assert.assertEquals("Unable to properly add a new analysis variable with the proper name",
				BreedingViewImportServiceImplTest.ANALYSIS_VAR_NAME, argument.getName());
		Assert.assertNotNull(argument.getVariableTypes());
		Assert.assertEquals("Expecting only one variable type for new analysis variable but had more than one.", 1,
				argument.getVariableTypes().size());
		Assert.assertEquals("Expecting analysis variable to have variable type 'Analysis' but did not.", VariableType.ANALYSIS,
				argument.getVariableTypes().iterator().next());
	}

	@Test
	public void testCreateAnalysisVariableExisting() {
		final boolean isSummaryVariable = false;
		final DMSVariableType originalVariableType = this.createVariateVariableType(BreedingViewImportServiceImplTest.TRAIT_ASI);
		final Term meansMethod = new Term();
		meansMethod.setId(BreedingViewImportServiceImplTest.LS_MEAN_ID);

		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(),
				BreedingViewImportServiceImplTest.LS_MEAN_ID)).thenReturn(BreedingViewImportServiceImplTest.TEST_ANALYSIS_VARIABLE_TERM_ID);

		this.bvImportService.createAnalysisVariable(originalVariableType, BreedingViewImportServiceImplTest.ANALYSIS_VAR_NAME, meansMethod,
				BreedingViewImportServiceImplTest.PROGRAM_UUID, 1, isSummaryVariable);

		Mockito.verify(this.ontologyVariableDataManager, Mockito.never()).addVariable(Matchers.any(OntologyVariableInfo.class));
		Mockito.verify(this.ontologyDataManager, Mockito.never()).addCvTermRelationship(Matchers.eq(originalVariableType.getId()),
				Matchers.anyInt(), Matchers.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));

	}

	@Test
	public void testCreateAnalysisSummaryVariableNonExisting() {
		final boolean isSummaryVariable = true;
		final String analysisSummaryVariableName = BreedingViewImportServiceImplTest.TRAIT_ASI + "_NumMissing";
		final Integer methodId = 4130;

		final DMSVariableType originalVariableType = this.createVariateVariableType(BreedingViewImportServiceImplTest.TRAIT_ASI);
		final Term meansMethod = new Term();
		meansMethod.setId(methodId);
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(originalVariableType.getId(), methodId)).thenReturn(null);

		this.bvImportService.createAnalysisVariable(originalVariableType, analysisSummaryVariableName, meansMethod,
				BreedingViewImportServiceImplTest.PROGRAM_UUID, 1, isSummaryVariable);

		// Verify saving actions in Middleware
		final ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
		Mockito.verify(this.ontologyVariableDataManager).addVariable(infoArgument.capture());
		// anyInt is used as the 2nd argument since this represents the dynamic term ID that will be generated after saving the new variable
		// into the DB
		Mockito.verify(this.ontologyDataManager).addCvTermRelationship(Matchers.eq(originalVariableType.getId()), Matchers.anyInt(),
				Matchers.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));
		final OntologyVariableInfo argument = infoArgument.getValue();

		Assert.assertEquals("Unable to properly add a new analysis summary variable with the proper method ID", methodId,
				argument.getMethodId());
		Assert.assertEquals("Unable to properly add a new analysis summary variable with the proper name", analysisSummaryVariableName,
				argument.getName());
		Assert.assertNotNull(argument.getVariableTypes());
		Assert.assertEquals("Expecting only one variable type for new analysis summary variable but had more than one.", 1,
				argument.getVariableTypes().size());
		Assert.assertEquals("Expecting analysis summary variable to have variable type 'Analysis Summary' but did not.",
				VariableType.ANALYSIS_SUMMARY, argument.getVariableTypes().iterator().next());
	}

	@Test
	public void testCreateMeansVariablesFromImportFile() {
		// Setup test data from file - 4 traits with 2 analysis variables each (_Means and _ErrorEstimate suffixes)
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
		for (final DMSVariableType analysisVar : this.meansVariateVariableTypes) {
			csvHeaders.add(analysisVar.getLocalName());
		}
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);

		// Method to test
		final String[] csvHeadersArray = csvHeaders.toArray(new String[csvHeaders.size()]);
		this.bvImportService.createMeansVariablesFromImportFileAndAddToList(csvHeadersArray, plotVariateList, meansVariableList,
				BreedingViewImportServiceImplTest.PROGRAM_UUID, this.meansCVTerm, false);

		// Expecting 1 analysis variable for each trait: <trait name>_Means
		final int newVariablesSize = BreedingViewImportServiceImplTest.TRAITS.length;
		Assert.assertEquals("Expecting " + newVariablesSize + " analysis variables to be added to means dataset variables.",
				oldVariableListSize + newVariablesSize, meansVariableList.size());
		for (final String traitName : BreedingViewImportServiceImplTest.TRAITS) {
			boolean isMeansVarFound = false;
			for (final DMSVariableType trait : meansVariableList.getVariates().getVariableTypes()) {
				final String meansVariableName = traitName + MeansCSV.MEANS_SUFFIX;
				if (meansVariableName.equals(trait.getLocalName())) {
					isMeansVarFound = true;
					continue;
				}
			}
			Assert.assertTrue("Expecting means analysis variable for " + traitName + " was added but was not.", isMeansVarFound);
		}
		// Check that new variables were added to have "Analysis" variable type
		final ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
		Mockito.verify(this.ontologyVariableDataManager, Mockito.times(newVariablesSize)).addVariable(infoArgument.capture());
		for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()) {
			Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
			Assert.assertEquals("Expecting 'Analysis' as sole variable type for new variable.", VariableType.ANALYSIS,
					variableInfo.getVariableTypes().iterator().next());
		}
	}

	@Test
	public void testAppendVariableTypesToExistingMeans() {
		// Setup test data from file - 4 traits with 2 analysis variables each (_Means and _ErrorEstimate suffixes)
		final String[] prevAnalyzedTraits = {BreedingViewImportServiceImplTest.TRAIT_ASI, BreedingViewImportServiceImplTest.TRAIT_EPH};
		final VariableTypeList meansVariableList = new VariableTypeList();
		final List<String> csvHeaders = new ArrayList<String>();
		for (final DMSVariableType factor : this.factorVariableTypes) {
			meansVariableList.add(factor);
			csvHeaders.add(factor.getLocalName());
		}
		// Assuming there is existing means dataset with 2 traits previously analyzed
		for (final String traitName : prevAnalyzedTraits) {
			meansVariableList.add(this.createVariateVariableType(traitName + MeansCSV.MEANS_SUFFIX, "", "", "LS MEAN"));
		}
		final int oldVariableListSize = meansVariableList.size();

		final DataSet plotDataSet = new DataSet();
		plotDataSet.setId(BreedingViewImportServiceImplTest.MEASUREMENT_DATASET_ID);
		final VariableTypeList plotVariateList = new VariableTypeList();
		for (final DMSVariableType trait : this.variateVariableTypes) {
			plotVariateList.add(trait);
		}
		plotDataSet.setVariableTypes(plotVariateList);

		final DataSet meansDataSet = new DataSet();
		meansDataSet.setId(BreedingViewImportServiceImplTest.EXISTING_MEANS_DATASET_ID);
		for (final DMSVariableType analysisVar : this.meansVariateVariableTypes) {
			csvHeaders.add(analysisVar.getLocalName());
		}
		meansDataSet.setVariableTypes(meansVariableList);
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);

		// Method to test
		final String[] csvHeadersArray = csvHeaders.toArray(new String[csvHeaders.size()]);
		this.bvImportService.appendVariableTypesToExistingMeans(csvHeadersArray, plotDataSet, meansDataSet,
				BreedingViewImportServiceImplTest.PROGRAM_UUID, this.meansCVTerm, false);

		// Expecting 1 analysis variable for each unanalyzed trait: <trait name>_Means
		final int newVariablesSize = BreedingViewImportServiceImplTest.TRAITS.length - prevAnalyzedTraits.length;
		Assert.assertEquals("Expecting " + newVariablesSize + " analysis variables to be added to means dataset variables.",
				oldVariableListSize + newVariablesSize, meansVariableList.size());
		for (final String traitName : BreedingViewImportServiceImplTest.TRAITS) {
			boolean isMeansVarFound = false;
			for (final DMSVariableType trait : meansVariableList.getVariates().getVariableTypes()) {
				final String meansVariableName = traitName + MeansCSV.MEANS_SUFFIX;
				if (meansVariableName.equals(trait.getLocalName())) {
					isMeansVarFound = true;
					continue;
				}
			}
			Assert.assertTrue("Expecting means analysis variable for " + traitName + " but was not found.", isMeansVarFound);
		}
		final ArgumentCaptor<Integer> datasetIdArgument = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(this.studyDataManager, Mockito.times(newVariablesSize)).addDataSetVariableType(datasetIdArgument.capture(),
				Matchers.any(DMSVariableType.class));
		Assert.assertEquals("Expecting correct ID to be used for adding new variables to means dataset.",
				BreedingViewImportServiceImplTest.EXISTING_MEANS_DATASET_ID, datasetIdArgument.getValue().intValue());

		// Check that new variables were added to have "Analysis" variable type
		final ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
		Mockito.verify(this.ontologyVariableDataManager, Mockito.times(newVariablesSize)).addVariable(infoArgument.capture());
		final List<String> prevAnalyzedTraitsList = Arrays.asList(prevAnalyzedTraits);
		for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()) {
			Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
			Assert.assertEquals("Expecting 'Analysis' as sole variable type for new variable.", VariableType.ANALYSIS,
					variableInfo.getVariableTypes().iterator().next());
			final String analysisVariableName = variableInfo.getName();
			final String sourceTraitName = analysisVariableName.substring(0, analysisVariableName.lastIndexOf("_"));
			Assert.assertFalse("Expecting analysis variables are not added for previously analyzed traits.",
					prevAnalyzedTraitsList.contains(sourceTraitName));
		}
	}

	@Test
	public void testCreateSummaryStatsVariableTypes() throws IOException {
		this.setUpSummaryStatsData();

		final DataSet trialDataSet = new DataSet();
		trialDataSet.setId(BreedingViewImportServiceImplTest.MEASUREMENT_DATASET_ID);
		final VariableTypeList trialVariablesList = new VariableTypeList();
		trialVariablesList.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		trialDataSet.setVariableTypes(trialVariablesList);

		final VariableTypeList plotVariateList = new VariableTypeList();
		final Map<String, String> traitAliasMap = new HashMap<>();
		for (final DMSVariableType trait : this.variateVariableTypes) {
			plotVariateList.add(trait);
			traitAliasMap.put(trait.getLocalName(), trait.getLocalName());
		}
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);

		// Method to test
		final VariableTypeList summaryStatVariables = this.bvImportService.createSummaryStatsVariableTypes(this.summaryStatsCSV,
				trialDataSet, plotVariateList, traitAliasMap, BreedingViewImportServiceImplTest.PROGRAM_UUID);

		// Expecting one analysis summary variable per summary statistic method for each trait
		final List<String> expectedSummaryVariableNames = new ArrayList<>();
		for (final String trait : BreedingViewImportServiceImplTest.TRAITS) {
			for (final String method : this.summaryStatsCSV.getSummaryHeaders()) {
				expectedSummaryVariableNames.add(trait + "_" + method);
			}
		}
		Assert.assertEquals("Expecting " + this.summaryStatsCSV.getSummaryHeaders().size() + " summary statistics variables per trait.",
				expectedSummaryVariableNames.size(), summaryStatVariables.size());

		// Check that variable type "Analysis Summary" was used for new summary statistic variables
		for (final DMSVariableType variable : summaryStatVariables.getVariableTypes()) {
			Assert.assertEquals("Expecting 'Analysis Summary' variable type for added variable.", VariableType.ANALYSIS_SUMMARY,
					variable.getVariableType());

		}
		final ArgumentCaptor<OntologyVariableInfo> infoArgument = ArgumentCaptor.forClass(OntologyVariableInfo.class);
		Mockito.verify(this.ontologyVariableDataManager, Mockito.times(expectedSummaryVariableNames.size()))
				.addVariable(infoArgument.capture());
		for (final OntologyVariableInfo variableInfo : infoArgument.getAllValues()) {
			Assert.assertTrue(variableInfo.getVariableTypes().size() == 1);
			Assert.assertEquals("Expecting 'Analysis Summary' as sole variable type for new variable.", VariableType.ANALYSIS_SUMMARY,
					variableInfo.getVariableTypes().iterator().next());
		}

	}

	@Test
	public void testCreateSummaryStatsVariableTypesExistingAlreadyInTrialDataset() throws IOException {
		this.setUpSummaryStatsData();

		final VariableTypeList plotVariateList = new VariableTypeList();
		final Map<String, String> traitAliasMap = new HashMap<>();
		for (final DMSVariableType trait : this.variateVariableTypes) {
			plotVariateList.add(trait);
			traitAliasMap.put(trait.getLocalName(), trait.getLocalName());
		}

		final DataSet trialDataSet = new DataSet();
		trialDataSet.setId(BreedingViewImportServiceImplTest.MEASUREMENT_DATASET_ID);
		final VariableTypeList trialVariablesList = new VariableTypeList();
		trialVariablesList.add(this.createTrialEnvironmentVariableType("TRIAL_INSTANCE"));
		// Add summary analysis variables for all traits in trial dataset so there will be none left to create
		for (final String trait : traitAliasMap.keySet()) {
			for (final String summaryHeader : this.summaryStatsCSV.getSummaryHeaders()) {
				final String varName = trait + "_" + summaryHeader;
				trialVariablesList.add(this.createAnalysisSummaryVariableType(varName));
			}
		}
		trialDataSet.setVariableTypes(trialVariablesList);
		Mockito.when(this.ontologyDataManager.retrieveDerivedAnalysisVariable(Matchers.anyInt(), Matchers.anyInt())).thenReturn(1);

		// Method to test
		final VariableTypeList summaryStatVariables = this.bvImportService.createSummaryStatsVariableTypes(this.summaryStatsCSV,
				trialDataSet, plotVariateList, traitAliasMap, BreedingViewImportServiceImplTest.PROGRAM_UUID);

		// Check that no new summary variables saved and returned
		Assert.assertEquals("Expecting no new summary statistics variables created.", 0, summaryStatVariables.size());
		Mockito.verify(this.ontologyVariableDataManager, Mockito.never()).addVariable(Matchers.any(OntologyVariableInfo.class));
		Mockito.verify(this.ontologyDataManager, Mockito.never()).addCvTermRelationship(Matchers.anyInt(), Matchers.anyInt(),
				Matchers.eq(TermId.HAS_ANALYSIS_VARIABLE.getId()));

	}

	private void setUpSummaryStatsData() throws IOException {
		for (final String trait : BreedingViewImportServiceImplTest.TRAITS) {
			for (final String summaryHeader : SummaryStatsCSV.SUMMARY_STATS_METHODS) {
				this.summaryVariableTypes.add(this.createVariateVariableType(trait + "_" + summaryHeader));
			}
		}

		final Map<String, Map<String, List<String>>> data = SummaryStatsTestDataInitializer.generateData();
		final List<String> summaryHeaders = Arrays.asList(SummaryStatsCSV.SUMMARY_STATS_METHODS);
		Mockito.when(this.summaryStatsCSV.getSummaryHeaders()).thenReturn(summaryHeaders);
		Mockito.when(this.summaryStatsCSV.getData()).thenReturn(data);
	}

	@Test
	public void testCreateSummaryStatsExperimentValuesList() throws IOException {
		this.setUpSummaryStatsData();
		final Map<String, Map<String, List<String>>> data = this.summaryStatsCSV.getData();
		final List<String> summaryHeaders = this.summaryStatsCSV.getSummaryHeaders();

		final DataSet dataSet = new DataSet();
		final VariableTypeList varTypeList = new VariableTypeList();
		varTypeList.setVariableTypes(this.summaryVariableTypes);
		dataSet.setVariableTypes(varTypeList);

		final Map<String, Integer> locationNameToIdMap = new LinkedHashMap<>();
		for (final String environment : data.keySet()) {
			locationNameToIdMap.put(environment, Integer.valueOf(environment));
		}

		// Method to test
		final List<ExperimentValues> experimentValues =
				this.bvImportService.createSummaryStatsExperimentValuesList(dataSet, locationNameToIdMap, this.summaryStatsCSV);

		// Check the count and value of generated ExperimentValues list
		final int numberOfEnvironments = data.keySet().size();
		Assert.assertEquals(numberOfEnvironments * summaryHeaders.size() * SummaryStatsTestDataInitializer.TRAITS_LIST.size(),
				experimentValues.size());
		final Iterator<ExperimentValues> actualValuesIterator = experimentValues.iterator();
		for (final String environmentName : data.keySet()) {
			for (int i = 1; i <= summaryHeaders.size(); i++) {
				for (int j = 0; j < SummaryStatsTestDataInitializer.TRAITS_LIST.size(); j++) {
					final String trait = SummaryStatsTestDataInitializer.TRAITS_LIST.get(j);
					final String summaryVariableName = trait + "_" + summaryHeaders.get(i - 1);
					final Integer value = Integer.valueOf(environmentName) * 10 + i;

					final ExperimentValues experimentValue = actualValuesIterator.next();
					Assert.assertEquals(locationNameToIdMap.get(environmentName), experimentValue.getLocationId());
					final Variable variable = experimentValue.getVariableList().getVariables().get(0);
					Assert.assertEquals(summaryVariableName, variable.getVariableType().getLocalName());
					Assert.assertEquals(value.toString() + "." + j, variable.getActualValue());
				}
			}
		}

	}

}
