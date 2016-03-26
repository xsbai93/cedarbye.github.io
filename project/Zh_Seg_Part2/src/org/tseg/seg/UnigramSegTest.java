package org.tseg.seg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.tseg.tools.NLPTools;
import org.tseg.tools.TempFileManage;

public class UnigramSegTest {

	

	private void unigramTestInTestSet(UnigramSeg seg,
			String testSetPath, String charset) {

		try {

			long timeStart = Calendar.getInstance().getTimeInMillis();

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					new FileInputStream(testSetPath), charset));
			String line;

			// ArrayList<String> localWord ;

			int ret[] = new int[4];
			int rightNum = 0, origNum = 0, outNum = 0;
			// MaxMatch seg = new MaxMatch();

			while ((line = buff.readLine()) != null) {

				if (line.length() == 0) {
					continue;
				}

				// localWord = this.usg.localWordCount(line);

				if (line.matches("199801.*") && line.length() >= 22) {
					line = line.substring(22);

				

				}

				String lines[] = line.split(NLPTools.SENTENCEENDS);

				for (int i = 0; i < lines.length; i++) {
					String sentence = lines[i].replaceAll(
							"/[a-zA-Z\\]]+ |[ \\[]", "");

					

					sentence = seg.segment(sentence);

					
					ret = this.compare(lines[i], sentence);
					

					rightNum += ret[0];
					outNum += ret[1];
					origNum += ret[2];

					

				}

			}

			double F_Value = (double) 2 * rightNum / (origNum + outNum);


			if (true) {
				System.out
						.println("rightNum/origNum/outNum/F_Value/recall/precision\n"
								+ rightNum
								+ "/"
								+ origNum
								+ "/"
								+ outNum
								+ "/"
								+ F_Value
								+ "/"
								+ (double) rightNum
								/ origNum
								+ "/" + (double) rightNum / outNum);
			}

			

			buff.close();

			long timeEnd = Calendar.getInstance().getTimeInMillis();

			if (true) {
				System.out.println("中文分词用时:time\n"
						+ (double) (timeEnd - timeStart) / 1000);
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ArrayList<String[]> preCompare(String original, String output) {
		if (original.length() >= 2 && original.matches("/w.*")) {
			original = original.substring(2);
		}
		if (output.length() >= 2 && output.matches("/w.*")) {
			output = output.substring(2);
		}
		String originals[] = original.trim().split("/[a-zA-Z\\]]+ ");

		if (originals.length > 0) {
			originals[originals.length - 1] = originals[originals.length - 1]
					.replaceAll("/[a-zA-Z\\]]+", "");
		}

		ArrayList<String[]> lists = new ArrayList<String[]>();

		for (int i = 0; i < originals.length; i++) {
			originals[i] = originals[i].trim();
			if (originals[i].length() > 0
					&& originals[i].substring(0, 1).compareTo("[") == 0) {
				originals[i] = originals[i].substring(1);
			}
		}

		String outputs[] = output.trim().split(UnigramSeg.getStaticSplitSign()+"");
		
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = outputs[i].trim();
		}

		lists.add(originals);
		lists.add(outputs);
		return lists;
	}

	private int[] compare(String original, String output) throws IOException {
		int[] ret = new int[4];

		String originals[];
		String outputs[];

		ArrayList<String[]> lists = this.preCompare(original, output);
		originals = lists.get(0);
		outputs = lists.get(1);

		int outLength = 0, origLength = 0;

		if (originals.length == 1 && outputs.length == 1
				&& originals[0].length() == 0) {

			

			return ret;
		}

		int rightNum = 0;
		int i = 0, j = 0;

		while (i < outputs.length && j < originals.length) {
			if (outLength == origLength) {
				while (i < outputs.length && j < originals.length
						&& outputs[i].length() == originals[j].length()) {
					rightNum++;
					outLength += outputs[i].length();
					origLength = outLength;

					

					i++;
					j++;
				}

				

			}

			if (i < outputs.length && j < originals.length) {
				

				if (outLength < origLength) {
					outLength += outputs[i].length();
					i++;
				} else {
					origLength += originals[j].length();
					j++;
				}

				
			}

		}

		ret[0] = rightNum;
		ret[1] = outputs.length;
		ret[2] = originals.length;

		

		return ret;
	}

	public void trainTest() throws IOException {
		String trainPath = 
			"D:/workspace/segExcersice/src/data/1998train1_mergeNr.txt";
		
		
		String modelPath = TempFileManage.getTempFilePath("seg");

		UnigramSeg seg = new UnigramSeg(trainPath, "utf-8",
				SegModel.wordRateType, modelPath);

		String testPath = "D:/workspace/segExcersice/src/data/1998test2_mergeNr.txt";
		unigramTestInTestSet(seg, testPath, "utf-8");
		
//		seg = new UnigramSeg(modelPath);
//		
//		unigramTestInTestSet(seg, testPath, "utf-8");
		
	}
	
	public void trainBigramTest() throws IOException {
		String trainPath = 
			"D:/workspace/segExcersice/src/data/1998train1_mergeNr.txt";
//		trainPath = "D:/xing/199801q_MergeNrtr.txt";
		trainPath = 
			"D:/workspace/segExcersice/src/data/1998train1.txt";
		
		String modelPath = TempFileManage.getTempFilePath("seg");

		BigramSeg seg = new BigramSeg(trainPath, "utf-8",
				modelPath);

		String testPath = "D:/workspace/segExcersice/src/data/1998test2_mergeNr.txt";
//		testPath = "D:/xing/199801q_MergeNrtt.txt";
		testPath = "D:/workspace/segExcersice/src/data/1998test2.txt";
		
		unigramTestInTestSet(seg, testPath, "utf-8");
		
//		seg = new BigramSeg(modelPath);
//		
//		unigramTestInTestSet(seg, testPath, "utf-8");
		
	}
	
	public void biSegmentTest() throws IOException{
		String line = "９００ＭＨｚ我是中国人，有５５．５％个人5，出生于1983年5月！二十四个人！" +
				"３８９７８２．４２７２８８１７６６４５２０嗯５％. 更何况是一千三四百年前";
		String trainPath = 
			"D:/workspace/segExcersice/src/data/1998train1_mergeNr.txt";
		trainPath = 
			"D:/workspace/segExcersice/src/data/1998train1.txt";
		
		String modelPath = "D:/xing/seg-model.txt";
//		BigramSeg seg = new BigramSeg(modelPath);
		
		BigramSeg seg = new BigramSeg(trainPath, "utf-8",
				modelPath);
		
		System.out.println(seg.segment(line));
		
//		String testPath = "D:/workspace/segExcersice/src/data/1998test2_mergeNr.txt";
//		unigramTestInTestSet(seg, testPath, "utf-8");
		
		System.out.println(seg.getSegWordList(line));
		
		
	}
	
	public void mergeTest(){
		
		String trainPath = 
			"D:/workspace/segExcersice/src/data/199801q.txt";
		trainPath = "D:/xing/199801q.txt";
		
		String trainPathMergeNr =  
			"D:/workspace/segExcersice/src/data/199801q_MergeNr.txt";
		trainPathMergeNr =  
			"D:/xing/199801q_MergeNr.txt";
		
		String trainPathMergeN =  
			"D:/workspace/segExcersice/src/data/199801q_MergeN.txt";
		trainPathMergeN =  
			"D:/xing/199801q_MergeN.txt";

		MergeNamedEntity merge = new MergeNamedEntity();
		
		merge.mergeNrFile(trainPath, trainPathMergeNr);
		
		merge.mergeNFile(trainPath, trainPathMergeN);
	}
	
	public void writeDefaultModel(){

		
		String trainPathMergeNr =  
			"D:/workspace/segExcersice/src/data/199801q_MergeNr.txt";

		
		this.trainAndWriteDefaultModel(trainPathMergeNr, "utf-8", SegModel.wordRateType);
		this.trainAndWriteDefaultModel(trainPathMergeNr, "utf-8", SegModel.wordFrequenceType);
		this.trainAndWriteDefaultModel(trainPathMergeNr, "utf-8", SegModel.biWordRateType);
	}

	private void trainAndWriteDefaultModel(String trainPath, String charset, 
			int segType){
		
		String modelPath = "src/org/tseg/seg/"+SegModel.getStringName(segType)+".out";
		new UnigramSeg(trainPath, charset,
				segType, modelPath);
	}
	
	public void testDefaultSeg(){
		UnigramSeg seg = UnigramSeg.getSeg();
		String testPath = "D:/workspace/segExcersice/src/data/1998test2_mergeNr.txt";
		unigramTestInTestSet(seg, testPath, "utf-8");
	}
	
	public void testGetSegWordList(){
		String line = "因早盘低开大幅低开，抄底资金纷纷进入，指数亦被拉升，截至中午收盘，上证指数下跌50.53点。";
		ArrayList<String> wordList = UnigramSeg.getSeg().getSegWordList(line);
		System.out.println(wordList);
	}
	
	String line = "19980101-02-007-003/m  １２月/t  ２７/t  #/w  至/p  ３１日/t  ，/w  " +
	"温/nr  家宝/nr  在/p  [贵州/ns  省委/n]nt  书记/n   书记/n 在/p 贵州/ns " +
	"中华人民共和国万岁/i 啊/n ";
	
	public void addPairsTest(){
		
		
		CountBiGram cbg = new CountBiGram();
		HashMap<String, Double>[] maps = cbg.initMaps(); 
		cbg.addPairs(cbg.getSignedWords(line), maps);
		for(Map map : maps){
			System.out.println(map);
		}
	}
	
	public void getSignedWordsTest(){
	
		
		CountBiGram cbg = new CountBiGram();
		ArrayList<String> signedWords = cbg.getSignedWords(line);
		System.out.println(signedWords);
		
	}
	
	public void addRatePairsTest(){
		CountBiGram cbg = new CountBiGram();
		ArrayList<String> signedWords = cbg.getSignedWords(line);
		HashMap<String,Double> wordRate = new HashMap<String,Double>();
		for(int i = 0; i < signedWords.size(); i++){
			String word = signedWords.get(i);
			if(i > 0){
				String lastWord = signedWords.get(i-1);
				wordRate.put(lastWord+word, 1.0);
			}
			wordRate.put(word, 1.0);
		}
		
//		System.out.println(wordRate);
		System.out.println();
		
		HashMap<String, Double>[] rateMaps = cbg.initMaps(); 
		
		cbg.addRatePairs(signedWords, rateMaps, wordRate);
		
		for(Map map : rateMaps){
			System.out.println(map);
		}
		
	}
	
	private void countBiWordRateTest(){
		String file = "D:/workspace/segExcersice/src/data/1998test2_mergeNr.txt";
		String charset = "utf-8";
		CountBiGram cbg = new CountBiGram();
		
		HashMap<String, Double> wordRate = cbg.countWordRate(file, charset, null); 
		
		HashMap<String, Double>[] rateMaps = cbg.countBiWordRate(file, charset, wordRate);
		int size = 0;
		for(Map<String,Double> map : rateMaps){
			for(Map.Entry<String, Double> entr : map.entrySet()){
				if(Math.random() > 0.999){
					System.out.println(entr.getKey()+"="+entr.getValue());
				}
			}
			size += map.size();
		}
		
		System.out.println(size);
		
	}
	
	private void ast(boolean bool){
		if(!bool){
			throw new RuntimeException();
		}
	}
	
	private void wordShapeTest(){
		String words[] = {
			"-",
			"-50.4%",
			"90",
			"20亿",
			"90.3",
			"60．7",
			"89.7%",
			"1990年",
			"三十亿",
			"一九九八年",
			"……&￥",
			"好",
			"hao",
			"hao_1",
			"?",
			"。t"
			
		};
		
		for(String word : words){
			System.out.println(word+"\t"+CountBiGram.wordShape(word));
		}
		
	}
	
	public static void main(String args[]) throws IOException{
		UnigramSegTest ust = new UnigramSegTest();
//		ust.countBiWordRateTest();
//		ust.getSignedWordsTest();
//		ust.addPairsTest();
//		ust.addRatePairsTest();
//		ust.trainTest();
		ust.trainBigramTest();
//		ust.wordShapeTest();
//		ust.biSegmentTest();
//		ust.writeDefaultModel();
//		ust.testDefaultSeg();
//		ust.mergeTest();
//		ust.testGetSegWordList();
	}
	
}
