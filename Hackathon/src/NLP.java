import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.hackathon.helper.SentiWordNetDemoCode;

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

public class NLP {

	private static List<TaggedWord> tagged;
	
	public static String classify(String str){
		String text=str;
		Annotation document = new Annotation(text);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(document);
		int mainSentiment = 0;
        int longest = 0;
        Tree tree = null;
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            String partText = sentence.toString();
            if (partText.length() > longest) {
                mainSentiment = sentiment;
                longest = partText.length();
            }
        }
		
		POSTagger(text);
		
		//Predefined classiffication Algorithm
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
	
	public static String inOrderTraversalScore(Tree tree) throws Exception{
		String retval = null,pos=null;
		if(tree.isLeaf()){
			pos = tagged.get(tagged.indexOf(new TaggedWord(tree.value()))).tag();
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
	
	public static String computeScore(String text,String tag) throws Exception{
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
	
	public static void POSTagger(String text){
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
	    MaxentTagger tagger = new MaxentTagger(taggerPath);
	    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
	    for (List<HasWord> sentence : tokenizer) {
	      tagged = tagger.tagSentence(sentence);
	      //System.out.println(tagged.toString());
	    }
	    
	}
	
	public static void main(String[] args) throws Exception{
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
}
