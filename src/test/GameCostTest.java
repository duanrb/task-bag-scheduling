package test;

import game.CostMCT;
import game.CostMET;
import game.CostMaxMin;
import game.CostMinMin;
import game.CostOLB;
import game.CostSufferage;
import game.GameCost;
import game.GameQuick;
import game.MCT;
import game.MET;
import game.MaxMin;
import game.MinMin;
import game.OLB;
import game.Sufferage;

public class GameCostTest {
	public void test1() {
		
		GameCost wo = new GameCost(2,2);
		wo.setBPrint(true);
		
		int[] iaLength = {1000,1000};
		wo.setIaTask(iaLength);

		int[] iaCPU = {32,32};
		wo.setIaCPU(iaCPU);
		
		double[] daPrice = {1,1};
		wo.setDaPrice(daPrice);

		wo.setDDeadline(50);
		
		double[][] dmPrediction = {{1.2,1},{1,1.2}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public void test2() {
		
		GameCost wo = new GameCost(3,3);
		wo.setBPrint(true);
		
		int[] iaLength = {1000,1000,1000};
		wo.setIaTask(iaLength);

		int[] iaCPU = {100,100,100};
		wo.setIaCPU(iaCPU);
		
		double[] daPrice = {1,2,3};
		wo.setDaPrice(daPrice);

		wo.setDDeadline(11);
		
		double[][] dmPrediction = {
				{1,1.1,1.1},
				{1.1,1,1.1},
				{1.1,1.1,1},
				};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public GameCost test3() {
		int heteroMachine = 10;
		int heteroTask = 10;
		int iClass=10, iSite =10;
		
		GameCost wo = new GameCost(iClass,iSite);
		wo.setBPrint(false);

		wo.setDDeadline(18000);
		
		int[] iaLength = new int[iClass];
		for (int j = 0; j < iClass; j++) {
			iaLength[j] = 1000;// + Math.round(Math.round(10000 * Math.random()));
		}
		wo.setIaTask(iaLength);

		double[] iaSpeedCPU = new double[iSite];
		double[] daPrice = new double[iSite];
		int[] iaCPU = new int[iSite];
		for (int j = 0; j < iSite; j++) {
			iaCPU[j] = 10;// + (int) (Math.random() * 64);
			iaSpeedCPU[j] = Math.random() * heteroMachine + 1;
			daPrice[j] = 10 + Math.round(Math.round(100 * Math.random()));
		}
		wo.setDaPrice(daPrice);
		wo.setIaCPU(iaCPU);
		
		
		double tmpPrediciton;
		double[][] dmPrediction = new double[iClass][iSite];
		for (int i = 0; i < iClass; i++) {
			
			tmpPrediciton = 1 + Math.random() * heteroTask;
			for (int j = 0; j < iSite; j++) {
				dmPrediction[i][j] = tmpPrediciton * iaSpeedCPU[j]
						* (Math.random() + 0.5);
			}
		}
		wo.setDmPrediction(dmPrediction);
		return wo;
		
	}

	void testFinal() {
		for (int s = 0; s < 1; s++) {
			GameCostTest gct = new GameCostTest();
			GameCost wo = gct.test3();
			System.out.println("----------------COST OPTIMIZATION--------------");
			long tw1 = System.currentTimeMillis();
			wo.schedule();
			System.out.println("Cost      = " + wo.getDCost());
			System.out.println("Time      = " + wo.getDTime());
			System.out.println("AlgExeTime= "+ (System.currentTimeMillis() - tw1));
			System.out.println("Makespan% = " + wo.getDFinalMakespan() / wo.getDDeadline() * 100);
			System.out.println();

			System.out.println("----------------QUICK OPTIMIZATION--------------");
			GameQuick opt = new GameQuick(wo.getIClass(), wo.getISite());
			opt.init(wo);
			long tw8 = System.currentTimeMillis();
			opt.schedule();
			System.out.println("Cost%     = " + opt.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + opt.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + opt.getDFinalMakespan()	/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw8));
			System.out.println();

			System.out.println("----------------OLB--------------");
			OLB olb = new OLB(wo.getIClass(), wo.getISite());
			olb.init(wo);
			long tolb = System.currentTimeMillis();
			olb.olbStart();
			System.out.println("Cost%     = " + olb.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + olb.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + olb.getDFinalMakespan() / wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tolb));
			System.out.println();

			System.out.println("----------------OLB(Cost)--------------");
			CostOLB colb = new CostOLB(wo.getIClass(), wo.getISite());
			colb.init(wo);
			long tw2 = System.currentTimeMillis();
			colb.minOLBCost();
			System.out.println("Cost%     = " + colb.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + colb.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + colb.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw2));
			System.out.println();

			System.out.println("----------------MCT--------------");
			MCT mct = new MCT(wo.getIClass(), wo.getISite());
			mct.init(wo);
			long tmct = System.currentTimeMillis();
			mct.minct();
			System.out.println("Cost%     = " + mct.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + mct.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + mct.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmct));
			System.out.println();

			System.out.println("----------------MCT(Cost)--------------");
			CostMCT cmct = new CostMCT(wo.getIClass(), wo.getISite());
			cmct.init(wo);
			long tw4 = System.currentTimeMillis();
			cmct.minCTCost();
			System.out.println("Cost%     = " + cmct.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + cmct.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + cmct.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw4));
			System.out.println();

			System.out.println("----------------MinMin--------------");
			MinMin minmin = new MinMin(wo.getIClass(), wo.getISite());
			minmin.init(wo);
			long tw9 = System.currentTimeMillis();
			minmin.schedule();
			System.out.println("Cost%     = " + minmin.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + minmin.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + minmin.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw9));
			System.out.println();

			System.out.println("----------------MinMin(Cost)--------------");
			CostMinMin minminc = new CostMinMin(wo.getIClass(), wo.getISite());
			minminc.init(wo);
			long tw3 = System.currentTimeMillis();
			minminc.minMinCost();
			System.out.println("Cost%     = " + minminc.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + minminc.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + minminc.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw3));
			System.out.println();

			System.out.println("----------------MaxMin--------------");
			MaxMin max = new MaxMin(wo.getIClass(), wo.getISite());
			max.init(wo);
			long tmax = System.currentTimeMillis();
			max.maxmin();
			System.out.println("Cost%     = " + max.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + max.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + max.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmax));
			System.out.println();

			System.out.println("----------------MaxMin(Cost)--------------");
			CostMaxMin mat = new CostMaxMin(wo.getIClass(), wo.getISite());
			mat.init(wo);
			long tw5 = System.currentTimeMillis();
			mat.maxmin();
			System.out.println("Cost%     = " + mat.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + mat.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + mat.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw5));
			System.out.println();

			System.out.println("----------------Sufferage--------------");
			Sufferage suff = new Sufferage(wo.getIClass(), wo.getISite());
			suff.init(wo);
			long tsuff = System.currentTimeMillis();
			suff.minSufferage();
			System.out.println("Cost%     = " + suff.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + suff.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + suff.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tsuff));
			System.out.println();

			System.out.println("----------------Sufferage(Cost)--------------");
			CostSufferage minsuff = new CostSufferage(wo.getIClass(), wo.getISite());
			minsuff.init(wo);
			long tw6 = System.currentTimeMillis();
			minsuff.minSufferage();
			System.out.println("Cost%     = " + minsuff.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + minsuff.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + minsuff.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw6));
			System.out.println();

			System.out.println("----------------MET--------------");
			MET met = new MET(wo.getIClass(), wo.getISite());
			met.init(wo);
			long tmet = System.currentTimeMillis();
			met.minet();
			System.out.println("Cost%     = " + met.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + met.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + met.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmet));
			System.out.println();

			System.out.println("----------------MET(Cost)--------------");
			CostMET cmet = new CostMET(wo.getIClass(), wo.getISite());
			cmet.init(wo);
			long tw7 = System.currentTimeMillis();
			cmet.minETCost();
			System.out.println("Cost%     = " + cmet.getDCost() / wo.getDCost() * 100);
			System.out.println("Time%     = " + cmet.getDTime() / wo.getDTime() * 100);
			System.out.println("Makespan% = " + cmet.getDFinalMakespan()
					/ wo.getDFinalMakespan() * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw7));
			System.out.println();
		}

	}

	void testCvg() {
		GameCostTest gct = new GameCostTest();
		GameCost wo = gct.test3();
		long tw1 = System.currentTimeMillis();
		wo.setBPrint(false);
		wo.schedule();
		System.out.println("----------------COST OPTIMIZATION--------------");
		System.out.println("Cost      = " + wo.getDCost());
		System.out.println("Time      = " + wo.getDTime());
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println("Makespan% = " + wo.getDFinalMakespan() / wo.getDDeadline() * 100);

		System.out.println("----------------WORKFLOW OPTIMIZATION--------------");
		GameQuick opt = new GameQuick(wo.getIClass(), wo.getISite());
		opt.init(wo);
		opt.setBPrint(false);
		long tw8 = System.currentTimeMillis();
		opt.schedule();
		System.out.println("Cost%     = " + opt.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + opt.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + opt.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw8));
		System.out.println();

		for (int i = 0; i < wo.getVCost().size(); i++) {
			System.out.println((i + 1) + " " + wo.getVCost().get(i));
		}

	}

	public static void main(String[] args) {
		GameCostTest co = new GameCostTest();
		co.testFinal();
		
	}
}
