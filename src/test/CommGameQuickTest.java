package test;

import game.CommGameQuickV1;

public class CommGameQuickTest {

	public void test1() {
		CommGameQuickV1 wo = new CommGameQuickV1(2,2);
		wo.setBPrint(true);

		int[] iaTask = {50,100};
		wo.setIaTask(iaTask);

		int[] iaCPU = {10,10};
		wo.setIaCPU(iaCPU);
		
		double[] daBandwidth = {10,10};
		wo.setDaBandwidth(daBandwidth);
		
		double[] daInputSize = {100,200};
		wo.setDaInputSize(daInputSize);

		double[][] dmPrediction = {
				{10,10},
				{10,5}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}
	
	public static void main(String[] args) {
		CommGameQuickTest test = new CommGameQuickTest();
		test.test1();
	}
}
