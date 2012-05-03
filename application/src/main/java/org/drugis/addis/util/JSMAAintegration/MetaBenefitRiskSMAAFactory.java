package org.drugis.addis.util.JSMAAintegration;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.mtc.summary.MultivariateNormalSummary;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.MultivariateGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.PerCriterionMeasurements;
import fi.smaa.jsmaa.model.RelativeGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;

public class MetaBenefitRiskSMAAFactory extends AbstractBenefitRiskSMAAFactory<DrugSet> {
	private final MetaBenefitRiskAnalysis d_brAnalysis;

	public MetaBenefitRiskSMAAFactory(MetaBenefitRiskAnalysis brAnalysis) {
		d_brAnalysis = brAnalysis;
	}

	public SMAAModel createSMAAModel() {
		PerCriterionMeasurements measurements = new PerCriterionMeasurements(Collections.<Criterion> emptyList(), Collections.<Alternative> emptyList());
		SMAAModel smaaModel = new SMAAModel(d_brAnalysis.getName(), measurements);
		addCriteriaAndAlternatives(smaaModel, d_brAnalysis);

		for(OutcomeMeasure om : d_brAnalysis.getCriteria()) {
			CriterionMeasurement m = createMeasurement(smaaModel.getAlternatives(), om);
			measurements.setCriterionMeasurement(getCriterion(om), m);
		}
		return smaaModel;
	}

	private CriterionMeasurement createMeasurement(List<Alternative> alts, OutcomeMeasure om) {
		GaussianMeasurement baseline = new GaussianMeasurement(
				d_brAnalysis.getBaselineDistribution(om).getMu(),
				d_brAnalysis.getBaselineDistribution(om).getSigma());
		MultivariateNormalSummary reSummary = d_brAnalysis.getRelativeEffectsSummary(om);
		MultivariateGaussianCriterionMeasurement delta = new MultivariateGaussianCriterionMeasurement(alts);
		double[] meanVector = createMeanVector(d_brAnalysis.getAlternatives(), d_brAnalysis.getBaseline(), reSummary.getMeanVector());
		double[][] covMatrix = createCovarianceMatrix(d_brAnalysis.getAlternatives(), d_brAnalysis.getBaseline(), reSummary.getCovarianceMatrix());
		delta.setMeanVector(new ArrayRealVector(meanVector));
		delta.setCovarianceMatrix(new Array2DRowRealMatrix(covMatrix));
		RelativeGaussianCriterionMeasurement relative = new RelativeGaussianCriterionMeasurement(delta, baseline);

		CriterionMeasurement m = null;
		if (om.getVariableType() instanceof RateVariableType) {
			m = new RelativeLogitGaussianCriterionMeasurement(relative);
		} else if (om.getVariableType() instanceof ContinuousVariableType) {
			m = relative;
		}
		return m;
	}

	/**
	 * Add the baseline to a covariance matrix that excludes the baseline by inserting an all-zero row and column at the baseline's index.
	 * @param alternatives Ordered list of alternatives.
	 * @param baseline The baseline.
	 * @param covarianceMatrix A covariance matrix for all alternatives except the baseline.
	 * @return A covariance matrix for all alternatives including the baseline.
	 */
	static double[][] createCovarianceMatrix(List<DrugSet> alternatives, DrugSet baseline, double[][] covarianceMatrix) {
		final int n = alternatives.size();
		final int b = alternatives.indexOf(baseline);

		double newCovMatrix[][] = new double[n][n];
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i == b || j == b) {
					newCovMatrix[i][j] = 0.0;
				} else {
					newCovMatrix[i][j] =  covarianceMatrix[i < b ? i : i - 1][j < b ? j : j - 1];
				}
			}
		}
		return newCovMatrix;
	}

	/**
	 * Add the baseline to a mean vector that excludes the baseline by inserting a zero at the baseline's index.
	 * @param alternatives Ordered list of alternatives.
	 * @param baseline The baseline.
	 * @param covarianceMatrix A mean vector for all alternatives except the baseline.
	 * @return A mean vector for all alternatives including the baseline.
	 */
	static double[] createMeanVector(List<DrugSet> alternatives, DrugSet baseline, double[] meanVector) {
		final int n = alternatives.size();
		final int b = alternatives.indexOf(baseline);

		double[] newMeanVector = new double[n];
		for (int i = 0; i < n; ++i) {
			if (i == b) {
				newMeanVector[i]  = 0.0;
			} else {
				newMeanVector[i] = meanVector[i < b ? i : i - 1];
			}
		}
		return newMeanVector;
	}

	@Override
	protected Alternative createAlternative(DrugSet a) {
		return new Alternative(a.getLabel());
	}

}
