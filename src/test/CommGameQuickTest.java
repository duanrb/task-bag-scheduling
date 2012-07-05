package test;

import game.GameQuick;

public class CommGameQuickTest {

	public void testpremature() {
		GameQuick wo = new GameQuick(2,2);
		wo.setBPrint(true);

		int[] iaLength = {50,200};
		wo.setIaLength(iaLength);

		int[] iaCPU = {10,10};
		wo.setIaCPU(iaCPU);

		double[][] dmPrediction = {
				{10,10},
				{10,5}};
		wo.setDmPrediction(dmPrediction);

		wo.schedule();
	}
	
	public static void main(String[] args) {
		CommGameQuickTest test = new CommGameQuickTest();
		
		test.testpremature();
	}
}
