package dictionaryAdder;
import java.io.*;
import java.util.*;

public class Dictionary implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> dic = new HashMap<String, Integer>();
	private int maxWordLength = 0;
	private long wordsNumOfTrainDoc = 0;
	
	
	public void addWord(String newWord)	//向词典中增加新词	
	{
		if (newWord.length() > maxWordLength)
			maxWordLength = newWord.length();
		if (checkWord(newWord))
		{
			int t = (Integer)dic.get(newWord);
			dic.put(newWord, new Integer(t + 1));
		}
		else
			dic.put(newWord, new Integer(1));
		wordsNumOfTrainDoc++;
	}
	
	public int getFrequency(String word)
	{
		if(checkWord(word))
			return (Integer)dic.get(word);
		else
			return 0;
	}

	public boolean checkWord(String word)	//检查这个词是否在词典中
	{
		if (dic.get(word) == null)
			return false;
		else
			return true;
	}

	public int getMaxLength() { return maxWordLength; }
	public String toString()
	{
		Iterator<String> keyIter = dic.keySet().iterator();
		String value = new String();
		while (keyIter.hasNext())
		{
			String key = (String)keyIter.next();
			value += key + " " + getFrequency(key) + "\n";
		}
		return value;
	}

	
	public long getWordsNumOfTrainDoc() {
		return wordsNumOfTrainDoc;
	}
	
}
