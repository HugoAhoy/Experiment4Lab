package FeElectric.FeElectric.Clean;

import FeElectric.FeElectric.Clean.Relation;
import Utils.CsvUtil;
import Utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Clean {
	private static Properties props;
	private static StanfordCoreNLP pipeline;
	private static String text = "often result from";
	private static String filepath = "D:\\GitRepository\\FeElectric\\data\\needclean.csv";
	
	public Clean() {
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public static void main(String[] args) throws IOException {
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
		pipeline = new StanfordCoreNLP(props);
		List<String[]> result = makeclean(filepath);
		String outputFilePath = "D:\\GitRepository\\FeElectric\\data\\CleanResult.csv";
		String []  header= {"subject","relation","object", "MainSubject","MainRelation","RelationProps","MainObject"};
		CsvUtil.writeCsv(header, result, outputFilePath);
	}
	
//	判断名词
	public static boolean isNoun(String Pos) {
		if(Pos.equals("NNP") || Pos.equals("NN") || Pos.equals("NNPS") || Pos.equals("NNS")){
			return true;
		}
		else {
			return false;
		}
	}
	
//	判断谓词
//	public static boolean isPredicate(String Pos) {
//		if(Pos.equals("VB")||Pos.equals("VBG")||Pos.equals("VBD")||Pos.equals("VBN")||Pos.equals("VBZ")||Pos.equals("VBP")) {
//			return true;
//		}
//		else {
//			return false;
//		}
//	}

	
//	判断副词（关系属性）
	public static boolean isProperty(String Pos) {
		if(Pos.equals("RB")||Pos.equals("RBR")||Pos.equals("RBS")) {
			return true;
		}
		else {
			return false;
		}
	}
	
//	获得实体主要成分
	public static String getMain(String Entity) {
		CoreDocument document = new CoreDocument(Entity);
		pipeline.annotate(document);
		CoreSentence sentence = document.sentences().get(0);
		List<String> posTags = sentence.posTags();
		Iterator<String> posIt = posTags.iterator();
		Iterator<CoreLabel> labelIt = document.tokens().iterator();
		
//		返回找到的第一个名词（组）
		while(posIt.hasNext()&&labelIt.hasNext()) {
			if(isNoun(posIt.next())) {
				String mainEntity = labelIt.next().word();
				while(posIt.hasNext()) {
					if(!isNoun(posIt.next())){
						if(labelIt.next().word().equals("of")) {
							mainEntity += " of";
							continue;
						}
						else {
							break;
						}
					}
					mainEntity += " " + labelIt.next().word();
				}
				return mainEntity;
//				return labelIt.next().word();
			}
			else {
				labelIt.next();				
			}
		}
		
//		如果没有找到一个名词，则可能是由于多词性导致POS标注的问题
//		返回原来的实体名称
		return Entity;
	}
	

	public static Relation parseRelation(String Rel) {
		List<String> properties = new ArrayList<>();
		String predicate = "";
		CoreDocument document = new CoreDocument(Rel);
		pipeline.annotate(document);
		CoreSentence sentence = document.sentences().get(0);
		List<String> posTags = sentence.posTags();

		Iterator<String> posIt = posTags.iterator();
		Iterator<CoreLabel> labelIt = document.tokens().iterator();

		while(posIt.hasNext()&&labelIt.hasNext()) {
			String pos = posIt.next();
			if(isProperty(pos)) {
				properties.add(labelIt.next().word());
				continue;
			}
			else {
				predicate += " " + labelIt.next().word();
			}
		}

		Relation relation = new Relation(properties, predicate);
		return relation;
	}
	

	public static List<String[]> makeclean(String filepath) {
		File file = new File(filepath);
		List<String> triples = FileUtil.readCsv(file);
		System.out.println(triples);
		List<String[]> result = new ArrayList<>();
		Iterator<String> it = triples.iterator();
		while(it.hasNext()) {
			String[] triple = it.next().split(",");
			String subject = triple[0];
			String rel = triple[1];
			String object = triple[2];
			System.out.println(subject+rel+object);
			String[] temp = new String[7];
			temp[0] = subject;
			temp[1] = rel;
			temp[2] = object;
			temp[3] = getMain(subject);
			Relation relation = parseRelation(rel);
			temp[4] = relation.getPredicate();
			temp[5] = relation.getProperties().toString();
			temp[6] = getMain(object);
			result.add(temp);
		}
		return result;
	}
}
