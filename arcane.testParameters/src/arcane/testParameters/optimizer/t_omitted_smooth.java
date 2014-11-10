package arcane.testParameters.optimizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class t_omitted_smooth {
	private int bw;

	/**
	 * 
	 * @param x
	 *            X value for calculate Gaussian Kernel
	 * @return Double value of calculated Gaussian kernel
	 */
	private Double gaussianKernel(int x) {
		Double kernVal = 0.0;
		kernVal = (Math.pow(Math.E, (-x * x) / (2 * bw * bw)))
				/ (bw * Math.sqrt(2 * Math.PI));
		return kernVal;
	}

	/**
	 * 
	 * @param input
	 *            input Queue
	 * @param bw
	 *            bandwidth
	 * @return Gaussian kernel regression smoothed Queue
	 */
	public Queue<Double> smooth(Queue<Double> input, int bw) {
		
		Double [] inputArray=input.toArray(new Double[input.size()]);
		
		this.bw = bw;
		Queue<Double> output = null;
		int size = input.size();
		Double kernUp = 0.0;
		Double kernDown = 0.0;
		//Iterator<Double> itr = null;
		Double price = 0.0;
		Double kern = 0.0;
		output = new LinkedList<Double>();
		for (int i = 0; i < inputArray.length; ++i) {
			kernUp = 0.0;
			kernDown = 0.0;
			//itr = input.iterator();
			int j = 0;
			while (j<inputArray.length) {
				if (j != i) {
					price = inputArray[j];
					kern = gaussianKernel(i - j);
					kernUp += kern * price;
					kernDown += kern;
				}
				++j;

			}
			output.add(kernUp / kernDown);
		}
		return output;
	}

}
