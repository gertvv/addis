package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

public class StudyAddPatientGroupPresentation {
	private PresentationModelFactory d_pmf;
	private BasicStudy d_study;
	private BasicPatientGroupPresentation d_pg;
	private Map<Endpoint, Measurement> d_measurements = new HashMap<Endpoint,Measurement>();
	
	public StudyAddPatientGroupPresentation(BasicStudy study, PresentationModelFactory pmf) {
		d_pmf = pmf;
		d_study = study;
		BasicPatientGroup pg = new BasicPatientGroup(null, new FlexibleDose(new Interval<Double>(0.0, 0.0), SIUnit.MILLIGRAMS_A_DAY), 0);
		d_pg = (BasicPatientGroupPresentation)d_pmf.getModel(pg);
		
		for (Endpoint e : d_study.getEndpoints()) {
			BasicMeasurement m = e.buildMeasurement(pg);
			if (m instanceof BasicMeasurement) {
				final BasicMeasurement rm = (BasicMeasurement)m;
				getSizeModel().addValueChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (rm.getSampleSize().equals(0)) {
							rm.setSampleSize((Integer)evt.getNewValue());
						}
					}
				});
			}
			d_measurements.put(e, m);
		}
	}
	
	public BasicPatientGroup getPatientGroup() {
		return d_pg.getBean();
	}
	
	public DosePresentationModel getDoseModel() {
		return d_pg.getDoseModel();
	}
	
	public List<Endpoint> getEndpoints(Endpoint.Type type) {
		List<Endpoint> result = new ArrayList<Endpoint>();
		for (Endpoint e : d_study.getEndpoints()) {
			if (e.getType().equals(type)) {
				result.add(e);
			}
		}
		return result;
	}
	
	public boolean hasEndpoints(Endpoint.Type type) {
		return !getEndpoints(type).isEmpty();
	}
	
	public PresentationModel<Measurement> getMeasurementModel(Endpoint e) {
		return d_pmf.getModel(d_measurements.get(e));
	}
	
	public AbstractValueModel getDrugModel() {
		return d_pg.getModel(BasicPatientGroup.PROPERTY_DRUG);
	}
	
	public AbstractValueModel getSizeModel() {
		return d_pg.getModel(BasicPatientGroup.PROPERTY_SIZE);
	}
	
	public void addToStudy() {
		d_study.addPatientGroup(getPatientGroup());
		for (Endpoint e: d_study.getEndpoints()) {
			d_study.setMeasurement(e, getPatientGroup(), d_measurements.get(e));
		}
	}
}