package org.drugis.addis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

public class MainData extends ExampleData {
	
	private static BasicStudy s_studySechter;
	private static BasicStudy s_studyNewhouse;
	private static BasicStudy s_studyFava02;
	private static BasicStudy s_studyBoyer;
	
	private static BasicStudy s_studyOrg022;
	private static BasicStudy s_studyOrg023;
	private static Drug s_remeron;
	private static Drug s_amitriptyline;
	private static Drug s_trazodone;
	
	public static void initDefaultData(Domain domain) {
		ExampleData.initDefaultData(domain);
		//studies testdata:
		domain.addStudy(buildStudyBoyer1998());
		domain.addStudy(buildStudyFava2002());
		domain.addStudy(buildStudyNewhouse2000());
		domain.addStudy(buildStudySechter1999());
		domain.addStudy(buildStudyOrg022());
		domain.addStudy(buildStudyOrg023());
		
		try {
			domain.addMetaAnalysis(buildMetaHansen2005());
		}catch (EntityIdExistsException e) {
			e.printStackTrace();
		}
		
	}

	public static RandomEffectsMetaAnalysis buildMetaHansen2005() {
		
		List<Study> studylist = new ArrayList<Study>();
		
		studylist.add(buildStudyBennie());
		studylist.add(buildStudyBoyer1998());
		studylist.add(buildStudyFava2002());
		studylist.add(buildStudyNewhouse2000());
		studylist.add(buildStudySechter1999());
		
		return new RandomEffectsMetaAnalysis("Hansen et al, 2005", buildEndpointHamd(), studylist, buildDrugFluoxetine(), buildDrugSertraline());
	}
	
	public static BasicStudy buildStudyFava2002() {
		if (s_studyFava02 == null) {
			s_studyFava02 = realBuildStudyFava02();
		}
	
		return s_studyFava02;
	}

	private static BasicStudy realBuildStudyFava02() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Fava et al, 2002", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 96);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(70);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 92);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(57);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudyNewhouse2000() {
		if (s_studyNewhouse == null) {
			s_studyNewhouse = realBuildStudyNewhouse();
		}
	
		return s_studyNewhouse;
	}

	private static BasicStudy realBuildStudyNewhouse() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Newhouse et al, 2000", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 117);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(85);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 119);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(84);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudySechter1999() {
		if (s_studySechter == null) {
			s_studySechter = realBuildStudySechter();
		}
	
		return s_studySechter;
	}

	private static BasicStudy realBuildStudySechter() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Sechter et al, 1999", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 118);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(86);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 120);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(76);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudyBoyer1998() {
		if (s_studyBoyer == null){ 
			s_studyBoyer = realBuildStudyBoyer();
		}
	
		return s_studyBoyer;
	}

	private static BasicStudy realBuildStudyBoyer() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Boyer et al, 1998", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 122);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(63);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 120);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(61);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudyOrg022() {
		if (s_studyOrg022 == null){ 
			s_studyOrg022 = realbuildStudyOrg022();
		}
	
		return s_studyOrg022;
	}

	public static BasicStudy realbuildStudyOrg022() {
		Endpoint cgis = buildEndpointCgi();
		Drug amitriptyline = buildDrugAmitriptyline();
		Drug remeron = buildDrugRemeron();
		Drug placebo = buildPlacebo();
		
		BasicStudy study = new BasicStudy("Organon 003-022, 1990", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(cgis));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 3);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		study.setCharacteristic(StudyCharacteristic.STUDY_END, new GregorianCalendar(1990, 1, 26).getTime());
		
		// Remeron data
		Dose dose = new Dose(20.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup remr = new BasicPatientGroup(study, remeron, dose, 47);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(remr);
		pCgi.setMean(1.94);
		pCgi.setStdDev(0.23);
		study.addPatientGroup(remr);
		study.setMeasurement(cgis, remr, pCgi);

		// Amitriptyline data
		dose = new Dose(150.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup amit = new BasicPatientGroup(study, amitriptyline, dose, 47);
		pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(amit);
		pCgi.setMean(1.57);
		pCgi.setStdDev(0.19);
		study.addPatientGroup(amit);
		study.setMeasurement(cgis, amit, pCgi);
		
		// Placebo data
		dose = new Dose(0.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup plac = new BasicPatientGroup(study, placebo, dose, 48);
		pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(plac);
		pCgi.setMean(.85);
		pCgi.setStdDev(.19);
		study.addPatientGroup(plac);
		study.setMeasurement(cgis, plac, pCgi);
		
		return study;
	}
	
	public static BasicStudy buildStudyOrg023() {
		if (s_studyOrg023 == null){ 
			s_studyOrg023 = realbuildStudyOrg023();
		}
	
		return s_studyOrg023;
	}

	public static BasicStudy realbuildStudyOrg023() {
		Endpoint cgis = buildEndpointCgi();
		Drug trazodone = buildDrugTrazodone();
		Drug remeron = buildDrugRemeron();
		Drug placebo = buildPlacebo();
		
		BasicStudy study = new BasicStudy("Organon 003-023, 1992", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(cgis));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 3);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		study.setCharacteristic(StudyCharacteristic.STUDY_END, new GregorianCalendar(1992, 4, 1).getTime());
		
		// Remeron data
		Dose dose = new Dose(20.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup remr = new BasicPatientGroup(study, remeron, dose, 49);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(remr);
		pCgi.setMean(.8);
		pCgi.setStdDev(0.18);
		study.addPatientGroup(remr);
		study.setMeasurement(cgis, remr, pCgi);

		// Trazodone data
		dose = new Dose(160.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup traz = new BasicPatientGroup(study, trazodone, dose, 48);
		pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(traz);
		pCgi.setMean(.62);
		pCgi.setStdDev(0.16);
		study.addPatientGroup(traz);
		study.setMeasurement(cgis, traz, pCgi);
		
		// Placebo data
		dose = new Dose(0.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup plac = new BasicPatientGroup(study, placebo, dose, 49);
		pCgi = (BasicContinuousMeasurement)cgis.buildMeasurement(plac);
		pCgi.setMean(.59);
		pCgi.setStdDev(.16);
		study.addPatientGroup(plac);
		study.setMeasurement(cgis, plac, pCgi);
		
		return study;
	}

	public static Drug buildDrugRemeron() {
		if (s_remeron == null)
			s_remeron = new Drug("Remeron", "NO6AX11");
		
		return s_remeron;
	}

	public static Drug buildDrugAmitriptyline() {
		if (s_amitriptyline == null)
			s_amitriptyline = new Drug("Amitriptyline", "N06AA09");
		
		return s_amitriptyline;
	}
	
	public static Drug buildDrugTrazodone() {
		if (s_trazodone == null)
			s_trazodone = new Drug("Trazodone", "N06AX05");
		
		return s_trazodone;
	}
}