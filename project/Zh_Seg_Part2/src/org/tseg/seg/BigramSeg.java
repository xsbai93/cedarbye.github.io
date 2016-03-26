package org.tseg.seg;

import java.util.ArrayList;
import java.util.Set;

import org.tseg.tools.MergeNumLetter;

public class BigramSeg extends UnigramSeg{

	private static BigramSeg singleSeg;

	public static BigramSeg getSeg() {
		if (singleSeg == null) {
			singleSeg = new BigramSeg();
		}

		return singleSeg;
	}

	private BigramSeg() {
		this(SegModel.biWordRateType);
	}

	public BigramSeg(int segType) {

		super(segType);
	}
	
	public BigramSeg(String modelPath) {
		super(modelPath);
	}
	
	public BigramSeg(String trainPath, String charset,
			String modelPath) {

		this(trainPath, charset, null, modelPath);

	}
	
	public BigramSeg(String trainPath, String charset, Set<String> newWords,
			String modelPath) {

		super(trainPath, charset, newWords,
				SegModel.biWordRateType, modelPath);

	}
	
	@Override
	public String segment(String sentence){
		WordGraph wgs[] = this.getWordGraphWithBiWordRate(sentence);

		return this.getForwardBestSegRet(sentence, wgs);
	}

	private WordGraph[] getWordGraphWithBiWordRate(String line) {

		if (model.segType != SegModel.biWordRateType) {
			throw new RuntimeException();
		}

		int len = line.length();
		WordGraph wgs[] = new WordGraph[len + 1];

		wgs[0] = new WordGraph();
		wgs[0].bestBeforeValue = 0.0;

		
		String lastWord = CountBiGram.startSign;
		// 改动处1
		for (int i = 0; i < len; i++) { // 从后向前扫描

			if (wgs[i] == null) {
				continue;
			}

			
			
			int k = 0;
			for (; i + k < len
					&& MergeNumLetter.matchNumAndZimu_log(line.charAt(i + k));) { // 连接相邻字母数字
				k++;
			}
			if (k > 0) {

//				String wordShape = CountBiGram.wordShape(line.substring(i, i+k));
//				
//				if(!CountBiGram.signs.contains(wordShape)){
//					System.out.println("symbol："+line.substring(i, i+k));
//				}
				
				this.add(wgs, i, k,  model.logProbFacPerCharInUnseemWord);
				


				if (i + k < len
						&& line.substring(i + k, i + k + 1).matches("[万千百亿年月日]")) {
					// wgs[i-k].before.put(i+1, wgs[i+1].bestBeforeValue +
					// model.logProbFacPerCharInUnseemWord);
//					wgs[i - k].set(i + 1, wgs[i + 1].bestBeforeValue
//							+ model.logProbFacPerCharInUnseemWord);
					
//					System.out.println("symbol: "+line.substring(i, i+k+1));
					
					this.add(wgs, i, k+1, model.logProbFacPerCharInUnseemWord);
				}

				i = i + k - 1;
				continue;
			}
			
			if(i > 0){
				lastWord = CountBiGram.wordShape(line.substring(wgs[i].bestBeforeIndex, i));
			}
			
			int lookLen = maxWordLen;
			
			for (int j = 1; j <= lookLen; j++) {
				if (i + j > len) {
					break;
				}
				
				int bid = j - 1;
				
				String wordShape = CountBiGram.wordShape(line.substring(i , i+j));
				if(CountBiGram.signs.contains(wordShape)){
					this.add(wgs, i, j, model.logProbFacPerCharInUnseemWord);
					if(lookLen == j){ //如果是字母数字，继续向后看
						lookLen ++;
//						System.out.println("org.tseg.seg.BigramSeg." +
//								"getWordGraphWithBiWordRate(String).lookLen/line.substring(i , i+j)： "+lookLen+"\t"+line.substring(i , i+j));
					}
				}
				
				else if (model.wordLogProb.containsKey(wordShape)) {
					
					
					
					String biWord = lastWord+wordShape;
					double logProb = 0.0;
					if(model.rateMaps[bid].containsKey(biWord)){
						logProb = model.rateMaps[bid].get(biWord);
					} else {
						logProb = model.wordLogProb.get(wordShape);
					}
					
//					logProb = model.wordLogProb.get(word);
					
//					if(wordShape.matches(CountBiGram.numSign+".")){
//						System.out.println("org.tseg.seg.BigramSeg." +
//								"getWordGraphWithBiWordRate(String).wordShape/logProb/biWord： "+wordShape+"\t"+logProb+"\t"
//								+line.substring(wgs[i].bestBeforeIndex, i)+"-"+line.substring(i , i+j));
//					}
					
					this.add(wgs, i, j, logProb);

				}
				

				else if (j <= this.maxUnseenWordLen) {
					// 出现字母数字则不再聚合
					if (j > 1 && MergeNumLetter.matchNumAndZimu(line.charAt(i))) {
						break;
					}

//					if (wgs[i + j] == null) {
//						wgs[i + j] = new WordGraph();
//					}
//					wgs[i + j].set(i, wgs[i].bestBeforeValue
//							+ model.logProbFacPerCharInUnseemWord
//							* (j + 0.1 * j - 0.1));
						
					this.add(wgs, i, j, model.logProbFacPerCharInUnseemWord
							* (j + 0.1 * j - 0.1));
					

				}

			}
			
			

		}


		return wgs;
	}
	
	private void add(WordGraph wgs[], int i, int j, double logProb){
		if (wgs[i + j] == null) {
			wgs[i + j] = new WordGraph();
		}
		wgs[i + j].set(i, wgs[i].bestBeforeValue + logProb);
	}
	
	private String getForwardBestSegRet(String sentence, WordGraph wgs[]) {

		int insertNum = 0;
		int index = sentence.length();
		while (index > 0) {
			int preIndex = wgs[index].bestBeforeIndex;

			insertNum++;
			
			index = preIndex;
		}

		char cs[] = new char[sentence.length()+insertNum];
		
		index = sentence.length();
		int pos = cs.length;
		while (index > 0) {
			int preIndex = wgs[index].bestBeforeIndex;

			for(int i = index - 1; i >= preIndex; i--){
				cs[--pos] = sentence.charAt(i);
			}
			cs[--pos] = splitSign;
			
			index = preIndex;
		}
		
		return new String(cs);
	}
	
	public ArrayList<String> getSegWordList(String sentence) {
		WordGraph wgs[] = this.getWordGraphWithBiWordRate(sentence);

		return this.getBestSegWordList(sentence, wgs);
	}

	private ArrayList<String> getBestSegWordList(String sentence,
			WordGraph wgs[]) {
		ArrayList<String> wordList = new ArrayList<String>();

//		int preIndex = 0;
//
//		while (preIndex < sentence.length()) {
//
//			// ObjToStr.print(wgs[preIndex]);
//
//			int index = wgs[preIndex].bestBeforeIndex;
//
//			wordList.add(sentence.substring(preIndex, index));
//
//			// System.out.println("preIndex: "+preIndex+"\t"+sentence.charAt(preIndex)+"\t"+sentence.substring(preIndex,index));
//
//			preIndex = index;
//		}
		int index = sentence.length();
		while (index > 0) {
			int preIndex = wgs[index].bestBeforeIndex;

			wordList.add(0, sentence.substring(preIndex, index));
			
			index = preIndex;
		}

		return wordList;
	}
	
}
