package test;

import game.GameQuick;
import game.GameStorage;
import game.StorageMCT;
import game.StorageMaxmin;
import game.StorageMinMin;
import game.StorageOLB;
import game.StorageSufferage;

public class GameStorageTest {

	public void test1() {
		GameStorage wo = new GameStorage(2,2);
		wo.setBPrint(true);
		
		int[] iaLength = {1000000,100000};
		wo.setIaLength(iaLength);

		int[] iaCPU = {100,100};
		wo.setIaCPU(iaCPU);
		
		double[][] dmPrediction = {{1,2},{21,20}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}

	public void test2() {
		GameStorage wo = new GameStorage(3,3);
		wo.setBPrint(true);

		int[] iaLength = {10000,10000,10000};
		wo.setIaLength(iaLength);

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
		GameStorage wo = new GameStorage(3,3);
		wo.setBPrint(true);

		int[] iaLength = {10000,10000,10000};
		wo.setIaLength(iaLength);

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

	public void test3_1() {
		GameStorage wo = new GameStorage(2,2);
		wo.setBPrint(true);

		int[] iaLength = {100,100};
		wo.setIaLength(iaLength);
		
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

	void testCvg() {
		GameStorageTest wo = new GameStorageTest();
		GameStorage gs =wo.test3storage();
		long tw1 = System.currentTimeMillis();
		gs.setBPrint(false);
		gs.scheduleOnce();
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println("Makespan% = " + 100);
		System.out.println();

		for (int i = 0; i < gs.getVExeTime().size(); i++) {
			System.out.println((i + 1) + " " + gs.getVExeTime().get(i));
		}

	}

	public GameStorage test3storage() {
		GameStorage gs = new GameStorage();
		gs.init(10,10);

		int heteroTask = 10;
		int heteroMachine = 10;
		int heteroStorage = 1000000;
		int heteroSpaceNeed = 100;

		int[] iaLength = new int[10];
		for (int j = 0; j < 10; j++) {
			iaLength[j] = 1000 + (int) (Math.random() * 1000);
		}
		gs.setIaLength(iaLength);
		
		int[] iaCPU = new int[10];
		double[] daStorageLimit = new double[10];
		double[] daStorageInput = new double[10];
		double[] daStorageOutput = new double[10];
		double[] daStorageUsed = new double[10];
		for (int j = 0; j < 10; j++) {
			iaCPU[j] = 64 + (int) (Math.random() * 64);
			daStorageLimit[j] = Math.random() * heteroStorage + 1;
			daStorageInput[j] = Math.random() * heteroSpaceNeed + 1;
			daStorageOutput[j] = 0;
			daStorageUsed[j] = 0;
		}
		gs.setIaCPU(iaCPU);
		gs.setDaStorageLimit(daStorageLimit);
		gs.setDaStorageInput(daStorageInput);
		gs.setDaStorageOutput(daStorageOutput);
		gs.setDaStorageUsed(daStorageUsed);
		
		

		double tmpPrediciton;
		double[][] dmPrediction = new double[10][10];
		for (int i = 0; i < 10; i++) {
			tmpPrediciton = 10 + Math.random() * heteroTask * 10;
			for (int j = 0; j < 10; j++) {
				dmPrediction[i][j] = tmpPrediciton * (0.5 + Math.random());
			}
		}
		gs.setDmPrediction(dmPrediction);

		double[] daPrice = new double[10];
		for (int j = 0; j < 10; j++) {
			daPrice[j] = 1 + Math.random() * 10;
		}

		return gs;
	}

	public void storageTest() {
		GameStorage wo = new GameStorage(2,2);
		wo.setBPrint(true);

		int[] iaLength = {100,100};
		wo.setIaLength(iaLength);

		int[] iaCPU = {10,10};
		wo.setIaCPU(iaCPU);
		
		double[] daStorageLimit= {10,3};
		wo.setDaStorageLimit(daStorageLimit);
		
		double[] daPrice = {1,2};
		wo.setDaPrice(daPrice);
		
		wo.setDDeadline(600);

		double[][] dmPrediction = {
				{2,1},
				{2,1.5}
		};
		wo.setDmPrediction(dmPrediction);

		double[] daStorageInput = {1,0.2};
		wo.setDaStorageInput(daStorageInput);
		
		wo.schedule();
	}

	public void storageExample() {
		GameStorage wo = new GameStorage(2,2);
		wo.setBPrint(true);

		int[] iaLength = {4,6};
		wo.setIaLength(iaLength);

		int[] iaCPU = {2,2};
		wo.setIaCPU(iaCPU);
		
		double[] daStorageLimit= {15,20};
		wo.setDaStorageLimit(daStorageLimit);
		
		double[] daPrice = {1,2};
		wo.setDaPrice(daPrice);
		
		wo.setDDeadline(600);

		double[][] dmPrediction = {
				{18,15},
				{10,12}
		};
		wo.setDmPrediction(dmPrediction);

		double[] daStorageInput = {10,2};
		wo.setDaStorageInput(daStorageInput);
		
		wo.schedule();
	}

	public static void main(String[] args) {
		GameStorageTest gst = new GameStorageTest();
		
		GameStorage wo = gst.test3storage();
		wo.setBPrint(false);
		wo.schedule();

		System.out.println("----------------MinMin--------------");
		StorageMinMin minmin = new StorageMinMin(wo.getIClass(), wo.getISite());
		minmin.setBPrint(false);
		minmin.initializeStorageEnv(wo);
		long tw9 = System.currentTimeMillis();
		minmin.minmin();
		System.out.println("Cost%     = " + minmin.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + minmin.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + minmin.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw9));
		System.out.println();

		System.out.println("----------------MaxMin--------------");
		StorageMaxmin maxmin = new StorageMaxmin(wo.getIClass(), wo.getISite());
		maxmin.setBPrint(false);
		maxmin.initializeStorageEnv(wo);
		long tw1 = System.currentTimeMillis();
		maxmin.maxmin();
		System.out.println("Cost%     = " + maxmin.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + maxmin.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + maxmin.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println();

		System.out.println("----------------Sufferage--------------");
		StorageSufferage sufferage = new StorageSufferage(wo.getIClass(), wo.getISite());
		sufferage.setBPrint(false);
		sufferage.initializeStorageEnv(wo);
		long tw2 = System.currentTimeMillis();
		sufferage.minSufferage();
		System.out.println("Cost%     = " + sufferage.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + sufferage.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + sufferage.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw2));
		System.out.println();

		System.out.println("----------------MCT--------------");
		StorageMCT mct = new StorageMCT(wo.getIClass(), wo.getISite());
		mct.setBPrint(false);
		mct.initializeStorageEnv(wo);
		long tw3 = System.currentTimeMillis();
		mct.minct();
		System.out.println("Cost%     = " + mct.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + mct.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + mct.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw3));
		System.out.println();

		System.out.println("----------------OLB--------------");
		StorageOLB olb = new StorageOLB(wo.getIClass(), wo.getISite());
		olb.setBPrint(false);
		olb.initializeStorageEnv(wo);
		long tw4 = System.currentTimeMillis();
		olb.olbStart();
		System.out.println("Cost%     = " + olb.getDCost() / wo.getDCost() * 100);
		System.out.println("Time%     = " + olb.getDTime() / wo.getDTime() * 100);
		System.out.println("Makespan% = " + olb.getDFinalMakespan()
				/ wo.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw4));
		System.out.println();
	}
}
