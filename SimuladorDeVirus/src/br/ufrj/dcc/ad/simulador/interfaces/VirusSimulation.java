package br.ufrj.dcc.ad.simulador.interfaces;

import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Statistics;

public interface VirusSimulation {

	public abstract Statistics runFullSimulation();

	public void setPrintOptions(PrintOptions args[]);
	
	public void setUpSimulation();
}