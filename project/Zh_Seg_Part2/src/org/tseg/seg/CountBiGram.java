package org.tseg.seg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tseg.tools.*;

public class CountBiGram extends Count{
	
	
	public static final String startSign = "s-";
	public static final String symbolSign = "m-";
	public static final String chnNumSign = "c-";
	public static final String numSign = "n-";
	public static final String endSign = "e-";

	static Set<String> signs = new HashSet<String>();
	static {
		signs.add(chnNumSign);
		signs.add(startSign);
		signs.add(numSign);
		signs.add(symbolSign);
		signs.add(endSign);
	}
	
	// 统计词频
	public HashMap<String, Double>[] countBiWord(String trainPath, String charset)
			throws IOException {
		HashMap<String, Double>[] maps = this.initMaps();
		
		BufferedReader buff = new BufferedReader(new InputStreamReader(
				new FileInputStream(trainPath), charset));

		String line;		
		while ((line = buff.readLine()) != null) {

			this.addPairs(this.getSignedWords(line), maps);
		}

		buff.close();

		return maps;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Double>[] initMaps(){
		HashMap<String, Double>[] maps = new HashMap[this.maxLength]; 
		
		for(int i = 0; i < this.maxLength; i++){
			maps[i] = new HashMap<String,Double>();
		}
		
		return maps;
	}
	
	public void addPairs(ArrayList<String> signedWords, HashMap<String, Double>[] maps){
		
		String lastWord = startSign;
		
		for(int i = 0; i < signedWords.size(); i++){
			String word = signedWords.get(i);
			if(word.length() <= this.maxLength){
				
				String biWord = lastWord+word;
				int id = (word.length()-1);
				CollectionTools.addOneToMap_double(maps[id], biWord);
			}
			
			lastWord = word;
		}
		
		
	}
	
	public ArrayList<String> getSignedWords(String line){
		ArrayList<String> signedWords = new ArrayList<String>();
		
		
		
		String words[] = line.split("/[a-zA-Z\\]]+ ");

		for (String word : words) {
//			int beforeLen = word.length();
			
			
			word= word.trim();

//			if (beforeLen > 0 && word.length() == 0) {
//				signedWords.add(spaceSign);
//				continue;
//			}
			
			if(word.length() == 0){
				continue;
			}

			if (word.matches("\\[.*")) {
				
				word = word.substring(1);

				
			}
			
			signedWords.add(CountBiGram.wordShape(word));

		}
		
		return signedWords;
	}
	
	
	
	public static String wordShape(String word){
	
		
		if(word.length() == 0){
			return word;
		}
		
		char fch = word.charAt(0);
		
		if(isNumChar(fch) || (fch =='-' && word.length() >= 2)){
			boolean num = true;
			for(int i = 1; i < word.length(); i++){
				if(!isNumChar(word.charAt(i))){
					
					if(i == word.length() - 1){
						return numSign + word.charAt(i);
					}
					
					if(word.charAt(i) != '.' && word.charAt(i) != '．'){
						num = false;
						break;
					}
					
					
				}
			}
			
			if(num){
				return  numSign;
			}
			
		} 
		
		if(endSet.contains(fch) && word.length() == 1){
			
			return  endSign;
		
		} 
		
		else if(symbolSet.contains(fch)){
			
			
			
			boolean symbol = true;
			for(int i = 1; i < word.length(); i++){
				if(!symbolSet.contains(word.charAt(i))){
					symbol = false;
					break;
				}
			}
			
			if(symbol){
				return  symbolSign;
			}
		} else if(chnNumSet.contains(fch)){
			boolean cnum = true;
			for(int i = 1; i < word.length(); i++){
				if(!chnNumSet.contains(word.charAt(i))){
					
					if(i == word.length() - 1 && i > 1){
						return numSign + word.charAt(i);
					}
					
					cnum = false;
					break;
				}
			}
			
			if(cnum){
				return  numSign;
			}
		}

//		if(word.matches("[0-9０-９]+")
//				|| word.matches(chineseNumStr)){
//			return numSign;
//			
//		}
//		
//		if(word.matches(NLPTools.SENTENCEENDS)){
//			return endSign;
//			
//		}
//		
//		if (word.matches(symbolRegx)) {
//			return symbolSign;
//		}
//		
//		if(word.matches("[0-9０-９]+.")){
//			return numSign+word.charAt(word.length()-1);
//			
//		}
		
		
		
		return word;
	}
	
	private static boolean isNumChar(char ch){
		return ((ch >= '0'&& ch <='9') || (ch == '０'&& ch <='９'));
	}
	
	static private Set<Character> symbolSet = new HashSet<Character>();
	private static Set<Character> numSet = new HashSet<Character>();
	private static Set<Character> chnNumSet = new HashSet<Character>();
	private static Set<Character> endSet = new HashSet<Character>();
	private static String chineseNumStr = "[一二三四五六七八九零十百千万亿]+";
	
	static {
		
		symbolSet.add('_');
//		symbolSet.add('&');
		symbolSet.add( '/');
		symbolSet.add( '-');
		symbolSet.add('@');
//		symbolSet.add('%');
//		symbolSet.add('％');
//		symbolSet.add('\\');
//		symbolSet.add('.');
//		symbolSet.add('*');
//		symbolSet.add('￥');
//		symbolSet.add('…');

		for(char ch = '0'; ch <='9';ch++){
			symbolSet.add(ch);
			numSet.add(ch);
		}
		
		for(char ch = '０'; ch <='９';ch++){
			symbolSet.add( ch);
			numSet.add(ch);
		}
		
		for(char ch = 'a'; ch <='z';ch++){
			symbolSet.add(ch);
		}
		
		for(char ch = 'ａ'; ch <='ｚ';ch++){
			symbolSet.add(ch);
		}
		
		for(char ch = 'A'; ch <='Z';ch++){
			symbolSet.add( ch);
		}
		
		
		for(char ch = 'Ａ'; ch <='Ｚ';ch++){
			symbolSet.add( ch);
		}
		

		for(int i = 1; i < NLPTools.SENTENCEENDS.length() - 1; i++){
			endSet.add(NLPTools.SENTENCEENDS.charAt(i));
		}
		
		
		for(int i = 1; i < chineseNumStr.length()-2; i++){
			chnNumSet.add(chineseNumStr.charAt(i));
		}
		
	}

	public HashMap<String, Double>[] countBiWordRate(String trainPath,
			String charset, HashMap<String, Double> wordRate) {		

		try {
			
			HashMap<String,Double>[] rateMaps = this.initMaps();
			

			
			BufferedReader buff = new BufferedReader(new InputStreamReader(
					new FileInputStream(trainPath), charset));
			String line;
			while ((line = buff.readLine()) != null) {

				ArrayList<String> signedWords = this.getSignedWords(line);

				this.addRatePairs(signedWords, rateMaps, wordRate);

			}


			buff.close();
			
			HashMap<String, Double>[] maps = this.countBiWord(trainPath, charset);
			for(int i = 0; i < rateMaps.length; i++){
				HashMap<String,Double> rateMap = rateMaps[i];
				HashMap<String, Double> countMap = maps[i];
				
//				this.computeWordRate(rateMap, countMap);
				this.computeBiWordRate(rateMap, countMap, i+1, wordRate);
				
			}
			
			return rateMaps;

		} catch (Exception ex) {
			ex.printStackTrace();
		}



		return null;
	}
	
	protected void computeBiWordRate(HashMap<String, Double> biWordRate,
			HashMap<String, Double> biWordCount, int len, HashMap<String,Double> singleWordRate) {
		
//		if (isPrint1) {
//			System.out.println("biWordRate-size:" + biWordRate.size());
//		}
		
		Set<String> tempList = new HashSet<String>();
		for(Map.Entry<String, Double> entr : biWordRate.entrySet()) {
			String key = entr.getKey();
			double n = entr.getValue();
			
			double m = 0;
			if (biWordCount.containsKey(key)) {
				m = biWordCount.get(key);
			} else if(n <= 1.01){
				tempList.add(key);
				continue;
			}

			double newV = this.logedRate(m, n);
			String singleWord = key.substring(key.length() - len);
			if(Math.abs(newV - singleWordRate.get(singleWord)) < 1e-6){ //如果二元与一元相同，不必用二元
				
//				if(Math.random() > 0.99){
//					System.out.println(key+"\t"+singleWord+"\t"+newV);
//				}
				
				tempList.add(key);
			}
			
			biWordRate.put(key, newV);

		}

		for(String word : tempList){
			biWordRate.remove(word);
		}
//		if (isPrint1) {
//			System.out.println("biWordRate-after-size:" + biWordRate.size());
//		}
		
	}

	public void addRatePairs(ArrayList<String> signedWords, 
			HashMap<String, Double>[] rateMaps,
			HashMap<String,Double> wordRate){
		String lastWord = startSign;
		for (int si = 0; si < signedWords.size(); si++){
			
			int curSi = si;
			int curPos = -1;
			String curWord = "";
			int curLen = signedWords.get(si).length();
			for(int li = 0; li < this.maxLength; li++){

				if(++curPos >= curLen){

					if(++curSi >= signedWords.size()){
						break;
					}
					
					curPos = 0;
					curLen = signedWords.get(curSi).length();
				} 

				
				curWord = curWord + signedWords.get(curSi).charAt(curPos);
				
				if(wordRate.containsKey(curWord)){
					
					CollectionTools.addOneToMap_double(rateMaps[li], lastWord+curWord);
					
				}
				
			}
			
			lastWord = signedWords.get(si);
		}
	}
	
	
	
	static boolean specialWord(String wordShape){
		return (signs.contains(wordShape)
				|| wordShape.substring(0,wordShape.length()-1).compareTo(numSign) == 0);
	}
	
}
