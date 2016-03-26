package org.tseg.seg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import org.tseg.tools.ObjectIO;

public class SegModel {

	public Map<String,Double> wordLogProb;
	public Map<String,Double>[] rateMaps;
	public int segType = SegModel.wordRateType;
	public double logProbFacPerCharInUnseemWord;
	
	public static final int wordFrequenceType = 1;
	public static final int wordRateType = 0;
	public static final int biWordRateType = 2;
	
	
	public SegModel(Map<String, Double> wordLogProb, int segType,
			double logProbFacPerCharInUnseemWord) {
		super();
		this.wordLogProb = wordLogProb;
		this.segType = segType;
		this.logProbFacPerCharInUnseemWord = logProbFacPerCharInUnseemWord;
	}
	
	

	
	
	public static SegModel readWordRateModel() {

		return readModel(SegModel.wordRateType);

	}

	public static SegModel readWordFrequenceModel() {

		return SegModel.readModel(SegModel.wordFrequenceType);

	}
	
	public static SegModel readBiWordRateModel() {

		return SegModel.readModel(SegModel.biWordRateType);

	}


	static String getStringName(int segType) {
		if (segType == SegModel.wordRateType) {
			return "wordRate";
		} else if(segType == SegModel.wordFrequenceType){
			return "wordFrequence";
		} else if(segType == SegModel.biWordRateType){
			return "biWordRate";
		}
		
		return "error";
	}

	static SegModel readModel(int segType) {

		try {

			ObjectInputStream in = new ObjectInputStream(Count.class
					.getResource(getStringName(segType) + ".out")
					.openStream());

			SegModel segModel = readModel(in);
			in.close();
			
			return segModel;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	static SegModel readModel(String modelPath) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(modelPath));
			SegModel segModel = readModel(in);
			in.close();
			
			return segModel;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static SegModel readModel(ObjectInputStream in)
			throws IOException {
		 

		int segType = in.readInt();
		double logProbFacPerCharInUnseemWord = in.readDouble();
		Map<String, Double> wordLogProb = ObjectIO.readMapDobule(in);
		
//		while (in.available() > 0) {
//			String key = ObjectIO.readKey(in);
//			Double v = in.readDouble();
//			wordLogProb.put(key, v);
//		}
		
		SegModel segModel = new SegModel(wordLogProb, segType, logProbFacPerCharInUnseemWord);
		
		if(segType == SegModel.biWordRateType){
			
			
			int len = in.readInt();
			Map<String,Double>[] rateMaps = new HashMap[len];
			for(int i = 0; i < len; i++){
				rateMaps[i] = ObjectIO.readMapDobule(in);
			}
			
			segModel.rateMaps = rateMaps;
		}

		if (true) {
			System.out.println("Count-getWordRateOrFrequence-wordCount.size:"
					+ wordLogProb.size());
		}

		
		
		
		return segModel;
	}

	
	
	public void writeModel(String modelPath) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(modelPath));
		out.writeInt(this.segType);
		out.writeDouble(this.logProbFacPerCharInUnseemWord);
//		for (String key : this.wordLogProb.keySet()) {
//			ObjectIO.writeKey(key, out);
//			out.writeDouble(this.wordLogProb.get(key));
//		}
		ObjectIO.writeMapDouble(wordLogProb, out);
		
		if(this.segType == SegModel.biWordRateType){
			out.writeInt(rateMaps.length);
			
			for(int i = 0; i < rateMaps.length; i++){
				ObjectIO.writeMapDouble(rateMaps[i], out);
			}
			
		}
		
		out.close();
	}
	
}
