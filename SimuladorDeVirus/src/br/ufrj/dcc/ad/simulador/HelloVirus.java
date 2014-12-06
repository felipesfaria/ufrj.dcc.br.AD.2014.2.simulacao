package br.ufrj.dcc.ad.simulador;

import java.text.DecimalFormat;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.utils.ExponentialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Printer;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

public class HelloVirus {

	private static final int MAX_SIMULATION = 5;
	public static double piZero = 0;
	public static double piP = 0;
	public static double totalTime = 0;
	public static double initialTime;
	public static long MAX_EVENTS;
	public static ExponentialGenerator genR1;
	public static ExponentialGenerator genR2;
	public static ExponentialGenerator genR3;
	public static ExponentialGenerator genLambda;
	public static ExponentialGenerator genR4;
	public static Tests tests = new Tests();
	public static VirusSimulation simulation;

	public static final double r1 = 2.0;
	public static final double r2 = 0.8;
	public static final double r3 = 3.0;
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);
	public static final double BETA = 0.08;	
	
	static Double r4 = 0.02;
	static Double delta = 0.001;
	static Double min_r4 = 0.0;
	
	static int maxEvents = 10000;
	
	public static Printer printer = new Printer();

	public static void main(String[] args) {
		printer = new Printer();
		
		// System.out.println("Welcome to the best assignment in the world!");

		if (args.length == 3) {
			r4 = Double.parseDouble(args[0]);
			delta = Double.parseDouble(args[1]);
			min_r4 = Double.parseDouble(args[2]);
		}
		
		runMeshCostAnalysis();
//		runSingleNodeCostAnalysis();
//		runSingleNodeTimeAnalysis();
		
		System.out.println("Finished");
	}

	static void runMeshCostAnalysis() {
		boolean printCSV = true;

		FileUtil file = new FileUtil("MeshCostAnalysis.csv", "r4;piO;piP;piR;piF;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new NewVirusMeshSimulation(maxEvents, r,4);
				printer.setPrintOptions(new PrintOptions[]{PrintOptions.steps,PrintOptions.states,PrintOptions.results,PrintOptions.states});
				simulation.setUpSimulation();
				stats = simulation.runFullSimulation();
				
				Statistics.incrementSimulation();
				Statistics.acumulatePiO(stats.getPiO());
				Statistics.acumulatePiP(stats.getPiP());
				Statistics.acumulateInfectedCost(stats.getInfectedCost());
				Statistics.acumulateSamplingCost(stats.getSamplingCost());
				Statistics.acumulateTotalCost(stats.getTotalCost());
			}

			if (printCSV) {
				file.saveInFile(
						dc.format(r4),
						dc.format(Statistics.getGlobalAveragePiO()),
						dc.format(Statistics.getGlobalAverageInfectedCost()),
						dc.format(Statistics.getGlobalAverageSamplingCost()),
						dc.format(Statistics.getGlobalAverageTotalCost()));
			}

			r4 -= delta;
		}

	}

	static void runSingleNodeCostAnalysis() {
		System.out.println("R4;piO;cV;cS;cT");
		FileUtil file = new FileUtil("CostAnalysis.csv", "R4;piO;cV;cS;cT");

		while (r4 >= min_r4) {
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA);
			simulation = new VirusSingleSimulation(maxEvents, r, file);
			printer.setPrintOptions(new PrintOptions[] { PrintOptions.CSV });
			simulation.setUpSimulation();
			simulation.runFullSimulation();
			r4 -= delta;
		}

	}

	static void runSingleNodeTimeAnalysis() {
		FileUtil file = new FileUtil("TimeAnalysis.csv", "t;P(t<T)");
		Rates r = new Rates(r1, r2, r3, 0.14, LAMBDA); // TODO melhorar
													  //TODO Felipe: melhorar o que?
		simulation = new VirusSingleSimulation(maxEvents, r, file);
		printer.setPrintOptions(new PrintOptions[] { PrintOptions.CSV, PrintOptions.CDF });
		simulation.setUpSimulation();
		simulation.runFullSimulation();
	}

}
