package arcane.testParameters.optimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.io.OutputStream;

public class CSVReader {

	Queue<Double> read(String filename) {
		String csvFileToRead = filename;
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		Queue<Double> l = new LinkedList<Double>();

		try {
			br = new BufferedReader(new FileReader(csvFileToRead));
			// line = br.readLine();
			line = br.readLine();
			for (int i = 0; i < 50; i++) {
				line = br.readLine();

			}
			while ((line = br.readLine()) != null) {
				// line = br.readLine();
				String[] prices = line.split(splitBy);

				l.add(Double.parseDouble(prices[4]));
			}
			// data=(Double [])l.toArray();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	boolean write(Double[][] data, int rowCount, int colCount) {

		try {
			File fout = new File("/home/madhushi/workspace/stock-market-pattern-recognition-fyp/window-extension/Data/bw_test.csv");
			FileOutputStream fos = new FileOutputStream(fout);
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//			BufferedWriter br = new BufferedWriter(new FileWriter("/home/madhushi/workspace/stock-market-pattern-recognition-fyp/window-extension/Data/bw_test.txt"));
			
			
			
			for (int i = 0; i < rowCount; i++) {
				StringBuilder sb = new StringBuilder();
//				System.out.println("BW value is "+i);
				for (int j = 0; j < colCount; j++) {
					sb.append(data[i][j]);
					sb.append(",");
				}
				bw.write(sb.toString());
				bw.newLine();
//				System.out.println(sb.toString());
//				sb.append(";");
			}

//			br.write(sb.toString());
			bw.close();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
