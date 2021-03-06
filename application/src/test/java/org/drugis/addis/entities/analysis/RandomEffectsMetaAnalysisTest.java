/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.drugis.addis.ADDISTestUtil;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.entities.relativeeffect.RelativeEffectTestBase;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class RandomEffectsMetaAnalysisTest extends RelativeEffectTestBase {
	/* 
	 * Test data from Figure 2 in "Efficacy and Safety of Second-Generation 
	 * Antidepressants in the Treatment of Major Depressive Disorder" 
	 * by Hansen et al. 2005
	 * 
	 */
	
	RandomEffectsMetaAnalysis d_rema;
	private List<Study> d_studyList;

	@Before
	public void setUp() {
		d_studyList = new ArrayList<Study>();
		d_studyList.add(d_bennie);
		d_studyList.add(d_boyer);
		d_studyList.add(d_fava);
		d_studyList.add(d_newhouse);
		d_studyList.add(d_sechter);
		d_rema = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
	}
	
	@Test
	public void testGetStudyArms() {
		List<StudyArmsEntry> entries = d_rema.getStudyArms();
		assertEquals(d_studyList.size(), entries.size());
		for (int i = 0; i < d_studyList.size(); ++i) {
			StudyArmsEntry studyArmsEntry = entries.get(i);
			assertEquals(d_studyList.get(i), studyArmsEntry.getStudy());
			assertEquals(TreatmentDefinition.createTrivial(d_fluox), studyArmsEntry.getStudy().getTreatmentDefinition(studyArmsEntry.getBase()));
			assertEquals(TreatmentDefinition.createTrivial(d_sertr), studyArmsEntry.getStudy().getTreatmentDefinition(studyArmsEntry.getSubject()));
			assertTrue(d_studyList.get(i).getArms().contains(studyArmsEntry.getBase()));
			assertTrue(d_studyList.get(i).getArms().contains(studyArmsEntry.getSubject()));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentIndicationsThrows() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = ExampleData.buildRateStudy("name", 0, 10, 0, 20);
		newStudy.setIndication(newInd);
		d_studyList.add(newStudy);
		d_rema = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentDrugs() {
		Indication newInd = new Indication(666L, "bad");
		Study newStudy = ExampleData.buildRateStudy("name", 0, 10, 0, 20);
		newStudy.setIndication(newInd);
		List<StudyArmsEntry> armsList = new ArrayList<StudyArmsEntry>();
		Arm subject = newStudy.getArms().get(1);
		Arm base = newStudy.getArms().get(0);
		armsList.add(new StudyArmsEntry(newStudy, base, subject));
		armsList.add(new StudyArmsEntry(newStudy, subject, base));
		
		d_rema = new RandomEffectsMetaAnalysis("meta", d_rateEndpoint, newStudy.getTreatmentDefinition(base), newStudy.getTreatmentDefinition(subject), armsList, false);
	}
	
	@Test
	public void testCategoryMatching() {
		TreatmentCategorization catz = TreatmentCategorization.createDefault("Include Fixed Dose", d_fluox, DoseUnit.createMilliGramsPerDay());
		Category fluoxCat = new Category(catz, "Include");
		catz.addCategory(fluoxCat);
		DecisionTree tree = catz.getDecisionTree();
		tree.replaceChild(tree.findMatchingEdge(tree.getRoot(), FixedDose.class), new LeafNode(fluoxCat));
		Category sertrCat = Category.createTrivial(d_sertr);
		List<StudyArmsEntry> entries = ExampleData.buildStudyArmEntries(d_studyList,  TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
		
		// The following should *not* throw an IllegalArgumentException
		new RandomEffectsMetaAnalysis("meta", d_rateEndpoint,
				new TreatmentDefinition(fluoxCat),
				new TreatmentDefinition(sertrCat),
				entries, false);
	}
	
	
	@Test
	public void testGetToString() {
		assertEquals(d_rema.getName(), d_rema.toString());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(144+142+120+122+92+96+119+117+120+118, d_rema.getSampleSize());
	}
	
	@Test
	public void testGetFirstDrug() {
		assertEquals(TreatmentDefinition.createTrivial(d_fluox), d_rema.getFirstAlternative());
	}
	
	@Test
	public void testGetSecondDrug() {
		assertEquals(TreatmentDefinition.createTrivial(d_sertr), d_rema.getSecondAlternative());
	}
	
	@Test
	public void testIncludedDrugs() {
		assertEquals(Arrays.asList(new TreatmentDefinition [] {TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr)}), d_rema.getAlternatives());
	}
	
	@Test
	public void testGetStudies() {
		assertEquals(d_studyList, d_rema.getIncludedStudies());
	}
	
	@Test
	public void testGetName() {
		JUnitUtil.testSetter(d_rema, RandomEffectsMetaAnalysis.PROPERTY_NAME, "meta", "newname");
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_rateEndpoint, d_rema.getOutcomeMeasure());
	}
	
	@Test
	public void testGetIndication() {
		assertEquals(d_ind, d_rema.getIndication());
	}	
	
	public double calculateI2(double hetr, int k) {
		return Math.max(0, 100 * ((hetr-(k-1)) / hetr ));
	}
	
	@Test
	public void testGetRiskRatioRelativeEffect() {
		RandomEffectMetaAnalysisRelativeEffect<Measurement> riskRatio = d_rema.getRelativeEffect(BasicRiskRatio.class);
		assertEquals(2.03, riskRatio.getHeterogeneity(), 0.01);
		assertEquals(calculateI2(2.03,d_rema.getIncludedStudies().size()), riskRatio.getHeterogeneityI2(), 0.01);
		assertEquals(1.10, (riskRatio.getConfidenceInterval().getPointEstimate()), 0.01); 
		assertEquals(1.01, (riskRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.20, (riskRatio.getConfidenceInterval().getUpperBound()), 0.01);		
	}
	
	@Test
	public void testGetOddsRatioRelativeEffect() {
		RandomEffectMetaAnalysisRelativeEffect<Measurement> oddsRatio = d_rema.getRelativeEffect(BasicOddsRatio.class);
		assertEquals(2.14, oddsRatio.getHeterogeneity(), 0.01);
		assertEquals(1.30, (oddsRatio.getConfidenceInterval().getPointEstimate()), 0.01);
		assertEquals(1.03, (oddsRatio.getConfidenceInterval().getLowerBound()), 0.01);
		assertEquals(1.65, (oddsRatio.getConfidenceInterval().getUpperBound()), 0.01);
	}
	
	@Test
	public void testContinuousMetaAnalysis() {
		Study s1 = ExampleData.buildContinuousStudy("s1", 50, 4, 2, 50, 6, 2);
		Study s2 = ExampleData.buildContinuousStudy("s2", 50, 4, 2, 50, 7, 2);
		List<Study> studies = new ArrayList<Study>();
		studies.add(s1);
		studies.add(s2);
		
		RandomEffectsMetaAnalysis ma = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_contEndpoint, studies, TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
		RandomEffectMetaAnalysisRelativeEffect<Measurement> relativeEffect = ma.getRelativeEffect(BasicMeanDifference.class);
		assertEquals(2.5, relativeEffect.getConfidenceInterval().getPointEstimate(), 0.01);
	}
		
	@Test
	public void testGetDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.add(d_fluox);
		deps.add(d_sertr);
		deps.add(DoseUnit.createMilliGramsPerDay().getUnit());
		deps.add(d_ind);
		deps.add(d_rateEndpoint);
		deps.addAll(Arrays.asList(new Study[]{d_bennie, d_boyer, d_fava, d_newhouse, d_sechter}));
		
		assertEquals(deps, d_rema.getDependencies());
	}
	
	@Test
	public void testFilterUndefinedRelativeEffects() {
		List<RelativeEffect<? extends Measurement>> expected = d_rema.getFilteredRelativeEffects(BasicOddsRatio.class);
		Study zeroRate = ExampleData.buildRateStudy("ZeroRate 2012", 0, 120, 86, 118);
		d_studyList.add(zeroRate);
		RandomEffectsMetaAnalysis rema = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_rateEndpoint, d_studyList, TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
		List<RelativeEffect<? extends Measurement>> actual = rema.getFilteredRelativeEffects(BasicOddsRatio.class);
		assertFalse(RelativeEffectFactory.buildRelativeEffect(zeroRate, d_rateEndpoint, TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr), BasicOddsRatio.class, false).isDefined());
		ADDISTestUtil.assertRelativeEffectListEquals(expected, actual);
	}
	
	@Test
	public void testPooledIsNullWhenAllUndefined() {
		Study zeroRate = ExampleData.buildRateStudy("ZeroRate 2012", 0, 120, 86, 118);
		RandomEffectsMetaAnalysis rema = ExampleData.buildRandomEffectsMetaAnalysis("meta", d_rateEndpoint, Collections.singletonList(zeroRate), TreatmentDefinition.createTrivial(d_fluox), TreatmentDefinition.createTrivial(d_sertr));
		assertNull(rema.getRelativeEffect(BasicOddsRatio.class));
	}
}
