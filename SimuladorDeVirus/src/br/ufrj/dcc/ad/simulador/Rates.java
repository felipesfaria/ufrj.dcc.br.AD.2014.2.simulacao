package br.ufrj.dcc.ad.simulador;

public class Rates {
	private double R1;
	private double R2;
	private double R3;
	private double R4;
	private double LAMBDA;

	public Rates(double r1,double r2,double r3,double lambda){
		R1=r1;
		R2=r2;
		R3=r3;
		LAMBDA=lambda;
	}
	
	public Rates(double r1,double r2,double r3,double r4,double lambda){
		R1=r1;
		R2=r2;
		R3=r3;
		R4=r4;
		LAMBDA=lambda;
	}
	public double getR4() {
		return R4;
	}

	public void setR4(double r4) {
		R4 = r4;
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
	
}
