
package org.generationcp.commons.service.impl;

import java.util.List;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Test;

public class LocationResolverTest {

	@Test
	public void testResolveForNurseryWithLocationVariableAndValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("MEX");

		workbook.setConditions(Lists.newArrayList(locationMV));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in Nursery settings.", "MEX",
				location);
	}

	@Test
	public void testResolveForNurseryWithLocationVariableButNoValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());

		workbook.setConditions(Lists.newArrayList(locationMV));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals("Location should be defaulted to an empty string when LOCATION_ABBR variable is present but has no value.", "",
				location);
	}

	@Test
	public void testLocationForNurseryWithoutLocationVariable() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals("Location should be defaulted to an empty string when LOCATION_ABBR variable is not present.", "", location);
	}

	@Test
	public void testResolveForNurseryWithLocationAbbreviationAndLocationName() {
		final String locationAbbr = "MEX";
		final String nurseryLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		instance1LocationAbbrMV.setValue(locationAbbr);

		MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		instance1LocationNameMV.setValue(nurseryLocation);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1LocationAbbrMV, instance1LocationNameMV, instance1MV));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in nursery settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForNurseryWithoutLocationVariable() {
		final String trialInstance = "1";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1MV));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals(
				"Location should be defaulted to TRIAL_INSTANCE when LOCATION_ABBR and LOCATION_NAME variables are not present in nursery settings.",
				trialInstance, location);
	}

	@Test
	public void testResolveForNurseryWithLocationName() {
		final String nurseryLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		instance1LocationNameMV.setValue(nurseryLocation);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");

		workbook.setConditions(Lists.newArrayList(instance1LocationNameMV, instance1MV));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, null, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_NAME variable value in nursery settings.",
				nurseryLocation,
				location);
	}

	@Test
	public void testResolveForTrialWithLocationVariableAndValue() {
		final String locationAbbr = "MEX";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue(locationAbbr);
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, instance1Measurements, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in environment level settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForTrialWithLocationVariableButNoValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		MeasurementData instance1LocationAbbrMD = new MeasurementData();
		// No value
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		List<MeasurementVariable> conditions = workbook.getConditions();
		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, trailInstanceObservation, studyType).resolve();
		Assert.assertEquals(
				"Location should be defaulted to an empty string when LOCATION_ABBR variable is present in environment level settings, but has no value.",
				"",
				location);
	}

	@Test
	public void testResolveForTrialWithLocationAbbreviationAndLocationName() {
		final String locationAbbr = "MEX";
		final String trialLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue(locationAbbr);
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		MeasurementData instance1LocationNameMD = new MeasurementData();
		instance1LocationNameMD.setValue(trialLocation);
		instance1LocationNameMD.setMeasurementVariable(instance1LocationNameMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationAbbrMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, instance1Measurements, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_ABBR variable value in environment level settings.",
				locationAbbr,
				location);
	}

	@Test
	public void testResolveForTrialWithoutLocationVariable() {
		final String trialInstance = "1";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue(trialInstance);
		instance1MD.setMeasurementVariable(instance1MV);
		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));
		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, instance1Measurements, studyType).resolve();
		Assert.assertEquals(
				"Location should be defaulted to TRIAL_INSTANCE when LOCATION_ABBR and LOCATION_NAME variables are not present in environment level settings.",
				trialInstance, location);
	}

	@Test
	public void testResolveForTrialWithLocationName() {
		final String trialLocation = "INTERNATIONAL FOOD POLICY RESEARCH INSTITUTE, WASHINGTON - (IFPRI)";

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationNameMV = new MeasurementVariable();
		instance1LocationNameMV.setTermId(TermId.TRIAL_LOCATION.getId());
		MeasurementData instance1LocationNameMD = new MeasurementData();
		instance1LocationNameMD.setValue(trialLocation);
		instance1LocationNameMD.setMeasurementVariable(instance1LocationNameMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1MD, instance1LocationNameMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		String location = new LocationResolver(conditions, instance1Measurements, studyType).resolve();
		Assert.assertEquals("Location should be resolved to the value of LOCATION_NAME variable value in environment level settings.",
				trialLocation,
				location);
	}

}
