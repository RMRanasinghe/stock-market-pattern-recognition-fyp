package org.test.cep.extension;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Helper {
	
	private int bw = 0;    //bandwith
	
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
	
	private double gaussianKernel(int x){
		Double kernVal = 0.0;
		
		kernVal = (Math.pow(Math.E, (-x*x)/(2*bw*bw)))/(bw*Math.sqrt(2*Math.PI));
		
		return kernVal;
	}
	
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

}
