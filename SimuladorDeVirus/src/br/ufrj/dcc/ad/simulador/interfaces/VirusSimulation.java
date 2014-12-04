package br.ufrj.dcc.ad.simulador.interfaces;

import br.ufrj.dcc.ad.simulador.model.Results;

public interface VirusSimulation {

	public abstract Results runFullSimulation();

	public void setPrintOptions(String args[]);
	
	public void setUpSimulation();
}