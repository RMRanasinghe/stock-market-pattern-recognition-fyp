package org.test.cep.extension;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Helper {
	
	private int bw = 0;    //bandwith
	private double Q = 0.000001;
    private double R = 0.0001;
    private double P = 1, X = 0, K;
	/**
	 * 
	 * @param s Queue
	 * @return the max value of the queue
	 */
	public Double max(Queue<Double> s){
		Iterator<Double> itr = s.iterator();
		Double m= Double.MIN_VALUE;
		while(itr.hasNext()){
			Double nxt = itr.next();
			if(m<nxt){
				m=nxt;
			}
		}
		//log.info(m);
		return m;
	}
	
	/**
	 * 
	 * @param s queue
	 * @return the min of the queue
	 */
	public Double min(Queue<Double> s){
		Iterator<Double> itr = s.iterator();
		Double m= Double.MAX_VALUE;
		while(itr.hasNext()){
			Double nxt = itr.next();
			if(m>nxt){
				m=nxt;
			}
		}
		//log.info(m);
		return m;
	}
	
	/**
	 * 
	 * @param x X value for calculate Gaussian Kernel
	 * @return Double value of calculated Gaussian kernel
	 */
	private Double gaussianKernel(int x){
		Double kernVal = 0.0;
		
		kernVal = (Math.pow(Math.E, (-x*x)/(2.0*bw*bw)))/(bw*Math.sqrt(2*Math.PI));
		
		return kernVal;
	}
	
	/**
	 * 
	 * @param input input Queue
	 * @param bw bandwidth
	 * @return Gaussian kernel regression smoothed Queue
	 */
	public Queue<Double> smooth(Queue<Double> input, int bw){
		this.bw = bw;
		Queue<Double> output = null;
		
		int size = input.size();
		Double kernUp = 0.0;
		Double kernDown = 0.0;
		Iterator<Double> itr = null;
		Double price = 0.0;
		Double kern = 0.0;
		output = new LinkedList<Double>();
		
		for(int i = 0 ; i < size ; ++i){
			kernUp = 0.0;
			kernDown = 0.0;
			itr = input.iterator();
			int j = 0;
			
			while(itr.hasNext()){
				price = itr.next();
				kern = gaussianKernel(i-j);
				kernUp += kern*price;
				kernDown += kern;
				++j;
			}
			output.add(kernUp/kernDown);
		}
		
		return output;
	}
	
	/**
	 * Finds maximum Local value of a Queue uses rule based approach
	 * @param input
	 * @param bandwidth Considering neighborhood
	 * @return Integer; location of the maximum if exist or returns nulls
	 */
	public Integer findMax(Queue<Double> input, int bandwidth){
		int size = input.size();
		Object[] inputArray = input.toArray();
		for(int i = (size-bandwidth)-1;i>=bandwidth;--i){
			Double max = Double.MIN_VALUE;
			for(int j=i-bandwidth;j<=i+bandwidth;++j){
				if((Double)inputArray[j]>max){
					max = (Double)inputArray[j];
				}
			}
			
			if((Double)inputArray[i]==max){
				return i;
			}
			
		}
		return null;
	}

    /**
     * Finds minimum Local value of a Queue uses rule based approach
     * @param input
     * @param bandwidth Considering neighborhood
     * @return Integer; location of the minimum if exist or returns nulls
     */
    public Integer findMin(Queue<Double> input, int bandwidth){
        int size = input.size();
        Object[] inputArray = input.toArray();
        for(int i = (size-bandwidth)-1;i>=bandwidth;--i){
            Double min = Double.MAX_VALUE;
            for(int j=i-bandwidth;j<=i+bandwidth;++j){
                if((Double)inputArray[j]< min){
                    min = (Double)inputArray[j];
                }
            }

            if((Double)inputArray[i]==min){
                return i;
            }

        }
        return null;
    }


    /**
 *
 * @param s Queue
 * @return Index of the position of where the maximum value contains
 */
	public int maxIndex(Queue<Double> s,Integer pos, Integer window) {
		Iterator<Double> itr = s.iterator();
		Double m = Double.MIN_VALUE;
		int index = 0;
		int i = 0;
		while (itr.hasNext()) {
			Double nxt = itr.next();
			if ((i<=pos+window)&&(i>=pos-window)&&(m < nxt)) {
				m = nxt;
				index = i;
			}
			++i;
		}
		// log.info(m);
		return index;
	}
/**
 * 
 * @param s Queue
 * @return minimum index
 */
	public int minIndex(Queue<Double> s,Integer pos, Integer window) {
		Iterator<Double> itr = s.iterator();
		Double m = Double.MAX_VALUE;
		int index = 0;
		int i = 0;
		while (itr.hasNext()) {
			Double nxt = itr.next();
			if ((i<=pos+window)&&(i>=pos-window)&&(m > nxt)) {
				m = nxt;
				index = i;
			}
			++i;
		}
		// log.info(m);
		return index;
	}

//Kalman filter
    private void measurementUpdate(){
        K = (P + Q) / (P + Q + R);
        P = R * (P + Q) / (R + P + Q);
    }

    public double update(double measurement){
        measurementUpdate();
        double result = X + (measurement - X) * K;
        X = result;

        return result;
    }

    public Queue<Double> kalmanFilter(Queue<Double> input){
        Queue<Double> dataInput = input;
        Queue<Double> dataOutput =new LinkedList<Double>();
        int size =dataInput.size();
        for(double d:dataInput){
            dataOutput.add(update(d));
        }
        return dataOutput;
    }

}
