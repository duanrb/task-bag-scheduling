package game;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Vector;

public class GenericGame {
	/**
	 * prediction of execution time of tasks on grid sites
	 */
	double[][] dmPrediction;

	/**
	 * number of tasks
	 */
	int[] iaTask;

	/**
	 * number of queued tasks
	 */
	int[] iaQueuedTask;

	/**
	 * weight of tasks on sites
	 */
	double[][] dmWeight;

	/**
	 * allocation of processors of grid sites for tasks, result of game
	 */
	double[][] dmAlloc;

	/**
	 * old allocation of processors of grid sites for tasks, result of game
	 */
	double[][] dmOldAlloc;

	/**
	 * distribution of tasks on grid sites, result of game
	 */
	double[][] dmDist;

	/**
	 * old distribution of tasks on grid sites, result of game
	 */
	double[][] dmOldDist;

	/**
	 * distribution of tasks on grid sites, result of game
	 */
	double[][] dmExeTime;

	/**
	 * cost of tasks on grid sites, result of game
	 */
	double[][] dmCost;

	/**
	 * price of resources
	 */
	double[] daPrice;

	/**
	 * Rank of resources: index of resources
	 */
	double[][] dmRankResource;

	/**
	 * Rank of Activity Class: index of resources
	 */
	double[][] dmRankClass;

	/**
	 * Rank of resources: index of resources
	 */
	double[][] dmPricePerTask;

	/**
	 * Sacrifice
	 */
	double[][] dmSacrifice;

	/**
	 * number of sites
	 */
	int iSite;

	/**
	 * number of activity class
	 */
	int iClass;

	/**
	 * number of CPU
	 */
	int iSumCPU;

	/**
	 * number of CPU on each site
	 */
	int iaCPU[];

	/**
	 * number of Game Stage
	 */
	int iStage;

	/**
	 * deadline(ms) of this game
	 */
	double dDeadline;

	/**
	 * processing rate of tasks on grid sites
	 */
	double[][] dmProcessRate;

	/**
	 * evaluation value
	 */
	double dEval = 1;
	double dLastEval = 1;

	/**
	 * execution time
	 */
	double dTime;

	/**
	 * machine utilization ( = dTotalExecutionTime/ iAllCPU)
	 */
	double dMachineUtilization;

	/**
	 * total execution time
	 */
	double dTotalExecutionTime;

	/**
	 * cost of whole workflow
	 */
	double dCost;

	/**
	 * Final makespan of whole workflow
	 */
	double dFinalMakespan;

	/**
	 * Number of CPU to init other algorithms
	 */
	int iCPUMaxNum;

	/**
	 * Execution time vector
	 */
	Vector<Double> vExeTime = new Vector<Double>();

	/**
	 * Execution time of each class
	 */
	Vector<Vector> vvExeTimes = new Vector<Vector>();;

	/**
	 * Execution cost vector
	 */
	Vector<Double> vCost = new Vector<Double>();

	/**
	 * Fairness vector vector
	 */
	Vector<Double> vFairness = new Vector<Double>();

	/**
	 * fairness
	 */
	double dFairness;

	/**
	 * Epsilon: in Game-quick to control the process of optimization.
	 */
	double dControl = 0;

	/**
	 * matrix of cost: intermediate data
	 */
	double[][] dmMinminCost;

	/**
	 * matrix of time: intermediate data
	 */
	double[][] dmMinminTime;

	/**
	 * array of makspan: intermediate data
	 */
	double[][][] dmMinMakespan;

	/**
	 * intermediate data
	 */
	int iMinSite, iMinCPU, iMinClass;

	/**
	 * if execute next phase
	 */
	boolean bNextPhase = true;

	/**
	 * if the execution can meet deadline
	 */
	boolean bDeadline = true;

	/**
	 * scheduling efficiency of each class
	 */
	double[] daSchedulingEfficiency;

	/**
	 * scheduling efficiency of each class
	 */
	double[] daSchedulingCostEfficiency;

	/**
	 * system-level resource efficiency
	 */
	double dSystemEfficiency;

	/**
	 * system-level cost resource efficiency
	 */
	double dSystemCostEfficiency;

	/**
	 * actual execution time of each class
	 */
	double[] daAcutalExeTime;

	/**
	 * actual execution cost of each class
	 */
	double[] daAcutalExeCost;

	public GenericGame() {
		super();
	}

	public GenericGame(int iClass, int iSite) {
		super();
		this.iClass = iClass;
		this.iSite = iSite;
		init();
	}

	public void init(int iClass, int iSite) {
		this.iSite = iSite;
		this.iClass = iClass;
		init();
	}

	public void init() {
		dmPrediction = new double[iClass][iSite];
		iaTask = new int[iClass];
		iaQueuedTask = new int[iClass];
		dmWeight = new double[iClass][iSite];
		dmAlloc = new double[iClass][iSite];
		dmOldAlloc = new double[iClass][iSite];
		dmDist = new double[iClass][iSite];
		dmOldDist = new double[iClass][iSite];
		dmSacrifice = new double[iClass][iSite];
		dmRankResource = new double[iClass][iSite];
		dmRankClass = new double[iSite][iClass];
		dmPricePerTask = new double[iClass][iSite];
		daPrice = new double[iSite];
		iaCPU = new int[iSite];
		dmProcessRate = new double[iClass][iSite];
		dmExeTime = new double[iClass][iSite];
		dmCost = new double[iClass][iSite];

		dmMinminTime = new double[iClass][iSite];

		for (int i = 0; i < iClass; i++) {
			vvExeTimes.add(new Vector<Double>());
		}

		daSchedulingEfficiency = new double[iClass];
		daSchedulingCostEfficiency = new double[iClass];
		daAcutalExeTime = new double[iClass];
		daAcutalExeCost = new double[iClass];

	}

	public void init(GenericGame gg) {
		init(gg.iClass, gg.iSite);
		setBPrint(gg.bPrint);
		setDeadline(gg.dDeadline);
		setDmPrediction(gg.dmPrediction);
		setIaTask(gg.iaTask);
		setIaCurrentLength(gg.iaTask);
		setDaPrice(gg.daPrice);
		setIaCPU(gg.iaCPU);
		setICPUinit(gg.iaCPU);
	}

	/**
	 * Scheduling efficiency: [-1, +1]
	 * 
	 */
	void calculateSchedulingEfficiency() {
		// only the first round efficiency
		if (dSystemEfficiency != 0) {
			return;
		}

		double max, min, actual = 0, sumGain = 0;
		double[] weight = new double[iClass];

		println("===Scheduling Efficiency===");
		for (int i = 0; i < iClass; i++) {
			max = dmPrediction[i][0];
			min = dmPrediction[i][1];
			actual = 0;
			for (int j = 0; j < iSite; j++) {
				if (max < dmPrediction[i][j]) {
					max = dmPrediction[i][j];
				}
				if (min > dmPrediction[i][j]) {
					min = dmPrediction[i][j];
				}
				actual += dmDist[i][j] * dmPrediction[i][j];
			}
			daAcutalExeTime[i] = actual;

			if (max > min && iaQueuedTask[i] != 0)
				daSchedulingEfficiency[i] = (max + min - 2 * actual / iaQueuedTask[i]) / (max - min);
			else if (max == min)
				daSchedulingEfficiency[i] = 1;
			else
				daSchedulingEfficiency[i] = 0;

			weight[i] = (max - min) * iaQueuedTask[i];
			if (weight[i] < 0)
				weight[i] = -weight[i];

			sumGain += weight[i];
			println(i + " " + daSchedulingEfficiency[i]);
		}

		for (int i = 0; i < iClass; i++) {
			weight[i] = weight[i] / sumGain;
			println("weight " + i + " = " + weight[i]);
		}

		dSystemEfficiency = 0;
		for (int i = 0; i < iClass; i++) {
			dSystemEfficiency += daSchedulingEfficiency[i] * weight[i];
		}

		println("System-level Efficiency = " + dSystemEfficiency);
	}

	/**
	 * Scheduling efficiency: [-1, +1]
	 * 
	 */
	void calculateSchedulingCostEfficiency() {
		// only the first round efficiency
		if (dSystemCostEfficiency != 0) {
			return;
		}

		double max, min, actual = 0, sumGain = 0;
		double[] weight = new double[iClass];

		println("===Scheduling Efficiency===");
		for (int i = 0; i < iClass; i++) {
			max = dmPrediction[i][0];
			min = dmPrediction[i][1];
			actual = 0;
			for (int j = 0; j < iSite; j++) {
				if (daPrice[j] == 0) {
					if (max < dmPrediction[i][j]) {
						max = dmPrediction[i][j];
					}
					if (min > dmPrediction[i][j]) {
						min = dmPrediction[i][j];
					}
					actual += dmDist[i][j] * dmPrediction[i][j];
				} else {
					if (max < dmPrediction[i][j] * daPrice[j]) {
						max = dmPrediction[i][j] * daPrice[j];
					}
					if (min > dmPrediction[i][j] * daPrice[j]) {
						min = dmPrediction[i][j] * daPrice[j];
					}

					actual += dmDist[i][j] * dmPrediction[i][j] * daPrice[j];
				}

			}
			daAcutalExeCost[i] = actual;

			if (max > min && iaQueuedTask[i] != 0)
				daSchedulingCostEfficiency[i] = (max + min - 2 * actual / iaQueuedTask[i]) / (max - min);
			else if (max == min)
				daSchedulingCostEfficiency[i] = 1;
			else
				daSchedulingCostEfficiency[i] = 0;

			weight[i] = (max - min) * iaQueuedTask[i];
			if (weight[i] < 0)
				weight[i] = -weight[i];

			sumGain += weight[i];
			println(i + " " + daSchedulingCostEfficiency[i]);
		}

		for (int i = 0; i < iClass; i++) {
			weight[i] = weight[i] / sumGain;
			println("weight " + i + " = " + weight[i]);
		}

		dSystemCostEfficiency = 0;
		for (int i = 0; i < iClass; i++) {
			dSystemCostEfficiency += daSchedulingCostEfficiency[i] * weight[i];
		}

		println("System-level Cost Efficiency = " + dSystemEfficiency);
	}

	void calculateOtherSchedulingEfficiency() {
		double max, min, sumGain = 0;
		double[] weight = new double[iClass];

		println("===Scheduling Efficiency===");
		for (int i = 0; i < iClass; i++) {
			max = dmPrediction[i][0];
			min = dmPrediction[i][1];
			for (int j = 0; j < iSite; j++) {
				if (max < dmPrediction[i][j]) {
					max = dmPrediction[i][j];
				}
				if (min > dmPrediction[i][j]) {
					min = dmPrediction[i][j];
				}
			}

			if (max > min && iaTask[i] != 0)
				daSchedulingEfficiency[i] = (max + min - 2 * daAcutalExeTime[i] / iaTask[i]) / (max - min);
			else if (max == min && iaTask[i] != 0)
				daSchedulingEfficiency[i] = 1;
			else
				daSchedulingEfficiency[i] = 0;

			weight[i] = (max - min) * iaTask[i];

			sumGain += weight[i];

			println(i + " " + daSchedulingEfficiency[i]);
		}

		for (int i = 0; i < iClass; i++) {
			weight[i] = weight[i] / sumGain;
			println("weight " + i + " = " + weight[i]);
		}

		dSystemEfficiency = 0;
		for (int i = 0; i < iClass; i++) {
			dSystemEfficiency += daSchedulingEfficiency[i] * weight[i];
		}

		println("System-level Efficiency = " + dSystemEfficiency);
	}

	void calculateOtherSchedulingCostEfficiency() {
		double max, min, sumGain = 0;
		double[] weight = new double[iClass];

		println("===Scheduling Efficiency===");
		for (int i = 0; i < iClass; i++) {
			max = dmPrediction[i][0];
			min = dmPrediction[i][1];
			for (int j = 0; j < iSite; j++) {
				
				if (daPrice[j] == 0) {
					if (max < dmPrediction[i][j]) {
						max = dmPrediction[i][j];
					}
					if (min > dmPrediction[i][j]) {
						min = dmPrediction[i][j];
					}
				} else {
					if (max < dmPrediction[i][j] * daPrice[j]) {
						max = dmPrediction[i][j] * daPrice[j];
					}
					if (min > dmPrediction[i][j] * daPrice[j]) {
						min = dmPrediction[i][j] * daPrice[j];
					}
				}
			}

			if (max > min && iaTask[i] != 0)
				daSchedulingCostEfficiency[i] = (max + min - 2 * daAcutalExeCost[i] / iaTask[i]) / (max - min);
			else if (max == min && iaTask[i] != 0)
				daSchedulingCostEfficiency[i] = 1;
			else
				daSchedulingCostEfficiency[i] = 0;

			weight[i] = (max - min) * iaTask[i];

			sumGain += weight[i];

			println(i + " " + daSchedulingCostEfficiency[i]);
		}

		for (int i = 0; i < iClass; i++) {
			weight[i] = weight[i] / sumGain;
			println("weight " + i + " = " + weight[i]);
		}

		dSystemCostEfficiency = 0;
		for (int i = 0; i < iClass; i++) {
			dSystemCostEfficiency += daSchedulingCostEfficiency[i] * weight[i];
		}

		println("System-level Cost Efficiency = " + dSystemCostEfficiency);
	}

	/**
	 * @return fairness value (standard derivation)
	 */
	double calculateFairness() {
		double sumTime = 0;
		int size = vFairness.size();
		/* compute the mean execution time */
		for (int i = 0; i < size; i++) {
			sumTime += vFairness.elementAt(i);
		}

		double sumSquare = 0;
		/* compute stardard derivation */
		for (int i = 0; i < size; i++) {
			sumSquare += Math.pow(vFairness.elementAt(i), 2);
		}

		dFairness = Math.pow(sumTime, 2) / (size * sumSquare);
		return dFairness;
	}

	/**
	 * @return fairness value (standard derivation)
	 */
	double calculateFairnessDeviation() {
		double sumTime = 0;
		double meanTime = 0;
		int size = vFairness.size();
		/* compute the mean execution time */
		for (int i = 0; i < size; i++) {
			sumTime += vFairness.elementAt(i);
		}
		meanTime = sumTime / size;

		double deviation = 0;
		/* compute stardard derivation */
		for (int i = 0; i < size; i++) {
			deviation += Math.pow(vFairness.elementAt(i) - meanTime, 2);
		}
		deviation = deviation / size;

		dFairness = Math.sqrt(deviation);
		return dFairness;
	}

	public void printTotalExeTime(int steps) {
		int i = 0;
		Enumeration<Double> enumeration = vExeTime.elements();
		double element = 0;
		System.out.println("CPU Time(Average response time):");
		while (enumeration.hasMoreElements()) {
			i++;
			element = (double) enumeration.nextElement();
			if (i > steps)
				continue;
			if ((i % 10) != 0)
				System.out.print(Math.round(element) + "(" + Math.round(element / (iClass * iSite)) + "), ");
			else
				System.out.println(Math.round(element) + "(" + Math.round(element / (iClass * iSite)) + "), ");
		}
		System.out.println();
		System.out.println("Final Execution Time: " + Math.round(element) + ". ");

	}

	public void printExeTimesForEachClass() {
		int stages = vvExeTimes.elementAt(0).size();
		double sum = 0;

		for (int i = 0; i < stages; i++) {
			System.out.print(i + " ");
			sum = 0;
			for (int j = 0; j < iClass; j++) {
				sum += Double.parseDouble(vvExeTimes.elementAt(j).elementAt(i).toString());
				System.out.print(vvExeTimes.elementAt(j).elementAt(i) + " ");
			}
			System.out.println(sum + " " + vExeTime.elementAt(i));
			if (i > 200) {
				break;
			}
		}
	}

	/**
	 * reset allocation and distribution
	 */
	public void reset() {
		for (int i = 0; i < iClass; i++) {
			for (int j = 0; j < iSite; j++) {
				dmAlloc[i][j] = 0;
				dmDist[i][j] = 0;
			}
		}
	}

	/**
	 * schedule the final results
	 */
	public void schedule() {
	}

	/**
	 * schedule the final results
	 */
	public void scheduleOnce() {
	}

	/**
	 * calculate the weights of tasks on site
	 * 
	 */
	public void calculateWeight() {
	}

	/**
	 * calculate the initial distribution of tasks on site
	 * 
	 */
	public void calculateInitDist() {
	}

	/**
	 * evaluate results for further execution
	 * 
	 */
	public boolean evaluateResults() {
		return true;
	}

	public double getDeadline() {
		return dDeadline;
	}

	public void setDeadline(double deadline) {
		dDeadline = deadline;
	}

	public double[] getDaPrice() {
		return daPrice;
	}

	public void setDaPrice(double[] daPrice) {
		this.daPrice = daPrice;
	}

	public double getDDeadline() {
		return dDeadline;
	}

	public void setDDeadline(double deadline) {
		dDeadline = deadline;
	}

	public double[][] getDmAllocation() {
		return dmAlloc;
	}

	public void setDmAllocation(double[][] dmAllocation) {
		this.dmAlloc = dmAllocation;
	}

	public double[][] getDmDistribution() {
		return dmDist;
	}

	public void setDmDistribution(double[][] dmDistribution) {
		this.dmDist = dmDistribution;
	}

	public double[][] getDmPrediction() {
		return dmPrediction;
	}

	public void setDmPrediction(double[][] dmPrediction) {
		this.dmPrediction = dmPrediction;
	}

	public double[][] getDmWeight() {
		return dmWeight;
	}

	public void setDmWeight(double[][] dmWeight) {
		this.dmWeight = dmWeight;
	}

	public int[] getIaLength() {
		return iaTask;
	}

	public void setIaTask(int[] iaLength) {
		for (int j = 0; j < iClass; j++) {
			this.iaTask[j] = iaLength[j];
			iaQueuedTask[j] = iaLength[j];
		}
	}

	public int getIClass() {
		return iClass;
	}

	public void setIClass(int class1) {
		iClass = class1;
	}

	public int getISite() {
		return iSite;
	}

	public void setISite(int site) {
		iSite = site;
	}

	public int[] getIaCPU() {
		return iaCPU;
	}

	public void setIaCPU(int[] iaCPU) {
		this.iaCPU = iaCPU;
		/* set CPU number of all sites */
		iSumCPU = 0;
		for (int j = 0; j < iSite; j++) {
			iSumCPU += iaCPU[j];
		}

		/* set Max CPU number on one site */
		int tmpCPU = 0;
		for (int i = 0; i < iaCPU.length; i++) {
			if (tmpCPU < iaCPU[i]) {
				tmpCPU = iaCPU[i];
			}
		}
		iCPUMaxNum = tmpCPU;
	}

	public int[] getIaCurrentLength() {
		return iaQueuedTask;
	}

	public void setIaCurrentLength(int[] ia) {
		this.iaQueuedTask = new int[ia.length];
		for (int i = 0; i < ia.length; i++) {
			this.iaQueuedTask[i] = ia[i];
		}

	}

	public int getICPUMaxNum() {
		return iCPUMaxNum;
	}

	public void setICPUinit(int[] iaCPU) {
		int tmpCPU = 0;
		for (int i = 0; i < iaCPU.length; i++) {
			if (tmpCPU < iaCPU[i]) {
				tmpCPU = iaCPU[i];
			}
		}
		iCPUMaxNum = tmpCPU;
	}

	/**
	 * if print some information
	 */
	boolean bPrint = true;

	public boolean isBPrint() {
		return bPrint;
	}

	public void setBPrint(boolean print) {
		bPrint = print;
	}

	public void print(String string) {
		if (bPrint == true) {
			System.out.print(string);
		}
	}

	public void println(String string) {
		print(string + "\n");
	}

	public void println() {
		print("\n");
	}

	public boolean isBDeadline() {
		return bDeadline;
	}

	public void setBDeadline(boolean deadline) {
		bDeadline = deadline;
	}

	public double getDCost() {
		return Math.round(dCost);
	}

	public void setDCost(double cost) {
		dCost = cost;
	}

	public double getDEval() {
		return dEval;
	}

	public void setDEval(double eval) {
		dEval = eval;
	}

	public double getDFairness() {
		return dFairness;
	}

	public void setDFairness(double fairness) {
		dFairness = fairness;
	}

	public double getDFinalMakespan() {
		return dFinalMakespan;
	}

	public void setDFinalMakespan(double finalMakespan) {
		dFinalMakespan = finalMakespan;
	}

	public double getDMachineUtilization() {
		return dMachineUtilization;
	}

	public void setDMachineUtilization(double machineUtilization) {
		dMachineUtilization = machineUtilization;
	}

	public double[][] getDmCost() {
		return dmCost;
	}

	public void setDmCost(double[][] dmCost) {
		this.dmCost = dmCost;
	}

	public double[][] getDmExeTime() {
		return dmExeTime;
	}

	public void setDmExeTime(double[][] dmExeTime) {
		this.dmExeTime = dmExeTime;
	}

	public double[][] getDmPricePerActivity() {
		return dmPricePerTask;
	}

	public void setDmPricePerActivity(double[][] dmPricePerActivity) {
		this.dmPricePerTask = dmPricePerActivity;
	}

	public double[][] getDmProcessRate() {
		return dmProcessRate;
	}

	public void setDmProcessRate(double[][] dmProcessRate) {
		this.dmProcessRate = dmProcessRate;
	}

	public double[][] getDmRankClass() {
		return dmRankClass;
	}

	public void setDmRankClass(double[][] dmRankClass) {
		this.dmRankClass = dmRankClass;
	}

	public double[][] getDmRankResource() {
		return dmRankResource;
	}

	public void setDmRankResource(double[][] dmRankResource) {
		this.dmRankResource = dmRankResource;
	}

	public double[][] getDmSacrifice() {
		return dmSacrifice;
	}

	public void setDmSacrifice(double[][] dmSacrifice) {
		this.dmSacrifice = dmSacrifice;
	}

	public double getDTime() {
		return dTime;
	}

	public void setDTime(double time) {
		dTime = time;
	}

	public double getDTotalExecutionTime() {
		return dTotalExecutionTime;
	}

	public void setDTotalExecutionTime(double totalExecutionTime) {
		dTotalExecutionTime = totalExecutionTime;
	}

	public int getISumCPU() {
		return iSumCPU;
	}

	public void setISumCPU(int allCPU) {
		iSumCPU = allCPU;
	}

	public int getIStage() {
		return iStage;
	}

	public void setIStage(int stage) {
		iStage = stage;
	}

	public Vector<Double> getVCost() {
		return vCost;
	}

	public void setVCost(Vector<Double> cost) {
		vCost = cost;
	}

	public Vector<Double> getVExeTime() {
		return vExeTime;
	}

	public void setVExeTime(Vector<Double> exeTime) {
		vExeTime = exeTime;
	}

	public Vector<Double> getVFairness() {
		return vFairness;
	}

	public void setVFairness(Vector<Double> fairness) {
		vFairness = fairness;
	}

	public void setICPUMaxNum(int uinit) {
		iCPUMaxNum = uinit;
	}

	public double getDControl() {
		return dControl;
	}

	public void setDControl(double dControl) {
		this.dControl = dControl;
	}

	DecimalFormat df = new DecimalFormat("#.00");

	public String getdSystemEfficiency() {
		return df.format(dSystemEfficiency);
	}

	public String getdSystemCostEfficiency() {
		return df.format(dSystemCostEfficiency);
	}

	public void setdSystemEfficiency(double dSystemEfficiency) {
		this.dSystemEfficiency = dSystemEfficiency;
	}

}
