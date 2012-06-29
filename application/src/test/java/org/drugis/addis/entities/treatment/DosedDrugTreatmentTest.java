package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class DosedDrugTreatmentTest {
	private static final DoseUnit MG_DAY = ExampleData.MILLIGRAMS_A_DAY;
	
	private DosedDrugTreatment d_treatment;

	@Before
	public void setUp() {
		d_treatment = new DosedDrugTreatment("", ExampleData.buildDrugCandesartan());
	}
	
	@Test
	public void testInitialization() {
		Drug drugCandesartan = ExampleData.buildDrugCandesartan();
		assertEquals(drugCandesartan, d_treatment.getDrug());
		assertTrue((d_treatment.getRootNode() instanceof LeafNode));
	}
	
	@Test 
	public void testAddCategory() {
		CategoryNode categoryNode = new CategoryNode("foo");
		d_treatment.addCategory(categoryNode);
		assertEquals(Arrays.asList(categoryNode), d_treatment.getCategories());
	}
	
	@Test
	public void testCategorization() {
		TypeNode rootNode = new TypeNode(FixedDose.class, new CategoryNode("Fixed Dose"));
		DoseRangeNode maxRangeNode = new DoseRangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, MG_DAY, 
				new CategoryNode("Flexible dose"));
		maxRangeNode.addCutOff(100, false, new ExcludeNode());

		DoseRangeNode minRangeNode = new DoseRangeNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, MG_DAY, 
				new ExcludeNode());
		minRangeNode.addCutOff(50.0, true, maxRangeNode);
		rootNode.setType(FlexibleDose.class, minRangeNode);
		
		FixedDose fixedDose = new FixedDose(10.0, MG_DAY);
		FlexibleDose lowFlexibleDose = new FlexibleDose(new Interval<Double>(0.0, 10.0), MG_DAY);
		FlexibleDose midFlexibleDose = new FlexibleDose(new Interval<Double>(50.0, 100.0), MG_DAY);
		FlexibleDose highFlexibleDose = new FlexibleDose(new Interval<Double>(75.0, 150.0), MG_DAY);
		
		d_treatment.setRootNode(rootNode);
		
		
		assertEquals("Fixed Dose", d_treatment.getCategoryNode(fixedDose).getName());
		assertEquals(ExcludeNode.NAME, d_treatment.getCategoryNode(lowFlexibleDose).getName());
		assertEquals("Flexible dose", d_treatment.getCategoryNode(midFlexibleDose).getName());
		assertEquals(ExcludeNode.NAME, d_treatment.getCategoryNode(highFlexibleDose).getName());
	}

}