/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.util;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.drugis.addis.util.JAXBConvertor.nameReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.BasicStudyCharacteristic.Status;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.data.ActivityUsedBy;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.Alternative;
import org.drugis.addis.entities.data.AlternativeDrugSets;
import org.drugis.addis.entities.data.AnalysisArms;
import org.drugis.addis.entities.data.AnalysisDrugs;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.ArmReferences;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.BenefitRiskAnalyses;
import org.drugis.addis.entities.data.CategoricalMeasurement;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.CategoryMeasurement;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.Epochs;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.MetaAnalyses;
import org.drugis.addis.entities.data.MetaAnalysisReferences;
import org.drugis.addis.entities.data.NameReferenceWithNotes;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.OutcomeMeasuresReferences;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.StudyActivities;
import org.drugis.addis.entities.data.StudyOutcomeMeasure;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.addis.entities.data.Treatment;
import org.drugis.addis.imports.PubMedDataBankRetriever;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.addis.util.JAXBHandler.XmlFormatType;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class JAXBConvertorTest {
	private static final String TEST_DATA_A_0 = "../testDataA-0.xml";
	private static final String TEST_DATA_3 = "../testData-3.addis";
	private static final String TEST_DATA_A_1 = "../testDataA-1.addis";

	private JAXBContext d_jaxb;
	private Unmarshaller d_unmarshaller;
	
	@Before
	public void setup() throws JAXBException{
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data" );
		d_unmarshaller = d_jaxb.createUnmarshaller();
	}
	
	@Test
	public void testConvertContinuousEndpoint() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";
		Direction dir = Direction.LOWER_IS_BETTER;
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setDirection(dir);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		Endpoint e = new Endpoint(name, Endpoint.convertVarType(Type.CONTINUOUS), dir);
		e.setDescription(desc);
		((ContinuousVariableType) e.getVariableType()).setUnitOfMeasurement(unit);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		assertEquals(m, JAXBConvertor.convertEndpoint(e));
	}
	
	@Test
	public void testConvertRateEndpoint() throws ConversionException {
		String name = "Efficacy";
		String desc = "Erection of >= 60% rigidity within 1 hr of medication";
		Direction dir = Direction.HIGHER_IS_BETTER;
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		value.setDirection(dir);
		m.setRate(value);
		
		Endpoint e = new Endpoint(name, Endpoint.convertVarType(Type.RATE), dir);
		e.setDescription(desc);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		
		value.setDirection(Direction.LOWER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.LOWER_IS_BETTER);
		
		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		assertEquals(m, JAXBConvertor.convertEndpoint(e));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertCategoricalEndpointThrows() throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Gender");
		m.setDescription("Which gender you turn out to be after taking medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);
		
		JAXBConvertor.convertEndpoint(m);
	}

	@Test
	public void testConvertContinuousAdverseEvent() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setDirection(Direction.LOWER_IS_BETTER);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		org.drugis.addis.entities.OutcomeMeasure e = new AdverseEvent(name, AdverseEvent.convertVarType(Type.CONTINUOUS));
		e.setDescription(desc);
		((ContinuousVariableType) e.getVariableType()).setUnitOfMeasurement(unit);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		assertEquals(m, JAXBConvertor.convertAdverseEvent(e));
	}
	
	@Test
	public void testConvertRateAdverseEvent() throws ConversionException {
		String name = "Seizure";
		String desc = "Its bad hmmkay";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		value.setDirection(Direction.LOWER_IS_BETTER);
		m.setRate(value);
		
		AdverseEvent e = new AdverseEvent(name, AdverseEvent.convertVarType(Type.RATE));
		e.setDescription(desc);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		
		value.setDirection(Direction.HIGHER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.HIGHER_IS_BETTER);
		
		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		assertEquals(m, JAXBConvertor.convertAdverseEvent(e));
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertCategoricalAdverseEventThrows() throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Efficacy");
		m.setDescription("Erection of >= 60% rigidity within 1 hr of medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);
		
		JAXBConvertor.convertAdverseEvent(m);
	}

	@Test
	public void testConvertContinuousPopChar() throws ConversionException {
		String name = "Age";
		String desc = "Age (years since birth)";
		String unit = "Years";
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);
		
		PopulationCharacteristic p = new PopulationCharacteristic(name, new ContinuousVariableType());
		((ContinuousVariableType) p.getVariableType()).setUnitOfMeasurement(unit);
		p.setDescription(desc);
		
		assertEntityEquals(p, JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(p));
	}
	
	@Test
	public void testConvertRatePopChar() throws ConversionException {
		String name = "Seizure";
		String description = "Its bad hmmkay";
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(description);
		m.setRate(new RateVariable());
		
		PopulationCharacteristic p = new PopulationCharacteristic(name, new RateVariableType());
		p.setDescription(description);
		
		assertEntityEquals(p, JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(p));
	}
	
	@Test
	public void testConvertCategoricalPopChar() throws ConversionException {
		String name = "Smoking habits";
		String desc = "Classification of smoking habits";
		String[] categories = new String[] {"Non-smoker", "Smoker", "Ex-smoker"};
		
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		CategoricalVariable var = new CategoricalVariable();
		for (String s : categories) {
			var.getCategory().add(s);
		}
		m.setCategorical(var);
		
		PopulationCharacteristic catChar = new PopulationCharacteristic(name, new CategoricalVariableType(Arrays.asList(categories)));
		catChar.setDescription(desc);
		
		assertEntityEquals(catChar, JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(catChar));
	}

	@Test
	public void testConvertIndication() {
		String name = "Erectile Dysfunction";
		long code = 12;
		
		org.drugis.addis.entities.data.Indication i1 = new org.drugis.addis.entities.data.Indication(); 
		i1.setCode(code);
		i1.setName(name);
	
		Indication i2 = new Indication(code, name);
		
		assertEntityEquals(i2, JAXBConvertor.convertIndication(i1));
		assertEquals(i1, JAXBConvertor.convertIndication(i2));
	}
	
	@Test
	public void testConvertDrug() {
		String name = "Sildenafil";
		String code = "G04BE03";
		
		org.drugis.addis.entities.data.Drug d1 = new org.drugis.addis.entities.data.Drug(); 
		d1.setAtcCode(code);
		d1.setName(name);
	
		Drug d2 = new Drug(name, code);
		
		assertEntityEquals(d2, JAXBConvertor.convertDrug(d1));
		assertEquals(d1, JAXBConvertor.convertDrug(d2));
	}
	
	@Test
	public void testConvertArm() throws ConversionException {
		int size = 99;
		String name = "Sildenafil";

		
		org.drugis.addis.entities.data.Arm arm1 = new org.drugis.addis.entities.data.Arm();
		arm1.setName(name + "-12");
		arm1.setSize(size);
		Notes armNotes = new Notes();
		Note note = new Note(Source.CLINICALTRIALS, "This is an arm note content");
		armNotes.getNote().add(JAXBConvertor.convertNote(note));
		arm1.setNotes(armNotes);
		
//		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
//		fixDose.setQuantity(quantity);
//		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
//		arm1.setFixedDose(fixDose);
//		arm1.setDrug(nameReference(name));
		
		Arm arm2 = new Arm(name + "-12", size);
		arm2.getNotes().add(note);
		
		assertEntityEquals(arm2, JAXBConvertor.convertArm(arm1));
		assertEquals(arm1, JAXBConvertor.convertArm(arm2));
		
//		arm1.setFixedDose(null);
//		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
//		flexDose.setMinDose(quantity);
//		flexDose.setMaxDose(maxQuantity);
//		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
//		arm1.setFlexibleDose(flexDose);
//		
//		Arm arm3 = buildFlexibleDoseArm(size, drug, 12, quantity, maxQuantity);
//		arm3.getNotes().add(note);
//		arm1.setId(12);
//		assertEntityEquals(arm3, JAXBConvertor.convertArm(arm1, domain));
//		arm1.setId(null);
//		assertEquals(arm1, JAXBConvertor.convertArm(arm3));
	}
	
	@Test
	public void testConvertTreatmentActivity() throws ConversionException {
		String name = "Sildenafil";
		String code = "G04BE03";
		double quantity = 12.5;
		double maxQuantity = 34.5;
		
		Domain domain = new DomainImpl();
		Drug drug = new Drug(name, code);
		domain.addDrug(drug);

		// fixdose part
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);		
		
		org.drugis.addis.entities.data.DrugTreatment dt = new org.drugis.addis.entities.data.DrugTreatment();
		dt.setDrug(nameReference(name));
		dt.setFixedDose(fixDose);		
		TreatmentActivity ta = buildFixedDoseTreatmentActivity(drug, quantity);
		
		assertTrue(EntityUtil.deepEqual(ta, JAXBConvertor.convertTreatmentActivity(wrapTreatment(dt), domain)));
		assertEquals(wrapTreatment(dt), JAXBConvertor.convertActivity(ta).getTreatment());		

		// flexdose part
		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
		flexDose.setMinDose(quantity);
		flexDose.setMaxDose(maxQuantity);
		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);

		org.drugis.addis.entities.data.DrugTreatment dt2 = new org.drugis.addis.entities.data.DrugTreatment();
		dt2.setDrug(nameReference(name));
		dt2.setFlexibleDose(flexDose);	
		TreatmentActivity ta2 = buildFlexibleDoseTreatmentActivity(drug, quantity, maxQuantity);
		assertTrue(EntityUtil.deepEqual(ta2, JAXBConvertor.convertTreatmentActivity(wrapTreatment(dt2), domain)));
		assertEquals(wrapTreatment(dt2), JAXBConvertor.convertActivity(ta2).getTreatment());

	}

	private static Treatment wrapTreatment(org.drugis.addis.entities.data.DrugTreatment dt2) {
		Treatment t2 = new org.drugis.addis.entities.data.Treatment();
		t2.getDrugTreatment().add(dt2);
		return t2;
	}

	@Test
	public void testConvertEpoch() throws DatatypeConfigurationException {
		String name = "Randomization";
		Duration duration = DatatypeFactory.newInstance().newDuration("P42D");
		
		Epoch e = new Epoch(name, duration);
		org.drugis.addis.entities.data.Epoch de = new org.drugis.addis.entities.data.Epoch();
		de.setName(name);
		de.setDuration(duration);
		de.setNotes(new Notes());
		
		assertEquals(de, JAXBConvertor.convertEpoch(e));
		assertTrue(EntityUtil.deepEqual(e, JAXBConvertor.convertEpoch(de)));
	}
	
	@Test
	public void testConvertStudyActivity() throws ConversionException, DatatypeConfigurationException {
		String drugName1 = "Sildenafil";
		String drugName2 = "Paroxeflox";
		String code1 = "G04BE03";
		String code2 = "G04BE04";
		double quantity = 12.5;
		
		Domain domain = new DomainImpl();
		Drug drug1 = new Drug(drugName1, code1);
		Drug drug2 = new Drug(drugName2, code2);
		domain.addDrug(drug1);
		domain.addDrug(drug2);
		
		// test with predefined activity
		String activityName = "Randomization";
		PredefinedActivity activity = PredefinedActivity.RANDOMIZATION;
		StudyActivity sa = new StudyActivity(activityName, activity);
		
		org.drugis.addis.entities.data.StudyActivity saData = new org.drugis.addis.entities.data.StudyActivity();
		org.drugis.addis.entities.data.Activity activData = new org.drugis.addis.entities.data.Activity();
		activData.setPredefined(activity);
		saData.setName(activityName);
		saData.setActivity(activData);
		saData.setNotes(new Notes());

		assertTrue(EntityUtil.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData, new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));
		
		// test with treatmentactivity
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);		
		org.drugis.addis.entities.data.DrugTreatment t = new org.drugis.addis.entities.data.DrugTreatment();
		t.setDrug(nameReference(drugName1));
		t.setFixedDose(fixDose);		
		TreatmentActivity ta1 = buildFixedDoseTreatmentActivity(drug1, quantity);
		
		sa = new StudyActivity(activityName, ta1);
		saData.getActivity().setPredefined(null);
		saData.getActivity().setTreatment(wrapTreatment(t));

		assertTrue(EntityUtil.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData, new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));
		
		// test with Combination Treatment
		org.drugis.addis.entities.data.DrugTreatment t2 = new org.drugis.addis.entities.data.DrugTreatment();
		t2.setDrug(nameReference(drugName2));
		t2.setFixedDose(fixDose);
		org.drugis.addis.entities.data.Treatment ctData = new org.drugis.addis.entities.data.Treatment();
		ctData.getDrugTreatment().add(t);
		ctData.getDrugTreatment().add(t2);
		
		TreatmentActivity ct = new TreatmentActivity();
		ct.getTreatments().add(JAXBConvertor.convertDrugTreatment(t, domain));
		ct.getTreatments().add(JAXBConvertor.convertDrugTreatment(t2, domain));
		sa = new StudyActivity(activityName, ct);
		saData.getActivity().setTreatment(ctData);
		
		assertTrue(EntityUtil.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData, new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));
		
		// test UsedBys
		String armName1 = "armName1";
		String armName2 = "armName2";
		Arm arm1 = new Arm(armName1, 100);
		Arm arm2 = new Arm(armName2, 200);
		String epochName1 = "epoch 1";
		String epochName2 = "epoch 2";
		Epoch epoch1 = new Epoch(epochName1, DatatypeFactory.newInstance().newDuration("P24D"));
		Epoch epoch2 = new Epoch(epochName2, null);
		sa.setUsedBy(Collections.singleton(new UsedBy(arm1, epoch1)));
		
		// dummy study containing arms & epochs
		Study s = new Study("studyname", ExampleData.buildIndicationChronicHeartFailure());
		s.getEpochs().add(epoch1);
		s.getEpochs().add(epoch2);
		s.getArms().add(arm1);
		s.getArms().add(arm2);
		
		ActivityUsedBy usedByData = buildActivityUsedby(armName1, epochName1);
		saData.getUsedBy().add(usedByData);

		assertTrue(EntityUtil.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData, s, domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));
		
		Set<UsedBy> usedBy = new HashSet<UsedBy>(sa.getUsedBy());
		usedBy.add(new UsedBy(arm2, epoch2));
		sa.setUsedBy(usedBy);
		assertFalse(EntityUtil.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData, s, domain)));
		JUnitUtil.assertNotEquals(saData, JAXBConvertor.convertStudyActivity(sa));
		
	}

	private ActivityUsedBy buildActivityUsedby(String armName, String epochName) {
		ActivityUsedBy usedByData = new ActivityUsedBy();
		usedByData.setArm(armName);
		usedByData.setEpoch(epochName);
		return usedByData;
	}
	
	private TreatmentActivity buildFixedDoseTreatmentActivity(Drug drug, double quantity) {
		return new TreatmentActivity(buildFixedDoseDrugTreatment(drug, quantity));
	}

	private DrugTreatment buildFixedDoseDrugTreatment(Drug drug, double quantity) {
		FixedDose dose = new FixedDose(quantity, SIUnit.MILLIGRAMS_A_DAY);
		DrugTreatment dt = new DrugTreatment(drug, dose);
		return dt;
	}

	private TreatmentActivity buildFlexibleDoseTreatmentActivity(Drug drug, double minQuantity, double maxQuantity) {
		FlexibleDose dose = new FlexibleDose(new Interval<Double> (minQuantity, maxQuantity), SIUnit.MILLIGRAMS_A_DAY);
		return new TreatmentActivity(new DrugTreatment(drug, dose));
	}
	

//	private org.drugis.addis.entities.data.Arm buildFlexibleDoseArmData(
//			Integer id, int size2, String name, double minQuantity, double maxQuantity) {
//		org.drugis.addis.entities.data.Arm newArm = new org.drugis.addis.entities.data.Arm();
//		newArm.setId(id);
//		newArm.setSize(size2);
//		newArm.setNotes(new Notes());
//		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
//		flexDose.setMinDose(minQuantity);
//		flexDose.setMaxDose(maxQuantity);
//		flexDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
//		newArm.setFlexibleDose(flexDose);
//		newArm.setDrug(nameReference(name));
//		return newArm;
//	}

//	private org.drugis.addis.entities.data.Arm buildFixedDoseArmData(
//			Integer id, int size1, String name, double quantity) {
//		org.drugis.addis.entities.data.Arm newArm = new org.drugis.addis.entities.data.Arm();
//		newArm.setId(id);
//		newArm.setSize(size1);
//		newArm.setNotes(new Notes());
//		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
//		fixDose.setQuantity(quantity);
//		fixDose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
//		newArm.setFixedDose(fixDose);
//		newArm.setDrug(nameReference(name));
//		return newArm;
//	}
	
	@Test
	public void testConvertStudyChars() {
		Allocation alloc = Allocation.RANDOMIZED;
		Blinding blind = Blinding.UNKNOWN;
		String title = "MyStudy";
		int centers = 5;
		String objective = "The loftiest of goals";
		String incl = "Obesity";
		String excl = "Diabetes";
		Status status = Status.ENROLLING;
		Source source = Source.MANUAL;
		GregorianCalendar studyStart = new GregorianCalendar(2008, 8, 12);
		GregorianCalendar studyEnd = new GregorianCalendar(2010, 1, 18);
		GregorianCalendar created = new GregorianCalendar(2011, 2, 15);
		PubMedIdList pmids = new PubMedIdList();
		pmids.add(new PubMedId("1"));
		pmids.add(new PubMedId("12345"));
		List<BigInteger> pmints = new ArrayList<BigInteger>();
		for (PubMedId id : pmids) {
			pmints.add(new BigInteger(id.getId()));
		}
		
		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		chars1.setTitle(JAXBConvertor.stringWithNotes(title));
		chars1.setAllocation(JAXBConvertor.allocationWithNotes(alloc));
		chars1.setBlinding(JAXBConvertor.blindingWithNotes(blind));
		chars1.setCenters(JAXBConvertor.intWithNotes(centers));
		chars1.setObjective(JAXBConvertor.stringWithNotes(objective));
		chars1.setStudyStart(JAXBConvertor.dateWithNotes(studyStart.getTime()));
		chars1.setStudyEnd(JAXBConvertor.dateWithNotes(studyEnd.getTime()));
		chars1.setStatus(JAXBConvertor.statusWithNotes(status));
		chars1.setInclusion(JAXBConvertor.stringWithNotes(incl));
		chars1.setExclusion(JAXBConvertor.stringWithNotes(excl));
		References refs = new References();
		refs.getPubMedId().addAll(pmints);
		chars1.setReferences(refs);
		chars1.setSource(JAXBConvertor.sourceWithNotes(source));
		chars1.setCreationDate(JAXBConvertor.dateWithNotes(created.getTime()));

		
		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.TITLE, new ObjectWithNotes<Object>(title));
		chars2.put(BasicStudyCharacteristic.ALLOCATION, new ObjectWithNotes<Object>(alloc));
		chars2.put(BasicStudyCharacteristic.BLINDING, new ObjectWithNotes<Object>(blind));
		chars2.put(BasicStudyCharacteristic.CENTERS, new ObjectWithNotes<Object>(centers));
		chars2.put(BasicStudyCharacteristic.OBJECTIVE, new ObjectWithNotes<Object>(objective));
		chars2.put(BasicStudyCharacteristic.STUDY_START, new ObjectWithNotes<Object>(studyStart.getTime()));
		chars2.put(BasicStudyCharacteristic.STUDY_END, new ObjectWithNotes<Object>(studyEnd.getTime()));
		chars2.put(BasicStudyCharacteristic.INCLUSION, new ObjectWithNotes<Object>(incl));
		chars2.put(BasicStudyCharacteristic.EXCLUSION, new ObjectWithNotes<Object>(excl));
		chars2.put(BasicStudyCharacteristic.PUBMED, new ObjectWithNotes<Object>(pmids)); // References
		chars2.put(BasicStudyCharacteristic.STATUS, new ObjectWithNotes<Object>(status));
		chars2.put(BasicStudyCharacteristic.SOURCE, new ObjectWithNotes<Object>(source));
		chars2.put(BasicStudyCharacteristic.CREATION_DATE, new ObjectWithNotes<Object>(created.getTime()));
		
		assertEntityEquals(chars2, JAXBConvertor.convertStudyCharacteristics(chars1));
		assertEquals(chars1, JAXBConvertor.convertStudyCharacteristics(chars2));
	}
	
	@Test
	public void testConvertCharacteristicsWithNulls() {
		String title = "title";
		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		initializeCharacteristics(chars1, title);
		
		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.TITLE, new ObjectWithNotes<Object>(title));
		chars2.put(BasicStudyCharacteristic.PUBMED, new ObjectWithNotes<Object>(new PubMedIdList()));
		chars2.put(BasicStudyCharacteristic.CENTERS, new ObjectWithNotes<Object>(null));
		
		assertEquals(chars1, JAXBConvertor.convertStudyCharacteristics(chars2));
		assertEntityEquals(chars2, JAXBConvertor.convertStudyCharacteristics(chars1));
	}
	
	@Test
	public void testConvertStudyOutcomeMeasure() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		
		Endpoint ep = ExampleData.buildEndpointHamd();
		StudyOutcomeMeasure om = new StudyOutcomeMeasure();
		om.setNotes(new Notes());
		om.setEndpoint(nameReference(ep.getName()));
		om.setPrimary(false);
		
		assertEntityEquals(ep, (Endpoint)JAXBConvertor.convertStudyOutcomeMeasure(om, domain).getValue());
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(new Study.StudyOutcomeMeasure<Variable>(ep)), om);
		
		AdverseEvent ade = ExampleData.buildAdverseEventDiarrhea();
		domain.addAdverseEvent(ade);
		om.setEndpoint(null);
		om.setAdverseEvent(nameReference(ade.getName()));
		om.setPrimary(false);
		
		assertEntityEquals(ade, (org.drugis.addis.entities.OutcomeMeasure)JAXBConvertor.convertStudyOutcomeMeasure(om, domain).getValue());
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(new Study.StudyOutcomeMeasure<Variable>(ade)), om);
		
		PopulationCharacteristic pc = ExampleData.buildGenderVariable();
		domain.addPopulationCharacteristic(pc);
		om.setAdverseEvent(null);
		om.setPopulationCharacteristic(nameReference(pc.getName()));
		
		assertEntityEquals(pc, (PopulationCharacteristic)JAXBConvertor.convertStudyOutcomeMeasure(om, domain).getValue());
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(new Study.StudyOutcomeMeasure<Variable>(pc)), om);
	}
	
	@Test(expected=ConversionException.class)
	public void testConvertStudyOutcomeMeasureThrows() throws ConversionException {
		Domain domain = new DomainImpl();
		StudyOutcomeMeasure om = new StudyOutcomeMeasure();
		JAXBConvertor.convertStudyOutcomeMeasure(om, domain);
	}
	
	@Test
	public void testConvertStudyOutcomeMeasures() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		Endpoint ep = ExampleData.buildEndpointHamd();
		domain.addAdverseEvent(ExampleData.buildAdverseEventDiarrhea());
		
		LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> vars = new LinkedHashMap<String, Study.StudyOutcomeMeasure<?>>();
		vars.put("X", new Study.StudyOutcomeMeasure<Variable>(ep));
		vars.put("Y", new Study.StudyOutcomeMeasure<Variable>(ExampleData.buildAdverseEventDiarrhea()));

		StudyOutcomeMeasure epRef = new StudyOutcomeMeasure();
		epRef.setNotes(new Notes());
		epRef.setId("X");
		epRef.setEndpoint(nameReference(ep.getName()));
		epRef.setPrimary(false);
		StudyOutcomeMeasure adeRef = new StudyOutcomeMeasure();
		adeRef.setNotes(new Notes());
		adeRef.setId("Y");
		adeRef.setAdverseEvent(nameReference(ExampleData.buildAdverseEventDiarrhea().getName()));
		adeRef.setPrimary(false);
		StudyOutcomeMeasures oms = new StudyOutcomeMeasures();
		oms.getStudyOutcomeMeasure().add(epRef);
		oms.getStudyOutcomeMeasure().add(adeRef);
		
		assertEquals(vars, JAXBConvertor.convertStudyOutcomeMeasures(oms, domain));
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasures(vars), oms);
	}
	
	@Test
	public void testConvertMeasurement() throws ConversionException {
		org.drugis.addis.entities.data.RateMeasurement rm = new org.drugis.addis.entities.data.RateMeasurement();
		int c = 12;
		int s = 42;
		rm.setRate(c);
		rm.setSampleSize(s);
		org.drugis.addis.entities.data.Measurement meas = buildRateMeasurement(null, null, rm, "Main phase");
		BasicRateMeasurement expected1 = new BasicRateMeasurement(c, s);
		assertEntityEquals(expected1, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected1, "Main phase"));
		
		org.drugis.addis.entities.data.ContinuousMeasurement cm = new org.drugis.addis.entities.data.ContinuousMeasurement();
		double m = 3.14;
		double e = 2.71;
		cm.setMean(m);
		cm.setStdDev(e);
		cm.setSampleSize(s);
		meas = buildContinuousMeasurement(null, null, cm, "Main phase");
		BasicContinuousMeasurement expected2 = new BasicContinuousMeasurement(m, e, s);
		assertEntityEquals(expected2, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected2, "Main phase"));
		
		List<CategoryMeasurement> cms = new ArrayList<CategoryMeasurement>();
		CategoryMeasurement c1 = new CategoryMeasurement();
		c1.setName("Cats");
		c1.setRate(18);
		CategoryMeasurement c2 = new CategoryMeasurement();
		c2.setName("Dogs");
		c2.setRate(2145);
		cms.add(c1);
		cms.add(c2);
		meas = buildCategoricalMeasurement(null, null, cms, "Main phase");

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Dogs", 2145);
		map.put("Cats", 18);
		FrequencyMeasurement expected3 = new FrequencyMeasurement(new String[] {"Cats", "Dogs"}, map);	
		assertEntityEquals(expected3, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected3, "Main phase"));
	}
	

	@Test
	public void testConvertMeasurements() throws ConversionException {
		List<Arm> arms = new ArrayList<Arm>();
		Arm arm5 = new Arm("Opium", 42);
		arms.add(arm5);
		Arm arm8 = new Arm("LSD", 42);
		arms.add(arm8);
		Map<String, Study.StudyOutcomeMeasure<?>> oms = new HashMap<String, Study.StudyOutcomeMeasure<?>>();
		String pcName = "popChar-hair";
		PopulationCharacteristic pc = new PopulationCharacteristic("Hair Length", new ContinuousVariableType());
		oms.put(pcName, new Study.StudyOutcomeMeasure<Variable>(pc));
		String epName = "endpoint-tripping";
		Endpoint ep = new Endpoint("Tripping achieved", Endpoint.convertVarType(Type.RATE), Direction.HIGHER_IS_BETTER);
		oms.put(epName, new Study.StudyOutcomeMeasure<Variable>(ep));
		String aeName = "ade-nojob";
		AdverseEvent ae = new AdverseEvent("Job loss", AdverseEvent.convertVarType(Type.RATE));
		oms.put(aeName, new Study.StudyOutcomeMeasure<Variable>(ae));
		
		org.drugis.addis.entities.data.RateMeasurement rm1 = new org.drugis.addis.entities.data.RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(100);
		BasicRateMeasurement crm1 = new BasicRateMeasurement(10, 100);
		
		org.drugis.addis.entities.data.RateMeasurement rm2 = new org.drugis.addis.entities.data.RateMeasurement();
		rm2.setRate(20);
		rm2.setSampleSize(100);
		BasicRateMeasurement crm2 = new BasicRateMeasurement(20, 100);
		
		org.drugis.addis.entities.data.ContinuousMeasurement cm1 = new org.drugis.addis.entities.data.ContinuousMeasurement();
		cm1.setMean(1.5);
		cm1.setStdDev(1.0);
		cm1.setSampleSize(100);
		BasicContinuousMeasurement ccm1 = new BasicContinuousMeasurement(1.5, 1.0, 100);
		
		Measurements measurements = new Measurements();
		List<org.drugis.addis.entities.data.Measurement> list = measurements.getMeasurement();
		list.add(buildRateMeasurement(arm5.getName(), epName, rm1, "Main phase"));		
		list.add(buildRateMeasurement(arm8.getName(), epName, rm2, "Main phase"));
		list.add(buildRateMeasurement(arm5.getName(), aeName, rm2, "Main phase"));
		list.add(buildRateMeasurement(arm8.getName(), aeName, rm1, "Main phase"));
		list.add(buildContinuousMeasurement(arm5.getName(), pcName, cm1, "Main phase"));
		list.add(buildContinuousMeasurement(arm8.getName(), pcName, cm1, "Main phase"));
		list.add(buildContinuousMeasurement(null, pcName, cm1, "Main phase"));
		
		
		Map<MeasurementKey, BasicMeasurement> expected = new HashMap<MeasurementKey, BasicMeasurement>();
		expected.put(new MeasurementKey(ep, arm5), crm1);
		expected.put(new MeasurementKey(ep, arm8), crm2);
		expected.put(new MeasurementKey(ae, arm5), crm2);
		expected.put(new MeasurementKey(ae, arm8), crm1);
		expected.put(new MeasurementKey(pc, arm5), ccm1);
		expected.put(new MeasurementKey(pc, arm8), ccm1);
		expected.put(new MeasurementKey(pc, null), ccm1);
		
		assertEquals(expected, JAXBConvertor.convertMeasurements(measurements, arms, oms));
		JUnitUtil.assertAllAndOnly(measurements.getMeasurement(), JAXBConvertor.convertMeasurements(expected, arms, "Main phase", oms).getMeasurement());
	}

	private org.drugis.addis.entities.data.Measurement buildContinuousMeasurement(String armName, String omName, org.drugis.addis.entities.data.ContinuousMeasurement cm, String epochName) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,	omName);
		m.setContinuousMeasurement(cm);
		return m;
	}

	private org.drugis.addis.entities.data.Measurement initMeasurement(String armName, String omName) {
		org.drugis.addis.entities.data.Measurement m = new org.drugis.addis.entities.data.Measurement();
		if (armName != null) {
			m.setArm(JAXBConvertor.nameReference(armName));
		}
		if(omName != null) {
			m.setStudyOutcomeMeasure(JAXBConvertor.stringIdReference(omName));
		}
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildRateMeasurement(String armName, String omName, org.drugis.addis.entities.data.RateMeasurement rm, String epochName) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,	omName);
		m.setRateMeasurement(rm);
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildCategoricalMeasurement(String armName, String omName, List<CategoryMeasurement> cmList, String epochName) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,	omName);
		CategoricalMeasurement cms = new CategoricalMeasurement();
		for (CategoryMeasurement cm: cmList) {
			cms.getCategory().add(cm);
		}
		m.setCategoricalMeasurement(cms);
		return m;
	}

	public org.drugis.addis.entities.data.Study buildStudy(String name) throws DatatypeConfigurationException, ConversionException {
		String indicationName = ExampleData.buildIndicationDepression().getName();
		String[] endpointName = new String[] { 
				ExampleData.buildEndpointHamd().getName(), 
				ExampleData.buildEndpointCgi().getName()
			};
		String[] adverseEventName = new String[] {
				ExampleData.buildAdverseEventConvulsion().getName()
			};
		String[] popCharName = new String[] {
				ExampleData.buildAgeVariable().getName()
			};
		String title = "WHOO";
		
		// Arms
		Arms arms = new Arms();
		String fluoxArmName = "fluox arm";
		String paroxArmName = "parox arm";
		arms.getArm().add(buildArmData(fluoxArmName, 100));
		arms.getArm().add(buildArmData(paroxArmName, 42));

		String randomizationEpochName = "Randomization";
		String mainEpochName = "Main phase";
		
		Epochs epochs = new Epochs();
		epochs.getEpoch().add(buildEpoch(randomizationEpochName, null));
		epochs.getEpoch().add(buildEpoch(mainEpochName, DatatypeFactory.newInstance().newDuration("P2D")));

		StudyActivities sas = new StudyActivities();
		sas.getStudyActivity().add(buildStudyActivity("Randomization", PredefinedActivity.RANDOMIZATION));
		DrugTreatment fluoxActivity = new DrugTreatment(ExampleData.buildDrugFluoxetine(), new FixedDose(12.5, SIUnit.MILLIGRAMS_A_DAY));
		DrugTreatment sertrActivity = new DrugTreatment(ExampleData.buildDrugSertraline(), new FixedDose(12.5, SIUnit.MILLIGRAMS_A_DAY));
		List<DrugTreatment> combTreatment = Arrays.asList(fluoxActivity, sertrActivity);
		sas.getStudyActivity().add(buildStudyActivity("Fluox + Sertr fixed dose", new TreatmentActivity(combTreatment)));
		DrugTreatment paroxActivity = new DrugTreatment(ExampleData.buildDrugParoxetine(), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY));
		sas.getStudyActivity().add(buildStudyActivity("Parox fixed dose", new TreatmentActivity(paroxActivity)));
		
		ActivityUsedBy aub1 = buildActivityUsedby(fluoxArmName,
				randomizationEpochName);

		ActivityUsedBy aub2 = buildActivityUsedby(fluoxArmName, mainEpochName);

		ActivityUsedBy aub3 = buildActivityUsedby(paroxArmName, mainEpochName);
		
		sas.getStudyActivity().get(0).getUsedBy().add(aub1);
		sas.getStudyActivity().get(1).getUsedBy().add(aub2);
		sas.getStudyActivity().get(2).getUsedBy().add(aub3);
		
		org.drugis.addis.entities.data.Study study = buildStudySkeleton(name,
				title, indicationName, endpointName, adverseEventName,
				popCharName, arms, epochs, sas);
		
		study.getCharacteristics().setCenters(JAXBConvertor.intWithNotes(3));
		study.getCharacteristics().setAllocation(JAXBConvertor.allocationWithNotes(Allocation.RANDOMIZED));
		
		// Measurements
		List<org.drugis.addis.entities.data.Measurement> list = study.getMeasurements().getMeasurement();
		RateMeasurement rm1 = new RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(110);
		ContinuousMeasurement cm1 = new ContinuousMeasurement();
		cm1.setMean(0.2);
		cm1.setStdDev(0.01);
		cm1.setSampleSize(110);
		org.drugis.addis.entities.data.Measurement m1 = buildRateMeasurement(paroxArmName, "endpoint-" + endpointName[0], rm1, "Main phase");
		org.drugis.addis.entities.data.Measurement m2 = buildContinuousMeasurement(null, "popChar-" + popCharName[0], cm1, "Main phase");
		list.add(m1);
		list.add(m2);
				
		return study;
	}

	private org.drugis.addis.entities.data.StudyActivity buildStudyActivity(String name, Activity activity) throws ConversionException {
		org.drugis.addis.entities.data.StudyActivity sa = new org.drugis.addis.entities.data.StudyActivity();
		sa.setName(name);
		org.drugis.addis.entities.data.Activity a = JAXBConvertor.convertActivity(activity);
		sa.setActivity(a);
		sa.setNotes(new Notes());
		return sa;
	}

	private org.drugis.addis.entities.data.Epoch buildEpoch(String epochName1, Duration d) {
		org.drugis.addis.entities.data.Epoch e = new org.drugis.addis.entities.data.Epoch();
		e.setName(epochName1);
		e.setDuration(d);
		e.setNotes(new Notes());
		return e;
	}

	private org.drugis.addis.entities.data.Arm buildArmData(String name, int size) {
		org.drugis.addis.entities.data.Arm a = new org.drugis.addis.entities.data.Arm();
		a.setName(name);
		a.setSize(size);
		a.setNotes(new Notes());
		return a;
	}

	private void initializeCharacteristics(Characteristics characteristics, String title) {
		characteristics.setAllocation(JAXBConvertor.allocationWithNotes(Allocation.UNKNOWN));
		characteristics.setBlinding(JAXBConvertor.blindingWithNotes(Blinding.UNKNOWN));
		characteristics.setCenters(JAXBConvertor.intWithNotes(null));
		characteristics.setCreationDate(JAXBConvertor.dateWithNotes(null));
		characteristics.setExclusion(JAXBConvertor.stringWithNotes(null));
		characteristics.setInclusion(JAXBConvertor.stringWithNotes(null));
		characteristics.setObjective(JAXBConvertor.stringWithNotes(null));
		characteristics.setReferences(new References());
		characteristics.setSource(JAXBConvertor.sourceWithNotes(Source.MANUAL));
		characteristics.setStatus(JAXBConvertor.statusWithNotes(Status.UNKNOWN));
		characteristics.setStudyEnd(JAXBConvertor.dateWithNotes(null));
		characteristics.setStudyStart(JAXBConvertor.dateWithNotes(null));
		characteristics.setTitle(JAXBConvertor.stringWithNotes(title));
	}

	private org.drugis.addis.entities.data.Study buildStudySkeleton(
			String name, String title, String indicationName,
			String[] endpointName, String[] adverseEventName,
			String[] popCharName, Arms arms, Epochs epochs, StudyActivities sas) {
		org.drugis.addis.entities.data.Study study = new org.drugis.addis.entities.data.Study();
		study.setName(name);
		NameReferenceWithNotes indicationRef = JAXBConvertor.nameReferenceWithNotes(indicationName);
		study.setIndication(indicationRef);
		
		// Outcome measures
		StudyOutcomeMeasures studyOutcomeMeasures = new StudyOutcomeMeasures();
		study.setStudyOutcomeMeasures(studyOutcomeMeasures);
		
		// Outcome measures: Endpoints
		for (String epName : endpointName) {
			StudyOutcomeMeasure ep = new StudyOutcomeMeasure();
			ep.setNotes(new Notes());
			ep.setId("endpoint-" + epName);
			ep.setEndpoint(nameReference(epName));
			ep.setPrimary(false);
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(ep);
		}
		
		// Outcome measures: Adverse events
		for (String aeName : adverseEventName) {
			StudyOutcomeMeasure ae = new StudyOutcomeMeasure();
			ae.setNotes(new Notes());
			ae.setId("adverseEvent-" + aeName);
			ae.setAdverseEvent(nameReference(aeName));
			ae.setPrimary(false);
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(ae);
		}
		
		// Outcome measures: Population chars
		for (String pcName : popCharName) {
			StudyOutcomeMeasure pc = new StudyOutcomeMeasure();
			pc.setNotes(new Notes());
			pc.setId("popChar-" + pcName);
			pc.setPopulationCharacteristic(nameReference(pcName));
			pc.setPrimary(false);
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(pc);
		}
		
		// Arms
		study.setArms(arms);
		
		// Epochs
		study.setEpochs(epochs);
		
		// StudyActivities
		study.setActivities(sas);
		
		// Study characteristics
		Characteristics chars = new Characteristics();
		study.setCharacteristics(chars);
		initializeCharacteristics(study.getCharacteristics(), title);
		
		// Measurements (empty)
		Measurements measurements = new Measurements();
		study.setMeasurements(measurements);
		
		study.setNotes(new Notes());
		
		return study;
	}

	@Test
	public void testConvertStudy() throws ConversionException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addEndpoint(ExampleData.buildEndpointCgi());
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		
		String name = "My fancy study";
		org.drugis.addis.entities.data.Study study = buildStudy(name);
		
		//----------------------------------------
		Study study2 = new Study();
		study2.setName(name);
		study2.setIndication(ExampleData.buildIndicationDepression());
		study2.getEndpoints().add(new org.drugis.addis.entities.Study.StudyOutcomeMeasure<Endpoint>(ExampleData.buildEndpointHamd()));
		study2.getEndpoints().add(new org.drugis.addis.entities.Study.StudyOutcomeMeasure<Endpoint>(ExampleData.buildEndpointCgi()));
		study2.getAdverseEvents().add(new org.drugis.addis.entities.Study.StudyOutcomeMeasure<AdverseEvent>(ExampleData.buildAdverseEventConvulsion()));
		study2.addVariable(ExampleData.buildAgeVariable());
		Arm fluoxArm = new Arm("fluox arm", 100);
		study2.getArms().add(fluoxArm);
		Arm paroxArm = new Arm("parox arm", 42);
		study2.getArms().add(paroxArm);
		Epoch epoch1 = new Epoch("Randomization", null);
		Epoch epoch2 = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("P2D"));
		study2.getEpochs().add(epoch1);
		study2.getEpochs().add(epoch2);
		
		StudyActivity sa1 = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		DrugTreatment fluoxDrugTreatment = new DrugTreatment(ExampleData.buildDrugFluoxetine(), new FixedDose(12.5, SIUnit.MILLIGRAMS_A_DAY));
		DrugTreatment sertrDrugTreatment = new DrugTreatment(ExampleData.buildDrugSertraline(), new FixedDose(12.5, SIUnit.MILLIGRAMS_A_DAY));;
		StudyActivity combTreatmentActivity = new StudyActivity("Fluox + Sertr fixed dose", new TreatmentActivity(Arrays.asList(fluoxDrugTreatment, sertrDrugTreatment)));
		StudyActivity paroxTreatmentActivity = new StudyActivity("Parox fixed dose", new TreatmentActivity(new DrugTreatment(ExampleData.buildDrugParoxetine(), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY))));
		study2.getStudyActivities().add(sa1);
		study2.getStudyActivities().add(combTreatmentActivity);
		study2.getStudyActivities().add(paroxTreatmentActivity);
		
		study2.setCharacteristic(BasicStudyCharacteristic.TITLE, "WHOO");
		study2.setCharacteristic(BasicStudyCharacteristic.CENTERS, 3);
		study2.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, Allocation.RANDOMIZED);
		study2.setCharacteristic(BasicStudyCharacteristic.PUBMED, new PubMedIdList());
		study2.setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, null);
		study2.setMeasurement(ExampleData.buildEndpointHamd(), paroxArm, new BasicRateMeasurement(10, 110));
		assertStudiesNotEqual(domain, study, study2);
		study2.setMeasurement(ExampleData.buildAgeVariable(), new BasicContinuousMeasurement(0.2, 0.01, 110));
		
		study2.setStudyActivityAt(fluoxArm, epoch1, sa1);
		assertStudiesNotEqual(domain, study, study2);
		study2.setStudyActivityAt(fluoxArm, epoch2, combTreatmentActivity);
		study2.setStudyActivityAt(paroxArm, epoch2, paroxTreatmentActivity);
		
		assertEntityEquals(study2, JAXBConvertor.convertStudy(study, domain));
		assertEquals(study, JAXBConvertor.convertStudy(study2));
	}

	private void assertStudiesNotEqual(DomainImpl domain,
			org.drugis.addis.entities.data.Study study, Study study2)
			throws ConversionException {
		assertFalse(EntityUtil.deepEqual(study2, JAXBConvertor.convertStudy(study, domain)));
		JUnitUtil.assertNotEquals(study, JAXBConvertor.convertStudy(study2));
	}
	
	@Test
	public void testConvertNote() {
		org.drugis.addis.entities.data.Note note = new org.drugis.addis.entities.data.Note();
		note.setSource(Source.CLINICALTRIALS);
		String text = "Some text here";
		note.setValue(text);
		
		Note expected = new Note(Source.CLINICALTRIALS, text);
		
		assertEquals(expected, JAXBConvertor.convertNote(note));
		assertEquals(note, JAXBConvertor.convertNote(expected));
	}
	
	@Test
	public void testConvertStudyWithNotes() throws ConversionException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addEndpoint(ExampleData.buildEndpointCgi());
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		
		String name = "My fancy study";
		org.drugis.addis.entities.data.Study studyData = buildStudy(name);
		Study studyEntity = JAXBConvertor.convertStudy(studyData, domain);
		
		Note armNote = new Note(Source.CLINICALTRIALS, "Some text here");
		studyData.getArms().getArm().get(0).getNotes().getNote().add(JAXBConvertor.convertNote(armNote));
		studyData.getArms().getArm().get(1).getNotes().getNote().add(JAXBConvertor.convertNote(armNote));
		studyEntity.getArms().get(0).getNotes().add(armNote);
		studyEntity.getArms().get(1).getNotes().add(armNote);
		
		Note adeNote = new Note(Source.MANUAL, "I would not like to suffer from this!");
		studyData.getStudyOutcomeMeasures().getStudyOutcomeMeasure().get(2).getNotes().getNote().add(JAXBConvertor.convertNote(adeNote));
		studyEntity.getStudyAdverseEvents().get(0).getNotes().add(adeNote);
		Note hamdNote = new Note(Source.MANUAL, "Mmm... HAM!");
		studyData.getStudyOutcomeMeasures().getStudyOutcomeMeasure().get(0).getNotes().getNote().add(JAXBConvertor.convertNote(hamdNote));
		studyEntity.getStudyEndpoints().get(0).getNotes().add(hamdNote);
		Note charNote = new Note(Source.CLINICALTRIALS, "A randomized double blind trial of something");
		studyData.getCharacteristics().getAllocation().getNotes().getNote().add(JAXBConvertor.convertNote(charNote));
		studyEntity.getCharacteristics().get(BasicStudyCharacteristic.ALLOCATION).getNotes().add(charNote);
		
		Note indicationNote = new Note(Source.CLINICALTRIALS, "Depression! Aah!");
		studyData.getIndication().getNotes().getNote().add(JAXBConvertor.convertNote(indicationNote));
		studyEntity.getIndicationWithNotes().getNotes().add(indicationNote);
		
		Note idNote = new Note(Source.CLINICALTRIALS, "NCT1337");
		studyData.getNotes().getNote().add(JAXBConvertor.convertNote(idNote));
		studyEntity.getNameWithNotes().getNotes().add(idNote);
		
		assertEntityEquals(studyEntity, JAXBConvertor.convertStudy(studyData, domain));
		assertEquals(studyData, JAXBConvertor.convertStudy(studyEntity));
	}
	
	private class MetaAnalysisWithStudies {
		public org.drugis.addis.entities.data.PairwiseMetaAnalysis d_pwma;
		public org.drugis.addis.entities.data.NetworkMetaAnalysis d_nwma;
		public List<org.drugis.addis.entities.data.Study> d_studies;
		
		public MetaAnalysisWithStudies(org.drugis.addis.entities.data.PairwiseMetaAnalysis ma, List<org.drugis.addis.entities.data.Study> s) {
			d_pwma = ma;
			d_studies = s;
		}
		
		public MetaAnalysisWithStudies(org.drugis.addis.entities.data.NetworkMetaAnalysis ma, List<org.drugis.addis.entities.data.Study> s) {
			d_nwma = ma;
			d_studies = s;
		}
	}
	
	@Test
	public void testConvertPairWiseMetaAnalysis() throws ConversionException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		
		String name = "Fluox-Venla Diarrhea for PMA";
		MetaAnalysisWithStudies ma = buildPairWiseMetaAnalysis(name);
		
		//-----------------------------------
		Study study = JAXBConvertor.convertStudy(ma.d_studies.get(0), domain);
		List<StudyArmsEntry> armsList = new ArrayList<StudyArmsEntry>();
		armsList.add(new StudyArmsEntry(study, study.getArms().get(0), study.getArms().get(1)));
		domain.addStudy(study);
		
		RandomEffectsMetaAnalysis pwma = new RandomEffectsMetaAnalysis(name, ExampleData.buildEndpointHamd(), armsList);
		
		assertEntityEquals(pwma, JAXBConvertor.convertPairWiseMetaAnalysis(ma.d_pwma, domain));
		
		assertEquals(ma.d_pwma, JAXBConvertor.convertPairWiseMetaAnalysis(pwma));
	}

	private MetaAnalysisWithStudies buildPairWiseMetaAnalysis(String name) throws DatatypeConfigurationException, ConversionException {
		String study_name = "My fancy pair-wise study";
		org.drugis.addis.entities.data.Study study = buildStudy(study_name);

		org.drugis.addis.entities.data.PairwiseMetaAnalysis pwma = new org.drugis.addis.entities.data.PairwiseMetaAnalysis();
		pwma.setName(name);		
		pwma.setIndication(nameReference(ExampleData.buildIndicationDepression().getName()));
		pwma.setEndpoint(nameReference(ExampleData.buildEndpointHamd().getName()));
		// Base
		Alternative combi = new Alternative();
		if (combi.getDrugs() == null) {
			combi.setDrugs(new AnalysisDrugs());
		}
		combi.getDrugs().getDrug().clear();
		combi.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugFluoxetine().getName()));
		combi.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugSertraline().getName()));
		AnalysisArms combiArms = new AnalysisArms();
		combiArms.getArm().add(JAXBConvertor.armReference(study_name, study.getArms().getArm().get(0).getName()));
		combi.setArms(combiArms);
		pwma.getAlternative().add(combi);
		// Subject
		Alternative parox = new Alternative();
		if (parox.getDrugs() == null) {
			parox.setDrugs(new AnalysisDrugs());
		}
		parox.getDrugs().getDrug().clear();
		parox.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugParoxetine().getName()));
		AnalysisArms paroxArms = new AnalysisArms();
		paroxArms.getArm().add(JAXBConvertor.armReference(study_name, study.getArms().getArm().get(1).getName()));
		parox.setArms(paroxArms);
		pwma.getAlternative().add(parox);
		
		return new MetaAnalysisWithStudies(pwma, Collections.singletonList(study));
	}

	@Test
	public void testConvertNetworkMetaAnalysis() throws Exception, InstantiationException, InvocationTargetException, NoSuchMethodException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		String name = "CGI network meta-analysis";
		
		MetaAnalysisWithStudies ma = buildNetworkMetaAnalysis(name);
		
		List<Study> studies = new ArrayList<Study>();
		for (org.drugis.addis.entities.data.Study study : ma.d_studies) {
			Study studyEnt = JAXBConvertor.convertStudy(study, domain);
			domain.addStudy(studyEnt);
			studies.add(studyEnt);
		}

		DrugSet combi = new DrugSet(Arrays.asList(ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugSertraline()));
		DrugSet parox = new DrugSet(ExampleData.buildDrugParoxetine());
		DrugSet sertr = new DrugSet(ExampleData.buildDrugSertraline());
		SortedSet<DrugSet> drugs = new TreeSet<DrugSet>();
		drugs.add(combi);
		drugs.add(parox);
		drugs.add(sertr);
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet,Arm>>();
		Map<DrugSet, Arm> study1map = new HashMap<DrugSet, Arm>();
		study1map.put(combi, studies.get(0).getArms().get(0));
		study1map.put(sertr, studies.get(0).getArms().get(1));
		armMap.put(studies.get(0), study1map);
		Map<DrugSet, Arm> study2map = new HashMap<DrugSet, Arm>();
		study2map.put(parox, studies.get(1).getArms().get(0));
		study2map.put(sertr, studies.get(1).getArms().get(1));
		armMap.put(studies.get(1), study2map);
		Map<DrugSet, Arm> study3map = new HashMap<DrugSet, Arm>();
		study3map.put(sertr, studies.get(2).getArms().get(0));
		study3map.put(parox, studies.get(2).getArms().get(1));
		study3map.put(combi, studies.get(2).getArms().get(2));
		armMap.put(studies.get(2), study3map);
		
		Collections.sort(studies); // So the reading *by definition* puts the studies in their natural order
		NetworkMetaAnalysis expected = new NetworkMetaAnalysis(name, ExampleData.buildIndicationDepression(),
				ExampleData.buildEndpointCgi(), studies, drugs, armMap);
		
		assertEntityEquals(expected, JAXBConvertor.convertNetworkMetaAnalysis(ma.d_nwma, domain));
		assertEquals(ma.d_nwma, JAXBConvertor.convertNetworkMetaAnalysis(expected));
	}

	private MetaAnalysisWithStudies buildNetworkMetaAnalysis(String name) throws DatatypeConfigurationException, ConversionException {
		String study_one = "A Network Meta analysis study 1";
		String study_two = "A Network Meta analysis study 2";
		String study_three = "A Network Meta analysis study 3";
		
		String[] endpoints = new String[] { ExampleData.buildEndpointHamd().getName(), ExampleData.buildEndpointCgi().getName() };
		
		Arms arms1 = new Arms();
		Epochs epochs1 = new Epochs();
		StudyActivities sas1 = new StudyActivities();
		String combiArmName = "fluox + sertr arm";
		String sertraArmName = "sertra arm";
		String paroxArmName = "parox arm";
		String mainPhaseName = "Main phase";
		String treatmentName = "Treatment";
		DrugTreatment fluox = buildFixedDoseDrugTreatment(ExampleData.buildDrugFluoxetine(), 12.5);
		DrugTreatment venla = buildFixedDoseDrugTreatment(ExampleData.buildDrugSertraline(), 12.5);
		TreatmentActivity combiFixedDose = new TreatmentActivity(Arrays.asList(fluox, venla));
		TreatmentActivity sertraFixedDose = buildFixedDoseTreatmentActivity(ExampleData.buildDrugSertraline(), 12.5);
		TreatmentActivity paroxFixedDose =  buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 12.5);
		buildArmEpochTreatmentActivityCombination(arms1, epochs1, sas1, 20, combiArmName, mainPhaseName, treatmentName, combiFixedDose);
		buildArmEpochTreatmentActivityCombination(arms1, epochs1, sas1, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);
		
		String indicationName = ExampleData.buildIndicationDepression().getName();
		org.drugis.addis.entities.data.Study study1 = buildStudySkeleton(study_one, study_one, 
				indicationName, endpoints, new String[] {}, new String[] {}, arms1, epochs1, sas1);
		
		Arms arms2 = new Arms();
		Epochs epochs2 = new Epochs();
		StudyActivities sas2 = new StudyActivities();
		buildArmEpochTreatmentActivityCombination(arms2, epochs2, sas2, 20, paroxArmName, mainPhaseName, treatmentName, paroxFixedDose);
		buildArmEpochTreatmentActivityCombination(arms2, epochs2, sas2, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);

		org.drugis.addis.entities.data.Study study2 = buildStudySkeleton(study_two, study_two, 
				indicationName, endpoints, new String[] {}, new String[] {}, arms2, epochs2, sas2);
		
		Arms arms3 = new Arms();
		Epochs epochs3 = new Epochs();
		StudyActivities sas3 = new StudyActivities();
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, paroxArmName, mainPhaseName, treatmentName, paroxFixedDose);
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, combiArmName, mainPhaseName, treatmentName, combiFixedDose);

		org.drugis.addis.entities.data.Study study3 = buildStudySkeleton(study_three, study_three, 
				indicationName, endpoints, new String[] {}, new String[] {}, arms3, epochs3, sas3);

		org.drugis.addis.entities.data.NetworkMetaAnalysis nma = new org.drugis.addis.entities.data.NetworkMetaAnalysis();
		nma.setName(name);		
		nma.setIndication(nameReference(indicationName));
		nma.setEndpoint(nameReference(ExampleData.buildEndpointCgi().getName()));
		
		List<org.drugis.addis.entities.data.Study> studiesl = new ArrayList<org.drugis.addis.entities.data.Study>();
		studiesl.add(study1);
		studiesl.add(study2);
		studiesl.add(study3);

		
		// Fluoxetine
		Alternative combi = new Alternative();
		if (combi.getDrugs() == null) {
			combi.setDrugs(new AnalysisDrugs());
		}
		combi.getDrugs().getDrug().clear();
		combi.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugFluoxetine().getName()));
		combi.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugSertraline().getName()));
		AnalysisArms combiArms = new AnalysisArms();
		combiArms.getArm().add(JAXBConvertor.armReference(study_one, arms1.getArm().get(0).getName())); // study 1
		combiArms.getArm().add(JAXBConvertor.armReference(study_three, arms3.getArm().get(2).getName())); // study 3
		combi.setArms(combiArms);
		nma.getAlternative().add(combi);
		// Paroxetine		
		Alternative parox = new Alternative();
		if (parox.getDrugs() == null) {
			parox.setDrugs(new AnalysisDrugs());
		}
		parox.getDrugs().getDrug().clear();
		parox.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugParoxetine().getName()));
		AnalysisArms paroxArms = new AnalysisArms();
		paroxArms.getArm().add(JAXBConvertor.armReference(study_two, arms2.getArm().get(0).getName())); // study 2
		paroxArms.getArm().add(JAXBConvertor.armReference(study_three, arms3.getArm().get(1).getName())); // study 3
		parox.setArms(paroxArms);
		nma.getAlternative().add(parox);
		// Sertraline
		Alternative setr = new Alternative();
		if (setr.getDrugs() == null) {
			setr.setDrugs(new AnalysisDrugs());
		}
		setr.getDrugs().getDrug().clear();
		setr.getDrugs().getDrug().add(nameReference(ExampleData.buildDrugSertraline().getName()));
		AnalysisArms sertrArms  = new AnalysisArms();
		sertrArms.getArm().add(JAXBConvertor.armReference(study_one, arms1.getArm().get(1).getName())); // study 1
		sertrArms.getArm().add(JAXBConvertor.armReference(study_two, arms2.getArm().get(1).getName())); // study 2
		sertrArms.getArm().add(JAXBConvertor.armReference(study_three, arms3.getArm().get(0).getName())); // study 2
		setr.setArms(sertrArms);
		nma.getAlternative().add(setr);
		
		return new MetaAnalysisWithStudies(nma, studiesl);
	}

	private void buildArmEpochTreatmentActivityCombination(Arms arms, Epochs epochs,
			StudyActivities sas, int armSize, String fluoxArmName,
			String mainPhaseName, String treatmentName, TreatmentActivity fluoxFixedDose)
			throws DatatypeConfigurationException, ConversionException {
		arms.getArm().add(buildArmData(fluoxArmName, armSize));
		epochs.getEpoch().add(buildEpoch(mainPhaseName, DatatypeFactory.newInstance().newDuration("P2D")));
		org.drugis.addis.entities.data.StudyActivity saTreatment1 = buildStudyActivity(treatmentName, fluoxFixedDose);
		saTreatment1.getUsedBy().add(buildActivityUsedby(fluoxArmName, mainPhaseName));
		sas.getStudyActivity().add(saTreatment1);
	}
	
	@Test
	public void testConvertMetaAnalyses() throws NullPointerException, ConversionException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		
		MetaAnalyses analyses = new MetaAnalyses();
		MetaAnalysisWithStudies ma1 = buildPairWiseMetaAnalysis("XXX");
		analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(ma1.d_pwma);
		MetaAnalysisWithStudies ma2 = buildNetworkMetaAnalysis("XXX 2");
		analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(ma2.d_nwma);
		
		addStudies(domain, ma1);
		addStudies(domain, ma2);
		
		List<MetaAnalysis> expected = new ArrayList<MetaAnalysis>();
		expected.add(JAXBConvertor.convertPairWiseMetaAnalysis(ma1.d_pwma, domain));
		expected.add(JAXBConvertor.convertNetworkMetaAnalysis(ma2.d_nwma, domain));
		
		assertEntityEquals(expected, JAXBConvertor.convertMetaAnalyses(analyses, domain));
		assertEquals(analyses, JAXBConvertor.convertMetaAnalyses(expected));
	}

	private void addStudies(DomainImpl domain, MetaAnalysisWithStudies ma1)
			throws ConversionException {
		for (org.drugis.addis.entities.data.Study study : ma1.d_studies) {
			domain.addStudy(JAXBConvertor.convertStudy(study, domain));
		}
	}
		
	@Test
	public void testConvertStudyBenefitRiskAnalysis() throws ConversionException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		domain.addAdverseEvent(ExampleData.buildAdverseEventDiarrhea());
		
		String name = "BR Analysis";
		String[] adverseEvents = {ExampleData.buildAdverseEventDiarrhea().getName(), ExampleData.buildAdverseEventConvulsion().getName() };
		String[] endpoints = { ExampleData.buildEndpointCgi().getName(), ExampleData.buildEndpointHamd().getName() };
		Arms arms = new Arms();
		Epochs epochs = new Epochs();
		StudyActivities sas = new StudyActivities();
		
		TreatmentActivity fluoxTA = buildFixedDoseTreatmentActivity(ExampleData.buildDrugFluoxetine(), 13);
		TreatmentActivity paroxTAHigh = buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 45);
		TreatmentActivity paroxTALow = buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 12);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 12, "fluox arm", "Main phase", "Treatment", fluoxTA);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 23, "parox arm high", "Main phase", "Treatment", paroxTAHigh);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 11, "parox arm low", "Main phase", "Treatment", paroxTALow);

		org.drugis.addis.entities.data.Study study = buildStudySkeleton("Study for Benefit-Risk", "HI", 
				ExampleData.buildIndicationDepression().getName(), endpoints, adverseEvents, new String[]{}, arms, epochs, sas);
		
		String[] whichArms = { "parox arm high", "parox arm low" };
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis br = buildStudyBR(name, study, endpoints, adverseEvents, whichArms);
		
		Study convertStudy = JAXBConvertor.convertStudy(study, domain);
		domain.addStudy(convertStudy);
		List<org.drugis.addis.entities.OutcomeMeasure> criteria = new ArrayList<org.drugis.addis.entities.OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		
		List<Arm> alternatives = new ArrayList<Arm>();
		alternatives.add(convertStudy.getArms().get(1));
		alternatives.add(convertStudy.getArms().get(2));
		
		StudyBenefitRiskAnalysis expected = new StudyBenefitRiskAnalysis(name, 
				ExampleData.buildIndicationDepression(), convertStudy,
				criteria , alternatives, AnalysisType.LyndOBrien);
		
		assertEntityEquals(expected, JAXBConvertor.convertStudyBenefitRiskAnalysis(br, domain));
		assertEquals(br, JAXBConvertor.convertStudyBenefitRiskAnalysis(expected));
	}

	private org.drugis.addis.entities.data.StudyBenefitRiskAnalysis buildStudyBR(
			String name, org.drugis.addis.entities.data.Study study,
			String[] endpoints, String[] adverseEvents, String[] whichArms) {
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis br = new org.drugis.addis.entities.data.StudyBenefitRiskAnalysis();
		br.setName(name);
		br.setIndication(nameReference(study.getIndication().getName()));
		br.setStudy(nameReference(study.getName()));
		br.setAnalysisType(AnalysisType.LyndOBrien);
		br.setOutcomeMeasures(new OutcomeMeasuresReferences());
		br.getOutcomeMeasures().getEndpoint().add(nameReference(endpoints[0]));
		br.getOutcomeMeasures().getAdverseEvent().add(nameReference(adverseEvents[1]));
		ArmReferences armRefs = new ArmReferences();
		for (String whichArm : whichArms) {
			armRefs.getArm().add(JAXBConvertor.armReference(study.getName(), whichArm));
		}
		br.setArms(armRefs);
		return br;
	}	
	
	@Test
	public void testConvertMetaBenefitRiskAnalysis() throws ConversionException, NullPointerException, IllegalArgumentException, EntityIdExistsException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		domain.addAdverseEvent(ExampleData.buildAdverseEventDiarrhea());
		
		String name = "Meta Benefit-Risk Analysis Test";
		// create entities
		String pairWiseName = "First Pair-Wise";
		String networkMetaName = "Second Network Meta Analysis";
		MetaAnalysisWithStudies ma1 = buildPairWiseMetaAnalysis(pairWiseName);
		MetaAnalysisWithStudies ma2 = buildNetworkMetaAnalysis(networkMetaName);
		
		// add to the domain
		addStudies(domain, ma1);
		addStudies(domain, ma2);
		RandomEffectsMetaAnalysis ma1ent = JAXBConvertor.convertPairWiseMetaAnalysis(ma1.d_pwma, domain);
		domain.addMetaAnalysis(ma1ent);
		MetaAnalysis ma2ent = JAXBConvertor.convertNetworkMetaAnalysis(ma2.d_nwma, domain);
		domain.addMetaAnalysis(ma2ent);

		// create BR analysis
		String[][] drugs = {
				new String[] { "Fluoxetine", "Sertraline" },
				new String[] { "Paroxetine" }
		};
		String indication = ma1.d_pwma.getIndication().getName();
		String[] meta = { pairWiseName, networkMetaName };
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br = buildMetaBR(
				name, drugs, indication, meta);

		List<MetaAnalysis> metaList = new ArrayList<MetaAnalysis>();
		metaList.add(ma1ent);
		metaList.add(ma2ent);
		
		List<DrugSet> drugsEnt = new ArrayList<DrugSet>(ma1ent.getIncludedDrugs());
		DrugSet baseline = drugsEnt.get(0);
		drugsEnt.remove(baseline);
		MetaBenefitRiskAnalysis expected = new MetaBenefitRiskAnalysis(name, ma1ent.getIndication(), metaList , baseline, 
				drugsEnt, AnalysisType.SMAA);
		assertEntityEquals(expected, JAXBConvertor.convertMetaBenefitRiskAnalysis(br, domain));
		assertEquals(br, JAXBConvertor.convertMetaBenefitRiskAnalysis(expected));
	}

	private org.drugis.addis.entities.data.MetaBenefitRiskAnalysis buildMetaBR(
			String name, String[][] drugs, String indication, String[] meta) {
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis();
		br.setName(name);
		br.setAnalysisType(AnalysisType.SMAA);
		br.setIndication(nameReference(indication));
		br.setBaseline(createAnalysisDrugs(drugs[0]));
		AlternativeDrugSets dRef = new AlternativeDrugSets();
		dRef.getAlternative().add(createAnalysisDrugs(drugs[0]));
		dRef.getAlternative().add(createAnalysisDrugs(drugs[1]));
		br.setAlternatives(dRef);
		MetaAnalysisReferences mRefs = new MetaAnalysisReferences();
		for (String mName : meta) {
			mRefs.getMetaAnalysis().add(nameReference(mName));
		}
		br.setMetaAnalyses(mRefs);
		return br;
	}

	private AnalysisDrugs createAnalysisDrugs(String[] drugs) {
		AnalysisDrugs drugs1 = new AnalysisDrugs();
		for (String name : drugs) {
			drugs1.getDrug().add(nameReference(name));
		}
		return drugs1;
	}
	
	@Test
	public void testConvertBenefitRiskAnalyses() throws ConversionException, EntityIdExistsException, DatatypeConfigurationException {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.addAdverseEvent(ExampleData.buildAdverseEventConvulsion());
		domain.addAdverseEvent(ExampleData.buildAdverseEventDiarrhea());
		
		String name = "BR Analysis";
		String[] adverseEvents = {ExampleData.buildAdverseEventDiarrhea().getName(), ExampleData.buildAdverseEventConvulsion().getName() };
		String[] endpoints = { ExampleData.buildEndpointCgi().getName(), ExampleData.buildEndpointHamd().getName() };
		Arms arms = new Arms();
		Epochs epochs = new Epochs();
		StudyActivities sas = new StudyActivities();
		
		TreatmentActivity fluoxTA = buildFixedDoseTreatmentActivity(ExampleData.buildDrugFluoxetine(), 13);
		TreatmentActivity paroxTAHigh = buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 45);
		TreatmentActivity paroxTALow = buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 12);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 12, "fluox arm", "Main phase", "Treatment", fluoxTA);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 23, "parox arm high", "Main phase", "Treatment", paroxTAHigh);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 11, "parox arm low", "Main phase", "Treatment", paroxTALow);

		org.drugis.addis.entities.data.Study study = buildStudySkeleton("Study for Benefit-Risk", "HI", 
				ExampleData.buildIndicationDepression().getName(),
				endpoints, adverseEvents, new String[]{}, arms, epochs, sas);
		
		String[] whichArms = { "parox arm high", "parox arm low" };
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis studyBR = buildStudyBR(
				name, study, endpoints, adverseEvents, whichArms);
		
		Study convertStudy = JAXBConvertor.convertStudy(study, domain);
		domain.addStudy(convertStudy);
		
		String pwName = "pairwise MA";
		String nwName = "network MA";
		MetaAnalysisWithStudies pairWiseMetaAnalysis = buildPairWiseMetaAnalysis(pwName);
		MetaAnalysisWithStudies networkMetaAnalysis = buildNetworkMetaAnalysis(nwName);
		addStudies(domain, pairWiseMetaAnalysis);
		domain.addMetaAnalysis(JAXBConvertor.convertPairWiseMetaAnalysis(pairWiseMetaAnalysis.d_pwma, domain));
		addStudies(domain, networkMetaAnalysis);
		domain.addMetaAnalysis(JAXBConvertor.convertNetworkMetaAnalysis(networkMetaAnalysis.d_nwma, domain));
		
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis metaBR = buildMetaBR("Meta BR", 
				new String[][] { 
					new String[] { ExampleData.buildDrugFluoxetine().getName(), ExampleData.buildDrugSertraline().getName() },
					new String[] { ExampleData.buildDrugParoxetine().getName() }
				}, 
				ExampleData.buildIndicationDepression().getName(),
				new String[] { nwName, pwName });
		
		BenefitRiskAnalyses analyses = new BenefitRiskAnalyses();
		analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(metaBR);
		analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(studyBR);
		
		List<BenefitRiskAnalysis<?>> expected = new ArrayList<BenefitRiskAnalysis<?>>();
		expected.add(JAXBConvertor.convertMetaBenefitRiskAnalysis(metaBR, domain));
		expected.add(JAXBConvertor.convertStudyBenefitRiskAnalysis(studyBR, domain));

		assertEntityEquals(expected, JAXBConvertor.convertBenefitRiskAnalyses(analyses, domain));
		assertEquals(analyses, JAXBConvertor.convertBenefitRiskAnalyses(expected));
	}
	
	@Test
	public void testDefaultDataRoundTripConversion() throws Exception {
		doRoundTripTest(getTransformedDefaultData());
	}


	@Test
	public void testSmallerDataRoundTripConversion() throws Exception {
		doRoundTripTest(getTransformedTestData());
	}

	@Test @Ignore
	public void testMysteriousDataRoundTripConversion() throws Exception {
		doRoundTripTest(getTestData(TEST_DATA_A_1));
	}
	
	@Test
	public void testCombinationTreatmentRoundTripConversion() throws Exception {
		doRoundTripTest(getTestData(TEST_DATA_3));
	}

	private void doRoundTripTest(InputStream transformedXmlStream) throws JAXBException, ConversionException {
		System.clearProperty("javax.xml.transform.TransformerFactory");
		AddisData data = (AddisData) d_unmarshaller.unmarshal(transformedXmlStream);
		sortMeasurements(data);
		sortAnalysisArms(data);
		sortBenefitRiskOutcomes(data);
		sortCategoricalMeasurementCategories(data);
		Domain domainData = JAXBConvertor.convertAddisDataToDomain(data);
		sortPopulationCharacteristics(data);
		sortUsedBys(data);
		AddisData roundTrip = JAXBConvertor.convertDomainToAddisData(domainData);
		sortPopulationCharacteristics(roundTrip);
		assertEquals(data, roundTrip);
	}
		
	private void sortUsedBys(AddisData data) {
		for (org.drugis.addis.entities.data.Study s : data.getStudies().getStudy()) {
			for(org.drugis.addis.entities.data.StudyActivity a : s.getActivities().getStudyActivity()) {
				Collections.sort(a.getUsedBy(), new UsedByComparator());
			}
		}
	}

	private class UsedByComparator implements Comparator<org.drugis.addis.entities.data.ActivityUsedBy> {
		public int compare(ActivityUsedBy o1, ActivityUsedBy o2) {
			if(o1.getArm().compareTo(o2.getArm()) != 0) {
				return o1.getArm().compareTo(o2.getArm());
			}
			return o1.getEpoch().compareTo(o2.getEpoch());
		}
	}
	
	private static void sortCategoricalMeasurementCategories(AddisData data) {
		for (org.drugis.addis.entities.data.Study s : data.getStudies().getStudy()) {
			for (org.drugis.addis.entities.data.Measurement m : s.getMeasurements().getMeasurement()) {
				if (m.getCategoricalMeasurement() != null) {
					CategoricalVariable var = findVariable(data, s, m.getStudyOutcomeMeasure().getId());
					Collections.sort(m.getCategoricalMeasurement().getCategory(), new CategoryMeasurementComparator(var));
				}
			}
		}
	}

	private static CategoricalVariable findVariable(AddisData data, org.drugis.addis.entities.data.Study s, String id) {
		String varName = null;
		for (StudyOutcomeMeasure x : s.getStudyOutcomeMeasures().getStudyOutcomeMeasure()) {
			if (x.getId().equals(id)) {
				varName = x.getPopulationCharacteristic().getName();
			}
		}
		for (OutcomeMeasure x : data.getPopulationCharacteristics().getPopulationCharacteristic()) {
			if (x.getName().equals(varName)) {
				return x.getCategorical();
			}
		}
		return null;
	}

	private static void sortPopulationCharacteristics(AddisData data) {
		Collections.sort(data.getPopulationCharacteristics().getPopulationCharacteristic(), new PopCharComparator());
	}

	private static void sortBenefitRiskOutcomes(AddisData data) {
		for(Object obj : data.getBenefitRiskAnalyses().getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis()) {
			if (obj instanceof org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) {
				org.drugis.addis.entities.data.StudyBenefitRiskAnalysis sbr = (org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) obj;
				Collections.sort(sbr.getOutcomeMeasures().getAdverseEvent(), new NameReferenceComparator());
				Collections.sort(sbr.getOutcomeMeasures().getEndpoint(), new NameReferenceComparator());
			}
		}
	}

	private static void sortAnalysisArms(AddisData data) {
		for(org.drugis.addis.entities.data.MetaAnalysis ma : data.getMetaAnalyses().getPairwiseMetaAnalysisOrNetworkMetaAnalysis()) {
			List<Alternative> alternatives = null;
			if (ma instanceof org.drugis.addis.entities.data.PairwiseMetaAnalysis) {
				org.drugis.addis.entities.data.PairwiseMetaAnalysis pwma = (org.drugis.addis.entities.data.PairwiseMetaAnalysis) ma;
				alternatives = pwma.getAlternative();
			}
			if (ma instanceof org.drugis.addis.entities.data.NetworkMetaAnalysis) {
				org.drugis.addis.entities.data.NetworkMetaAnalysis nwma = (org.drugis.addis.entities.data.NetworkMetaAnalysis) ma;
				alternatives = nwma.getAlternative();
				Collections.sort(alternatives, new AlternativeComparator());
			}
			for (Alternative a : alternatives) {
				Collections.sort(a.getArms().getArm(), new ArmComparator());
			}
		}
	}

	private static void sortMeasurements(AddisData data) {
		for(org.drugis.addis.entities.data.Study s : data.getStudies().getStudy()) {
			Collections.sort(s.getMeasurements().getMeasurement(), new MeasurementComparator(s));
		}
	}
	
	public static class PopCharComparator implements Comparator<org.drugis.addis.entities.data.OutcomeMeasure> {
		public int compare(OutcomeMeasure o1, OutcomeMeasure o2) {
			if (scoreType(o1) != scoreType(o2)) {
				return scoreType(o1) - scoreType(o2);
			}
			return o1.getName().compareTo(o2.getName());
		}

		private int scoreType(OutcomeMeasure o1) {
			if (o1.getContinuous() != null) {
				return 1;
			} else if (o1.getRate() != null) {
				return 2;
			} else if (o1.getCategorical() != null) {
				return 3;
			}
			return 0;
		}
	}
	
	public static class CategoryMeasurementComparator implements Comparator<CategoryMeasurement> {
		private final CategoricalVariable d_var;

		public CategoryMeasurementComparator(CategoricalVariable var) {
			d_var = var;
		}

		public int compare(CategoryMeasurement o1, CategoryMeasurement o2) {
			return d_var.getCategory().indexOf(o1.getName()) - d_var.getCategory().indexOf(o2.getName());
		}
	}
	
	public static class NameReferenceComparator implements Comparator<org.drugis.addis.entities.data.NameReference> {
		public int compare(org.drugis.addis.entities.data.NameReference o1, org.drugis.addis.entities.data.NameReference o2) {
			return o1.getName().compareTo(o2.getName());
		}		
	}
	
	public static class AlternativeComparator implements Comparator<org.drugis.addis.entities.data.Alternative> {
		public int compare(org.drugis.addis.entities.data.Alternative o1, org.drugis.addis.entities.data.Alternative o2) {
			return (o1.getDrugs() != null ? o1.getDrugs().getDrug().get(0) : null).getName().compareTo((o2.getDrugs() != null ? o2.getDrugs().getDrug().get(0) : null).getName());
		}
	}
	
	public static class ArmComparator implements Comparator<org.drugis.addis.entities.data.ArmReference> {
		public int compare(ArmReference o1, ArmReference o2) {
			return o1.getStudy().compareTo(o2.getStudy());
		}
	}
	
	public static class MeasurementComparator implements Comparator<org.drugis.addis.entities.data.Measurement> {
		private org.drugis.addis.entities.data.Study d_study;

		public MeasurementComparator(org.drugis.addis.entities.data.Study s) {
			d_study = s;
		}

		public int compare(org.drugis.addis.entities.data.Measurement o1, org.drugis.addis.entities.data.Measurement o2) {
			int omId1 = findOmIndex(o1.getStudyOutcomeMeasure().getId());
			int omId2 = findOmIndex(o2.getStudyOutcomeMeasure().getId());
			if (omId1 != omId2) {
				return omId1 - omId2;
			}
			if (o1.getArm() == null) {
				return o2.getArm() == null ? 0 : 1;
			}
			if (o2.getArm() == null) {
				return -1;
			}
			return findArmIndex(o1.getArm().getName()) - findArmIndex(o2.getArm().getName());
		}

		private int findArmIndex(String armName1) {
			for (org.drugis.addis.entities.data.Arm a : d_study.getArms().getArm()) {
				if (a.getName().equals(armName1)) {
					return d_study.getArms().getArm().indexOf(a);
				}
			}
			return -1;
		}

		private int findOmIndex(String id) {
			List<StudyOutcomeMeasure> oms = d_study.getStudyOutcomeMeasures().getStudyOutcomeMeasure();
			for (int i = 0; i < oms.size(); ++i) {
				if (oms.get(i).getId().equals(id)) {
					return i;
				}
			}
			return -1;
		}
	}
	
	@Test
	public void testDateWithNotes() {
		String date = "2010-11-12";
		Date oldXmlDate = new GregorianCalendar(2010, 11 -1, 12).getTime();
		DateWithNotes dwn = JAXBConvertor.dateWithNotes(oldXmlDate);
		
		XMLGregorianCalendar cal = XMLGregorianCalendarImpl.parse(date);
		DateWithNotes dwn2 = new DateWithNotes();
		dwn2.setNotes(new Notes());
		dwn2.setValue(cal);
		
		assertEquals(dwn, dwn2);
	}

	@Test
	public void writeTransformedXML() throws TransformerException, IOException {
		InputStream transformedXmlStream = getTransformedDefaultData();
		FileOutputStream output = new FileOutputStream("transformedDefaultData.xml");
		PubMedDataBankRetriever.copyStream(transformedXmlStream, output);
		output.close();
	}

	private static InputStream getTestData(String fileName) throws TransformerException, IOException {
		InputStream is = JAXBConvertorTest.class.getResourceAsStream(fileName);
		XmlFormatType xmlType = JAXBHandler.determineXmlType(is);
		int version = 0;
		if (xmlType.equals(XmlFormatType.SCHEMA_FUTURE)) {
			throw new RuntimeException("XML Version from the future");
		}
		if (xmlType.equals(XmlFormatType.LEGACY)) {
			is = JAXBConvertor.transformLegacyXML(is);
			version = 1;
		} else {
			version = xmlType.getVersion();
		}
		return JAXBConvertor.transformToLatest(is, version);
	}
	
	private static InputStream getTransformedDefaultData() throws TransformerException, IOException {
		return getTestData("../defaultData.addis");
	}

	private static InputStream getTransformedTestData() throws TransformerException, IOException {
		return getTestData(TEST_DATA_A_0);
	}
}
