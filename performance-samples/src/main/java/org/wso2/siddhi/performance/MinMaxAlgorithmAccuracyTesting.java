/*
 * Copyright (c) 2005 - 2014, WSO2 Inc. (http://www.wso2.org)
 * All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.siddhi.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.event.in.InStream;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.query.compiler.SiddhiQLGrammarParser.inputStream_return;

public class MinMaxAlgorithmAccuracyTesting {
	private static int count = 0;
	private static volatile long start = System.currentTimeMillis();
	private static int[] resultArray = null;

	public static void main(String[] args) throws InterruptedException {

		List<Class> extensionClasses = new ArrayList<Class>();
		extensionClasses
				.add(org.test.cep.extension.CustomTransformerKernelMinMax.class);
		SiddhiConfiguration siddhiConfiguration = new SiddhiConfiguration();
		siddhiConfiguration.setSiddhiExtensions(extensionClasses);

		SiddhiManager siddhiManager = new SiddhiManager(siddhiConfiguration);

		String csvFileToRead = "/run/media/rajitha/Miscs/abx2.csv";
		File fout = new File("/run/media/rajitha/Miscs/abx2_13_50.csv");
		
		String cseEventStream = "define stream cseEventStream (date string, price double);";
		String query = "from cseEventStream#transform.custom:kernelMinMax(price,13,50) select * insert into outputStream ;";
		siddhiManager.defineStream(cseEventStream);
		siddhiManager.addQuery(query);
		siddhiManager.addCallback("outputStream", new StreamCallback() {
			@Override
			public void receive(Event[] events) {
				int i = Integer.parseInt((String) events[0].getData(0));
				if ("max".equals((String) events[0].getData(2))) {
					resultArray[i-1] = 1;
				} else {
					resultArray[i-1] = -1;
				}
			}
		});

		InputHandler inputHandler = siddhiManager
				.getInputHandler("cseEventStream");

		
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		List<String> date = new ArrayList<String>();
		List<Double> price = new ArrayList<Double>();

		try {

			br = new BufferedReader(new FileReader(csvFileToRead));
			line = br.readLine();
			while ((line = br.readLine()) != null) {

				String[] prices = line.split(splitBy);
				date.add(prices[0]);
				price.add(Double.parseDouble(prices[4]));

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Object[] dateArray = date.toArray();
		Object[] priceArray = price.toArray();
		int size = date.size();

		resultArray = new int[size];
		for (int i = 0; i < size; ++i) {
			inputHandler.send(new Object[] { (String) dateArray[i],
					(Double) priceArray[i] });
		}
		
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
			for (int i = 0; i < size; ++i) {
				bw.write(Integer.toString(resultArray[i]));
				bw.newLine();
			}
		 
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
	}
}
