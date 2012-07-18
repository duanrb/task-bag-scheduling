package test;

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
		wo.setIaTask(iaLength);

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
		GameStorage wo = new GameStorage(3,3);
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

	public void test3_1() {
		GameStorage wo = new GameStorage(2,2);
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
		GameStorage gs = new GameStorage(10,10);
		gs.setBPrint(false);

		int heteroTask = 10;
		int heteroMachine = 10;
		double heteroStorage = 100000;
		double heteroSpaceNeed = 100;

		int[] iaLength = new int[10];
		for (int j = 0; j < 10; j++) {
			iaLength[j] = 1000 + (int) (Math.random() * 1000);
		}
		gs.setIaTask(iaLength);
		
		int[] iaCPU = new int[10];
		double[] daStorageLimit = new double[10];
		double[] daStorageInput = new double[10];
		double[] daStorageOutput = new double[10];
		double[] daStorageUsed = new double[10];
		
		for (int j = 0; j < 10; j++) {
			iaCPU[j] = 16 + (int) (Math.random() * 16);
			daStorageLimit[j] = Math.random() * heteroStorage + heteroStorage/2;
			daStorageInput[j] = Math.random() * heteroSpaceNeed + heteroSpaceNeed/2;
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
		gs.setDaPrice(daPrice);

		return gs;
	}

	public void storageTest() {
		GameStorage wo = new GameStorage(2,2);
		wo.setBPrint(true);

		int[] iaLength = {100,100};
		wo.setIaTask(iaLength);

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
		wo.setIaTask(iaLength);

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
	
	public void testFinal() {
		GameStorage gs = test3storage();
		gs.setBPrint(false);
		gs.schedule();
		System.out.println("Makespan GS = " + gs.getDFinalMakespan());

		System.out.println("----------------MinMin--------------");
		StorageMinMin minmin = new StorageMinMin(gs.getIClass(), gs.getISite());
		minmin.initializeStorageEnv(gs);
		long tw9 = System.currentTimeMillis();
		minmin.schedule();
		System.out.println("Cost%     = " + minmin.getDCost() / gs.getDCost() * 100);
		System.out.println("Time%     = " + minmin.getDTime() / gs.getDTime() * 100);
		System.out.println("Makespan% = " + minmin.getDFinalMakespan() / gs.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw9));
		System.out.println();

		System.out.println("----------------MaxMin--------------");
		StorageMaxmin maxmin = new StorageMaxmin(gs.getIClass(), gs.getISite());
		maxmin.initializeStorageEnv(gs);
		long tw1 = System.currentTimeMillis();
		maxmin.maxmin();
		System.out.println("Cost%     = " + maxmin.getDCost() / gs.getDCost() * 100);
		System.out.println("Time%     = " + maxmin.getDTime() / gs.getDTime() * 100);
		System.out.println("Makespan% = " + maxmin.getDFinalMakespan() / gs.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println();

		System.out.println("----------------Sufferage--------------");
		StorageSufferage sufferage = new StorageSufferage(gs.getIClass(), gs.getISite());
		sufferage.initializeStorageEnv(gs);
		long tw2 = System.currentTimeMillis();
		sufferage.minSufferage();
		System.out.println("Cost%     = " + sufferage.getDCost() / gs.getDCost() * 100);
		System.out.println("Time%     = " + sufferage.getDTime() / gs.getDTime() * 100);
		System.out.println("Makespan% = " + sufferage.getDFinalMakespan() / gs.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw2));
		System.out.println();

		System.out.println("----------------MCT--------------");
		StorageMCT mct = new StorageMCT(gs.getIClass(), gs.getISite());
		mct.initializeStorageEnv(gs);
		long tw3 = System.currentTimeMillis();
		mct.minct();
		System.out.println("Cost%     = " + mct.getDCost() / gs.getDCost() * 100);
		System.out.println("Time%     = " + mct.getDTime() / gs.getDTime() * 100);
		System.out.println("Makespan% = " + mct.getDFinalMakespan()	/ gs.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw3));
		System.out.println();

		System.out.println("----------------OLB--------------");
		StorageOLB olb = new StorageOLB(gs.getIClass(), gs.getISite());
		olb.setBPrint(false);
		olb.initializeStorageEnv(gs);
		long tw4 = System.currentTimeMillis();
		olb.olbStart();
		System.out.println("Cost%     = " + olb.getDCost() / gs.getDCost() * 100);
		System.out.println("Time%     = " + olb.getDTime() / gs.getDTime() * 100);
		System.out.println("Makespan% = " + olb.getDFinalMakespan() / gs.getDFinalMakespan() * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw4));
		System.out.println();
	}
	

	public static void main(String[] args) {
		GameStorageTest gst = new GameStorageTest();
		gst.testFinal();
		
	}
}
