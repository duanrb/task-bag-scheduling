package test;

import game.GameQuick;
import game.GenericGame;
import game.MCT;
import game.MET;
import game.MaxMin;
import game.MinMin;
import game.OLB;
import game.Sufferage;

public class GameQuickTest {
	
	public void test1() {
		GameQuick wo = new GameQuick(2,2);
		wo.setBPrint(true);
		
		int[] iaLength = {1000000,100000};
		wo.setIaTask(iaLength);

		int[] iaCPU = {100,100};
		wo.setIaCPU(iaCPU);
		
		double[][] dmPrediction = {{1,2},{21,20}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}
	
	public void testpremature() {
		GameQuick wo = new GameQuick(2,2);
		wo.setBPrint(true);

		int[] iaLength = {50,100};
		wo.setIaTask(iaLength);

		int[] iaCPU = {10,10};
		wo.setIaCPU(iaCPU);

		double[][] dmPrediction = {{10,10},{10,5}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public void test2() {
		GameQuick wo = new GameQuick(3,3);
		wo.setBPrint(true);

		int[] iaLength = {10000,10000,10000};
		wo.setIaTask(iaLength);

		int[] iaCPU = {100,100,100};
		wo.setIaCPU(iaCPU);

		double[] daPrice = {1.5,1.2,1};
		wo.setDaPrice(daPrice);
		
		wo.setDDeadline(600);

		double[][] dmPrediction = {
				{1,2,1},
				{2,4,5},
				{2,2,1}
		};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public void test2m() {
		GameQuick wo = new GameQuick(3,3);
		wo.setBPrint(true);

		int[] iaLength = {10000,10000,10000};
		wo.setIaTask(iaLength);

		int[] iaCPU = {100,100,100};
		wo.setIaCPU(iaCPU);

		double[] daPrice = {1.5,1.2,1};
		wo.setDaPrice(daPrice);

		wo.setDDeadline(600);

		double[][] dmPrediction = {
				{10,20,30},
				{12,10,15},
				{23,25,10}
		};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public GenericGame test3() {
		GameQuick wo = new GameQuick(10,10);
		wo.setBPrint(true);

		int heteroTask = 10;
		int heteroMachine = 10;


		int[] iaLength = new int[wo.getIClass()];
		for (int j = 0; j < wo.getIClass(); j++) {
			iaLength[j] = 10000 + (int) (Math.random() * 10000);
		}
		wo.setIaTask(iaLength);
		
		int[] iaCPU = new int[wo.getISite()];
		for (int j = 0; j < wo.getISite(); j++) {
			iaCPU[j] = 64 + (int) (Math.random() * 64);
		}
		wo.setIaCPU(iaCPU);

		double tmpPrediciton;

		double[][] dmPrediction = new double[wo.getIClass()][wo.getISite()];
		for (int i = 0; i < wo.getIClass(); i++) {
			tmpPrediciton = 10 + Math.random() * heteroTask * 10;
			for (int j = 0; j < wo.getISite(); j++) {
				dmPrediction[i][j] = tmpPrediciton * (0.5 + Math.random());
			}
		}
		wo.setDmPrediction(dmPrediction);

		return wo;
	}

	public void test3_1() {
		GameQuick wo = new GameQuick(2,2);
		wo.setBPrint(true);

		int[] iaLength = {100,100};
		wo.setIaTask(iaLength);
		
		int[] iaCPU = {10,10};
		wo.setIaCPU(iaCPU);
		
		double[][] dmPrediction = {
				{24,30},
				{21,15}};
		wo.setDmPrediction(dmPrediction);
		
		double[] daPrice = new double[2];
		for (int j = 0; j < 2; j++) {
			daPrice[j] = 1 + Math.random() * 10;
		}
		wo.setDaPrice(daPrice);
		
		wo.schedule();
	}

	public void testHungarian() {
		GameQuick wo = new GameQuick(3,3);
		wo.setBPrint(true);
		


		int[] iaLength = {1,1,1};
		wo.setIaTask(iaLength);

		int[] iaCPU = {1,1,1};
		wo.setIaCPU(iaCPU);
		
		double[][] dmPrediction = {
				{14,81,78},
				{54,95,28},
				{67,33,51}
		};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public void testHungarian2() {
		GameQuick wo = new GameQuick(4,4);
		wo.setBPrint(true);

		int[] iaLength = {1,1,1,1};
		wo.setIaTask(iaLength);

		int[] iaCPU = {1,1,1,1};
		wo.setIaCPU(iaCPU);

		// min: 75, max: 85; min = 77
		double[][] dmPrediction1 = { { 10, 12, 20, 21 }, { 10, 12, 21, 24 },
				{ 14, 17, 28, 30 }, { 16, 20, 30, 35 } };
		// min: 55, max: 66; min = 55
		double[][] dmPrediction2 = { { 18, 14, 19, 14 }, { 20, 15, 19, 12 },
				{ 25, 19, 18, 15 }, { 21, 9, 19, 8 } };
		// min: 202, max: 248; min = ?
		double[][] dmPrediction3 = { { 82, 70, 40, 90 }, { 50, 20, 15, 70 },
				{ 75, 40, 32, 100 }, { 63, 30, 26, 75 } };

		double[][] dmPrediction3_1 = { { 82, 70, 90 }, { 75, 40, 100 },
				{ 63, 30, 75 } };

		double[][] dmPrediction3_2 = { { 82, 90 }, { 75, 100 } };

		double[][] dmPrediction4 = { { 4, 4.8, 13.4, 5 }, { 5, 8.2, 8.8, 8.9 },
				{ 5.5, 6.8, 9.4, 9.3 }, { 5.2, 6, 7.8, 10.8 } };
		double[][] dmPrediction5 = { { 1, 2, 3, 4 }, { 1, 2, 3, 4 },
				{ 1, 2, 3, 4 }, { 1, 2, 3, 4 } };
		wo.setDmPrediction(dmPrediction2);

		wo.schedule();
	}

	void testFinal() {
		for (int s = 0; s < 1; s++) {
			GameQuickTest wo = new GameQuickTest();
			GameQuick  gq = (GameQuick) wo.test3();
			long tw1 = System.currentTimeMillis();
			gq.schedule();
			double t1 = gq.getDTotalExecutionTime();
			System.out.println("Makespan GQ = " +  gq.getDFinalMakespan());
			
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw1));
			System.out.println("Makespan% = " + 100);
			System.out.println();
			

			OLB mt = new OLB(gq.getIClass(),gq.getISite());
			mt.init(gq);
			long tw2 = System.currentTimeMillis();
			double t2 = mt.olbStart();
			System.out.println("Time%     = " + t2 / t1 * 100);
			System.out.println("Makespan% = " + mt.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + mt.getDFairness() / gq.getDFairness()
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw2));
			System.out.println();

			MCT mctime = new MCT();
			mctime.init(gq);
			long tw4 = System.currentTimeMillis();
			double t4 = mctime.minct();
			System.out.println("Time%     = " + t4 / t1 * 100);
			System.out.println("Makespan% = " + mctime.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + mctime.getDFairness() / gq.getDFairness()
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw4));
			System.out.println();

			MinMin minmin = new MinMin(gq.getIClass(),gq.getISite());
			minmin.init(gq);
			long tw3 = System.currentTimeMillis();
			minmin.schedule();
			double t3 = minmin.getDTime();
			System.out.println("Time%     = " + t3 / t1 * 100);
			System.out.println("Makespan% = " + minmin.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + minmin.getDFairness() / gq.getDFairness()
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw3));
			System.out.println();

			MaxMin mat = new MaxMin();
			mat.init(gq);
			long tw5 = System.currentTimeMillis();
			double t5 = mat.maxmin();
			System.out.println("Time%     = " + t5 / t1 * 100);
			System.out.println("Makespan% = " + mat.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + mat.getDFairness() / gq.getDFairness()
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw5));
			System.out.println();

			Sufferage minsuff = new Sufferage(gq.getIClass(),gq.getISite());
			minsuff.init(gq);
			long tw6 = System.currentTimeMillis();
			double t6 = minsuff.minSufferage();
			System.out.println("Time%     = " + t6 / t1 * 100);
			System.out.println("Makespan% = " + minsuff.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + minsuff.getDFairness()
					/ gq.getDFairness() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw6));
			System.out.println();

			MET met = new MET(gq.getIClass(),gq.getISite());
			met.init(gq);
			long tw7 = System.currentTimeMillis();
			double t7 = met.minet();
			System.out.println("Time%     = " + t7 / t1 * 100);
			System.out.println("Makespan% = " + met.getDFinalMakespan()
					/ gq.getDFinalMakespan() * 100);
			System.out.println("Fairness% = " + met.getDFairness() / gq.getDFairness()
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw7));
			System.out.println();
		}
	}

	void testCvg() {
		GameQuickTest wo = new GameQuickTest();
		GameQuick gq = (GameQuick) wo.test3();
		long tw1 = System.currentTimeMillis();
		
		gq.scheduleOnce();
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println("Makespan% = " + 100);
		System.out.println();

		for (int i = 0; i < gq.getVExeTime().size(); i++) {
			System.out.println((i + 1) + " " + gq.getVExeTime().get(i));
		}
	}

	public static void main(String[] args) {
		GameQuickTest test = new GameQuickTest();
		
		test.testFinal();
	}

}
