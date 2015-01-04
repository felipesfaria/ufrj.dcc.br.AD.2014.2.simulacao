package br.ufrj.dcc.ad.simulador.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.Result;
import br.ufrj.dcc.ad.simulador.model.State;

public class Statistics {
	
	static int simulations=0;
	static Double globalAcumulatedPiO=0.0;
	static Double globalAcumulatedPiP=0.0;
	static Double globalAcumulatedPiR=0.0;
	static Double globalAcumulatedPiF=0.0;
	static Double globalAcumulatedInfectedCost=0.0;
	static Double globalAcumulatedSamplingCost=0.0;
	static Double globalAcumulatedTotalCost=0.0;
	
	Double r4;
	Double timeInO=0.0;
	Double timeInP=0.0;
	Double timeInR=0.0;
	Double timeInF=0.0;
	Double piO;
	Double piP;
	Double piR;
	Double piF;
	//Pra cada r4 tem um arraylist dos resultados da simulação
	static Map<Double,ArrayList<Result>> completeResults = new HashMap<Double,ArrayList<Result>>();
	Result result;
	
	Double infectedWeight=10.0;//CV
	Double samplingWeight=9.0;//CS
	Double infectedCost;
	Double samplingCost;
	Double totalCost;
	Double totalTime;
	
	Rates rates;

	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	private int counter=0;
	
	public Statistics(Rates r, final Double infectedWeight, final Double samplingWeight) {
		rates = r;
		this.infectedWeight = infectedWeight;
		this.samplingWeight = samplingWeight;
	}
	
	public Statistics(Rates r) {
		rates = r;
	}

	public void addTimePerState(double time, State state){
		switch(state){
		case O:
			timeInO+=time;
			break;
		case P:
			timeInP+=time;
			break;
		case R:
			timeInR+=time;
			break;
		case F:
			timeInF+=time;
			break;
		default:
			System.out.println("Error: invalidState");
			break;
		}
	}
	
	public Double getPiO() {
		return piO;
	}
	public Double getPiP() {
		return piP;
	}
	public Double getPiR() {
		return piR;
	}
	public Double getPiF() {
		return piF;
	}
	public Double getInfectedCost() { return infectedCost; }
	public Double getSamplingCost() { return samplingCost; }
	public Double getTotalCost() { return totalCost; }
		
	public int getCounter() {
		return counter;
	}

	public Double getR4() {
		return rates.getR4();
	}

	public Double getTotalTime() {
		return totalTime;
	}

	public void count(){
		counter++;
	}
	
	public void finish(){
		totalTime = timeInO+timeInP+timeInR+timeInF;
		piO=timeInO/totalTime;
		piP=timeInP/totalTime;
		piR=timeInR/totalTime;
		piF=timeInF/totalTime;
		infectedCost=(1-piO)*infectedWeight;
		samplingCost=(piO+piP)*rates.getR4()*samplingWeight;
		totalCost=samplingCost+infectedCost;
		result = new Result(rates,timeInO,timeInP,timeInR,timeInF,piO,piP,piR,piF,infectedCost ,samplingCost,totalCost,totalTime);
		
		if(!completeResults.containsKey(rates.getR4())){
			ArrayList<Result> results = new ArrayList<Result>();
			results.add(result);
			completeResults.put(rates.getR4(),results);
		}
		else{
			completeResults.get(rates.getR4()).add(result);
		}
	}
	
	public static double GetIntervaloDeConfiança(Double r4){
		double intervaloDeConfiança;
		double variancia = GetVariancia(r4);
		
		intervaloDeConfiança = 2*1.96*Math.sqrt(variancia)/Math.sqrt(completeResults.get(r4).size());
		
		return intervaloDeConfiança;
	}
	
	public static Map<String,Double>  GetIntervalosDeConfiança(Double r4){
		Map<String,Double> intervalosDeConfianca = new HashMap<String, Double>();
		Map<String,Double> variancias = GetVariancias(r4);
		intervalosDeConfianca.put("piO",2*1.96*Math.sqrt(variancias.get("piO"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("piP",2*1.96*Math.sqrt(variancias.get("piP"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("piR",2*1.96*Math.sqrt(variancias.get("piR"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("piF",2*1.96*Math.sqrt(variancias.get("piF"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("CustoInfectado",2*1.96*Math.sqrt(variancias.get("CustoInfectado"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("CustoAmostragem",2*1.96*Math.sqrt(variancias.get("CustoAmostragem"))/Math.sqrt(completeResults.get(r4).size()));
		intervalosDeConfianca.put("CustoTotal",2*1.96*Math.sqrt(variancias.get("CustoTotal"))/Math.sqrt(completeResults.get(r4).size()));
		
		return intervalosDeConfianca;
	}
	
	public static double GetMedia(Double r4){
		int n=0;
		double custoInfectadoAcumulado=0;
		double mediaDeCustoInfectado=0;
		Map<String,Double> medias = new HashMap<String, Double>();
		ArrayList<Result> results = completeResults.get(r4);
		for(Result result : results){
			n++;
			custoInfectadoAcumulado+=result.getInfectedCost();
		}
		mediaDeCustoInfectado = custoInfectadoAcumulado/n;
		return mediaDeCustoInfectado;
	}
	
	public static Map<String,Double> GetMedias(Double r4){
		int n=0;
		double custoInfectadoAcumulado=0;
		double mediaDeCustoInfectado=0;
		Map<String,Double> medias = new HashMap<String, Double>();
		medias.put("piO",0.0);
		medias.put("piP",0.0);
		medias.put("piR",0.0);
		medias.put("piF",0.0);
		medias.put("CustoInfectado",0.0);
		medias.put("CustoAmostragem",0.0);
		medias.put("CustoTotal",0.0);
		ArrayList<Result> results = completeResults.get(r4);
		for(Result result : results){
			n++;
			custoInfectadoAcumulado+=result.getInfectedCost();
			medias.replace("piO",medias.get("piO")+result.getPiO());
			medias.replace("piP",medias.get("piP")+result.getPiP());
			medias.replace("piR",medias.get("piR")+result.getPiR());
			medias.replace("piF",medias.get("piF")+result.getPiF());
			medias.replace("CustoInfectado",medias.get("CustoInfectado")+result.getInfectedCost());
			medias.replace("CustoAmostragem",medias.get("CustoAmostragem")+result.getSamplingCost());
			medias.replace("CustoTotal",medias.get("CustoTotal")+result.getTotalCost());
		}
		mediaDeCustoInfectado = custoInfectadoAcumulado/n;
		medias.replace("piO",medias.get("piO")/n);
		medias.replace("piP",medias.get("piP")/n);
		medias.replace("piR",medias.get("piR")/n);
		medias.replace("piF",medias.get("piF")/n);
		medias.replace("CustoInfectado",medias.get("CustoInfectado")/n);
		medias.replace("CustoAmostragem",medias.get("CustoAmostragem")/n);
		medias.replace("CustoTotal",medias.get("CustoTotal")/n);
		return medias;
	}
	
	public static double GetVariancia(Double r4){
		double media =GetMedia(r4);
		Map<String,Double> medias = GetMedias(r4);
		int n=0;
		double custoInfectadoVarianciaAcumulado=0;
		double custoInfectadoVarianciaCalculada=0;
		ArrayList<Result> results = completeResults.get(r4);
		for(Result result : results){
			n++;
			custoInfectadoVarianciaAcumulado+=Math.pow(result.getInfectedCost()-media,2);
		}
		custoInfectadoVarianciaCalculada=custoInfectadoVarianciaAcumulado/(n-1);
		return custoInfectadoVarianciaCalculada;
	}
	
	public static Map<String,Double> GetVariancias(Double r4){
		Map<String,Double> medias = GetMedias(r4);
		int n=0;
		ArrayList<Result> results = completeResults.get(r4);
		Map<String,Double> variancias = new HashMap<String, Double>();
		variancias.put("piO",0.0);
		variancias.put("piP",0.0);
		variancias.put("piR",0.0);
		variancias.put("piF",0.0);
		variancias.put("CustoInfectado",0.0);
		variancias.put("CustoAmostragem",0.0);
		variancias.put("CustoTotal",0.0);
		for(Result result : results){
			n++;
			variancias.replace("piO",variancias.get("piO")+Math.pow(result.getPiO()-medias.get("piO"),2));
			variancias.replace("piP",variancias.get("piP")+Math.pow(result.getPiP()-medias.get("piP"),2));
			variancias.replace("piR",variancias.get("piR")+Math.pow(result.getPiR()-medias.get("piR"),2));
			variancias.replace("piF",variancias.get("piF")+Math.pow(result.getPiF()-medias.get("piF"),2));
			variancias.replace("CustoInfectado",variancias.get("CustoInfectado")+Math.pow(result.getInfectedCost()-medias.get("CustoInfectado"),2));
			variancias.replace("CustoAmostragem",variancias.get("CustoAmostragem")+Math.pow(result.getSamplingCost()-medias.get("CustoAmostragem"),2));
			variancias.replace("CustoTotal",variancias.get("CustoTotal")+Math.pow(result.getTotalCost()-medias.get("CustoTotal"),2));
		}
		variancias.replace("piO",variancias.get("piO")/(n-1));
		variancias.replace("piP",variancias.get("piP")/(n-1));
		variancias.replace("piR",variancias.get("piR")/(n-1));
		variancias.replace("piF",variancias.get("piF")/(n-1));
		variancias.replace("CustoInfectado",variancias.get("CustoInfectado")/(n-1));
		variancias.replace("CustoAmostragem",variancias.get("CustoAmostragem")/(n-1));
		variancias.replace("CustoTotal",variancias.get("CustoTotal")/(n-1));
		return variancias;
	}
	
	public void printResult() {
		System.out.println("Simulation finished.");
		System.out.println("Steps: " + counter + "\tTime Simulated: " + dc.format(totalTime));
		System.out.println("pi0: " + dc.format(piO) + "\tpiP: " + dc.format(piP));
		System.out.println("Custo Infectado: " + dc.format(infectedCost) + "\t" + "Custo Amostragem: " + dc.format(samplingCost));
		System.out.println("Custo Total: " + dc.format(totalCost));
	}
	public static void accumulatePiO(Double piO){
		globalAcumulatedPiO+=piO;
	}
	public static void accumulatePiP(Double piP){
		globalAcumulatedPiP+=piP;
	}
	public static void accumulatePiR(Double piR) { globalAcumulatedPiR+=piR; }
	public static void accumulatePiF(Double piF) { globalAcumulatedPiR+=piF; }

	public static Double getGlobalAveragePiO(){
		return globalAcumulatedPiO/simulations;
	}
	public static Double getGlobalAveragePiP(){ return globalAcumulatedPiP/simulations; }
	public static Double getGlobalAveragePiR(){ return globalAcumulatedPiR/simulations; }
	public static Double getGlobalAveragePiF(){ return globalAcumulatedPiF/simulations; }

	public static void accumulateInfectedCost(Double InfectedCost){
		globalAcumulatedInfectedCost+=InfectedCost;
	}
	public static void accumulateSamplingCost(Double SamplingCost){
		globalAcumulatedSamplingCost+=SamplingCost;
	}
	public static void accumulateTotalCost(Double TotalCost){
		globalAcumulatedTotalCost+=TotalCost;
	}

	public static Double getGlobalAverageInfectedCost(){
		return globalAcumulatedInfectedCost/simulations;
	}
	public static Double getGlobalAverageSamplingCost(){
		return globalAcumulatedSamplingCost/simulations;
	}
	public static Double getGlobalAverageTotalCost(){
		return globalAcumulatedTotalCost/simulations;
	}
	
	public static void incrementSimulation(){
		simulations++;
	}

	public static void resetGlobalStatistics() {
		globalAcumulatedPiO=0.0;
		globalAcumulatedPiP=0.0;
		globalAcumulatedPiR=0.0;
		globalAcumulatedPiF=0.0;
		globalAcumulatedInfectedCost=0.0;
		globalAcumulatedSamplingCost=0.0;
		globalAcumulatedTotalCost=0.0;
	}


}
