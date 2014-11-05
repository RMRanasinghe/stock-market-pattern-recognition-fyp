package hmm_predict;

import mloss.roc.Curve;

public class roc_calc {
	double cacl(int[] trueLabels, double[] scores) {
		// Create the analysis. The ranking is computed automatica

		Curve analysis = new Curve.PrimitivesBuilder().predicteds(scores)
				.actuals(trueLabels).build();

		// Calculate AUC ROC
		double area = analysis.rocArea();

		// Convex hull
		// Curve hull = analysis.convexHull();
		// double maxArea = hull.rocArea();
		//
		// // Points for plotting the curve
		// double[][] points = hull.rocPoints();
		return area;
	}

}
