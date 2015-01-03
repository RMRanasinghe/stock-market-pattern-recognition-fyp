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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

public class SimpleFilterSingleQueryPerformance {
	private static int count = 0;
	private static volatile long start = System.currentTimeMillis();

	public static void main(String[] args) throws InterruptedException {

		List<Class> extensionClasses = new ArrayList<Class>();
		extensionClasses
				.add(org.test.cep.extension.CustomTransformerKernelMinMax.class);
		SiddhiConfiguration siddhiConfiguration = new SiddhiConfiguration();
		siddhiConfiguration.setSiddhiExtensions(extensionClasses);

		SiddhiManager siddhiManager = new SiddhiManager(siddhiConfiguration);

		String cseEventStream = "define stream cseEventStream (date string, price double);";
		String query = "from cseEventStream#transform.custom:kernelMinMax(price,3,16) select * insert into outputStream ;";
		siddhiManager.defineStream(cseEventStream);
		siddhiManager.addQuery(query);
		siddhiManager.addCallback("outputStream", new StreamCallback() {
			@Override
			public void receive(Event[] events) {
				count++;
				if (count % 1000000 == 0) {
					long end = System.currentTimeMillis();
					double tp = (1000000 * 1000.0 * 9.1 / (end - start));
					System.out.println("Throughput = " + tp + " Event/sec");
					start = end;
				}
			}
		});

		InputHandler inputHandler = siddhiManager
				.getInputHandler("cseEventStream");

		String csvFileToRead = "/home/rajitha/workspace/wso2cep-3.1.0/stockDataClient/data/table.csv";
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
				price.add(Double.parseDouble(prices[1]));

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
		while (true) {
			for (int i = 0; i < size; ++i) {
				inputHandler.send(new Object[] { (String) dateArray[i],
						(Double) priceArray[i] });
			}
		}

	}
}
