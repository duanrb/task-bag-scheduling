package game;

import java.util.Vector;

public class GameStorage extends GenericStorage {

	public GameStorage(int iClass, int iSite) {
		super(iClass, iSite);
		bStorageProblem = new boolean[iSite];
		daStorageLack = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			bStorageProblem[i] = false;
			daStorageLack[i] = 0;
		}

	}

	Vector phasesResult = new Vector();

	boolean bNextPhase = true;

	/**
	 * calculate the final distribution and allocation
	 * 
	 */
	@Override
	public double schedule() {

		calculateWeight();
		compStorageWeight();
		sortStorageRequirement();
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
			currentMakespan += compFinalResult();

			for (int i = 0; i < iSite; i++) {
				bStorageProblem[i] = false;
				daStorageLack[i] = 0;
			}

			for (int i = 0; i < iSite; i++) {
				bStorageProblem[i] = false;
			}

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
				dTotalExecutionTime += dmDist[i][j] * dmPrediction[i][j];
			}
		}

		dTime = dTotalExecutionTime;
		System.out.println("Fairness  = " + calculateFairness());
		System.out.println("Game Time = " + dTotalExecutionTime);
		System.out.println("Game Cost = " + dCost);
		System.out.println("Makespan  = " + dFinalMakespan);
		System.out.println("Stage     = " + iStage);
		return dTotalExecutionTime;
	}

	public void optimizeOnce() {
		calculateWeight();
		calculateInitDist();
		compExecTime();
		compFinalResult();
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
	public void compStorageWeight() {
		double sum = 0;
		/* calculate*/
		for (int i = 0; i < iClass; i++) {
			sum += 1 / (daStorageInput[i] + daStorageOutput[i]);
		}

		/* calculate weight */
		for (int i = 0; i < iClass; i++) {
			print("StorageWeight[" + i + "]");
			daStorageWeight[i] = 1 / (daStorageInput[i] + daStorageOutput[i])
					/ sum;
			print(daStorageWeight[i] + ", ");
			println();
		}
	}

	@Override
	public void calculateInitDist() {
		/* calculate processing rate of each site */
		double tmp = 0;
		double[] daProcRateByClass = new double[iClass];
		println("-----------ProcessRate-----------");
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			print("ProcessRate[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				if (dmProcessRate[i][j] > daStorageLimit[j]
						/ (daStorageInput[i] + daStorageOutput[i])) {
					dmProcessRate[i][j] = daStorageLimit[j]
							/ (daStorageInput[i] + daStorageOutput[i]);
				}
				tmp += dmProcessRate[i][j];
				print(dmProcessRate[i][j] + ", ");
			}
			println();
			daProcRateByClass[i] = tmp;
		}
		println("-----------Init Distribution-----------");
		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i])
						* iaCurrentLength[i];
				println("Distribution[" + i + "][" + j + "] = " + dmDist[i][j]);
			}

		}
	}

	public void compDistribution() {
		/* calculate processing rate of each site */
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
			print(iStage + "Distribution[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = (dmProcessRate[i][j] / daProcRateByClass[i])
						* iaCurrentLength[i];
				print(dmDist[i][j] + ", ");
			}
			println();
		}
	}

	/**
	 * compute the allocation of the stage
	 * 
	 */
	boolean bStorageProblem[];

	public void compAllocation() {
		/* calculate processing rate of each site */
		double tmp = 0;
		bStorageViolation = false;
		double[] daRelativeWeightBySite = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			daRelativeWeightBySite[i] = 0;
			for (int j = 0; j < iClass; j++) {
				daRelativeWeightBySite[i] += dmPrediction[j][i]
						* dmWeight[j][i] * dmDist[j][i];
			}
			// System.out.println("RelativeValue["+i+"] = "+ tmp);
		}

		double[] oldAllocation = new double[iClass];
		double[] daAdjustedRelativeWeightBySite = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			print(iStage + "Allocation[" + i + "]");
			for (int j = 0; j < iClass; j++) {
				oldAllocation[j] = dmAlloc[j][i];
				if (daRelativeWeightBySite[i] == 0) {
					dmAlloc[j][i] = 0;
				} else {
					dmAlloc[j][i] = (dmDist[j][i] * dmPrediction[j][i]
							* dmWeight[j][i] * iaCPU[i])
							/ daRelativeWeightBySite[i];
				}

				if (dmAlloc[j][i] > daStorageLimit[i]
						/ (daStorageInput[j] + daStorageOutput[j])) {
					dmAlloc[j][i] = daStorageLimit[i]
							/ (daStorageInput[j] + daStorageOutput[j]);
				}

				print(dmAlloc[j][i] + ", ");
			}

			if (detectStorage(i)) {
				bStorageViolation = true;
			}

			// detect the problem, and solve the storage problem
			if (bStorageViolation | bStorageProblem[i]) {
				bStorageProblem[i] = true;
				println("FIND STORAGE PROBLEM! NEED ADJUSTMENT!");

				for (int n = 0; n < iClass; n++) {
					daAdjustedRelativeWeightBySite[i] += dmPrediction[n][i]
							* dmWeight[n][i]
							* dmDist[n][i]
							/ (daStorageInput[n] + daStorageOutput[n] + daStorageLack[i]);
				}

				// adjust the allocation
				print(iStage + "Allocation[" + i + "]");
				for (int j = 0; j < iClass; j++) {
					dmAlloc[j][i] = (dmDist[j][i] * dmPrediction[j][i]
							* dmWeight[j][i] * iaCPU[i])
							/ ((daStorageInput[j] + daStorageOutput[j] + daStorageLack[i]) * daAdjustedRelativeWeightBySite[i]);
					print(dmAlloc[j][i] + ", ");
				}
			}

			println();
		}

	}

	/**
	 * Sort space requirements by each class
	 */
	double daRankStorageRequirments[];

	public void sortStorageRequirement() {
		daRankStorageRequirments = new double[iClass];
		double[][] array = new double[iClass][2];
		println("Storage requirment");
		for (int i = 0; i < iClass; i++) {
			array[i][0] = daStorageInput[i] + daStorageOutput[i];
			array[i][1] = i;
			print(array[i][0] + ", ");
		}
		QuickSort.sort(array, 0, iClass - 1);
		println("Rank of storage requirment");
		for (int i = 0; i < iClass; i++) {
			daRankStorageRequirments[i] = array[i][1];
			print(daRankStorageRequirments[i] + ", ");
		}
		println();
	}

	/**
	 * detect storage problem. allocation*storage requirement*
	 * 
	 * @return
	 * 
	 *         Two possibilities? 1. the important site 2. the unimportant site
	 */
	double daStorageLack[];

	boolean detectStorage(int site) {
		boolean result = false;
		double storageRequirment = 0;

		for (int j = 0; j < iClass; j++) {
			storageRequirment += dmAlloc[j][site]
					* (daStorageInput[j] + daStorageOutput[j]);
		}

		println("Storage Requirment=" + storageRequirment);
		if (daStorageLimit[site] < storageRequirment) {
			result = true;
		}

		if (daStorageLack[site] == 0) {// init this value in the first time
			daStorageLack[site] = storageRequirment - daStorageLimit[site];
		} else if (daStorageLimit[site] < storageRequirment) { // ? if init this
																// value always,
																// maybe no
																// differecne.
																// TODO: test if
																// it is useful
																// to do this
			daStorageLack[site] = storageRequirment - daStorageLimit[site];
		}
		return result;
	}


	double[] daMaxMakespan;

	boolean bStorageViolation;

	/**
	 * one phase of the game
	 * 
	 * @return
	 */
	public double compFinalResult() {
		daMaxMakespan = new double[iClass];

		double[][] dmLastDistribution = new double[iClass][iSite];

		do {
			iStage++;
			compAllocation();

			compDistribution();

			compExecTime();

			for (int i = 0; i < iClass; i++) {
				for (int j = 0; j < iSite; j++) {
					dmLastDistribution[i][j] = dmDist[i][j];
				}
			}

			println("Evaluation Value ==========" + dEval);
			println("bStorageViolation Value ==========" + bStorageViolation);
		} while (dEval > dControl || bStorageViolation);

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
		compDistribution();
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
		double CPUTime = 0;
		int xMaxTime = 0, yMaxtime = 0;
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
					maxMakespan = Math.round(dmDist[i][j] / dmAlloc[i][j])
							* dmPrediction[i][j];
				}

				if (daMaxMakespan[i] < maxMakespan) {
					daMaxMakespan[i] = maxMakespan;
				}

				print(maxMakespan + ",");
				if (phaseMaxMakespan < maxMakespan) {
					xMaxTime = i;
					yMaxtime = j;
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
		return dMinMaxMakespan;
	}

	public void compExecTime() {
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

				if (Math.round(dmAlloc[i][j] * 100) > 0) {
					finalExeTime += (Math.round(dmDist[i][j] * 100) * dmPrediction[i][j])
							/ Math.round(dmAlloc[i][j] * 100);
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
		println("FianlExeTime = " + finalExeTime);

	}


}
