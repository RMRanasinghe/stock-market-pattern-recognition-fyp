package hmm_predict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class data_read {

	Integer[] read(String filename) {

		String csvFileToRead = filename;
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		LinkedList<Integer> l = new LinkedList<Integer>();
		Integer[] l2 = {};

		try {
			br = new BufferedReader(new FileReader(csvFileToRead));
			// line = br.readLine();
			int i = 1;
			while ((line = br.readLine()) != null) {
				// line = br.readLine();
				String[] prices = line.split(splitBy);

				l.add(Integer.parseInt(prices[0]));
				i++;
			}
			l2 = l.toArray(new Integer[l.size()]);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l2;
	}

}
