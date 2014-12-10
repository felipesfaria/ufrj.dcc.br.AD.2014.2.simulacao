package br.ufrj.dcc.ad.simulador.utils;

import java.util.ArrayList;
import java.util.List;

public class CumulativeDensityFunctionCalculator {

	private List<Integer> prePdf = new ArrayList<>();
	private List<Double> cdf = new ArrayList<>();
	
	private double timeToRecuparation;
	int totalCount = 0;
	double PRECISION = 0.01;

	public List<Double> getCDF() {
		
		List<Double> pdf = getPDF();
		
		double acumulator = 0;
		for (int i = 0; i < pdf.size(); i++) {
			acumulator += pdf.get(i);
			cdf.add(acumulator);
		}

		return cdf;
	}

	public List<Double> getPDF() {
		List<Double> pdf = new ArrayList<>();
		for (int i = 0; i < prePdf.size(); i++) {
			pdf.add((prePdf.get(i) * 1.0) / totalCount);
		}
		return pdf;
	}

	public void inRecuperation(Double value) {
		timeToRecuparation += value;
	}

	public void recupered(Double value) {
		Long index = Math.round(timeToRecuparation / PRECISION);
		int i = index.intValue();
		while (prePdf.size() <= i)
			prePdf.add(0);

		prePdf.set(i, (prePdf.get(i) + 1));
		
		timeToRecuparation = 0;
		totalCount++;
	}
	
	public void printCDF(){
		List<Double> cdf = getCDF();
		FileUtil file = new FileUtil("CDF.csv", "time;density");
		for ( int i = 0; i < cdf.size();i++) {
			file.saveInFile(i * PRECISION + "", 
							cdf.get(i) + "");
		}
		
	}
	
	public void printPDF(){
		List<Double> cdf = getPDF();
		FileUtil file = new FileUtil("PDF.csv", "time;probability");
		for ( int i = 0; i < cdf.size();i++) {
			file.saveInFile(i * PRECISION + "", 
							cdf.get(i) + "");
		}
		
	}

}
