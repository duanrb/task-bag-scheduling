package game;

import org.apache.log4j.Logger;

public class CommGameQuick extends GenericGame {
	
	public CommGameQuick(int iClass, int iSite) {
		super(iClass, iSite);
	}

	boolean bNextPhase = true;

	/**
	 * calculate the final distribution and allocation and consider multiple phases
	 * 
	 */
	@Override
	public double schedule() {

		calculateWeight();
		calculateInitDist();
		compExecTime();
		double currentMakespan = 0;
		double lastPhaseMakespan = 0;

		double[] tmpLength = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmpLength[i] = iaCurrentLength[i];
		}

		while (bNextPhase) {

			lastPhaseMakespan = currentMakespan;
			currentMakespan += calculateFinalResult();

			/* prepare data for fairness evaluation */
			for (int i = 0; i < iClass; i++) {
				if (iaCurrentLength[i] == 0 & tmpLength[i] > 0) {
					vFairness.add(currentMakespan);// lastPhaseMakespan+daMaxMakespan[i]
													// );
				}
				tmpLength[i] = iaCurrentLength[i];  
			}
		}
		
		for (int i = 0; i < iClass; i++) {
			if (tmpLength[i] > 0) {
				vFairness.add(currentMakespan);// lastPhaseMakespan+daMaxMakespan[i]
												// );
			}
			tmpLength[i] = iaCurrentLength[i];
		}

		dFinalMakespan = currentMakespan;

		/* compute the cost */
		dCost = 0;
		for (int j = 0; j < iSite; j++) {
			dCost += lastPhaseMakespan * daPrice[j] * iaCPU[j];
		}

		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dCost += dmDist[i][j] * daPrice[j];
			}
		}

		/* compute summed execution time */
		System.out.println("iAllCPU = " + iAllCPU);
		if (lastPhaseMakespan > 0) {
			dTotalExecutionTime = lastPhaseMakespan * iAllCPU;
		}
		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dTotalExecutionTime += dmDist[i][j]
						* dmPrediction[i][j];
			}
		}

		dTime = dTotalExecutionTime;
		System.out.println("Fairness  = " + calculateFairness());
		System.out.println("Game Time = " + dTotalExecutionTime);
		System.out.println("Game Cost = " + dCost);
		System.out.println("Makespan  = " + currentMakespan);
		System.out.println("Stage     = " + iStage);

		return dTotalExecutionTime;
	}

	/**
	 * calculate the final distribution and allocation and consider only one phase
	 */
	public void scheduleOnce() {

		calculateWeight();
		calculateInitDist();
		compExecTime();
		calculateFinalResult();
	}

	@Override
	public void calculateWeight() {
		double[] daPredictionByClass = new double[iClass];
		double tmp = 0;
		/* comp prediction by Class */
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			for (int j = 0; j < iSite; j++) {
				tmp += 1 / dmPrediction[i][j];
			}
			daPredictionByClass[i] = tmp;
		}

		/* comp weight */
		for (int i = 0; i < iClass; i++) {
			System.out.print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmWeight[i][j] = 1 / (dmPrediction[i][j] * daPredictionByClass[i]);
				System.out.print(dmWeight[i][j] + ", ");
			}
			System.out.println();
		}
	}

	@Override
	public void calculateInitDist() {
		/* comp processing rate of each site */
		double tmp = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			// System.out.print("ProcessRate["+i+"]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				// System.out.print(dmProcessRate[i][j] + ", ");
			}
			// System.out.println();
			daProcRateByClass[i] = tmp;
		}

		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i])
						* iaCurrentLength[i];
				// System.out.println("0Distribution["+i+"]["+j+"] = "+
				// dmDistribution[i][j]);
			}
		}
	}

	public void calculateDist() {
		/* compute the processing rate of each site */
		double tmp = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = dmAlloc[i][j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				// System.out.println("ProcessRate["+i+"]["+j+"] = "+
				// dmProcessRate[i][j]);
			}
			daProcRateByClass[i] = tmp;
		}

		for (int i = 0; i < iClass; i++) {
			System.out.print(iStage + "Distribution[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i])
						* iaCurrentLength[i];
				System.out.print(dmDist[i][j] + ", ");
			}
			System.out.println();
		}
	}

	public void calculateAlloc() {
		/* comp processing rate of each site */
		double tmp = 0;
		double[] daRelativeWeightBySite = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			tmp = 0;
			for (int j = 0; j < iClass; j++) {
				tmp += dmPrediction[j][i] * dmWeight[j][i]
						* dmDist[j][i];
			}
			// System.out.println("RelativeValue["+i+"] = "+ tmp);
			daRelativeWeightBySite[i] = tmp;
		}

		for (int i = 0; i < iSite; i++) {
			System.out.print(iStage + "Allocation[" + i + "]");
			for (int j = 0; j < iClass; j++) {
				dmAlloc[j][i] = (dmDist[j][i] * dmPrediction[j][i]
						* dmWeight[j][i] * iaCPU[i])
						/ daRelativeWeightBySite[i];
				System.out.print(dmAlloc[j][i] + ", ");
			}
			System.out.println();
		}
	}

	double dFinalMakespan;

	double[] daMaxMakespan;

	public double calculateFinalResult() {
		daMaxMakespan = new double[iClass];

		double[][] dmLastDistribution = new double[iClass][iSite];
		do {
			iStage++;
			calculateAlloc();

			calculateDist();

			compExecTime();

			for (int i = 0; i < iClass; i++) {
				for (int j = 0; j < iSite; j++) {
					dmLastDistribution[i][j] = dmDist[i][j];
				}
			}

			println("Evaluation Value =========="+dEval);
		} while (dEval > dControl);

		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = dmLastDistribution[i][j];
			}
		}

		println("==================Allocation=====================");
		for (int i = 0; i < iClass; i++) {
			print("FinalAllocation[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmAlloc[i][j] = Math.round(dmAlloc[i][j]);
				print(dmAlloc[i][j] + ",");
			}
			println();
		}
		calculateDist();
		println("==================Distribution=====================");
		for (int i = 0; i < iClass; i++) {
			print("FinalDistribution[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = Math.round(dmDist[i][j]);
				print(dmDist[i][j] + ",");
			}
			println();
		}
		println("==================Makespan=====================");
		double maxMakespan;
		double phaseMaxMakespan = -1;

		/* compute makespan */

		for (int i = 0; i < iClass; i++) {
			print("Makespan[" + i + "] ");
			daMaxMakespan[i] = 0;
			for (int j = 0; j < iSite; j++) {

				if (dmAlloc[i][j] == 0) {
					maxMakespan = 0;
				} else {
					maxMakespan = Math.round(dmDist[i][j]
							/ dmAlloc[i][j])
							* dmPrediction[i][j];
				}

				if (daMaxMakespan[i] < maxMakespan) {
					daMaxMakespan[i] = maxMakespan;
				}

				print(maxMakespan + ",");
				if (phaseMaxMakespan < maxMakespan) {
					phaseMaxMakespan = maxMakespan;
				}

			}
			println();
		}

		/* Decide if need next phase, and clean the completed class */
		int counterForNextPhase = 0;
		double dDiffMakespan = 0;
		double dMinMaxMakespan = 0;
		for (int i = 0; i < iClass; i++) {
			if (daMaxMakespan[i] > 0) {
				if (dDiffMakespan == 0) {
					dDiffMakespan = daMaxMakespan[i];
					counterForNextPhase++;
				} else if (dDiffMakespan != daMaxMakespan[i]) {
					counterForNextPhase++;
				}

				if (dMinMaxMakespan == 0) {
					dMinMaxMakespan = daMaxMakespan[i];
				} else if (dMinMaxMakespan > daMaxMakespan[i]) {
					dMinMaxMakespan = daMaxMakespan[i];
				}
			}
		}

		if (counterForNextPhase > 1) {
			System.out.println("===need next phase=== counterForNextPhase="+counterForNextPhase);
			bNextPhase = true;
			// reorganize iaCurrentLength
			for (int i = 0; i < iClass; i++) {
				if (dMinMaxMakespan == daMaxMakespan[i]) {
					iaCurrentLength[i] = 0;
				} else {
					for (int j = 0; j < iSite; j++) {
						if (dmAlloc[i][j] != 0) {
							iaCurrentLength[i] = iaCurrentLength[i]
									- (int) Math.floor(dMinMaxMakespan
											* dmAlloc[i][j]
											/ dmPrediction[i][j]);
						}
						if (iaCurrentLength[i] < 0) {
							iaCurrentLength[i] = 0;
						}
					}
				}
			}

			/* execution time */

			/* the last run check */
			int tmpNumAct = 0;
			for (int i = 0; i < iClass; i++) {
				tmpNumAct += iaCurrentLength[i];
			}
			if (tmpNumAct == 0) {
				bNextPhase = false;
				return phaseMaxMakespan;
			}

			// reset distribution and allocation
			reset();
			calculateWeight();
			calculateInitDist();
			compExecTime();
		} else {
			bNextPhase = false;

		}
		System.out.println("Final Makespan = "+ dMinMaxMakespan);
		return dMinMaxMakespan;
	}

	public void compExecTime() {
		double newExeTime;
		double finalExeTime = 0;
		dEval = 0;
		dTime = 0;
		dCost = 0;
		for (int i = 0; i < iClass; i++) {
			// System.out.print("Time["+i+"]");
			newExeTime = 0;
			for (int j = 0; j < iSite; j++) {
				newExeTime = (dmDist[i][j] * dmPrediction[i][j])
						/ dmAlloc[i][j];

				if (Math.round(dmAlloc[i][j] * 10000) > 0) {
					finalExeTime += (Math.round(dmDist[i][j] * 10000) * dmPrediction[i][j])
							/ Math.round(dmAlloc[i][j] * 10000);
				}

				dTime += newExeTime;
				dEval += dmExeTime[i][j] - newExeTime;
				dmExeTime[i][j] = newExeTime;
				// System.out.print(dmExeTime[i][j]+", ");
			}
			// System.out.println();
		}
		
		vExeTime.add(dTime);
		// println("AllTime = "+ dTime);
		println("FinalExeTime = " + finalExeTime);

	}

//===========================================================================
	
	public void test1() {
		this.iClass = 2;
		this.iSite = 2;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		iaLength[0] = 1000000;
		iaLength[1] = 100000;
		for (int j = 0; j < iClass; j++) {
			iaCurrentLength[j] = iaLength[j];
		}

		iaCPU[0] = 100;
		iaCPU[1] = 100;

		dmPrediction[0][0] = 1;
		dmPrediction[0][1] = 2;
		dmPrediction[1][0] = 21;
		dmPrediction[1][1] = 20;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		// compWeight();
		// compFirstDistribution();
		// compExecTime();
		// compFinalResult();
	}
	
	public void testComm() {
		this.iClass = 2;
		this.iSite = 2;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		iaLength[0] = 50;
		iaLength[1] = 200;
		for (int j = 0; j < iClass; j++) {
			iaCurrentLength[j] = iaLength[j];
		}

		iaCPU[0] = 10;
		iaCPU[1] = 10;

		dmPrediction[0][0] = 10;
		dmPrediction[0][1] = 10;
		dmPrediction[1][0] = 10;
		dmPrediction[1][1] = 5;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		schedule();
//		compWeight();
//		compFirstDistribution();
//		compExecTime();
//		compFinalResult();
	}

	public void test2() {
		this.iClass = 3;
		this.iSite = 3;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		iaLength[0] = 10000;
		iaLength[1] = 10000;
		iaLength[2] = 10000;
		for (int j = 0; j < iClass; j++) {
			iaCurrentLength[j] = iaLength[j];
		}

		iaCPU[0] = 100;
		iaCPU[1] = 100;
		iaCPU[2] = 100;

		daPrice[0] = 1.5;
		daPrice[1] = 1.2;
		daPrice[2] = 1;

		dDeadline = 600;

		dmPrediction[0][0] = 1;
		dmPrediction[0][1] = 2;
		dmPrediction[0][2] = 1;
		dmPrediction[1][0] = 2;
		dmPrediction[1][1] = 4;
		dmPrediction[1][2] = 5;
		dmPrediction[2][0] = 2;
		dmPrediction[2][1] = 2;
		dmPrediction[2][2] = 1;

		// dmPrediction[0][0] = 1;
		// dmPrediction[0][1] = 1;
		// dmPrediction[0][2] = 1;
		// dmPrediction[1][0] = 10;
		// dmPrediction[1][1] = 10;
		// dmPrediction[1][2] = 10;
		// dmPrediction[2][0] = 20;
		// dmPrediction[2][1] = 20;
		// dmPrediction[2][2] = 20;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		// compWeight();
		// compFirstDistribution();
		// compExecTime();
		// compFinalResult();
	}

	public void test2m() {
		this.iClass = 3;
		this.iSite = 3;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		iaLength[0] = 10000;
		iaLength[1] = 10000;
		iaLength[2] = 10000;
		for (int j = 0; j < iClass; j++) {
			iaCurrentLength[j] = iaLength[j];
		}

		iaCPU[0] = 100;
		iaCPU[1] = 100;
		iaCPU[2] = 100;

		daPrice[0] = 1.5;
		daPrice[1] = 1.2;
		daPrice[2] = 1;

		dDeadline = 600;

		dmPrediction[0][0] = 10;
		dmPrediction[0][1] = 20;
		dmPrediction[0][2] = 30;
		dmPrediction[1][0] = 12;
		dmPrediction[1][1] = 10;
		dmPrediction[1][2] = 15;
		dmPrediction[2][0] = 23;
		dmPrediction[2][1] = 25;
		dmPrediction[2][2] = 10;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		calculateWeight();
		calculateInitDist();
		compExecTime();
		calculateFinalResult();
	}

	public void test3() {
		this.iClass = 10;
		this.iSite = 10;

		int heteroTask = 10;
		int heteroMachine = 10;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		for (int j = 0; j < iClass; j++) {
			iaLength[j] = 10000 + (int) (Math.random() * 10000);
			iaCurrentLength[j] = iaLength[j];
		}
		double[] iaSpeedCPU = new double[iSite];
		for (int j = 0; j < iSite; j++) {
			iaCPU[j] = 64 + (int) (Math.random() * 64);
			iaSpeedCPU[j] = Math.random() * heteroMachine + 1;
		}

		double tmpPrediciton;

		dControl = 0;

		for (int i = 0; i < iClass; i++) {
			tmpPrediciton = 10 + Math.random() * heteroTask * 10;
			for (int j = 0; j < iSite; j++) {
				dmPrediction[i][j] = tmpPrediciton * (0.5 + Math.random());
			}
		}

		iAllCPU = 0;
		for (int j = 0; j < iSite; j++) {
			daPrice[j] = 1 + Math.random() * 10;
			iAllCPU += iaCPU[j];
		}

	}

	public void test3_1() {
		this.iClass = 2;
		this.iSite = 2;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		for (int j = 0; j < iClass; j++) {
			iaLength[j] = 100;
			iaCurrentLength[j] = iaLength[j];
		}
		for (int j = 0; j < iSite; j++) {
			iaCPU[j] = 10;
		}

		dmPrediction[0][0] = 24;
		dmPrediction[0][1] = 30;
		dmPrediction[1][0] = 21;
		dmPrediction[1][1] = 15;

		for (int j = 0; j < iSite; j++) {
			daPrice[j] = 1 + Math.random() * 10;
			iAllCPU += iaCPU[j];
		}

	}

	public void testHungarian() {
		this.iClass = 3;
		this.iSite = 3;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		iaLength[0] = 1;
		iaLength[1] = 1;
		iaLength[2] = 1;

		iaCPU[0] = 1;
		iaCPU[1] = 1;
		iaCPU[2] = 1;

		dmPrediction[0][0] = 14;
		dmPrediction[0][1] = 81;
		dmPrediction[0][2] = 78;
		dmPrediction[1][0] = 54;
		dmPrediction[1][1] = 95;
		dmPrediction[1][2] = 28;
		dmPrediction[2][0] = 67;
		dmPrediction[2][1] = 33;
		dmPrediction[2][2] = 51;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		calculateWeight();
		calculateInitDist();
		compExecTime();
		calculateFinalResult();
	}

	public void testHungarian2() {
		this.iClass = 4;
		this.iSite = 4;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		iaCurrentLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];

		for (int j = 0; j < iClass; j++) {
			iaLength[j] = 1;
			iaCurrentLength[j] = iaLength[j];
			iaCPU[j] = 1;
		}

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
		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmPrediction[i][j] = dmPrediction2[i][j];
			}
		}

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		calculateWeight();
		calculateInitDist();
		compExecTime();
		calculateFinalResult();
		compExecTime();
	}

	void testFinal() {
		for (int s = 0; s < 1; s++) {
			GameQuick wo = new GameQuick(3, 3);
			wo.test3();
			wo.setBPrint(false);
			long tw1 = System.currentTimeMillis();
			double t1 = wo.schedule();
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw1));
			System.out.println("Makespan% = " + 100);
			System.out.println();

			OLB mt = new OLB(wo.iClass, wo.iSite);
			mt.setDmPrediction(wo.dmPrediction);
			mt.setIaLength(wo.iaLength);
			mt.setIaCurrentLength(wo.iaLength);
			mt.setDaPrice(wo.daPrice);
			mt.setIaCPU(wo.iaCPU);
			mt.setICPUinit(wo.iaCPU);
			long tw2 = System.currentTimeMillis();
			double t2 = mt.olbStart();
			System.out.println("Time%     = " + t2 / t1 * 100);
			System.out.println("Makespan% = " + mt.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + mt.dFairness / wo.dFairness
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw2));
			System.out.println();

			MCT mctime = new MCT(wo.iClass, wo.iSite);
			mctime.setDmPrediction(wo.dmPrediction);
			mctime.setIaLength(wo.iaLength);
			mctime.setIaCurrentLength(wo.iaLength);
			mctime.setDaPrice(wo.daPrice);
			mctime.setIaCPU(wo.iaCPU);
			mctime.setICPUinit(wo.iaCPU);
			long tw4 = System.currentTimeMillis();
			double t4 = mctime.minct();
			System.out.println("Time%     = " + t4 / t1 * 100);
			System.out.println("Makespan% = " + mctime.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + mctime.dFairness / wo.dFairness
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw4));
			System.out.println();

			MinMin minmin = new MinMin(wo.iClass, wo.iSite);
			minmin.setDmPrediction(wo.dmPrediction);
			minmin.setIaLength(wo.iaLength);
			minmin.setIaCurrentLength(wo.iaLength);
			minmin.setDaPrice(wo.daPrice);
			minmin.setIaCPU(wo.iaCPU);
			minmin.setICPUinit(wo.iaCPU);
			long tw3 = System.currentTimeMillis();
			double t3 = minmin.minmin();
			System.out.println("Time%     = " + t3 / t1 * 100);
			System.out.println("Makespan% = " + minmin.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + minmin.dFairness / wo.dFairness
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw3));
			System.out.println();

			MaxMin mat = new MaxMin(wo.iClass, wo.iSite);
			mat.setDmPrediction(wo.dmPrediction);
			mat.setIaLength(wo.iaLength);
			mat.setIaCurrentLength(wo.iaLength);
			mat.setDaPrice(wo.daPrice);
			mat.setIaCPU(wo.iaCPU);
			mat.setICPUinit(wo.iaCPU);
			long tw5 = System.currentTimeMillis();
			double t5 = mat.maxmin();
			System.out.println("Time%     = " + t5 / t1 * 100);
			System.out.println("Makespan% = " + mat.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + mat.dFairness / wo.dFairness
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw5));
			System.out.println();

			Sufferage minsuff = new Sufferage(wo.iClass, wo.iSite);
			minsuff.setDmPrediction(wo.dmPrediction);
			minsuff.setIaLength(wo.iaLength);
			minsuff.setIaCurrentLength(wo.iaLength);
			minsuff.setDaPrice(wo.daPrice);
			minsuff.setIaCPU(wo.iaCPU);
			minsuff.setICPUinit(wo.iaCPU);
			long tw6 = System.currentTimeMillis();
			double t6 = minsuff.minSufferage();
			System.out.println("Time%     = " + t6 / t1 * 100);
			System.out.println("Makespan% = " + minsuff.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + minsuff.dFairness
					/ wo.dFairness * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw6));
			System.out.println();

			MET met = new MET(wo.iClass, wo.iSite);
			met.setDmPrediction(wo.dmPrediction);
			met.setIaLength(wo.iaLength);
			met.setIaCurrentLength(wo.iaLength);
			met.setDaPrice(wo.daPrice);
			met.setIaCPU(wo.iaCPU);
			met.setICPUinit(wo.iaCPU);
			long tw7 = System.currentTimeMillis();
			double t7 = met.minet();
			System.out.println("Time%     = " + t7 / t1 * 100);
			System.out.println("Makespan% = " + met.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("Fairness% = " + met.dFairness / wo.dFairness
					* 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw7));
			System.out.println();
		}
	}

	void testCvg() {
		GameQuick wo = new GameQuick(1, 1);
		wo.test3();
		long tw1 = System.currentTimeMillis();
		wo.setBPrint(false);
		wo.scheduleOnce();
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println("Makespan% = " + 100);
		System.out.println();

		for (int i = 0; i < wo.vExeTime.size(); i++) {
			System.out.println((i + 1) + " " + wo.vExeTime.get(i));
		}

	}

	public static void main(String[] args) {
		CommGameQuick wo = new CommGameQuick(1, 1);
		wo.setBPrint(true);
		wo.dControl = 0;
		wo.testComm();

	}

}
