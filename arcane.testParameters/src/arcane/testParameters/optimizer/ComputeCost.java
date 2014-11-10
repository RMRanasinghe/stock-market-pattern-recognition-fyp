package arcane.testParameters.optimizer;

import java.util.Queue;

//import org.test.cep.extension.Helper;



public class ComputeCost {
	
	
	public static void main(String[] arg) {
		
//		calcCost();
		checkSmooth_TB();
		  
		//get real price
		//get smooth
		//calc cost for each h value
		//print	
	}
	/*
	 * check smoothness for various BW values for triple bottom
	 */
	static void checkSmooth_TB(){
		

		Queue<Double> trueVal;
		Queue<Double> smoothedVal;
		
		CSVReader reader=new CSVReader();
		trueVal=reader.read("/home/madhushi/workspace/stock-market-pattern-recognition-fyp/window-extension/Data/table.csv");
//		System.out.println(trueVal);
		Helper util=new Helper();
		Double [][] smoothen=new Double[30][trueVal.size()];
		for(int i=0;i<30;i++){
			smoothedVal=util.smooth(trueVal, i+1);
			smoothen[i]=smoothedVal.toArray(new Double[smoothedVal.size()]);
			for(int j=0;j<trueVal.size();j++){
				System.out.print(smoothen[i][j]+" ");
			}
			
		}
		reader.write(smoothen,30,trueVal.size());
		System.out.println("Done");
		
	}
	
	
	static void calcCost(){
		
		Queue<Double> trueVal;
		Queue<Double> smoothedVal;
		
		CSVReader reader=new CSVReader();
		trueVal=reader.read("/home/madhushi/workspace/stock-market-pattern-recognition-fyp/window-extension/Data/table.csv");
//		System.out.println(trueVal);
		t_omitted_smooth util=new t_omitted_smooth();
		CalcCV calc=new CalcCV();
		Double [] cost =new Double[610];
		
		
		
		for(int i=1;i<210;i++){
			smoothedVal=util.smooth(trueVal, i);
			cost[i-1] =calc.calc(trueVal,smoothedVal);
			System.out.println(i+"  cost  "+cost[i-1]);
		}
		
		System.out.println("end");
	}
	

}
