package org.tseg.seg;

import java.util.*;

import org.tseg.tools.MergeNumLetter;

public class UnigramSeg {

	protected SegModel model;

	int maxUnseenWordLen = 3;
	int maxWordLen = 7;

	private static final char staticSplitSign = ' ';
	protected char splitSign = staticSplitSign;

	public char getSplitSign() {
		return splitSign;
	}

	public void setSplitSign(char splitSign) {
		this.splitSign = splitSign;
	}

	public static char getStaticSplitSign() {
		return staticSplitSign;
	}

	public Map<String, Double> getWordLogProb() {
		return model.wordLogProb;
	}

	/**
	 * @param maxUnseenWordLen
	 *            the maxUnseenWordLen to set
	 */
	public void setMaxUnseenWordLen(int maxUnseenWordLen) {
		this.maxUnseenWordLen = maxUnseenWordLen;
	}

	/**
	 * @param maxWordLen
	 *            the maxWordLen to set
	 */
	public void setMaxWordLen(int maxWordLen) {
		this.maxWordLen = maxWordLen;
	}

	/**
	 * @return the model
	 */
	public SegModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(SegModel model) {
		this.model = model;
	}

	private static UnigramSeg singleSeg;

	public static UnigramSeg getSeg() {
		if (singleSeg == null) {
			singleSeg = new UnigramSeg();
		}

		return singleSeg;
	}

	private UnigramSeg() {
		this(SegModel.wordRateType);
	}

	public UnigramSeg(int segType) {

		model = SegModel.readModel(segType);
	}

	public UnigramSeg(String trainPath, String charset, int segType,
			String modelPath) {

		this(trainPath, charset, null, segType, modelPath);

	}

	public UnigramSeg(String trainPath, String charset, Set<String> newWords,
			int segType, String modelPath) {

		model = new Count().trainAndWriteModel(trainPath, charset, newWords,
				segType, modelPath);

	}

	public UnigramSeg(String modelPath) {

		model = SegModel.readModel(modelPath);

	}

	public String segment(String sentence) {


			WordGraph wgs[] = this.getWordGraph(sentence);

			return this.getBestSegRet(sentence, wgs);
		

	}

	private WordGraph[] getWordGraph(String sentence) {
		int len = sentence.length();
		WordGraph wgs[] = new WordGraph[len + 1];

		wgs[len] = new WordGraph();
		// wgs[len].before.put(len, 0.0);
		// wgs[len].set(len, 0.0);
		// wgs[len].bestBeforeIndex = len;
		wgs[len].bestBeforeValue = 0.0;

		// 改动处1
		for (int i = len; i > 0; i--) { // 从后向前扫描

			if (wgs[i] == null) {
				continue;
			}

			// if(i < len){
			// wgs[i].fillBest();
			// }
			//    
			// boolean hasWord = false;

			int k = 0;
			for (; i - k - 1 >= 0
					&& MergeNumLetter.matchNumAndZimu_log(sentence.charAt(i - k
							- 1));) { // 连接相邻字母数字
				k++;
			}
			if (k > 0) {

				if (wgs[i - k] == null) {
					wgs[i - k] = new WordGraph();
				}
				// wgs[i-k].before.put(i, wgs[i].bestBeforeValue +
				// model.logProbFacPerCharInUnseemWord);
				wgs[i - k].set(i, wgs[i].bestBeforeValue
						+ model.logProbFacPerCharInUnseemWord);

				if (i < len
						&& sentence.substring(i, i + 1).matches("[万千百亿年月日]")) {
					// wgs[i-k].before.put(i+1, wgs[i+1].bestBeforeValue +
					// model.logProbFacPerCharInUnseemWord);
					wgs[i - k].set(i + 1, wgs[i + 1].bestBeforeValue
							+ model.logProbFacPerCharInUnseemWord);
				}

				i = i - k + 1;
				continue;
			}

			int lookLen = maxWordLen;
			for (int j = 1; j <= lookLen; j++) {
				if (0 > i - j) {
					break;
				}

				String wordShape = sentence.substring(i -j , i);
					
				wordShape =	CountBiGram.wordShape(wordShape);
				if(CountBiGram.signs.contains(wordShape)){
					if (wgs[i - j] == null) {
						wgs[i - j] = new WordGraph();
					}

					wgs[i - j].set(i, wgs[i].bestBeforeValue
							+ model.logProbFacPerCharInUnseemWord);
					if(lookLen == j){ //如果是字母数字，继续向后看
						lookLen ++;
//						System.out.println("org.tseg.seg.BigramSeg." +
//								"getWordGraphWithBiWordRate(String).lookLen/line.substring(i , i+j)： "+lookLen+"\t"+line.substring(i , i+j));
					}
				} else 
				
				if (model.wordLogProb.containsKey(wordShape)) {
					if (wgs[i - j] == null) {
						wgs[i - j] = new WordGraph();
					}

//					if(wordShape.matches(CountBiGram.numSign+".")){
//						System.out.println(sentence.substring(i - j, i)+"\t"+model.wordLogProb.get(wordShape));
////						System.out.println(this.segment(sentence));
//					}
					
					// wgs[i-j].before.put(i, wgs[i].bestBeforeValue +
					// model.wordCount.get(sentence.substring(i-j, i)));
//					wgs[i - j].set(i, wgs[i].bestBeforeValue
//							+ model.wordLogProb.get(sentence
//									.substring(i - j, i)));
					wgs[i - j].set(i, wgs[i].bestBeforeValue
							+ model.wordLogProb.get(wordShape));

				}

				else if (j <= this.maxUnseenWordLen) {
					// 出现字母数字则不再聚合
					if (MergeNumLetter.matchNumAndZimu(sentence.charAt(i - j))) {
						break;
					}

					// 下面这段代码在进行使用基于词频的unigram，且进行未登录字串聚合时启用
					if (model.segType == SegModel.wordFrequenceType) {
						if (wgs[i - j] == null) {
							wgs[i - j] = new WordGraph();
						}
						// wgs[i-j].before.put(i, wgs[i].bestBeforeValue +
						// model.logProbFacPerCharInUnseemWord* ((0.1*j+0.3)*j +
						// 0.5));
						wgs[i - j].set(i, wgs[i].bestBeforeValue
								+ model.logProbFacPerCharInUnseemWord
								* ((0.1 * j + 0.3) * j + 0.5));

					} else if (model.segType == SegModel.wordRateType) {// 下面这段代码在进行使用基于wordRate的unigram，且进行未登录字串聚合时启用
						if (wgs[i - j] == null) {
							wgs[i - j] = new WordGraph();
						}
						// wgs[i-j].before.put(i, wgs[i].bestBeforeValue
						// +model.logProbFacPerCharInUnseemWord*(j+0.1*j-0.1));
						wgs[i - j].set(i, wgs[i].bestBeforeValue
								+ model.logProbFacPerCharInUnseemWord
								* (j + 0.1 * j - 0.1));

					} else {
						throw new RuntimeException();
					}

				}

			}

		}

		// wgs[0].fillBest();

		return wgs;
	}

	private String getBestSegRet(String sentence, WordGraph wgs[]) {
		StringBuilder ret = new StringBuilder();

		int preIndex = 0;

		while (preIndex < sentence.length()) {
			int index = wgs[preIndex].bestBeforeIndex;

			ret = ret.append(sentence.substring(preIndex, index) + splitSign);

			preIndex = index;
		}

		return ret.toString();
	}

	public ArrayList<String> getSegWordList(String sentence) {
		WordGraph wgs[] = this.getWordGraph(sentence);

		return this.getBestSegWordList(sentence, wgs);
	}

	private ArrayList<String> getBestSegWordList(String sentence,
			WordGraph wgs[]) {
		ArrayList<String> wordList = new ArrayList<String>();

		int preIndex = 0;

		while (preIndex < sentence.length()) {

			// ObjToStr.print(wgs[preIndex]);

			int index = wgs[preIndex].bestBeforeIndex;

			wordList.add(sentence.substring(preIndex, index));

			// System.out.println("preIndex: "+preIndex+"\t"+sentence.charAt(preIndex)+"\t"+sentence.substring(preIndex,index));

			preIndex = index;
		}

		return wordList;
	}

	

}
