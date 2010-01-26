package org.drugis.addis.imports;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.OutcomeMeasure.Type;



public class ClinicaltrialsImporter {
	
	public static Study getClinicaltrialsData(String url) throws MalformedURLException, IOException {
		Study study = new Study("", new Indication(0l, ""));
		getClinicaltrialsData(study ,url);
		return study;
	}
	
	public static void getClinicaltrialsData(Study study, String url) throws IOException, MalformedURLException{
		URL updateWebService;
		
		try {
			updateWebService = new URL(url);
			URLConnection conn = updateWebService.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			
			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports"); //
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(isr);
			getClinicalTrialsData(study,studyImport);
			isr.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void getClinicaltrialsData(Study study, File file){
		try {
			JAXBContext jc = JAXBContext.newInstance("org.drugis.addis.imports"); //
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ClinicalStudy studyImport = (ClinicalStudy) unmarshaller.unmarshal(file);// the .xml file to be read
			getClinicalTrialsData(study,studyImport);
		} 
		catch (JAXBException e){
			System.out.println("Error in parsing xml file (ClinicaltrialsImporter.java))");
			e.printStackTrace();
		} 
	}
	
	private static void getClinicalTrialsData(Study study, ClinicalStudy studyImport) {
		// ID  (& ID note =study url)
		study.setId(studyImport.getIdInfo().getNctId());
		study.putNote(Study.PROPERTY_ID, new Note(studyImport.getIdInfo().getOrgStudyId()));
		
		// Title
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, studyImport.getBriefTitle());
		study.putNote(BasicStudyCharacteristic.TITLE, new Note(studyImport.getOfficialTitle()));
		
		// Study Centers
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, studyImport.getLocation().size());
		String noteStr = "";
		for (Location l : studyImport.getLocation()) {
			noteStr += l.getFacility().getName()+"\n";
		}
		study.putNote(BasicStudyCharacteristic.CENTERS, new Note());
		
		// Randomization
		if (designContains(studyImport, "non-randomized")) 
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.NONRANDOMIZED);
		else if (designContains(studyImport, "randomized"))
			study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.putNote(BasicStudyCharacteristic.ALLOCATION, new Note(studyImport.getStudyDesign()));
		
		// Blinding
		if (designContains(studyImport, "open label"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.OPEN);
		else if (designContains(studyImport, "single blind"))    
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.SINGLE_BLIND);
		else if (designContains(studyImport, "double blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		else if (designContains(studyImport, "triple blind"))
			study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.TRIPLE_BLIND);
		study.putNote(BasicStudyCharacteristic.BLINDING, new Note(studyImport.getStudyDesign()));
		
		// Objective
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, studyImport.getBriefSummary().getTextblock());
		study.putNote(BasicStudyCharacteristic.OBJECTIVE, new Note(studyImport.getBriefSummary().getTextblock()));
		
		// Indication // FIXME: Print label instead of setting it.
		study.setIndication(new Indication(0l, studyImport.getCondition().get(0)) ) ;
		
		String out = "";
		for(String s : studyImport.getCondition()){
			out = out+s+"\n";
		}
		study.putNote(Study.PROPERTY_INDICATION, new Note(out));

		// Dates
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			if (studyImport.startDate != null)
					study.setCharacteristic(BasicStudyCharacteristic.STUDY_START, sdf.parse(studyImport.startDate));
			if (studyImport.endDate != null)
					study.setCharacteristic(BasicStudyCharacteristic.STUDY_END, sdf.parse(studyImport.endDate));
		} catch (ParseException e) {
			System.err.println("ClinicalTrialsImporter:: Couldn't parse date. Left empty.");
		}
		study.putNote((Object)BasicStudyCharacteristic.STUDY_START, new Note(studyImport.startDate));
		study.putNote((Object)BasicStudyCharacteristic.STUDY_END,   new Note(studyImport.endDate));
		
		// Import date & Source.
		study.setCharacteristic(BasicStudyCharacteristic.CREATION_DATE,new Date());
		study.setCharacteristic(BasicStudyCharacteristic.SOURCE, BasicStudyCharacteristic.Source.CLINICALTRIALS);
		study.putNote((Object)BasicStudyCharacteristic.CREATION_DATE, new Note(studyImport.getRequiredHeader().getDownloadDate()));
		study.putNote((Object)BasicStudyCharacteristic.SOURCE, new Note(studyImport.getRequiredHeader().getUrl()));

		// Status
		if (studyImport.getOverallStatus().toLowerCase().contains("recruiting"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Enrolling"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.RECRUITING);
		else if (studyImport.getOverallStatus().contains("Active"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.ONGOING);
		else if (studyImport.getOverallStatus().contains("Completed"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.FINISHED);
		else if (studyImport.getOverallStatus().contains("Available"))
			study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.FINISHED);
		study.putNote((Object)BasicStudyCharacteristic.STATUS, new Note(studyImport.getOverallStatus()));
		
		
		// Inclusion + Exclusion criteria
		final String EXCLUSION_CRITERIA = "exclusion criteria";
		final String INCLUSION_CRITERIA = "inclusion criteria";
		String criteria = studyImport.getEligibility().getCriteria().getTextblock();
		int inclusionStart 	= criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) + INCLUSION_CRITERIA.length()+1;
		int inclusionEnd 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA);
		int exclusionStart 	= criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) + EXCLUSION_CRITERIA.length()+1;
		
		if(inclusionEnd == -1)
			inclusionEnd = criteria.length()-1; 
		
		if(criteria.toLowerCase().indexOf(INCLUSION_CRITERIA) != -1)
			study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,criteria.substring(inclusionStart, inclusionEnd));

		if(criteria.toLowerCase().indexOf(EXCLUSION_CRITERIA) != -1)
			study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,criteria.substring(exclusionStart));
		study.putNote((Object)BasicStudyCharacteristic.INCLUSION, new Note(criteria));
		study.putNote((Object)BasicStudyCharacteristic.EXCLUSION, new Note(criteria));
		
		// Add note to the study-arms.
		for(ArmGroup ag : studyImport.getArmGroup()){

			Arm arm = new Arm(new Drug("",""), new FixedDose(0,SIUnit.MILLIGRAMS_A_DAY),0);
			study.addArm(arm);
			noteStr = ag.getArmGroupType()+"\n"+ag.getDescription();
			study.putNote(arm, new Note(noteStr));
		}
		
		// Add a separate arm for all dangling interventions.
		Arm danglingInterventionsArm = new Arm(new Drug("",""), new FixedDose(0,SIUnit.MILLIGRAMS_A_DAY),0);
		study.addArm(danglingInterventionsArm);

		// Add note to the drugs within the study-arm.
		for(Intervention i : studyImport.getIntervention()){
			noteStr = i.getInterventionName()+"\n"+i.getInterventionType()+"\n"+i.getDescription()+"\n";
			for (String label : i.getArmGroupLabel()) {
				Arm arm = study.getArms().get(Integer.parseInt(label) -1 );
				study.putNote(arm.getDrug(), new Note(noteStr));
			}
			if (i.getArmGroupLabel().size() == 0)
				study.putNote(danglingInterventionsArm, new Note(noteStr));
		}

		// Outcome Measures
		for (PrimaryOutcome endp : studyImport.getPrimaryOutcome()) {
			Endpoint e = new Endpoint("", Type.RATE);
			study.addOutcomeMeasure(e);
			study.putNote(e, new Note(endp.getMeasure()));
		}
		for (SecondaryOutcome endp : studyImport.getSecondaryOutcome()) {
			Endpoint e = new Endpoint("", Type.RATE);
			study.addOutcomeMeasure(e);
			study.putNote(e, new Note(endp.getMeasure()));
		}
	}

	private static boolean designContains(ClinicalStudy studyImport, String contains) {
		return studyImport.getStudyDesign().toLowerCase().contains(contains) || studyImport.getStudyDesign().toLowerCase().contains(contains.replace(' ', '-'));
	}
}