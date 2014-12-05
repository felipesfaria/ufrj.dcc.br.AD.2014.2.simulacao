package br.ufrj.dcc.ad.simulador.model;

public class Results {
	Double r4;
	Double piO;
	Double piP;

	Double infectedCost;
	Double samplingCost;
	Double totalCost;

	public Results(final Double r4, final Double piO, final Double piP,final Double infectedCost, final Double samplingCost) {
		this.r4 = r4;
		this.piO = piO;
		this.piP = piP;
		this.infectedCost = infectedCost;
		this.samplingCost = samplingCost;
		this.totalCost = infectedCost + samplingCost;
	}

	public Double getPiO() {
		return piO;
	}
	public Double getPiP() {
		return piP;
	}
	public Double getInfectedCost() { return infectedCost; }
	public Double getSamplingCost() { return samplingCost; }
	public Double getTotalCost() { return totalCost; }

}
