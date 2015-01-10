package br.ufrj.dcc.ad.simulador.utils;

import java.util.ArrayList;
import java.util.List;

public class CumulativeDensityFunctionCalculator {

	private List<Integer> mPrePdf = new ArrayList<>();
	private List<Double> cdf = new ArrayList<>();
	
	private double timeToRecuparation;
	int totalCount = 0;
	double PRECISION = 0.5;


	public List<Integer> getmPrePdf() {
		return mPrePdf;
	}

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
		for (int i = 0; i < mPrePdf.size(); i++) {
			pdf.add((mPrePdf.get(i) * 1.0) / totalCount);
		}
		return pdf;
	}

	public void inRecuperation(Double value) {
		timeToRecuparation += value;
	}

	public void recupered(Double value) {
		Long index = Math.round(timeToRecuparation / PRECISION);
		int i = index.intValue();
		while (mPrePdf.size() <= i)
			mPrePdf.add(0);

		mPrePdf.set(i, (mPrePdf.get(i) + 1));
		
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

	public void printCDF(double r4){
		List<Double> cdf = getCDF();
		FileUtil file = new FileUtil("CDF.csv", "time;density;r4");
		for ( int i = 0; i < cdf.size();i++) {
			file.saveInFile(i * PRECISION + "",
					cdf.get(i) + "",
					r4 + "");
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

	public void accumulatePrePDF(List<Integer> prePdf, int tCount) {

		List<Integer> newGlobalCDF = new ArrayList<Integer>();

		if( this.mPrePdf.size() > prePdf.size() ){
			Integer value = 0;
			for (int i = 0; i < prePdf.size(); i++) {
				value = this.mPrePdf.get(i) + prePdf.get(i);
				newGlobalCDF.add(i, value);
			}
			for (int i = prePdf.size(); i < this.mPrePdf.size(); i++){
				value = this.mPrePdf.get(i);
				newGlobalCDF.add(i, value);
			}

		}else{
			Integer value = 0;
			for (int i = 0; i < this.mPrePdf.size(); i++) {
				value = this.mPrePdf.get(i) + prePdf.get(i);
				newGlobalCDF.add(i, value);

			}
			for (int i = this.mPrePdf.size(); i < prePdf.size(); i++){
				value = prePdf.get(i);
				newGlobalCDF.add(i, value);
			}
		}
		totalCount += tCount;
		this.mPrePdf = newGlobalCDF;
	}

	public int getTotalCount() {
		return totalCount;
	}
}
