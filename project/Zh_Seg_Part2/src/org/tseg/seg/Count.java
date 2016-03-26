package org.tseg.seg;

import java.io.*;
import java.util.*;

import org.tseg.tools.ObjectIO;
import org.tseg.tools.NLPTools;

public class Count {

	// isPrint用来控制是否打印，供调试用
	// 通常情况下，isPrint是false，isPrint1是ture
	// 如果要打印某个东西，就将其放在if(isPrint1)条件分支内
	// 如果不再打印，就将isPrint1改为isPrint
	// 如果不打印任何东西，就将isPrint1改为false
	boolean isPrint = false;
	boolean isPrint1 = true;

	int maxLength = 8;
	double smoothFac = 0.1;
	double logProbFacPerCharInUnseemWord = smoothFac;

	double pTotalNum;

	// symbol用来排除字母、数字等，这些字母数字所组成的词我们不统计词频
	// static String symbol = "[)|(_—0-9a-zA-Z#&@\\/\\*\\.\\] \\-０-９Ａ-Ｚａ-ｚ．％／]";
	// static String symbolRegx =
	// "[0-9a-zA-Z)|(_—#&@\\/\\*\\.\\] \\-０-９Ａ-Ｚａ-ｚ．％／]+";
	static String symbol = "[)|(_—0-9a-zA-Z#&@\\/\\*\\.\\] \\-０-９Ａ-Ｚａ-ｚ．％／%]";
	static String symbolRegx = "[0-9a-zA-Z)|(_—#&@\\/\\*\\.\\] \\-０-９Ａ-Ｚａ-ｚ．％／%]+";
	String nn = "[０-９／]";

	// ６４８５１０９２７３

	static public String getSymbol() {
		return Count.symbol;
	}

	public String getSymbolRegx() {
		return Count.symbolRegx;
	}

	public HashMap<String, Double> countWordRate(String trainPath,
			String charset) {
		return this.countWordRate(trainPath, charset, null);
	}

	public HashMap<String, Double> countWordRate(String trainPath,
			String charset, Set<String> newWords) {

		HashMap<String, Double> wordRate = new HashMap<String, Double>();

		try {

			HashMap<String, Double> wordCount = this.countWord(trainPath,
					charset);
			if (isPrint1) {
				System.out.println("wordCount.size:" + wordCount.size());
			}

			if (newWords == null) {
				newWords = new HashSet<String>();
			}
			
			this.getWordsInDefaultDict(
//					wordCount, wordRate,
					newWords
					);
			
			this.addNewWordsToWordCount(wordCount, wordRate, newWords);

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					new FileInputStream(trainPath), charset));
			String line;
			while ((line = buff.readLine()) != null) {

				line = line.replaceAll("/[a-zA-Z\\]]+ |[ \\[]", "");

				String sts[] = line.split(NLPTools.SENTENCEENDS);

				for (int i = 0; i < sts.length; i++) {
					for (int j = 0; j < sts[i].length(); j++) {

						for (int k = 1; k <= maxLength
								&& j + k <= sts[i].length(); k++) {
							String word = sts[i].substring(j, j + k);
							if (word.matches(".*/.*")) {
								continue;
							}
							
							word = CountBiGram.wordShape(word);

							if (wordRate.containsKey(word)) {

								wordRate.put(word, wordRate.get(word) + 1.0);

							} else if (wordCount.containsKey(word)) {
								wordRate.put(word, 1.0);

								if (isPrint) {
									System.out.println("word:" + word);
								}

							} else if (k == 1) {
								if (!word.matches(symbol)) {
									wordRate.put(word, 1.0);
									if (isPrint) {
										System.out.println("ss:" + word);
									}

								}

							}

						}
					}
				}

			}

			buff.close();
			
			this.computeWordRate(wordRate, wordCount);
			
			this.logProbFacPerCharInUnseemWord = Math.log(this.smoothFac);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
		
		return wordRate;
	}

	protected void computeWordRate(HashMap<String, Double> wordRate,
			HashMap<String, Double> wordCount) {
		
		for(Map.Entry<String, Double> entr : wordRate.entrySet()) {
			String key = entr.getKey();
			double n = entr.getValue();
			double m = 0;
			if (wordCount.containsKey(key)) {
				m = wordCount.get(key);
			}

			wordRate.put(key, this.logedRate(m, n));

		}

		if (isPrint1) {
			System.out.println("wordRate-size:" + wordRate.size());
		}

		
	}
	
	protected double logedRate(double m, double n){
		double newV = ((m + 0.5 * smoothFac) * n - 0.5 * smoothFac * m)	/ (n * n);
		return Math.log(newV);
	}
	
	public void getWordsInDefaultDict(
//			HashMap<String, Double> wordCount,
//			HashMap<String, Double> wordRate,
			Set<String> newWords
			)
			throws NumberFormatException, IOException {

		String line;
		BufferedReader buff = this.readerFromDefaultDict();

		while ((line = buff.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}

			String word = line.split(" ")[1].trim();

			newWords.add(word);

		}

	}

	protected void addNewWordsToWordCount(HashMap<String, Double> wordCount,
			HashMap<String, Double> wordRate,
			// Set<String> tempList,
			Set<String> newWords) {
		for (String word : newWords) {
			if (!wordCount.containsKey(word)) {
				wordRate.put(word, 1.0);
				// tempList.add(word);
				wordCount.put(word, 1.0);

			}
		}
	}

	public HashMap<String, Double> countWordFrequence(String trainPath,
			String charset) {

		Map<String, Double> tempMap = new HashMap<String, Double>();

		try {

			HashMap<String, Double> wordCount = this.countWord(trainPath,
					charset);
			if (isPrint1) {
				System.out.println("wordCount.size:" + wordCount.size());
			}
			Iterator<String> iter = wordCount.keySet().iterator();
			double tCount = 0, wCount = 0;
			while (iter.hasNext()) {
				String key = iter.next();
				wCount++;
				tCount += wordCount.get(key);

				if (isPrint) {
					System.out.println("key:" + key);
				}

			}

			BufferedReader dicBr = this.readerFromDefaultDict();

			String line;

			while ((line = dicBr.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}

				String w = line.split(" ")[1];

				if (!wordCount.containsKey(w)) {
					tempMap.put(w, 0.0);
					wCount++;
				}

				if (isPrint) {
					System.out.println(w);
				}

			}

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					new FileInputStream(trainPath), charset));

			while ((line = buff.readLine()) != null) {

				line = line.replaceAll("/[a-zA-Z\\]]+ |[ \\[]", "");

				String sts[] = line.split(NLPTools.SENTENCEENDS);

				for (int i = 0; i < sts.length; i++) {
					for (int j = 0; j < sts[i].length(); j++) {

						for (int k = 1; k <= maxLength
								&& j + k <= sts[i].length(); k++) {
							String word = sts[i].substring(j, j + k);
							if (word.matches(".*/.*")) {
								continue;
							}

							if (tempMap.containsKey(word)) {
								// 在词典中出现，且在训练语料中不作为词出现的次数
								tempMap.put(word, tempMap.get(word) + 1.0);

								if (isPrint) {

									System.out.println("word:" + word);
									System.out.println(sts[i]);

								}
							}
						}
					}

				}
			}
			buff.close();

			iter = tempMap.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();

				// 这里是为了消除粒度不一致的影响，能够略微提高F值
				wordCount.put(key, 0.5 / (tempMap.get(key) + 0.5));
				// wordCount.put(key, 1.0);

				if (isPrint) {
					System.out
							.println("key/tempMap.get(key)/wordCount.get(key):"
									+ key + "/" + tempMap.get(key) + "/"
									+ wordCount.get(key));
				}

			}

			if (tCount > 0) {
				iter = wordCount.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					wordCount.put(key, (wordCount.get(key) + 1)
							/ (tCount + wCount));
				}
			}

			this.logProbFacPerCharInUnseemWord = 1.0 / (tCount + wCount);

//			if (isPrint1) {
//				System.out
//						.println("countWordFrequence:tCount/wCount/notappear:"
//								+ tCount + "/" + wCount + "/"
//								+ logProbFacPerCharInUnseemWord);
//				iter = wordCount.keySet().iterator();
//				while (iter.hasNext()) {
//					String key = iter.next();
					// System.out.println(key+"/"+wordCount.get(key));
//				}
//			}

			iter = wordCount.keySet().iterator();
			while (iter.hasNext()) {
				String word = iter.next();
				wordCount.put(word, Math.log(wordCount.get(word)));
			}

			this.logProbFacPerCharInUnseemWord = Math
					.log(this.logProbFacPerCharInUnseemWord);

			// wordCount.put("#all", this.probFacPerCharInUnseemWord);

			return wordCount;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private BufferedReader readerFromDefaultDict() throws IOException {
		return ObjectIO.getBufferedReaderFromFileInSamePackage(Count.class, "词典.txt",
				"utf-8");
	}

	public SegModel trainAndWriteModel(String trainPath, String charset,
			int segType, String outPath) {
		return this.trainAndWriteModel(trainPath, charset, null, segType,
				outPath);
	}

	public SegModel trainAndWriteModel(String trainPath, String charset,
			Set<String> newWords, int segType, String modelPath) {
		HashMap<String, Double> wordLogProb = null;
		SegModel model = null;

		try {
			if (segType == SegModel.wordRateType) {
				if (newWords == null) {
					wordLogProb = this.countWordRate(trainPath, charset);
				} else {
					wordLogProb = this
							.countWordRate(trainPath, charset, newWords);
				}
				model = new SegModel(wordLogProb, segType,
						logProbFacPerCharInUnseemWord);

			} else if(segType == SegModel.wordFrequenceType){
				
				wordLogProb = this.countWordFrequence(trainPath, charset);
				model = new SegModel(wordLogProb, segType,
						logProbFacPerCharInUnseemWord);
				
			} else if(segType == SegModel.biWordRateType){
				CountBiGram cbg = new CountBiGram();
				wordLogProb = this.countWordRate(trainPath, charset, newWords);
				
				
				model = new SegModel(wordLogProb, segType,
						logProbFacPerCharInUnseemWord);
				model.rateMaps = cbg.countBiWordRate(trainPath, charset, wordLogProb);
				
			}

			
			
			
			model.writeModel(modelPath);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	// 统计词频
	public HashMap<String, Double> countWord(String trainPath, String charset)
			throws IOException {

		BufferedReader buff = new BufferedReader(new InputStreamReader(
				new FileInputStream(trainPath), charset));

		String line;
		HashMap<String, Double> wordCount = new HashMap<String, Double>();

		while ((line = buff.readLine()) != null) {

			String words[] = line.split("/[a-zA-Z\\]]+ ");

			for (int i = 0; i < words.length; i++) {
				words[i] = words[i].trim();

				if (words[i].length() == 0) {
					continue;
				}

				if (words[i].matches("\\[.*")) {
					if (isPrint) {
						System.out.println("countWord:words[i]:" + words[i]);
					}
					words[i] = words[i].substring(1);

					if (isPrint) {
						System.out.println("countWordFrequence:words[i]:"
								+ words[i]);
					}
				}

//				if (words[i].matches(symbolRegx)
//				// || words[i].matches(NLPTools.SENTENCEENDS)
//				) {
//					if (isPrint) {
//						System.out.println("countWord:words[i]:" + words[i]);
//					}
//					continue;
//
//				}
				words[i] = CountBiGram.wordShape(words[i]);

				if (wordCount.containsKey(words[i])) {
					wordCount.put(words[i], wordCount.get(words[i]) + 1.0);
				} else {
					wordCount.put(words[i], 1.0);
				}

			}
		}

		buff.close();

		return wordCount;
	}

}
