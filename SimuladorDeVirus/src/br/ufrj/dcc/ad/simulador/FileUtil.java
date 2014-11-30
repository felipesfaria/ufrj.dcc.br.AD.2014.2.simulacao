package br.ufrj.dcc.ad.simulador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtil {
	private String file;
	
	public FileUtil(String file,String header) {
		this.file = file;
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println(header);
		    out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader 
		}
	}
	
	public void saveInFile(String... values){
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < values.length; i++){
			builder.append(values[i]).append(";");
		}
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println(builder.toString());
		    out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader 
		}
	}
}
