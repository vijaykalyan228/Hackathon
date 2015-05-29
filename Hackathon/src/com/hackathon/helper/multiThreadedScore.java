package com.hackathon.helper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

class threadScorer extends Thread {

	private List<TaggedWord> tagged;
	private int k;

	public threadScorer(int k){
		this.k=k;
	}

	public String classify(String str){
		String text=str;
		Annotation document = new Annotation(text);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(document);
		int mainSentiment = 0;
		int longest = 0;
		Tree tree = null;
		System.out.println("before");
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
			System.out.println("in");
			tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
			int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			String partText = sentence.toString();
			if (partText.length() > longest) {
				mainSentiment = sentiment;
				longest = partText.length();
			}
			System.out.println("out");
		}
		System.out.println("After");
		POSTagger(text);

		//Predefined classification Algorithm
		String retval ="";
		if(mainSentiment==2){
			retval = "Neutral";
		}
		else if(mainSentiment==1){
			retval="Negative";
		}
		else{
			retval = "Positive";
		}
		return retval;
	}

	public String inOrderTraversalScore(Tree tree) throws Exception{
		String retval = null,pos=null;
		if(tree.isLeaf()){
			pos = this.tagged.get(this.tagged.indexOf(new TaggedWord(tree.value()))).tag();
			retval = computeScore(tree.value(),pos);
		}
		else{
			Tree[] children = tree.children();
			Tree left = children[0];
			Tree right = children[1];
			String leftScore = inOrderTraversalScore(left);
			String rightScore = inOrderTraversalScore(right);
			if(leftScore=="Positive"&&rightScore=="Positive"){
				retval = "Positive";
			}
			else if(leftScore=="Negative"&&rightScore=="Negative"){
				retval = "Negative";
			}
			else if(leftScore=="Positive"&&rightScore=="Negative"){
				retval = "Negative";
			}
			else if(leftScore=="Negative"&&rightScore=="Positive"){
				retval = "Positive";
			}
			else if(leftScore=="Neutral"){
				retval = rightScore;
			}
			else if(rightScore=="Neutral"){
				retval = leftScore;
			}

		}
		return retval;
	}

	public String computeScore(String text,String tag) throws Exception{
		String pos="a";
		if(tag=="NN"||tag=="NNP"||tag=="NNPS"||tag=="NNS"){
			pos="n";
		}
		else if(tag=="JJ"||tag=="JJS"||tag=="JJR"){
			pos="a";
		}
		else if(tag=="MD"||tag=="VB"||tag=="VBD"||tag=="VBG"||tag=="VBN"||tag=="VBP"||tag=="VBZ"){
			pos="v";
		}
		else if(tag=="RB"||tag=="RBR"||tag=="RBS"||tag=="WRB"){
			pos="r";
		}
		else{
			return "Neutral";
		}
		double score = SentiWordNetDemoCode.getSentiScore(text, pos);
		if(score==0){
			return "Neutral";
		}
		else if(score>0){
			return "Positive";
		}
		else{
			return "Negative";
		}
	}

	public void POSTagger(String text){
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
		MaxentTagger tagger = new MaxentTagger(taggerPath);
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			tagged = tagger.tagSentence(sentence);
			//System.out.println(tagged.toString());
		}

	}

	public void foo(String[] args) throws Exception{
		Scanner in = new Scanner(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\inputsample.txt")));
		PrintWriter out = new PrintWriter(new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\test.txt")));
		String text = "";
		while(in.hasNextLine()){
			text = in.nextLine();
			out.println(classify(text)+"\n");
		}
		out.close();
		in.close();
	}

	public void run(){
		try{
			Scanner in = new Scanner(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\dataset.txt")));
			PrintWriter out = new PrintWriter(new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\text.txt")));
			String text = "";
			int lines=0;
			HashMap<String,Integer> h = new HashMap<String,Integer>();
			h.put("Positive", 0);
			h.put("Negative", 0);
			h.put("Neutral", 0);
			HashMap<String,Integer> hFile = new HashMap<String,Integer>();
			hFile.put("Positive", 0);
			hFile.put("Negative", 0);
			hFile.put("Neutral", 0);
			while(in.hasNextLine()){
				lines+=1;
				text = in.nextLine();

				if(lines/2!=k){
					text = in.nextLine();
				}

				System.out.println("Line "+lines);
				String score = classify(text.substring(0,text.indexOf(",")));
				String fscore = text.substring(text.indexOf(",")+1);
				h.put(score, h.get(score)+1);
				hFile.put(fscore, hFile.get(fscore)+1);
				out.println(score);
			}
			out.close();
			in.close();

			int p = Math.abs(h.get("Positive")-hFile.get("Positive"));
			int n = Math.abs(h.get("Negative")-hFile.get("Negative"));
			int o = Math.abs(h.get("Neutral")-hFile.get("Neutral"));

			System.out.println("Accuracy:\t"+((lines-(p+o+n))/(lines*1.0)));
			System.out.println("P: "+p+" O: "+o+" N: "+n);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
public class multiThreadedScore {
	public static void main(String args[]) {
		threadScorer[] T = new threadScorer[2];
		for(int i=0;i<2;i++){
			T[i] = new threadScorer(i);
			T[i].start();
		}
	}   
}