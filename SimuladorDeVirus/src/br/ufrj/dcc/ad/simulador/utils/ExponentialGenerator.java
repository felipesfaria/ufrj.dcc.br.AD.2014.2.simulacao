package br.ufrj.dcc.ad.simulador.utils;

import java.util.Random;

public class ExponentialGenerator {

	private Random rand;
	private Double rate;
	public ExponentialGenerator(Double rate) {
		super();
		this.rand = new Random(System.currentTimeMillis());
		//this.rand = new Random(5);
		this.rate = rate;
	}
	public Double generate(){
		return -Math.log(rand.nextDouble())/rate;
	}
	
}
