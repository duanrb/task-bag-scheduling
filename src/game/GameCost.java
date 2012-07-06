package game;

public class GameCost extends GenericGame {
	public GameCost(int iClass, int iSite) {
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
				tmp += 1 / dmPricePerTask[i][j];
			}
			daPredictionByClass[i] = tmp;
		}

		/* calculate weight */
		for (int i = 0; i < iClass; i++) {
			// print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				/* the weight is 1(maximum), when the site is free */
				if (dmPricePerTask[i][j] == 0) {
					dmWeight[i][j] = 1;
				} else {
					dmWeight[i][j] = 1 / (dmPricePerTask[i][j] * daPredictionByClass[i]);
				}
				// print(dmWeight[i][j] + ", ");
			}
			// println();
		}
	}

	@Override
	public void calculateInitDist() {
		/* calculate processing rate of each site */
		double tmp = 0, rest = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			print("ProcessRate[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				print(dmProcessRate[i][j] + ", ");
			}
			println();
			daProcRateByClass[i] = tmp;
		}
		/* distribute tasks according to the sort */
		int k;
		for (int i = 0; i < iClass; i++) {
			print("0Distribution[" + i + "]");
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
			for (int j = 0; j < iSite; j++) {
				print(dmDist[i][j] + ", ");
			}
			println();
		}
	}


	public boolean compDistribution() {
		bDeadline = true;
		boolean bUsedNewResource = false;
		double tmp = 0, rest = 0;
		/* distribute tasks according to the sort */
		int k;
		double lastAllocation;

		for (int i = 0; i < iClass; i++) {
			print(iStage + " Distribution[" + i + "]");
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
			for (int j = 0; j < iSite; j++) {
				print(dmDist[i][j] + ", ");
			}
			println();
		}
		return bUsedNewResource;
	}

	public void compFinalDistribution() {
		bDeadline = true;
		boolean bUsedNewResource = false;
		double tmp = 0, rest = 0;
		/* distribute tasks according to the sort */
		int k;
		double lastAllocation;
		for (int i = 0; i < iClass; i++) {
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
		}
	}

	public void compAllocation() {
		/* calculate processing rate of each site */
		double tmp = 0;
		double[] daRelativeWeightBySite = new double[iSite];
		for (int i = 0; i < iSite; i++) {
			tmp = 0;
			for (int j = 0; j < iClass; j++) {
				tmp += dmPrediction[j][i] * dmWeight[j][i] * dmDist[j][i];
			}
			// System.out.println("RelativeValue["+i+"] = "+ tmp);
			daRelativeWeightBySite[i] = tmp;
		}

		for (int i = 0; i < iSite; i++) {
			for (int j = 0; j < iClass; j++) {
				if (daRelativeWeightBySite[i] != 0) {
					dmAlloc[j][i] = (dmDist[j][i] * dmPrediction[j][i] * dmWeight[j][i] * iaCPU[i]) / daRelativeWeightBySite[i];
				} else {
					dmAlloc[j][i] = -1;
				}

				// if the allocated resource is more than requirement,...
				// if (dmAllocation[j][i] > dmDistribution[j][i] *
				// dmPrediction[j][i] / dDeadline)
				// {
				// dmAllocation[j][i] = dmDistribution[j][i] *
				// dmPrediction[j][i] / dDeadline;
				// }
			}
		}

		for (int i = 0; i < iClass; i++) {
			print(iStage + " Allocation[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				print(dmAlloc[i][j] + ", ");
			}
			println();
		}
	}

	public boolean compFinalResult() {
		double tempCost = 0;
		do {
			iStage++;
			compAllocation();
			tempCost = 0;
			for (int i = 0; i < iClass; i++) {
				for (int j = 0; j < iSite; j++) {
					tempCost += dmDist[i][j] * dmPrediction[i][j]
							* daPrice[j];
				}
			}
			vCost.add(tempCost);

			if (compDistribution()) {
				/* deadline can not be satisfied */
				if (!bDeadline) {
					System.out.println("THE DEADLINE CAN NOT BE SATISFIED!");
					return false;
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

		println("==================Allocation=====================");
		for (int i = 0; i < iClass; i++) {
			print("FinalAllocation[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmAlloc[i][j] = Math.round(dmAlloc[i][j]);
				print(dmAlloc[i][j] + ",");
			}
			println();
		}
		compFinalDistribution();
		println("==================Distribution=====================");
		for (int i = 0; i < iClass; i++) {
			print("FinalDistribution[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = Math.round(dmDist[i][j]);
				print(dmDist[i][j] + ",");
			}
			println();
		}
		println("==================Cost&Time=====================");
		compExecTime();
		println("Stage = " + iStage);
		return true;
	}

	public void compExecTime() {
		double newExeTime;
		double newCost;
		dEval = 0;
		dTime = 0;
		dCost = 0;
		for (int i = 0; i < iClass; i++) {
			newExeTime = 0;
			print("Cost[" + i + "]");
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

				dTime += dmDist[i][j] * dmPrediction[i][j];
				dCost += newCost;

				dEval += dmCost[i][j] - newCost;
				dmExeTime[i][j] = newExeTime;
				dmCost[i][j] = newCost;
				print(Math.round(dmCost[i][j]) + ", ");
			}
			println();
		}
		for (int i = 0; i < iClass; i++) {
			print("Time[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				print(Math.round(dmExeTime[i][j]) + ", ");
			}
			println();
		}
		dFinalMakespan = dDeadline;
		println("My Time = " + dTime);
		println("My Cost = " + dCost);
	}

	/**
	 * Sort reosurces for each class and get one sorted matrix back
	 */
	public void sortResources() {
		/* compute cost per acitivity */
		for (int i = 0; i < iClass; i++) {
			print("PricePerActivity[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmPricePerTask[i][j] = daPrice[j] * dmPrediction[i][j];
				print( dmPricePerTask[i][j] + ", ");
			}
			println();
		}
		double[][] array = new double[iSite][2];
		/* sort every class */
		for (int i = 0; i < iClass; i++) {
			// init array
			for (int j = 0; j < iSite; j++) {
				array[j][0] = dmPricePerTask[i][j];
				array[j][1] = j;
			}
			QuickSort.sort(array, 0, iSite - 1);
			print("RANK[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmRankResource[i][j] = array[j][1];
				print(dmRankResource[i][j] + ", ");
			}
			println();
		}

	}

	@Override
	public void schedule() {

		iStage = 0;
		sortResources();
		calculateWeight();
		calculateInitDist();
		compAllocation();
		compExecTime();
		if (!compFinalResult()) {
			GameQuick opt = new GameQuick(this.iClass, this.iSite);
			opt.init(this);
			opt.schedule();
			if (opt.dDeadline >= opt.dFinalMakespan) {
				this.dCost = opt.dCost;
				this.dTime = opt.dTime;
				this.dFinalMakespan = opt.dFinalMakespan;
				println("SUCCESSFULLY COMPLETE!");
			} else {
				System.out.println("FAILED!!! ");
			}

		} else {
			println("FIND THE SOLUTION!!");
		}
		println("Deadline =" + dDeadline);
		
		System.out.println("Stage     = " + iStage);
	}

	public void minCostWithWorkflowOptimization() {

		iStage = 0;
		sortResources();
		calculateWeight();

		/* get first */
		GameQuick optInit = new GameQuick(this.iClass, this.iSite);
		optInit.init(this);
		optInit.scheduleOnce();
		this.setDmDistribution(optInit.dmDist);

		compAllocation();
		compExecTime();
		if (!compFinalResult()) {
			GameQuick opt = new GameQuick(this.iClass, this.iSite);
			opt.init(this);
			opt.schedule();
			if (opt.dDeadline > opt.dFinalMakespan) {
				this.dCost = opt.dCost;
				this.dTime = opt.dTime;
				this.dFinalMakespan = opt.dFinalMakespan;
				System.out.println("SUCCESSFULLY COMPLETE!");
			} else {
				System.out.println("FAILED!!! ");
			}

		}
		System.out.println("Deadline =" + dDeadline);
	}



}
