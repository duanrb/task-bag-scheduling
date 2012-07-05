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
				tmp += 1 / dmPricePerActivity[i][j];
			}
			daPredictionByClass[i] = tmp;
		}

		/* calculate weight */
		for (int i = 0; i < iClass; i++) {
			// print("Weight[" + i + "]");
			for (int j = 0; j < iSite; j++) {
				/* the weight is 1(maximum), when the site is free */
				if (dmPricePerActivity[i][j] == 0) {
					dmWeight[i][j] = 1;
				} else {
					dmWeight[i][j] = 1 / (dmPricePerActivity[i][j] * daPredictionByClass[i]);
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
		/* distribute activities according to the sort */
		int k;
		for (int i = 0; i < iClass; i++) {
			print("0Distribution[" + i + "]");
			tmp = 0;
			rest = iaLength[i];
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

	boolean bDeadline = true;

	public boolean compDistribution() {
		bDeadline = true;
		boolean bUsedNewResource = false;
		double tmp = 0, rest = 0;
		/* distribute activities according to the sort */
		int k;
		double lastAllocation;

		for (int i = 0; i < iClass; i++) {
			print(iStage + "Distribution[" + i + "]");
			tmp = 0;
			rest = iaLength[i];
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
		/* distribute activities according to the sort */
		int k;
		double lastAllocation;
		for (int i = 0; i < iClass; i++) {
			tmp = 0;
			rest = iaLength[i];
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
			print(iStage + "Allocation[" + i + "]");
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
		System.out.println("Stage = " + iStage);
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
		System.out.println("My Time = " + dTime);
		System.out.println("My Cost = " + dCost);
	}

	/**
	 * Sort reosurces for each class and get one sorted matrix back
	 */
	public void sortResources() {
		/* compute cost per acitivity */
		for (int i = 0; i < iClass; i++) {
			print("PricePerActivity[" + i + "] ");
			for (int j = 0; j < iSite; j++) {
				dmPricePerActivity[i][j] = daPrice[j] * dmPrediction[i][j];
				print(j + ":" + dmPricePerActivity[i][j] + ", ");
			}
			println();
		}
		double[][] array = new double[iSite][2];
		/* sort every class */
		for (int i = 0; i < iClass; i++) {
			// init array
			for (int j = 0; j < iSite; j++) {
				array[j][0] = dmPricePerActivity[i][j];
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

	public void minCost() {

		iStage = 0;
		sortResources();
		calculateWeight();
		calculateInitDist();
		compAllocation();
		compExecTime();
		if (!compFinalResult()) {
			GameQuick opt = new GameQuick(this.iClass, this.iSite);
			opt.setBPrint(this.bPrint);
			opt.init(this);
			opt.schedule();
			if (opt.dDeadline >= opt.dFinalMakespan) {
				this.dCost = opt.dCost;
				this.dTime = opt.dTime;
				this.dFinalMakespan = opt.dFinalMakespan;
				System.out.println("SUCCESSFULLY COMPLETE!");
			} else {
				System.out.println("FAILED!!! ");
			}

		} else {
			System.out.println("FIND THE SOLUTION!!");
		}
		System.out.println("Deadline =" + dDeadline);
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

	public void test1() {
		this.iClass = 2;
		this.iSite = 2;
		iaLength[0] = 1000;
		iaLength[1] = 1000;

		iaCPU[0] = 32;
		iaCPU[1] = 32;

		daPrice[0] = 1;
		daPrice[1] = 1;

		dDeadline = 50;

		dmPrediction[0][0] = 1.2;
		dmPrediction[0][1] = 1;
		dmPrediction[1][0] = 1;
		dmPrediction[1][1] = 1.2;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}
		minCost();
		// sortResources();
		// compWeight();
		// compFirstDistribution();
		// compAllocation();
		// compExecTime();
		// compFinalResult();
	}

	public void test2() {
		this.iClass = 3;
		this.iSite = 3;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		dmRankResource = new double[iClass][iSite];
		dmPricePerActivity = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];
		dmCost = new double[iClass][iSite];

		iaLength[0] = 1000;
		iaLength[1] = 1000;
		iaLength[2] = 1000;

		iaCPU[0] = 100;
		iaCPU[1] = 100;
		iaCPU[2] = 100;

		daPrice[0] = 1;
		daPrice[1] = 2;
		daPrice[2] = 3;

		dDeadline = 11;

		dmPrediction[0][0] = 1;
		dmPrediction[0][1] = 1.1;
		dmPrediction[0][2] = 1.1;
		dmPrediction[1][0] = 1.1;
		dmPrediction[1][1] = 1;
		dmPrediction[1][2] = 1.1;
		dmPrediction[2][0] = 1.1;
		dmPrediction[2][1] = 1.1;
		dmPrediction[2][2] = 1;

		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		minCost();
	}

	public void test3() {
		this.iClass = 100;
		this.iSite = 100;

		int heteroMachine = 10;
		int heteroTask = 10;

		dmPrediction = new double[iClass][iSite];
		iaLength = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		dmRankResource = new double[iClass][iSite];
		dmPricePerActivity = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];
		dmCost = new double[iClass][iSite];

		dDeadline = 1800;

		for (int j = 0; j < iClass; j++) {
			iaLength[j] = 10000;// + Math.round(Math.round(10000 *
								// Math.random()));
		}

		double[] iaSpeedCPU = new double[iSite];
		for (int j = 0; j < iSite; j++) {
			iaCPU[j] = 100;// + (int) (Math.random() * 64);
			iaSpeedCPU[j] = Math.random() * heteroMachine + 1;
			daPrice[j] = 10 + Math.round(Math.round(100 * Math.random()));
		}
		double tmpPrediciton;
		for (int i = 0; i < iClass; i++) {
			tmpPrediciton = 1 + Math.random() * heteroTask;
			for (int j = 0; j < iSite; j++) {
				dmPrediction[i][j] = tmpPrediciton * iaSpeedCPU[j]
						* (Math.random() + 0.5);
			}
		}
		iAllCPU = 0;
		for (int j = 0; j < iSite; j++) {
			iAllCPU += iaCPU[j];
		}

		// minCost();

		// sortResources();
		// compWeight();
		// compFirstDistribution();
		// compAllocation();
		// compExecTime();
		// compFinalResult();
	}

	void testFinal() {
		for (int s = 0; s < 1; s++) {
			GameCost wo = new GameCost(3, 3);
			wo.test3();
			long tw1 = System.currentTimeMillis();
			wo.minCost();
			System.out
					.println("----------------COST OPTIMIZATION--------------");
			System.out.println("Cost      = " + wo.dCost);
			System.out.println("Time      = " + wo.dTime);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw1));
			System.out.println("Makespan% = " + wo.dFinalMakespan
					/ wo.dDeadline * 100);
			System.out.println();

			System.out
					.println("----------------QUICK OPTIMIZATION--------------");
			GameQuick opt = new GameQuick(wo.iClass, wo.iSite);
			opt.init(wo);
			long tw8 = System.currentTimeMillis();
			opt.schedule();
			System.out.println("Cost%     = " + opt.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + opt.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + opt.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw8));
			System.out.println();

			System.out.println("----------------OLB--------------");
			OLB olb = new OLB(wo.iClass, wo.iSite);
			olb.init(wo);
			long tolb = System.currentTimeMillis();
			olb.olbStart();
			System.out.println("Cost%     = " + olb.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + olb.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + olb.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tolb));
			System.out.println();

			System.out.println("----------------OLB(Cost)--------------");
			CostOLB colb = new CostOLB(wo.iClass, wo.iSite);
			colb.init(wo);
			long tw2 = System.currentTimeMillis();
			colb.minOLBCost();
			System.out.println("Cost%     = " + colb.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + colb.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + colb.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw2));
			System.out.println();

			System.out.println("----------------MCT--------------");
			MCT mct = new MCT(wo.iClass, wo.iSite);
			mct.init(wo);
			long tmct = System.currentTimeMillis();
			mct.minct();
			System.out.println("Cost%     = " + mct.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + mct.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + mct.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmct));
			System.out.println();

			System.out.println("----------------MCT(Cost)--------------");
			CostMCT cmct = new CostMCT(wo.iClass, wo.iSite);
			cmct.init(wo);
			long tw4 = System.currentTimeMillis();
			cmct.minCTCost();
			System.out.println("Cost%     = " + cmct.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + cmct.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + cmct.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw4));
			System.out.println();

			System.out.println("----------------MinMin--------------");
			MinMin minmin = new MinMin(wo.iClass, wo.iSite);
			minmin.init(wo);
			long tw9 = System.currentTimeMillis();
			minmin.minmin();
			System.out.println("Cost%     = " + minmin.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + minmin.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + minmin.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw9));
			System.out.println();

			System.out.println("----------------MinMin(Cost)--------------");
			CostMinMin minminc = new CostMinMin(wo.iClass, wo.iSite);
			minminc.init(wo);
			long tw3 = System.currentTimeMillis();
			minminc.minMinCost();
			System.out.println("Cost%     = " + minminc.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + minminc.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + minminc.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw3));
			System.out.println();

			System.out.println("----------------MaxMin--------------");
			MaxMin max = new MaxMin(wo.iClass, wo.iSite);
			max.init(wo);
			long tmax = System.currentTimeMillis();
			max.maxmin();
			System.out.println("Cost%     = " + max.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + max.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + max.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmax));
			System.out.println();

			System.out.println("----------------MaxMin(Cost)--------------");
			CostMaxMin mat = new CostMaxMin(wo.iClass, wo.iSite);
			mat.init(wo);
			long tw5 = System.currentTimeMillis();
			mat.maxmin();
			System.out.println("Cost%     = " + mat.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + mat.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + mat.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw5));
			System.out.println();

			System.out.println("----------------Sufferage--------------");
			Sufferage suff = new Sufferage(wo.iClass, wo.iSite);
			suff.init(wo);
			long tsuff = System.currentTimeMillis();
			suff.minSufferage();
			System.out.println("Cost%     = " + suff.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + suff.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + suff.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tsuff));
			System.out.println();

			System.out.println("----------------Sufferage(Cost)--------------");
			CostSufferage minsuff = new CostSufferage(wo.iClass, wo.iSite);
			minsuff.init(wo);
			long tw6 = System.currentTimeMillis();
			minsuff.minSufferage();
			System.out.println("Cost%     = " + minsuff.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + minsuff.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + minsuff.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw6));
			System.out.println();

			System.out.println("----------------MET--------------");
			MET met = new MET(wo.iClass, wo.iSite);
			met.init(wo);
			long tmet = System.currentTimeMillis();
			met.minet();
			System.out.println("Cost%     = " + met.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + met.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + met.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tmet));
			System.out.println();

			System.out.println("----------------MET(Cost)--------------");
			CostMET cmet = new CostMET(wo.iClass, wo.iSite);
			cmet.init(wo);
			long tw7 = System.currentTimeMillis();
			cmet.minETCost();
			System.out.println("Cost%     = " + cmet.dCost / wo.dCost * 100);
			System.out.println("Time%     = " + cmet.dTime / wo.dTime * 100);
			System.out.println("Makespan% = " + cmet.dFinalMakespan
					/ wo.dFinalMakespan * 100);
			System.out.println("AlgExeTime= "
					+ (System.currentTimeMillis() - tw7));
			System.out.println();
		}

	}

	void testCvg() {
		GameCost wo = new GameCost(3, 3);
		wo.test3();
		long tw1 = System.currentTimeMillis();
		wo.setBPrint(false);
		wo.minCost();
		System.out.println("----------------COST OPTIMIZATION--------------");
		System.out.println("Cost      = " + wo.dCost);
		System.out.println("Time      = " + wo.dTime);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw1));
		System.out.println("Makespan% = " + wo.dFinalMakespan / wo.dDeadline
				* 100);

		System.out
				.println("----------------WORKFLOW OPTIMIZATION--------------");
		GameQuick opt = new GameQuick(wo.iClass, wo.iSite);
		opt.init(wo);
		opt.setBPrint(false);
		long tw8 = System.currentTimeMillis();
		opt.schedule();
		System.out.println("Cost%     = " + opt.dCost / wo.dCost * 100);
		System.out.println("Time%     = " + opt.dTime / wo.dTime * 100);
		System.out.println("Makespan% = " + opt.dFinalMakespan
				/ wo.dFinalMakespan * 100);
		System.out.println("AlgExeTime= " + (System.currentTimeMillis() - tw8));
		System.out.println();

		for (int i = 0; i < wo.vCost.size(); i++) {
			System.out.println((i + 1) + " " + wo.vCost.get(i));
		}

	}

	public static void main(String[] args) {
		GameCost co = new GameCost(2, 2);
		co.testCvg();
	}

}
