package org.tseg.seg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.tseg.tools.NLPTools;
import org.tseg.tools.SplitSentence;



public class SplitSentence_seg extends SplitSentence{

	
	
	public Set<Integer> getSegPos(String line,UnigramSeg seg){
		ArrayList<String> parts = seg.getSegWordList(line);
		Set<Integer> list = new HashSet<Integer>();
		int pos = 0;
		list.add(0);
		for(int i = 0; i < parts.size(); i++){
			pos += parts.get(i).length();
			list.add(pos);
		}
		return list;
	}
	
	//取得�?��的所有分词组合�?�?��可以切成多个单句
	public ArrayList<String> getAllFeatures_seg(String line,UnigramSeg seg){
		ArrayList<String> features = new ArrayList<String>();
		String sens[] = line.split(NLPTools.SENTENCEENDS_log);
		for(int i = 0; i < sens.length; i++){
			
			features.addAll(this.getAllFeatures_seg_sen(sens[i], seg));
		}
		return features;
	}
	
	//取得单句的所有分词组�?
	public ArrayList<String> getAllFeatures_seg_sen(String sen,UnigramSeg seg){
		ArrayList<String> features = seg.getSegWordList(sen);
		
		this.wordToPhrase(features);
		
		return features;
	}
	
	//取得词组以及词组对应的�?辑长�?
	public Map<String,Integer> getFeaMap(String line,UnigramSeg seg){
		
		return this.wordToPhrase(this.getAllParts(line, seg));
	}
	
	public ArrayList<String> getAllParts(String line,UnigramSeg seg){
		ArrayList<String> features = new ArrayList<String>();
		String sens[] = line.split(NLPTools.SENTENCEENDS_log);
		for(int i = 0; i < sens.length; i++){
			
			features.addAll(seg.getSegWordList(line));
		}
		return features;
	}
	

	
	static Set<Character> set_log_noSetSplit ;
	static String SENTENCEENDS_log_no_SplitSign;
	
	static {
		
		clearSplitSign();
		set_log_noSetSplit = NLPTools.getSet(SENTENCEENDS_log_no_SplitSign);
	}
	
	
	private static void clearSplitSign(){
		SENTENCEENDS_log_no_SplitSign = NLPTools.SENTENCEENDS_log;
		int splitSignIndex = SENTENCEENDS_log_no_SplitSign.indexOf(UnigramSeg.getStaticSplitSign());
		
		while(splitSignIndex > 0 && splitSignIndex < SENTENCEENDS_log_no_SplitSign.length()-1){
			SENTENCEENDS_log_no_SplitSign = SENTENCEENDS_log_no_SplitSign.substring(0, splitSignIndex)
			+SENTENCEENDS_log_no_SplitSign.substring(splitSignIndex+1);
			
			splitSignIndex = SENTENCEENDS_log_no_SplitSign.indexOf(UnigramSeg.getStaticSplitSign());
		}
	}
	
	public Set<String> getPhrasesFromOneLineWithOutUselessSign(String segLine){
//		String sens[] = segLine.split(SENTENCEENDS_log_no_SplitSign);
		
		
		Set<String> sets = new HashSet<String>();
		
//		for(int i = 0; i < sens.length; i++){
//			sets.addAll(this.getPhrasesFromOneLine(sens[i]));
//		}
		
		int lastIndex = -1;
		for(int i = 0; i < segLine.length(); i++){
			
			if(set_log_noSetSplit.contains((int) segLine.charAt(i))){
				if(lastIndex < i){
					sets.addAll(getPhrasesFromOneLine(segLine.substring(lastIndex+1, i)));
				}
				lastIndex = i;
			}
			
			
		}
		
		if(lastIndex + 1 < segLine.length()){
			sets.addAll(getPhrasesFromOneLine(segLine.substring(lastIndex+1)));
		}
		
		return sets;
	}
	
	//对长句进行分词，并去掉无用字符，并将分词�?��词语加入到集合中返回
	public static void getPartsWithOutUselessSign(Collection<String> sets,
			String line){

		sets.clear();
		
		int lastIndex = -1;
		for(int i = 0; i < line.length(); i++){
			
			if(NLPTools.getSets_log().contains(line.charAt(i))){
				if(lastIndex < i){
					sets.addAll(SplitSentence.getParts(
							UnigramSeg.getSeg().segment(line.substring(lastIndex+1, i)), 
							UnigramSeg.getSeg().getSplitSign()));
				}
				lastIndex = i;
			}
			
			
		}
		
		if(lastIndex + 1 < line.length()){
			sets.addAll(SplitSentence.getParts(UnigramSeg.getSeg().segment(line.substring(lastIndex+1, line.length())), 
					UnigramSeg.getSeg().getSplitSign()));
		}
		
		
	}
	
	public Set<String> getPhrasesFromOneLine(String segLine){
		
		return getPhrasesFromOneLine(segLine, 
				UnigramSeg.getStaticSplitSign(),
				this.maxLengthSeg).keySet();
		
	}
	
	//对长句进行分词，并去掉无用字符，并将分词�?��词语加入到集合中返回
	public static ArrayList<String> getPartsWithOutUselessSign_ArrayList(String line) throws IOException{
//		String sens[] = segLine.split(SENTENCEENDS_log_no_SplitSign);
		
		
		ArrayList<String> list = new ArrayList<String>();
		
		getPartsWithOutUselessSign(list, line);
		
		return list;

	}

	
}
