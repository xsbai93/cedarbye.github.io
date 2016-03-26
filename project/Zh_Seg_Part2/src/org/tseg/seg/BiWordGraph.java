package org.tseg.seg;

import java.util.*;

public class BiWordGraph extends WordGraph{

	Map<Integer,Double> idValues;
	
	public BiWordGraph(int maxUnseenWordLen){
		idValues = new HashMap<Integer, Double>(maxUnseenWordLen);
	}
	
}
