package WordSegment;

import java.io.*;
import java.util.*;

public class WordSegment {
	private Dictionary dic;

	private SegStrategy segmentStrategy;

	public WordSegment() {
	}

	public WordSegment(String dicFile, SegStrategy strategy) {
		SetDic(dicFile);
		setStrategy(strategy);
	}

	public Vector<String> Segment(String sentence) {
		return segmentStrategy.Segment(sentence, dic);
	}
	
	public void SetDic(Dictionary d) {
		dic = d;
	}

	public void SetDic(String dicFile) {
		ObjectInputStream objectIn = null;
		try {
			objectIn = new ObjectInputStream(new FileInputStream(new File(dicFile)));
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}

		try {
			dic = (Dictionary) (objectIn.readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	public void setStrategy(SegStrategy aStrategy) {
		segmentStrategy = aStrategy;
	}
}
