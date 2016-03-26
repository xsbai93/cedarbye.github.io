package org.tseg.seg;


public class WordGraph {

	public int bestBeforeIndex;
	public double bestBeforeValue = -Double.MAX_VALUE;

	
	public void set(int index, double value){
		if(this.bestBeforeValue < value){
			this.bestBeforeValue = value;
			this.bestBeforeIndex = index;
		}
	}

}
