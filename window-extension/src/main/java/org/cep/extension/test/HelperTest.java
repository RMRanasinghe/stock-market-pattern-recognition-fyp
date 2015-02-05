package org.cep.extension.test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.cep.extension.Helper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class HelperTest {
	private Helper helper = null;
	Queue<Double> queue = null;

	private double Q = 0.000001;
	private double R = 0.0001;
	private double P = 1, X = 0, K;

	@Before
	public void init() {
		helper = new Helper();

		queue = new LinkedList<Double>();

		queue.add(2.4);
		queue.add(5.853);
		queue.add(35.843);
		queue.add(9.03984);
		queue.add(4.92372);
		queue.add(84.533);
		queue.add(34.64);
		queue.add(33.43);
		queue.add(994.34);
		queue.add(84.32);
		queue.add(392.4);
		queue.add(3.556);
		queue.add(7.333);
		queue.add(0.493);
		queue.add(84.593);
		queue.add(83.454);
		queue.add(81.4);
		queue.add(23.3);
		queue.add(2.43);
		queue.add(2.4);
	}

	@Test
	public void testGaussianSmooth1() {
		// check output Size
		Queue<Double> output = helper.smooth(queue, 1);
		Assert.assertEquals(queue.size(), output.size());

	}

	@Test
	public void testGaussianSmooth2() {
		// check output with bandwith 0
		Queue<Double> output = helper.smooth(queue, 0);
		Assert.assertEquals(queue.size(), output.size());

	}

	@Test
	public void testGaussianSmooth3() {
		// check output bandwith negative value
		Queue<Double> output = helper.smooth(queue, -1);
		Assert.assertEquals(queue.size(), output.size());

	}

	@Test
	public void testGaussianSmooth4() {
		// check smoothed values with manually taken values
		Queue<Double> output = helper.smooth(queue, -1);
		Assert.assertEquals(queue.size(), output.size());

	}

	@Test
	public void testGaussianSmooth5() {
		// check smoothed values with manually taken values
		Queue<Double> output = helper.smooth(queue, 1);

		Double[] expected = { 6.218644487846104, 12.863195983102068,
				18.76335860194655, 18.523369627603586, 28.71927397111238,
				50.16830727239391, 96.78524926064928, 273.19884018798217,
				448.62467216547566, 371.383734174943, 232.05621122322296,
				107.51894092983918, 30.546096162501765, 29.2535279283408,
				59.023218091276036, 74.78814299290478, 63.026039152478425,
				34.74878514035326, 12.708293316074673, 4.539839539004713 };
		List<Double> expectedList = Arrays.asList(expected);

		Iterator<Double> itr = output.iterator();
		Iterator<Double> itr2 = expectedList.iterator();

		while (itr.hasNext()) {
			Assert.assertTrue(Math.abs(itr.next() - itr2.next()) < 0.00001);
		}

	}

	@Test
	public void testGaussianSmooth6() {
		// check smoothed values with manually taken values for very large
		// bandwith
		Queue<Double> output = helper.smooth(queue, 1000);

		Double[] expected = { 98.53563317501548, 98.53559631891129,
				98.5355594603972, 98.53552259947315, 98.53548573613921,
				98.5354488703953, 98.5354120022415, 98.53537513167781,
				98.53533825870423, 98.53530138332076, 98.53526450552738,
				98.53522762532411, 98.53519074271097, 98.53515385768796,
				98.53511697025506, 98.53508008041227, 98.53504318815966,
				98.5350062934972, 98.5349693964249, 98.53493249694269 };
		List<Double> expectedList = Arrays.asList(expected);
		//
		Iterator<Double> itr = output.iterator();
		Iterator<Double> itr2 = expectedList.iterator();

		while (itr.hasNext()) {
			Assert.assertTrue(Math.abs(itr.next() - itr2.next()) < 0.00001);
		}

	}

	@Test
	public void testMax() {

		Double max = helper.max(queue);

		Assert.assertEquals(994.34, max);

	}

	@Test
	public void testMin() {

		Double min = helper.min(queue);

		Assert.assertEquals(0.493, min);

	}

	@Test
	public void testFindMax() {

		// Check the position of the max
		Integer maxPosition = helper.findMax(queue, 1);

		Assert.assertEquals(14, (int) maxPosition);

	}

	@Test
	public void testFindMax2() {

		// Check the position of the max if there is no max should return null
		Integer maxPosition = helper.findMax(queue, 10);

		Assert.assertNull(maxPosition);

	}

	@Test
	public void testFindMin() {

		// Check the position of the min
		Integer minPosition = helper.findMin(queue, 1);

		Assert.assertEquals(13, (int) minPosition);

	}

	@Test
	public void testFindMin2() {

		// Check the position of the min if there is no max should return null
		Integer minPosition = helper.findMin(queue, 10);

		Assert.assertNull(minPosition);

	}

	@Test
	public void testFindMaxTwoParams() {

		// Check the position of the max
		Integer maxPosition = helper.findMax(queue, 1, 2);

		Assert.assertEquals(14, (int) maxPosition);

	}

	@Test
	public void testFindMaxTwoParams2() {

		// Check the position of the max if there is no max should return null
		Integer maxPosition = helper.findMax(queue, 10, 20);

		Assert.assertNull(maxPosition);

	}

	@Test
	public void testFindMinTwoParams() {

		// Check the position of the min
		Integer minPosition = helper.findMin(queue, 1, 2);

		Assert.assertEquals(13, (int) minPosition);

	}

	@Test
	public void testFindMinTwoParams2() {

		// Check the position of the min if there is no max should return null
		Integer minPosition = helper.findMin(queue, 10, 20);

		Assert.assertNull(minPosition);

	}

	@Test
	public void testMaxIndex() {

		// Check the position of the max
		Integer maxPosition = helper.maxIndex(queue, 3, 3);

		Assert.assertEquals(5, (int) maxPosition);

	}

	@Test
	public void testMinIndex() {

		// Check the position of the max
		Integer minPosition = helper.minIndex(queue, 3, 3);

		Assert.assertEquals(0, (int) minPosition);

	}

	@Test
	public void testGaussianKalmanSmooth() {
		// check smoothed values with manually taken values for very large
		// bandwith
		Queue<Double> output = helper.kalmanFilter(queue, 0.0001, 0.000001);

		Double[] expected = { 2.3999976002423757, 5.819147034964635,
				35.548620205475686, 9.299754986633001, 4.966626428218917,
				83.75286252241465, 35.121544941226546, 33.44658536822066,
				984.9185706817098, 93.15024656910816, 389.4658968439852,
				7.339794082653327, 7.333066615056911, 0.5600659234050953,
				83.76906850379491, 83.45708920385312, 81.42016948002808,
				23.869860361602086, 2.64021491657142, 2.402355274605086 };
		List<Double> expectedList = Arrays.asList(expected);
		//
		Iterator<Double> itr = output.iterator();
		Iterator<Double> itr2 = expectedList.iterator();

		while (itr.hasNext()) {
			Assert.assertTrue(Math.abs(itr.next() - itr2.next()) < 0.00001);
		}

	}
}
