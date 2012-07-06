package game;

import java.util.Iterator;
import java.util.Vector;

public class GenericStorage extends GenericGame {

	/**
	 * storage weight of acitivities on grid sites [iClass]
	 */
	double[] daStorageWeight;

	/**
	 * Storage Limit: [iSite]
	 */
	double[] daStorageLimit;

	/**
	 * Used Storage: [iSite]
	 */
	double[] daStorageUsed;

	/**
	 * Storage Requirment by input: [iClass]
	 */
	double[] daStorageInput;

	/**
	 * Storage Requirment by output: [iClass]
	 */
	double[] daStorageOutput;

	/**
	 * Unreleased activity
	 */
	Vector<ReleaseStorage> vUnreleased = new Vector<ReleaseStorage>();
	
	public GenericStorage(int iClass, int iSite) {
		super(iClass, iSite);
	}
	
	@Override
	public void init() {
		super.init();
		daStorageWeight = new double[iClass];
		daStorageLimit = new double[iSite];
		daStorageUsed = new double[iSite];
		daStorageInput = new double[iClass];
		daStorageOutput = new double[iClass];
	}
	
	
	void releaseAllStorage(int activityclass) {
		for (int i = 0; i < iSite; i++) {
			if (dmDist[activityclass][i] > 0) {
				daStorageUsed[i] = daStorageUsed[i]
						- daStorageOutput[activityclass]
						* dmDist[activityclass][i];
			}
		}
	}

	void registerStorage(boolean lastActivity) {
		ReleaseStorage rs = new ReleaseStorage();
		rs.activityNo = iMinClass;
		rs.site = iMinSite;
		rs.lastActivity = lastActivity;
		rs.releaseTime = dmMinminTime[iMinSite][iMinCPU];
		vUnreleased.add(rs);
		daStorageUsed[iMinSite] += daStorageInput[iMinClass]
				+ daStorageOutput[iMinClass];
	}

	void updateStorage(double time, int site) {

		println("st0 before= " + daStorageUsed[0]);
		println("st1 before= " + daStorageUsed[1]);

		int size = vUnreleased.size();
		println("size=" + size + "; iMinClass=" + iMinClass);
		ReleaseStorage rs1 = null, rs2 = null;
		Vector<ReleaseStorage> vRemove = new Vector<ReleaseStorage>();
		
		for (int i = 0; i < size; i++) {
			rs1 = vUnreleased.elementAt(i);
			if ((rs1.releaseTime <= time & rs1.site == site) | site == -1) {
				
				// get the input storage;
				daStorageUsed[rs1.site] -= daStorageInput[rs1.activityNo];
				vRemove.add(rs1);

				/* release space from the completed workflow */
				if (rs1.lastActivity) {
					// find the last one which have been compeleted
					for (int k = 0; k < size; k++) {
						rs2 = vUnreleased.elementAt(k);
						
						// exchange the position
						if (rs2.activityNo == rs1.activityNo & rs2.releaseTime > rs1.releaseTime) {
							rs1.lastActivity = false;
							rs2.lastActivity = true;
						}
					}
					if (rs1.lastActivity || (rs2.lastActivity & rs2.releaseTime <= time))
						for (int j = 0; j < iSite; j++) {
							// get the output storage;
							daStorageUsed[j] -= daStorageOutput[rs1.activityNo]	* dmDist[rs1.activityNo][j];
						}
				}
			}
		}

		println("st0 after= " + daStorageUsed[0]);
		println("st1 after= " + daStorageUsed[1]);
		println("===============================");
		for (Iterator iter = vRemove.iterator(); iter.hasNext();) {
			ReleaseStorage element = (ReleaseStorage) iter.next();
			vUnreleased.remove(element);
		}
	}

	double getAvailableStorage(int site, double time) {
		int size = vUnreleased.size();
		ReleaseStorage rs1 = null;
		double result = 0;
		for (int i = 0; i < size; i++) {
			rs1 = vUnreleased.elementAt(i);
			if (rs1.site == site & rs1.releaseTime <= time) {
				result += daStorageInput[rs1.activityNo];
			}

			/* release space from the completed workflow */
			if (rs1.lastActivity & rs1.releaseTime <= time) {
				// get the output storage;
				result += daStorageOutput[rs1.activityNo] * dmDist[rs1.activityNo][site];
			}
		}

		result = daStorageLimit[site] - daStorageUsed[site] + result;
		println("available on site "+site+"=" + result+"time=" + time);

		return result;
	}
	
	public void initializeStorageEnv(GenericStorage game) {
		this.setDaStorageInput(game.daStorageInput);
		this.setDaStorageOutput(game.daStorageOutput);
		this.setDaStorageLimit(game.daStorageLimit);
		this.setDmPrediction(game.dmPrediction);
		this.setDaPrice(game.daPrice);
		this.setIaLength(game.iaTask);
		this.setIaCurrentLength(game.iaTask);
		this.setIaCPU(game.iaCPU);
		this.setICPUinit(game.iaCPU);

	}

	/**
	 * compute the storage weights of activites
	 * 
	 */
	public void calculateStorageWeight() {
	}


	public double[] getDaStorageLimit() {
		return daStorageLimit;
	}

	public void setDaStorageLimit(double[] storageLimit) {
		daStorageLimit = storageLimit;
	}

	public double[] getDaStorageRequirment() {
		return daStorageInput;
	}

	public void setDaStorageRequirment(double[] storageRequirment) {
		daStorageInput = storageRequirment;
	}

	public double[] getDaStorageInput() {
		return daStorageInput;
	}

	public void setDaStorageInput(double[] daStorageInput) {
		this.daStorageInput = daStorageInput;
	}

	public double[] getDaStorageOutput() {
		return daStorageOutput;
	}

	public void setDaStorageOutput(double[] daStorageOutput) {
		this.daStorageOutput = daStorageOutput;
	}

	public double[] getDaStorageUsed() {
		return daStorageUsed;
	}

	public void setDaStorageUsed(double[] daStorageUsed) {
		this.daStorageUsed = daStorageUsed;
	}

	public double getDControl() {
		return dControl;
	}

	public void setDControl(double control) {
		dControl = control;
	}

	public Vector<ReleaseStorage> getVUnreleased() {
		return vUnreleased;
	}

	public void setVUnreleased(Vector<ReleaseStorage> unreleased) {
		vUnreleased = unreleased;
	}


	
	

}
