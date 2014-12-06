package br.ufrj.dcc.ad.simulador;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.Results;
import br.ufrj.dcc.ad.simulador.utils.ExponentialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;

import java.text.DecimalFormat;

public class HelloVirus {

	private static final int MAX_SIMULATION = 50;
//	public static double piZero = 0;
//	public static double piP = 0;
//	public static double totalTime = 0;
//	public static double initialTime;
//	public static long MAX_EVENTS;
//	public static ExponentialGenerator genR1;
//	public static ExponentialGenerator genR2;
//	public static ExponentialGenerator genR3;
//	public static ExponentialGenerator genLambda;
//	public static ExponentialGenerator genR4;
//	public static Tests tests = new Tests();
	public static VirusSimulation simulation;

	public static final double r1 = 2.0;
	public static final double r2 = 0.8;
	public static final double r3 = 3.0;
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);
	public static final double BETA = 0.08;

	static Double r4 = 0.9;
	static Double delta = 0.0001;
	static Double min_r4 = 0.0;
	
	static int maxEvents = 10000;

	public static void main(String[] args) {

		// System.out.println("Welcome to the best assignment in the world!");

		if (args.length == 3) {
			r4 = Double.parseDouble(args[0]);
			delta = Double.parseDouble(args[1]);
			min_r4 = Double.parseDouble(args[2]);
		}

//		runMeshOnce();
		runMeshCostAnalysis();
//		runSingleNodeCostAnalysis();
//		runSingleNodeTimeAnalysis();
		
		System.out.println("Finished");
	}

	static void runMeshCostAnalysis() {
		boolean printCSV = true;

		FileUtil file = new FileUtil("CostAnalysis.csv", "r4;piO;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			Double piO = 0.0;
			Double piP = 0.0;
			Double infectedCost = 0.0;
			Double samplingCost = 0.0;
			Double totalCost = 0.0;
			Rates r = new Rates(r1, BETA, r3, r4, LAMBDA);

			for (int i = 0; i < MAX_SIMULATION; i++) {

				Results res;
				simulation = new NewVirusMeshSimulation(maxEvents, r);
				simulation.setPrintOptions(new String[]{});
				simulation.setUpSimulation();
				res = simulation.runFullSimulation();

				piO += res.getPiO();
				piP += res.getPiP();
				infectedCost += res.getInfectedCost();
				samplingCost += res.getSamplingCost();
				totalCost += res.getTotalCost();
			}

			piO /= MAX_SIMULATION;
			piP /= MAX_SIMULATION;
			infectedCost /= MAX_SIMULATION;
			samplingCost /= MAX_SIMULATION;
			totalCost /= MAX_SIMULATION;

			if (printCSV) {
						file.saveInFile(""+r.getR4(),
								""+piO,
								""+infectedCost,
								""+samplingCost,
								""+totalCost);
			}

			r4 -= delta;
		}

	}

	static void runMeshOnce(){
		FileUtil file = new FileUtil("CostAnalysis.csv", "r4;piO;cV;cS;cT");
		Rates r = new Rates(r1, BETA, r3, r4, LAMBDA);
		simulation = new NewVirusMeshSimulation(maxEvents, r, file);
		simulation.setPrintOptions(new String[]{"CSV"});
		simulation.setUpSimulation();
		simulation.runFullSimulation();

	}


	static void runSingleNodeCostAnalysis() {
		System.out.println("R4;piO;cV;cS;cT");
		FileUtil file = new FileUtil("CostAnalysis.csv", "R4;piO;cV;cS;cT");

		while (r4 >= min_r4) {
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA);
			simulation = new VirusSingleSimulation(maxEvents, r, file);
			simulation.setPrintOptions(new String[] { "CSV" });
			simulation.setUpSimulation();
			simulation.runFullSimulation();
			r4 -= delta;
		}

	}

	static void runSingleNodeTimeAnalysis() {
		FileUtil file = new FileUtil("TimeAnalysis.csv", "t;P(t<T)");
		Rates r = new Rates(r1, r2, r3, 0.14, LAMBDA); // TODO melhorar
		simulation = new VirusSingleSimulation(maxEvents, r, file);
		simulation.setPrintOptions(new String[] { "CSV", "CDF" });
		simulation.setUpSimulation();
		simulation.runFullSimulation();
	}

}
