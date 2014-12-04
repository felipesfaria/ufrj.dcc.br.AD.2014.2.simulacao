package br.ufrj.dcc.ad.simulador.utils;
import java.text.DecimalFormat;

public class DecimalFormatter {

	   static public void customFormat(String pattern, double value ) {
	      DecimalFormat myFormatter = new DecimalFormat(pattern);
	      String output = myFormatter.format(value);
	      System.out.println(value + "  " + pattern + "  " + output);
	   }

	   static public void main(String[] args) {

	      customFormat("###,###.###", 123456.789);
	      customFormat("###.##", 123456.789);
	      customFormat("000000.000", 123.78);
	      customFormat("$###,###.###", 12345.67);  
	   }
	   
	   static public String dec(double value){
		  DecimalFormat myFormatter = new DecimalFormat(",000.000000000000000");
		  String output = myFormatter.format(value);
		  return output;
	   }
	}