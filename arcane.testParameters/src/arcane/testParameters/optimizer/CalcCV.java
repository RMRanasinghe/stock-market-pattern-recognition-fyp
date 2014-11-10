package arcane.testParameters.optimizer;

import java.util.Iterator;
import java.util.Queue;

public class CalcCV {

	Double calc(Queue<Double> trueVal, Queue<Double> smoothedValue) {

		Double cv = 0.0;
		int size = trueVal.size();
		Iterator<Double> itrP = null;
		Iterator<Double> itrV = null;
		itrP = trueVal.iterator();
		itrV = smoothedValue.iterator();
		double err = 0;
		for (int i = 0; i < size; i++) {
			err = itrP.next() - itrV.next();
			cv += err * err;
		}
		cv = cv / size;
		return cv;

	}

}
