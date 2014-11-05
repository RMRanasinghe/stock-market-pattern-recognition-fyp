package hmm_predict;

import java.util.Arrays;

public class HMM {

	public static void main(String[] args) {
		System.out.println("HMM running...");
		data_read dataReader = new data_read();
		Integer[] input_tarin = dataReader.read("data/IBM_Train.csv");
		int[] train = convertToInt(input_tarin);

		// System.out.println(train.length+" "+train[0]+" "+train[1]+" "+train[129]+" ");
		int [] train_set=Arrays.copyOfRange(train,0,200);
		int [] test_set=Arrays.copyOfRange(train,196,220);
		double[][][][][] A = order4_Transition_Prob_Calc(train_set, 2);

		Integer[] input_test = dataReader.read("data/IBM_Test.csv");
		int[] test = convertToInt(input_test);
		// System.out.println(test[0]+" "+test[1]+" "+test[2]+" "+test[3]);
		double[] result = new double[test_set.length - 4];
		for (int i = 3; i < test_set.length - 1; i++) {
			result[i - 3] = computeIncreasingProbability_order4(test_set, A);
		}

		int[] trueValues = Arrays.copyOfRange(test_set, 4, test_set.length);
		System.out.println("true length:" + trueValues.length + " prediction: "
				+ result.length);

		roc_calc ROC = new roc_calc();
		double ROC_score = ROC.cacl(trueValues, result);
		System.out.println("ROC Score : " + ROC_score);

		// double [][][][][] A1=order4_Transition_Prob_Calc(new
		// int[]{1,1,0,1,0,0,0,1,1,1,1,0,1,0,0,0,1,1,1,1,0,1,0,0,0,1,1,1,1,0,1,0,0,0,1,1,1,1,0,1,0,0,0,1,1,1},2);
		// double prob_inc=computeProbability(0,new int[]{1,1,1,1,1,1},A);
		// double prob_dec=computeProbability(0,new int[]{0,0,0,0,0,0},A);
		// double prob_min=computeProbability(0,new int[]{0,0,0,1,1,1},A);
		// double prob_max=computeProbability(0,new int[]{1,1,1,0,0,0},A);
		// System.out.println(prob_inc+" "+prob_dec+" "+prob_min+" "+prob_max);

	}

	// 1st order markove chain transition prob calc.
	static double[][] Transition_Prob_Calc(int[] l, int numStates) {

		double[] denom = new double[numStates];
		double[][] A = new double[numStates][numStates];

		for (int i = 0; i < l.length - 1; i++) { // omit final state for
													// calculation
			A[l[i]][l[i + 1]] = A[l[i]][l[i + 1]] + 1;
		}
		for (int i = 0; i < numStates; i++) {
			denom[i] = 0;
			for (int j = 0; j < numStates; j++) {
				denom[i] = denom[i] + A[i][j];
			}
		}
		for (int i = 0; i < numStates; i++) {
			if (denom[i] > 0) {
				for (int j = 0; j < numStates; j++) {
					A[i][j] = A[i][j] / denom[i];
				}
			}
		}
		for (int i = 0; i < numStates; i++) {
			for (int j = 0; j < numStates; j++) {
				System.out.print(A[i][j] + " ");
			}
			System.out.print('\n');
		}
		return A;
	}

	// 4th order markove chain transition prob calc.
	static double[][][][][] order4_Transition_Prob_Calc(int[] l, int numStates) {
		double[][][][] denom = new double[numStates][numStates][numStates][numStates];
		double[][][][][] A = new double[numStates][numStates][numStates][numStates][numStates];

		for (int i = 3; i < l.length - 1; i++) { // omit final state for
													// calculation
			A[l[i - 3]][l[i - 2]][l[i - 1]][l[i]][l[i + 1]] = A[l[i - 3]][l[i - 2]][l[i - 1]][l[i]][l[i + 1]] + 1;
		}
		for (int i = 0; i < numStates; i++) {
			for (int j = 0; j < numStates; j++) {
				for (int k = 0; k < numStates; k++) {
					for (int m = 0; m < numStates; m++) {
						denom[i][j][k][m] = 0;
						for (int n = 0; n < numStates; n++) {
							denom[i][j][k][m] = denom[i][j][k][m]
									+ A[i][j][k][m][n];
						}
					}
				}

			}
		}
		for (int i = 0; i < numStates; i++) {
			for (int j = 0; j < numStates; j++) {
				for (int k = 0; k < numStates; k++) {
					for (int m = 0; m < numStates; m++) {
						if (denom[i][j][k][m] > 0) {
							for (int n = 0; n < numStates; n++) {
								A[i][j][k][m][n] = A[i][j][k][m][n]
										/ denom[i][j][k][m];
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < numStates; i++) {
			for (int j = 0; j < numStates; j++) {
				for (int k = 0; k < numStates; k++) {
					for (int m = 0; m < numStates; m++) {
						for (int n = 0; n < numStates; n++) {
							System.out.print(A[i][j][k][m][n] + " ");
						}
						System.out.print('\n');
					}
				}
			}
		}
		return A;
	}

	static double computeProba_order1(int current_state, int[] sequence,
			double[][] A) {
		double res = 0;
		double init = A[current_state][sequence[0]];
		res = init;
		for (int i = 0; i < sequence.length - 1; i++) {
			res = res * A[sequence[i]][sequence[i + 1]];
		}
		return res;
	}


	static double computeIncreasingProbability_order4(int[] current_state,
			double[][][][][] A) {

		double p = A[current_state[0]][current_state[1]][current_state[2]][current_state[3]][1];
		return p;
	}

	static int[] convertToInt(Integer[] integers) {
		int[] int_array = new int[integers.length];
		for (int i = 0; i < int_array.length; i++) {
			int_array[i] = integers[i];
		}
		return int_array;
	}

}
