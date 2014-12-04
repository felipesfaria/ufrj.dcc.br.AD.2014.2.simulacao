package br.ufrj.dcc.ad.simulador.utils;

import java.util.Random;

public class ExponencialGenerator {

	private Random rand;
	private Double rate;
	public ExponencialGenerator(Double rate) {
		super();
		this.rand = new Random(System.currentTimeMillis());
		this.rate = rate;
	}
	public Double generate(){
		return -Math.log(rand.nextDouble())/rate;
	}
	
}
