package br.ufrj.dcc.ad.simulador.model;

public class Rates {
	private double R1;
	private double R2;
	private double R3;
	private double R4;
	private double LAMBDA;
	private double BETA;

	public Rates(double r1, double r2, double r3, double r4, double lambda) {
		R1 = r1;
		R2 = r2;
		R3 = r3;
		R4 = r4;
		LAMBDA = lambda;
	}
	
	public Rates(double r1, double r2, double r3, double r4, double lambda, double beta) {
		R1 = r1;
		R2 = r2;
		R3 = r3;
		R4 = r4;
		LAMBDA = lambda;
		BETA = beta;
	}

	public double getR4() {
		return R4;
	}

	public double getR1() {
		return R1;
	}

	public double getR2() {
		return R2;
	}

	public double getR3() {
		return R3;
	}

	public double getLAMBDA() {
		return LAMBDA;
	}

	public double getBETA() {
		return BETA;
	}
	
}
