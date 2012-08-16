package game;

public class MCT extends GenericGame {

	public MCT() {
		super();

	}
	
	public MCT(int iClass, int iSite) {
		super(iClass, iSite);

	}

	@Override
	public void calculateWeight() {
		double[] daPredictionByClass = new double[iClass];
		double tmp = 0;
		/* calculate prediction by Class */
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			for (int j = 0; j < iSite; j++) {
				tmp += dmPricePerTask[i][j];
			}
			daPredictionByClass[i] = tmp;
		}

		/* calculate weight */
		for (int i = 0; i < iClass; i++) {
			// System.out.print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				/* the weight is 1(maximum), when the site is free */
				if (dmPricePerTask[i][j] == 0) {
					dmWeight[i][j] = 1;
				} else {
					dmWeight[i][j] = dmPricePerTask[i][j]
							/ daPredictionByClass[i];
				}
				// System.out.print(dmWeight[i][j] + ", ");
			}
			// System.out.println();
		}
	}

	@Override
	public void calculateInitDist() {
		/* calculate processing rate of each site */
		double tmp = 0, rest = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			// System.out.print("ProcessRate[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				// System.out.print(dmProcessRate[i][j] + ", ");
			}
			// System.out.println();
			daProcRateByClass[i] = tmp;
		}
		/* distribute tasks according to the sort */
		int k;
		for (int i = 0; i < iClass; i++) {
			// System.out.print("0Distribution[" + i + "]");
			tmp = 0;
			rest = iaTask[i];
			for (int j = 0; j < iSite; j++) {
				if (rest != 0) {
					// the first site to distribute
					k = (int) dmRankResource[i][j];
					tmp = dDeadline * iaCPU[k] / dmPrediction[i][k];
					if (rest > tmp) {
						dmDist[i][k] = tmp;
						rest = rest - tmp;
					} else {
						dmDist[i][k] = rest;
						rest = 0;
					}
				} else {
					k = (int) dmRankResource[i][j];
					dmDist[i][k] = 0;
				}

			}
			// for (int j = 0; j < iSite; j++)
			// {
			// System.out.print(dmDistribution[i][j] + ", ");
			// }
			// System.out.println();
		}
	}

	boolean bDeadline = true;

	public boolean compDistribution() {
		bDeadline = true;
		boolean bUsedNewResource = false;
		double tmp = 0, rest = 0;
		/* distribute tasks according to the sort */
		int k;
		double lastAllocation;
		for (int i = 0; i < iClass; i++) {
			// System.out.print(iStage + "Distribution[" + i + "]");
			tmp = 0;
			rest = iaTask[i];
			for (int j = 0; j < iSite; j++) {
				if (rest != 0) {
					// the first site to distribute
					k = (int) dmRankResource[i][j];
					if (dmAlloc[i][k] == -1) {
						lastAllocation = iaCPU[j];
						bUsedNewResource = true;
					} else {
						lastAllocation = dmAlloc[i][k];
					}
					tmp = dDeadline * lastAllocation / dmPrediction[i][k];
					if (rest > tmp) {
						dmDist[i][k] = tmp;
						rest = rest - tmp;
					} else {
						dmDist[i][k] = rest;
						rest = 0;
						continue;
					}
				} else {
					k = (int) dmRankResource[i][j];
					dmDist[i][k] = 0;
				}
			}
			/* after distribution, the deadline can not be fulfilled */
			if (rest > 0) {
				bDeadline = false;
				return true;
			}
			// for (int j = 0; j < iSite; j++)
			// {
			// System.out.print(dmDistribution[i][j] + ", ");
			// }
			// System.out.println();
		}
		return bUsedNewResource;
	}

	public void compAllocation() {
		/* calculate processing rate of each site */
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
			for (int j = 0; j < iClass; j++) {
				if (daRelativeWeightBySite[i] != 0) {
					dmAlloc[j][i] = (dmDist[j][i]
							* dmPrediction[j][i] * dmWeight[j][i] * iaCPU[i])
							/ daRelativeWeightBySite[i];
				} else {
					dmAlloc[j][i] = -1;
				}
			}
		}

		// for (int i = 0; i < iClass; i++)
		// {
		// System.out.print(iStage + "Allocation[" + i + "]");
		// for (int j = 0; j < iSite; j++)
		// {
		// System.out.print(dmAllocation[i][j] + ", ");
		// }
		// System.out.println();
		// }
	}

	public void compFinalResult() {
		do {
			iStage++;
			compAllocation();

			if (compDistribution()) {
				/* deadline can not be satisfied */
				if (!bDeadline) {
					System.out.println("THE DEADLINE CAN NOT BE SATISFIED!");
					return;
				} else {
					System.out.println("\nNEW ROUND WITHOUT CHECKING:");
					dEval = 1;
				}

			} else {
				compExecTime();
			}

			// System.out.println("Evaluation Value =========="+dEval);
		} while (dEval > 0);
		// while (evaluateResults());

		// System.out.println("==================Distribution=====================");
		for (int i = 0; i < iClass; i++) {
			// System.out.print("FinalDistribution[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = Math.round(dmDist[i][j]);
				// System.out.print(dmDistribution[i][j] + ",");
			}
			// System.out.println();
		}
		// System.out.println("==================Allocation=====================");
		for (int i = 0; i < iClass; i++) {
			System.out.print("FinalAllocation[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmAlloc[i][j] = Math.round(dmAlloc[i][j]);
				// System.out.print(dmAllocation[i][j] + ",");
			}
			// System.out.println();
		}
		// System.out.println("Stage = " + iStage);
	}

	public void compExecTime() {
		double newExeTime;
		double newCost;
		dEval = 0;
		dTime = 0;
		dCost = 0;
		for (int i = 0; i < iClass; i++) {
			newExeTime = 0;
			// System.out.print("Cost[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				if (dmAlloc[i][j] != -1) {
					if (dmAlloc[i][j] < 1) {
						newExeTime = 0;
					} else {
						newExeTime = (dmDist[i][j] * dmPrediction[i][j])
								/ dmAlloc[i][j];
						if (newExeTime > dDeadline + 1) {
							newExeTime = Double.MAX_VALUE;
						}
					}
				}
				if (newExeTime > dDeadline + 1) {
					// System.out.println("newExeTime - dDeadline="+ (newExeTime
					// - dDeadline -1));
					newCost = Double.MAX_VALUE;
				} else {
					newCost = dmDist[i][j] * dmPrediction[i][j]
							* daPrice[j];
				}

				dTime += newExeTime;
				dCost += newCost;

				dEval += dmCost[i][j] - dCost;
				dmExeTime[i][j] = newExeTime;
				dmCost[i][j] = newCost;
				// System.out.print(dmCost[i][j] + ", ");
			}
			// System.out.println();
		}
		for (int i = 0; i < iClass; i++) {
			System.out.print("Time[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				System.out.print(dmExeTime[i][j] + ", ");
			}
			System.out.println();
		}

		// System.out.println("AllTime = " + dTime + " AllCost = " + dCost);
		// System.out.println();
	}

	/**
	 * Sort reosurces for each class and get one sorted matrix back
	 */
	public void sortClass() {
		/* compute cost per acitivity */
		for (int i = 0; i < iClass; i++) {
			// System.out.print("PricePerActivity[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmPricePerTask[i][j] = 
						daPrice[j] * 
						dmPrediction[i][j];
				// System.out.print(j + ":" + dmPricePerActivity[i][j] + ", ");
			}
			// System.out.println();
		}
		double[][] array = new double[iClass][2];
		/* sort every class */
		for (int i = 0; i < iSite; i++) {
			// init array
			for (int j = 0; j < iClass; j++) {
				array[j][0] = dmPrediction[j][i];
				array[j][1] = j;
			}
			QuickSort.sort(array, 0, iClass - 1);
			// System.out.print("RANK CLASS[" + i + "] ");
			for (int j = 0; j < iClass; j++) {
				dmRankClass[i][j] = array[j][1];
				// System.out.print(dmRankClass[i][j] + ", ");
			}
			// System.out.println();
		}

	}

	public double minct() {
		sortClass();
		calculateWeight();

		dmMinminCost = new double[iSite][iCPUMaxNum];
		dmMinminTime = new double[iSite][iCPUMaxNum];

		double[] daMinminTimeBySite = new double[iSite];
		// find the current cheapest site for the acitvities.

		// int k =0;

		while (getRestLength() > 0) {
			for (int k = 0; k < iClass; k++) {
				for (int i = 0; i < iaTask[k]; i++) {
					iMinClass = k;
					findMinCompleteCPU();
					iaQueuedTask[k]--;
					updateMin();
					iStage++;

				}
			}

			// k = Math.round(Math.round(Math.random()*(iClass+1))) % iClass;
			// if (iaCurrentLength[k]>0)
			// {
			// iMinClass = k;
			// findMinCompleteCPU();
			// //
			// System.out.println(k+":"+iMinSite+"="+dmPrediction[iMinClass][iMinSite]);
			// iaCurrentLength[k]--;
			// updateMin();
			// iStage++;
			//                    
			// }
		}
		double sumTime = 0, sumCost = 0;
		int xMaxTime = 0, yMaxtime = 0;
		double tmpTime = -1;
		for (int i = 0; i < iSite; i++) {

			for (int j = 0; j < iaCPU[i]; j++) {
				daMinminTimeBySite[i] += dmMinminTime[i][j];
				// find cpu with the maximum execution time
				if (tmpTime < dmMinminTime[i][j]) {
					xMaxTime = i;
					yMaxtime = j;
					tmpTime = dmMinminTime[i][j];
				}

				sumTime += dmMinminTime[i][j];
				sumCost += dmMinminCost[i][j];
			}
		}
		// for (int i = 0; i < iClass; i++)
		// {
		// System.out.print(iStage + "Distribution[" + i + "]");
		// for (int j = 0; j < iSite; j++)
		// {
		// System.out.print(dmDistribution[i][j] + ", ");
		// }
		// System.out.println();
		// }
		calculateOtherSchedulingEfficiency();
		System.out.println("MCT Fairness = " + calculateFairness());
		System.out.println("MCT Time     = " + sumTime);
		System.out.println("MCT Cost     = " + sumCost);
		System.out.println("MCT Makespan = " + tmpTime);
		System.out.println("MCT Stage    = " + iStage);

		dFinalMakespan = tmpTime;
		dCost = sumCost;
		dTime = sumTime;
		return sumTime;

	}

	int getRestLength() {
		int sum = 0;
		// init array
		for (int j = 0; j < iClass; j++) {
			sum += iaQueuedTask[j];
		}
		return sum;
	}

	void findMinCompleteCPU() {
		double tmpCompetleTime = Double.MAX_VALUE;
		for (int i = 0; i < iSite; i++) {
			for (int j = 0; j < iaCPU[i]; j++) {
				if (tmpCompetleTime > dmMinminTime[i][j]
						+ dmPrediction[iMinClass][i]) {
					iMinSite = i;
					iMinCPU = j;
					tmpCompetleTime = dmMinminTime[i][j]
							+ dmPrediction[iMinClass][i];
				}
			}
		}
	}

	int iMinClass;

	void findMinAct() {
		for (int i = 0; i < iClass; i++) {
			iMinClass = (int) dmRankClass[iMinSite][i];
			if (iaQueuedTask[iMinClass] > 0) {
				iaQueuedTask[iMinClass]--;
				break;
			}
		}
	}

	void updateMin() {
		dmMinminTime[iMinSite][iMinCPU] += dmPrediction[iMinClass][iMinSite];
		dmMinminCost[iMinSite][iMinCPU] += dmPricePerTask[iMinClass][iMinSite];
		dmDist[iMinClass][iMinSite]++;
		if (iaQueuedTask[iMinClass] == 0) {
			vFairness.add(dmMinminTime[iMinSite][iMinCPU]);
		}
		daAcutalExeTime[iMinClass] +=  dmPrediction[iMinClass][iMinSite];
	}

}
