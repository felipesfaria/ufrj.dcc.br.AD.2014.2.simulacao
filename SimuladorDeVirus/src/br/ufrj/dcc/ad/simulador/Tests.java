package br.ufrj.dcc.ad.simulador;

import br.ufrj.dcc.ad.simulador.utils.ExponencialGenerator;

public class Tests {

	public static ExponencialGenerator genR1;
	public static ExponencialGenerator genR2;
	public static ExponencialGenerator genR3;
	public static ExponencialGenerator genLambda;
	public static ExponencialGenerator genR4; 
    
    public static void testAverages(){

    	Double R1=2.0, R2 = 0.8, R3 = 3.0, R4 = 0.5, LAMBDA = 1.0/(12*24*30);
        genR1 = new ExponencialGenerator(R1);
        genR2 = new ExponencialGenerator(R2);
        genR3 = new ExponencialGenerator(R3);
        genR4 = new ExponencialGenerator(R4);
        genLambda = new ExponencialGenerator(LAMBDA);

        double avgR1=0;
        double avgR2=0;
        double avgR3=0;
        double avgR4=0;
        double avgLAMBDA=0;
        int LOOPS = 1000000;
        for(int i=0;i<LOOPS;i++){
			avgR1+=genR1.generate();
			avgR2+=genR2.generate();
			avgR3+=genR3.generate();
			avgR4+=genR4.generate();
			avgLAMBDA+=genLambda.generate();
        }
        
    	System.out.println("Test averages.");
    	System.out.println("This test compares the number generated with the random number generator with the expected value of the average for an exponential distribution.");
    	System.out.println("Numbers generated: "+LOOPS);
    	String measuredAvgR1 = String.format("%9.9f",(avgR1/LOOPS)); String actualAvgR1 = String.format("%9.9f",(1.0/R1)); String diferenceR1 = String.format("%9.9f",Math.abs((avgR1/LOOPS)-(1.0/R1)));
    	String measuredAvgR2 = String.format("%9.9f",(avgR2/LOOPS)); String actualAvgR2 = String.format("%9.9f",(1.0/R2)); String diferenceR2 = String.format("%9.9f",Math.abs((avgR2/LOOPS)-(1.0/R2)));
    	String measuredAvgR3 = String.format("%9.9f",(avgR3/LOOPS)); String actualAvgR3 = String.format("%9.9f",(1.0/R3)); String diferenceR3 = String.format("%9.9f",Math.abs((avgR3/LOOPS)-(1.0/R3)));
    	String measuredAvgR4 = String.format("%9.9f",(avgR4/LOOPS)); String actualAvgR4 = String.format("%9.9f",(1.0/R4)); String diferenceR4 = String.format("%9.9f",Math.abs((avgR4/LOOPS)-(1.0/R4)));
    	String measuredAvgLAMBDA = String.format("%9.9f",(avgLAMBDA/LOOPS)); String actualAvgLAMBDA = String.format("%9.9f",(1.0/LAMBDA)); String diferenceLAMBDA = String.format("%9.9f",Math.abs((avgLAMBDA/LOOPS)-(1.0/LAMBDA)));
		System.out.println("R1:"+R1+"\t"+"Measured Avg "+measuredAvgR1+"\t"+"Actual Avg: "+actualAvgR1+"\t"+"Diference: "+diferenceR1);
		System.out.println("R2:"+R2+"\t"+"Measured Avg "+measuredAvgR2+"\t"+"Actual Avg: "+actualAvgR2+"\t"+"Diference: "+diferenceR2);
		System.out.println("R3:"+R3+"\t"+"Measured Avg "+measuredAvgR3+"\t"+"Actual Avg: "+actualAvgR3+"\t"+"Diference: "+diferenceR3);
		System.out.println("R4:"+R4+"\t"+"Measured Avg "+measuredAvgR4+"\t"+"Actual Avg: "+actualAvgR4+"\t"+"Diference: "+diferenceR4);
		System.out.println("LAMBDA:"+LAMBDA+"\t"+"Measured Avg "+measuredAvgLAMBDA+"\t"+"Actual Avg: "+actualAvgLAMBDA+"\t"+"Diference: "+diferenceLAMBDA);
    }
}
