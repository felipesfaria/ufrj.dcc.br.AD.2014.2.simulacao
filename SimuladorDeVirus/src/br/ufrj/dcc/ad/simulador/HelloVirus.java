package br.ufrj.dcc.ad.simulador;

public class HelloVirus {

	public static double piZero = 0;
	public static double piP = 0;
	public static double totalTime = 0;
	public static double initialTime;
	public static long MAX_EVENTS;
	public static ExponencialGenerator genR1;
	public static ExponencialGenerator genR2;
	public static ExponencialGenerator genR3;
	public static ExponencialGenerator genLambda;
	public static ExponencialGenerator genR4;
	public static Tests tests = new Tests();
	public static VirusSimulation simulation;
	
	public static final double r1 = 2.0;
	public static final double r2 = 0.8;
	public static final double r3 = 3.0;
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);

	public static void main(String[] args) {

		// System.out.println("Welcome to the best assignment in the world!");
		Double r4;
		Double delta;
		Double min_r4;
		int maxEvents = 10000;

		if(args.length == 3){
			r4 = Double.parseDouble(args[0]);
			delta = Double.parseDouble(args[1]);
			min_r4 = Double.parseDouble(args[2]);
		} else {
			delta = 0.0001;
			r4 = 0.5;
			min_r4 = 0.0;
		}

		System.out.println("R4;piO;cV;cS;cT");
		FileUtil file = new FileUtil("output.csv", "R4;piO;cV;cS;cT");
		
		while (r4 >= min_r4) {
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA);
			simulation = new VirusSimulation(maxEvents, r, file);
			simulation.setPrintOptions(new String[] { "CSV" });
			simulation.setUpSimulation();
			simulation.runFullSimulation();
			r4 -= delta;
		}
		System.out.println("Finished");
	}

}
