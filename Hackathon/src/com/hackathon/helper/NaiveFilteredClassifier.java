package com.hackathon.helper;
 
import weka.core.*;
import weka.core.FastVector;
import weka.classifiers.meta.FilteredClassifier;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

 public class NaiveFilteredClassifier {

	String text;
	Instances instances;
	FilteredClassifier classifier;

	public void load(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
                text = text + " " + line;
            }
			System.out.println("===== Loaded text data: " + fileName + " =====");
			reader.close();
			System.out.println(text);
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	
	public void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
			classifier = (FilteredClassifier) tmp;
            in.close();
 			System.out.println("===== Loaded model: " + fileName + " =====");
       } 
		catch (Exception e) {
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	
	public void makeInstance() {
		// Create the attributes, class and text
		FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("Positive");
		fvNominalVal.addElement("Negative");
		fvNominalVal.addElement("Neutral");
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		Attribute attribute2 = new Attribute("text",(FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		instances = new Instances("Test relation", fvWekaAttributes, 1);           
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance
		Instance instance = new Instance(2);
		instance.setValue(attribute2, text);
		// Another way to do it:
		// instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
		instances.add(instance);
 		System.out.println("===== Instance created with reference dataset =====");
		System.out.println(instances);
	}

	public void classify() {
		try {
			double pred = classifier.classifyInstance(instances.instance(0));
			System.out.println("===== Classified instance =====");
			System.out.println("Class predicted: " + instances.classAttribute().value((int) pred));
		}
		catch (Exception e) {
			System.out.println("Problem found when classifying the text");
		}		
	}

	public static void mainFoo (String[] args) {
	
		NaiveFilteredClassifier classifier;
			//System.out.println("Usage: java MyClassifier <fileData> <fileModel>");
			classifier = new NaiveFilteredClassifier();
			classifier.load("C:\\Users\\Administrator\\Desktop\\input.txt");
			classifier.loadModel("C:\\Users\\Administrator\\Desktop\\datamodel.dat");
			classifier.makeInstance();
			classifier.classify();
	}
}