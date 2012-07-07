package game;

public class CommGameQuick extends GenericGame {
	
	public CommGameQuick(int iClass, int iSite) {
		super(iClass, iSite);
	}

	/**
	 * calculate the final distribution and allocation and consider multiple phases
	 */
	@Override
	public void schedule() {

		calculateWeight();
		calculateInitDist();
		calculateExecTime();
		
		double currentMakespan = 0;
		double lastPhaseMakespan = 0;

		double[] tmpLength = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmpLength[i] = iaQueuedTask[i];
		}

		while (bNextPhase) {

			lastPhaseMakespan = currentMakespan;
			currentMakespan += calculateFinalResult();

			/* prepare data for fairness evaluation */
			for (int i = 0; i < iClass; i++) {
				if (iaQueuedTask[i] == 0 & tmpLength[i] > 0) {
					vFairness.add(currentMakespan);
				}
				tmpLength[i] = iaQueuedTask[i];  
			}
		}
		
		for (int i = 0; i < iClass; i++) {
			if (tmpLength[i] > 0) {
				vFairness.add(currentMakespan);
			}
			tmpLength[i] = iaQueuedTask[i];
		}

		dFinalMakespan = currentMakespan;

		/* calculate the cost */
		dCost = 0;
		for (int j = 0; j < iSite; j++) {
			dCost += lastPhaseMakespan * daPrice[j] * iaCPU[j];
		}

		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dCost += dmDist[i][j] * daPrice[j];
			}
		}

		/* calculate summed execution time */
		println("iAllCPU = " + iSumCPU);
		if (lastPhaseMakespan > 0) {
			dTotalExecutionTime = lastPhaseMakespan * iSumCPU;
		}
		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dTotalExecutionTime += dmDist[i][j] * dmPrediction[i][j];
			}
		}

		dTime = dTotalExecutionTime;
		System.out.println("Fairness  = " + calculateFairness());
		System.out.println("Game Time = " + dTotalExecutionTime);
		System.out.println("Game Cost = " + dCost);
		System.out.println("Makespan  = " + currentMakespan);
		System.out.println("Stage     = " + iStage);

	}

	/**
	 * calculate the final distribution and allocation and consider only one phase
	 */
	public void scheduleOnce() {

		calculateWeight();
		calculateInitDist();
		calculateExecTime();
		calculateFinalResult();
	}

	@Override
	public void calculateWeight() {
		double[] daPredictionByClass = new double[iClass];
		double tmp = 0;
		/* calculate prediction by Class */
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			for (int j = 0; j < iSite; j++) {
				tmp += 1 / dmPrediction[i][j];
			}
			daPredictionByClass[i] = tmp;
		}

		/* calculate weight */
		for (int i = 0; i < iClass; i++) {
			print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmWeight[i][j] = 1 / (dmPrediction[i][j] * daPredictionByClass[i]);
				print(dmWeight[i][j] + ", ");
			}
			println();
		}
	}

	@Override
	public void calculateInitDist() {
		
		double tmp = 0;
		double[] daProcRateByClass = new double[iClass];
		
		/* calculate processing rate of each site */
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			print("ProcessRate["+i+"]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				print(dmProcessRate[i][j] + ", ");
			}
			println();
			daProcRateByClass[i] = tmp;
		}

		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i]) * iaQueuedTask[i];
				
				println("0Distribution["+i+"]["+j+"] = "+ dmDist[i][j]);
			}
		}
	}

	public void calculateDist() {
		/* calculate the processing rate of each site */
		double tmp = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = dmAlloc[i][j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				// println("ProcessRate["+i+"]["+j+"] = "+ dmProcessRate[i][j]);
			}
			daProcRateByClass[i] = tmp;
		}

		for (int i = 0; i < iClass; i++) {
			print(iStage + "Distribution[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i])
						* iaQueuedTask[i];
				print(dmDist[i][j] + ", ");
			}
			println();
		}
	}

	public void calculateAlloc() {
		/* calculate processing rate of each site */
		double tmp = 0;
		double[] daRelativeWeightBySite = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			tmp = 0;
			for (int j = 0; j < iClass; j++) {
				tmp += dmPrediction[j][i] * dmWeight[j][i]
						* dmDist[j][i];
			}
			// println("RelativeValue["+i+"] = "+ tmp);
			daRelativeWeightBySite[i] = tmp;
		}

		for (int i = 0; i < iSite; i++) {
			print(iStage + "Allocation[" + i + "]");
			for (int j = 0; j < iClass; j++) {
				dmAlloc[j][i] = (dmDist[j][i] * dmPrediction[j][i]
						* dmWeight[j][i] * iaCPU[i])
						/ daRelativeWeightBySite[i];
				print(dmAlloc[j][i] + ", ");
			}
			println();
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

			calculateExecTime();

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

		/* calculate makespan */

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
			println("===need next phase=== counterForNextPhase="+counterForNextPhase);
			bNextPhase = true;
			// reorganize iaCurrentLength
			for (int i = 0; i < iClass; i++) {
				if (dMinMaxMakespan == daMaxMakespan[i]) {
					iaQueuedTask[i] = 0;
				} else {
					for (int j = 0; j < iSite; j++) {
						if (dmAlloc[i][j] != 0) {
							iaQueuedTask[i] = iaQueuedTask[i]
									- (int) Math.floor(dMinMaxMakespan
											* dmAlloc[i][j]
											/ dmPrediction[i][j]);
						}
						if (iaQueuedTask[i] < 0) {
							iaQueuedTask[i] = 0;
						}
					}
				}
			}

			/* execution time */

			/* the last run check */
			int tmpNumAct = 0;
			for (int i = 0; i < iClass; i++) {
				tmpNumAct += iaQueuedTask[i];
			}
			if (tmpNumAct == 0) {
				bNextPhase = false;
				return phaseMaxMakespan;
			}

			// reset distribution and allocation
			reset();
			calculateWeight();
			calculateInitDist();
			calculateExecTime();
		} else {
			bNextPhase = false;

		}
		println("Final Makespan = "+ dMinMaxMakespan);
		return dMinMaxMakespan;
	}

	public void calculateExecTime() {
		double newExeTime;
		double finalExeTime = 0;
		dEval = 0;
		dTime = 0;
		dCost = 0;
		for (int i = 0; i < iClass; i++) {
			// print("Time["+i+"]");
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
				// print(dmExeTime[i][j]+", ");
			}
			// println();
		}
		
		vExeTime.add(dTime);
		// println("AllTime = "+ dTime);
		println("FinalExeTime = " + finalExeTime);

	}
}
