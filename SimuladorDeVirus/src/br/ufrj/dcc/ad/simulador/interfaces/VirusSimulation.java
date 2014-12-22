package br.ufrj.dcc.ad.simulador.interfaces;

import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.utils.CumulativeDensityFunctionCalculator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

public interface VirusSimulation {

	public abstract Statistics runFullSimulation();
	
	public void setUpSimulation();

	public FileUtil getFile();

	public Statistics getStats();

	public Rates getRates();

	public CumulativeDensityFunctionCalculator getCDFCalculator();
}