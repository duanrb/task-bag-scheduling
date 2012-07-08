package game;

public class GenericCommGame extends GenericGame {

	/**
	 * allocation of processors of grid sites for tasks, result of game
	 */
	double[][] dmBandwidthAlloc;
	
	/**
	 * sizes of data needed by each activity
	 */
	double[] daInputSize;
		
	/**
	 * bandwidth of sites
	 */
	double[] daBandwidth;

	
	public GenericCommGame() {
	}

	public GenericCommGame(int iClass, int iSite) {
		super(iClass, iSite);
	}
	
	@Override
	public void init() {
		super.init();
		daBandwidth = new double[iSite];
		daInputSize = new double[iClass];
	}

	public double[] getDaInputSize() {
		return daInputSize;
	}

	public void setDaInputSize(double[] daInputSize) {
		this.daInputSize = daInputSize;
	}
	

	public double[] getDaBandwidth() {
		return daBandwidth;
	}

	public void setDaBandwidth(double[] daBandwidth) {
		this.daBandwidth = daBandwidth;
	}


	
}
