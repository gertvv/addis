package org.drugis.addis.entities;

import java.util.Set;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;


public class MeanDifference extends AbstractEntity implements RelativeEffect<ContinuousMeasurement>{
	private static final long serialVersionUID = 5993936352514545950L;

	private ContinuousMeasurement d_subject;
	private ContinuousMeasurement d_baseline;

	/**
	 * The MeanDifference of two ContinuousMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param baseline
	 * @param subject
	 */
	
	public MeanDifference(ContinuousMeasurement baseline, ContinuousMeasurement subject) throws IllegalArgumentException {
		if (!subject.getEndpoint().equals(baseline.getEndpoint())) {
			throw new IllegalArgumentException();
		}
		d_subject = subject;
		d_baseline = baseline;
	}
	
	public Interval<Double> getConfidenceInterval() {
		double t = StudentTTable.getT(d_subject.getPatientGroup().getSize() + d_baseline.getPatientGroup().getSize() - 2);
		return new Interval<Double>(getRelativeEffect() - t*getError(),getRelativeEffect() + t*getError());
	}

	public ContinuousMeasurement getBaseline() {
		return d_baseline;
	}

	public Endpoint getEndpoint() {
		return d_subject.getEndpoint();
	}

	public ContinuousMeasurement getSubject() {
		return d_subject;
	}

	public Double getRelativeEffect() {
		return d_subject.getMean() - d_baseline.getMean();
	}
	
	public Double getError() {
		return Math.sqrt(square(d_subject.getStdDev()) / (double) d_subject.getPatientGroup().getSize() 
						+ square(d_baseline.getStdDev()) / (double) d_baseline.getPatientGroup().getSize());
	}

	public Integer getSampleSize() {
		return d_subject.getPatientGroup().getSize() + d_baseline.getPatientGroup().getSize();
	}
	
	private Double square(double x) {
		return x*x;
	}

	@Override
	public Set<Entity> getDependencies() {
		return null;
	}

	public String getName() {
		return "Mean Difference";
	}

	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}
}
