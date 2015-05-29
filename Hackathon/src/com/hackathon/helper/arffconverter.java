package com.hackathon.helper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class arffconverter {

	public static void convert(String file) throws Exception {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(new FileReader(new File(file)));//"C:\\Users\\Administrator\\Desktop\\dataset1.txt"
		PrintWriter out = new PrintWriter(new FileWriter(new File("dataset.arff")));//C:\\Users\\Administrator\\Desktop\\dataset00.arff
		out.println("@RELATION feedback");
		out.println("@ATTRIBUTE review string");
		out.println("@ATTRIBUTE class {Positive,Negative,Neutral}");
		out.println("\n@DATA");
		
		while(in.hasNextLine()){
			String text = in.nextLine();
			out.println("'"+text+"',?");
		}
		out.close();
		in.close();

	}

}