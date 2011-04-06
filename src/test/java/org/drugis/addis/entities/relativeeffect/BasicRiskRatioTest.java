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

package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class BasicRiskRatioTest {
	BasicMeasurement d_numerator;
	BasicMeasurement d_denominator;
	RelativeEffect<RateMeasurement> d_ratio;
	
	
	private Drug d_fluox;
	private Drug d_sertra;
	
	private Indication d_ind;
	private Endpoint d_ep;
	
	private Study d_bennie, d_boyer, d_fava, d_newhouse, d_sechter;
	
	private RelativeEffect<?> d_ratioBennie;
	private RelativeEffect<?> d_ratioBoyer;
	private RelativeEffect<?> d_ratioFava;
	private RelativeEffect<?> d_ratioNewhouse;
	private RelativeEffect<?> d_ratioSechter;
	
	@Before
	public void setUp() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertra = new Drug("Sertraline","02");
		d_ep = new Endpoint("ep", Variable.Type.RATE);
		
		d_bennie = createStudy("Bennie 1995",63,144,73,142);
		d_boyer = createStudy("Boyer 1998", 61,120, 63,122);
		d_fava = createStudy("Fava 2002", 57, 92, 70, 96);
		d_newhouse = createStudy("Newhouse 2000", 84,119, 85,117);
		d_sechter = createStudy("Sechter 1999", 76,120, 86,118);
				
		
		d_ratioBennie = RelativeEffectFactory.buildRelativeEffect(d_bennie, d_ep, d_fluox, d_sertra, BasicRiskRatio.class);
		d_ratioBoyer = RelativeEffectFactory.buildRelativeEffect(d_boyer, d_ep, d_fluox, d_sertra, BasicRiskRatio.class);
		d_ratioFava = RelativeEffectFactory.buildRelativeEffect(d_fava, d_ep, d_fluox, d_sertra, BasicRiskRatio.class);
		d_ratioNewhouse = RelativeEffectFactory.buildRelativeEffect(d_newhouse, d_ep, d_fluox, d_sertra, BasicRiskRatio.class);
		d_ratioSechter = RelativeEffectFactory.buildRelativeEffect(d_sechter, d_ep, d_fluox, d_sertra, BasicRiskRatio.class);		
	}
	

	@Test
	public void testGetMean() {
		assertEquals(1.18, d_ratioBennie.getConfidenceInterval().getPointEstimate(), 0.01);
		assertEquals(1.02, d_ratioBoyer.getConfidenceInterval().getPointEstimate(), 0.01); 
		assertEquals(1.18, d_ratioFava.getConfidenceInterval().getPointEstimate(), 0.01);
		assertEquals(1.03, d_ratioNewhouse.getConfidenceInterval().getPointEstimate(), 0.01);
		assertEquals(1.15, d_ratioSechter.getConfidenceInterval().getPointEstimate(), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalBennie() {
		Interval<Double> ival = d_ratioBennie.getConfidenceInterval();
		assertEquals(0.92, (ival.getLowerBound()), 0.01);
		assertEquals(1.50, (ival.getUpperBound()), 0.01);
	}
	
	@Test
	public void testGetConfidenceIntervalBoyer() {
		Interval<Double> ival = d_ratioBoyer.getConfidenceInterval();
		assertEquals(0.79, (ival.getLowerBound()), 0.01); 
		assertEquals(1.30, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalFava() {
		Interval<Double> ival = d_ratioFava.getConfidenceInterval();
		assertEquals(0.96, (ival.getLowerBound()), 0.01); 
		assertEquals(1.45, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalNewhouse() {
		Interval<Double> ival = d_ratioNewhouse.getConfidenceInterval();
		assertEquals(0.87, (ival.getLowerBound()), 0.01); 
		assertEquals(1.21, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetConfidenceIntervalSechter() {
		Interval<Double> ival = d_ratioSechter.getConfidenceInterval();
		assertEquals(0.97, (ival.getLowerBound()), 0.01); 
		assertEquals(1.38, (ival.getUpperBound()), 0.01); 
	}
	
	@Test
	public void testGetName() {
		assertEquals("Risk ratio", d_ratioSechter.getName());
	}
	
	@Test
	public void testZeroBaselineRateShouldBeUndefined() {
		RateMeasurement base = new BasicRateMeasurement(0, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		BasicRiskRatio or = new BasicRiskRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testFullSubjectRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(100, 100);
		BasicRiskRatio or = new BasicRiskRatio(base, subj);
		assertTrue(or.isDefined());
	}

	@Test
	public void testZeroSubjectRateShouldBeUndefined() { // although we can calculate a point-estimate, we can't get a CI.
		RateMeasurement base = new BasicRateMeasurement(50, 100);
		RateMeasurement subj = new BasicRateMeasurement(0, 100);
		BasicRiskRatio or = new BasicRiskRatio(base, subj);
		assertFalse(or.isDefined());
	}

	@Test
	public void testFullBaselineRateShouldBeDefined() {
		RateMeasurement base = new BasicRateMeasurement(100, 100);
		RateMeasurement subj = new BasicRateMeasurement(50, 100);
		BasicRiskRatio or = new BasicRiskRatio(base, subj);
		assertTrue(or.isDefined());
	}

	@Test
	public void testUndefinedShouldResultInNaN() {
		RateMeasurement rmA1 = new BasicRateMeasurement(0, 100);
		RateMeasurement rmC1 = new BasicRateMeasurement(50, 100);
		BasicOddsRatio or = new BasicOddsRatio(rmA1, rmC1);
		assertEquals(Double.NaN, or.getError(), 0.001);
		assertEquals(Double.NaN, or.getConfidenceInterval().getPointEstimate(), 0.001);
	}
	
	private Study createStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize) {
		Study s = new Study(studyName, d_ind);
		s.addEndpoint(d_ep);
		Arm g_fluox = new Arm("fluox", fluoxSize, d_fluox, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY));
		Arm g_parox = new Arm("sertr", sertraSize, d_sertra, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY));		
		
		s.addArm(g_parox);
		s.addArm(g_fluox);
		
		BasicRateMeasurement m_parox = (BasicRateMeasurement) d_ep.buildMeasurement(g_parox);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_ep.buildMeasurement(g_fluox);
		
		m_parox.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
		
		s.setMeasurement(d_ep, g_parox, m_parox);
		s.setMeasurement(d_ep, g_fluox, m_fluox);		
		
		return s;
	}			
}
