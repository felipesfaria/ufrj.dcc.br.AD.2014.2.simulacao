package br.ufrj.dcc.ad.simulador.model;

public class Result {

	static int staticID = 0;
	int ID;
	Rates rates;
	
	Double timeInO = 0.0;
	Double timeInP = 0.0;
	Double timeInR = 0.0;
	Double timeInF = 0.0;
	Double piO;
	Double piP;
	Double piR;
	Double piF;
	Double infectedCost;
	Double samplingCost;
	Double totalCost;
	Double totalTime;
	
	
	public Result(Rates rates, Double timeInO, Double timeInP, Double timeInR,
			Double timeInF, Double piO, Double piP, Double piR, Double piF,
			Double infectedCost, Double samplingCost, Double totalCost,
			Double totalTime) {
		super();
		ID = staticID++;
		this.rates = rates;
		this.timeInO = timeInO;
		this.timeInP = timeInP;
		this.timeInR = timeInR;
		this.timeInF = timeInF;
		this.piO = piO;
		this.piP = piP;
		this.piR = piR;
		this.piF = piF;
		this.infectedCost = infectedCost;
		this.samplingCost = samplingCost;
		this.totalCost = totalCost;
		this.totalTime = totalTime;
	}
	
	public static int getID() {
		return staticID;
	}
	public static void setID(int iD) {
		staticID = iD;
	}
	public Rates getRates() {
		return rates;
	}
	public void setRates(Rates rates) {
		this.rates = rates;
	}
	public Double getTimeInO() {
		return timeInO;
	}
	public void setTimeInO(Double timeInO) {
		this.timeInO = timeInO;
	}
	public Double getTimeInP() {
		return timeInP;
	}
	public void setTimeInP(Double timeInP) {
		this.timeInP = timeInP;
	}
	public Double getTimeInR() {
		return timeInR;
	}
	public void setTimeInR(Double timeInR) {
		this.timeInR = timeInR;
	}
	public Double getTimeInF() {
		return timeInF;
	}
	public void setTimeInF(Double timeInF) {
		this.timeInF = timeInF;
	}
	public Double getPiO() {
		return piO;
	}
	public void setPiO(Double piO) {
		this.piO = piO;
	}
	public Double getPiP() {
		return piP;
	}
	public void setPiP(Double piP) {
		this.piP = piP;
	}
	public Double getPiR() {
		return piR;
	}
	public void setPiR(Double piR) {
		this.piR = piR;
	}
	public Double getPiF() {
		return piF;
	}
	public void setPiF(Double piF) {
		this.piF = piF;
	}
	public Double getInfectedCost() {
		return infectedCost;
	}
	public void setInfectedCost(Double infectedCost) {
		this.infectedCost = infectedCost;
	}
	public Double getSamplingCost() {
		return samplingCost;
	}
	public void setSamplingCost(Double samplingCost) {
		this.samplingCost = samplingCost;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(Double totalTime) {
		this.totalTime = totalTime;
	}
	public static int getStaticID() {
		return staticID;
	}
	public static void setStaticID(int staticID) {
		Result.staticID = staticID;
	}

	@Override
	public String toString() {
		return "piO:"+piO+"piP:"+piP+"piR:"+piR+"piF:"+piF+"cV:"+infectedCost+"cS:"+samplingCost+"cT:"+totalCost;
	}
}
