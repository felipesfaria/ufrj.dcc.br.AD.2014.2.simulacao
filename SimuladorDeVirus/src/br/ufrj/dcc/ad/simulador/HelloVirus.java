package br.ufrj.dcc.ad.simulador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);

	public static void main(String[] args) {

		// System.out.println("Welcome to the best assignment in the world!");
		double r4;
		double delta;
		double min_r4;

		if (args[0] != null && !args[0].isEmpty()) {
			r4 = Double.parseDouble(args[0]);
			delta = Double.parseDouble(args[1]);
			min_r4 = Double.parseDouble(args[2]);
			
		} else {
			delta = 0.001;
			r4 = 2.0;
			min_r4 = 0.0000000001;
		}

		System.out.println("R4;piO;cV;cS;cT");

		while (r4 > min_r4) {
			Rates r = new Rates(2.0, 0.8, 3.0, r4, LAMBDA);
			simulation = new VirusSimulation(10000, r);
			simulation.setPrintOptions(new String[] { "CSV" });
			simulation.setUpSimulation();
			simulation.runFullSimulation();
			r4 -= delta;
		}
		System.out.println("Finished");
	}

	static void runProcess(EventQueue queue) {

		Event currentEvent = queue.pop();
		Double currentTime;
		Double nextTime;
		Event nextEvent;
		Double rTime;
		Double fTime;
		Double delta;

		// System.out.println("Node " + currentEvent.getNd().getNodeId()
		// + " executed event -> went from :" + currentEvent.getCurrentState() +
		// " to " + currentEvent.getNextState()
		// + " At: " + String.format("%.8f",currentEvent.getTime()) );

		switch (currentEvent.getNextState()) {
		case O:
			// Set the new state of the node
			currentEvent.getNd().setState(currentEvent.getNextState());

			// Get the time
			currentTime = currentEvent.getTime();
			delta = genR2.generate();
			nextTime = currentTime + delta;
			piZero += delta;

			// Next Event in Queue
			nextEvent = new Event(currentEvent.getNd(), State.P, nextTime);
			queue.add(nextEvent);
			break;
		case P:
			// Set the new state of the node
			currentEvent.getNd().setState(currentEvent.getNextState());

			// Get the time
			currentTime = currentEvent.getTime();
			rTime = genR4.generate();
			fTime = genLambda.generate();

			// We are going to execute the faster event
			if (rTime >= fTime) {
				nextTime = currentTime + fTime;
				piP += fTime;
				// Next Event in Queue
				nextEvent = new Event(currentEvent.getNd(), State.F, nextTime);
				queue.add(nextEvent);

			} else {
				nextTime = currentTime + rTime;
				piP += rTime;
				// Next Event in Queue
				nextEvent = new Event(currentEvent.getNd(), State.R, nextTime);
				queue.add(nextEvent);
			}
			break;
		case R:
			// Set the new state of the node
			currentEvent.getNd().setState(currentEvent.getNextState());

			// Get the time
			currentTime = currentEvent.getTime();
			delta = genR3.generate();
			nextTime = currentTime + genR3.generate();
			// Next Event in Queue
			nextEvent = new Event(currentEvent.getNd(), State.O, nextTime);
			queue.add(nextEvent);
			break;
		case F:
			// Set the new state of the node
			currentEvent.getNd().setState(currentEvent.getNextState());

			// Get the time
			currentTime = currentEvent.getTime();
			delta = genR1.generate();
			nextTime = currentTime + delta;

			// Next Event in Queue
			nextEvent = new Event(currentEvent.getNd(), State.O, nextTime);
			queue.add(nextEvent);
			break;
		default:
			System.out.println("You should not be here!");
			break;
		}
		totalTime = queue.getLastTime();
	}
}
