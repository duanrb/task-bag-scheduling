package game;

public class CostMinMin2 extends GenericGame {
	public CostMinMin2(int iClass, int iSite) {
		super(iClass, iSite);
	}

	@Override
	public void schedule() {
		init();
		sortResources();
		calculateWeight();
		calculateInitDist();
		calculateFinalResult();
	}

	@Override
	public void init() {
		for (int i = 0; i < iClass; i++) {
			iaTask[i] = 0;
			for (int j = 0; j < iSite; j++) {
				dmPrediction[i][j] = 0;
				dmWeight[i][j] = 0;
				dmAlloc[i][j] = 0;
				dmDist[i][j] = 0;
			}
		}

		for (int j = 0; j < iSite; j++) {
			daPrice = new double[j];
			iSumCPU += iaCPU[j];
		}
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
			System.out.print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				/* the weight is 1(maximum), when the site is free */
				if (dmPricePerTask[i][j] == 0) {
					dmWeight[i][j] = 1;
				} else {
					dmWeight[i][j] = dmPricePerTask[i][j]
							/ daPredictionByClass[i];
				}
				System.out.print(dmWeight[i][j] + ", ");
			}
			System.out.println();
		}
	}

	@Override
	public void calculateInitDist() {
		/* calculate processing rate of each site */
		double tmp = 0, rest = 0;
		double[] daProcRateByClass = new double[iClass];
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			System.out.print("ProcessRate[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				dmProcessRate[i][j] = iaCPU[j] / dmPrediction[i][j];
				tmp += dmProcessRate[i][j];
				System.out.print(dmProcessRate[i][j] + ", ");
			}
			System.out.println();
			daProcRateByClass[i] = tmp;
		}
		/* distribute tasks according to the sort */
		int k;
		for (int i = 0; i < iClass; i++) {
			System.out.print("0Distribution[" + i + "]");
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
				System.out.print(dmDist[i][j] + ", ");
			}
			System.out.println();
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
			System.out.print(iStage + "Distribution[" + i + "]");
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
				System.out.print(dmDist[i][j] + ", ");
			}
			System.out.println();
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

		for (int i = 0; i < iClass; i++) {
			System.out.print(iStage + "Allocation[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				System.out.print(dmAlloc[i][j] + ", ");
			}
			System.out.println();
		}
	}

	void minMinCost() {

	}

	public void calculateFinalResult() {
		do {
			iStage++;
			compAllocation();

			if (compDistribution()) {
				/* deadline can not be satisfied */
				if (!bDeadline) {
					System.out.println("CostMinMin2: THE DEADLINE CAN NOT BE SATISFIED!");
					return;
				} else {
					println("\nNEW ROUND WITHOUT CHECKING:");
					dEval = 1;
				}

			} else {
				compExecTime();
			}

			// System.out.println("Evaluation Value =========="+dEval);
		} while (dEval > 0);
		// while (evaluateResults());

		System.out.println("==================Distribution=====================");
		for (int i = 0; i < iClass; i++) {
			System.out.print("FinalDistribution[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmDist[i][j] = Math.round(dmDist[i][j]);
				System.out.print(dmDist[i][j] + ",");
			}
			System.out.println();
		}
		System.out.println("==================Allocation=====================");
		for (int i = 0; i < iClass; i++) {
			System.out.print("FinalAllocation[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmAlloc[i][j] = Math.round(dmAlloc[i][j]);
				System.out.print(dmAlloc[i][j] + ",");
			}
			System.out.println();
		}
		System.out.println("Stage = " + iStage);

	}

	public void compExecTime() {
		double newExeTime;
		double newCost;
		dEval = 0;
		dTime = 0;
		dCost = 0;
		for (int i = 0; i < iClass; i++) {
			newExeTime = 0;
			System.out.print("Cost[" + i + "]");
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
				System.out.print(dmCost[i][j] + ", ");
			}
			System.out.println();
		}
		for (int i = 0; i < iClass; i++) {
			System.out.print("Time[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				System.out.print(dmExeTime[i][j] + ", ");
			}
			System.out.println();
		}

		System.out.println("AllTime = " + dTime + " AllCost = " + dCost);
		System.out.println();
	}

	/**
	 * Sort reosurces for each class and get one sorted matrix back
	 */
	public void sortResources() {
		/* compute cost per acitivity */
		for (int i = 0; i < iClass; i++) {
			System.out.print("PricePerActivity[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmPricePerTask[i][j] = daPrice[j] * dmPrediction[i][j];
				System.out.print(j + ":" + dmPricePerTask[i][j] + ", ");
			}
			System.out.println();
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
			System.out.print("RANK[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmRankResource[i][j] = array[j][1];
				System.out.print(dmRankResource[i][j] + ", ");
			}
			System.out.println();
		}

	}

	/**
	 * Sort reosurces for each class and get one sorted matrix back
	 */
	public void sortClass() {
		/* compute cost per acitivity */
		for (int i = 0; i < iClass; i++) {
			System.out.print("PricePerActivity[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmPricePerTask[i][j] = daPrice[j] * dmPrediction[i][j];
				System.out.print(j + ":" + dmPricePerTask[i][j] + ", ");
			}
			System.out.println();
		}
		double[][] array = new double[iClass][2];
		/* sort every class */
		for (int i = 0; i < iSite; i++) {
			// init array
			for (int j = 0; j < iClass; j++) {
				array[j][0] = dmPricePerTask[j][i];
				array[j][1] = j;
			}
			QuickSort.sort(array, 0, iClass - 1);
			System.out.print("RANK CLASS[" + i + "] ");
			for (int j = 0; j < iClass; j++) {
				dmRankClass[i][j] = array[j][1];
				System.out.print(dmRankClass[i][j] + ", ");
			}
			System.out.println();
		}

	}
}
