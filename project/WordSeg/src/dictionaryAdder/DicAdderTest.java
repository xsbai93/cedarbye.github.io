package dictionaryAdder;

import java.io.*;
import java.util.*;


public class DicAdderTest
{
	private Dictionary dic = new Dictionary();

	public Dictionary getDic()
	{
		return dic;
	}
	
	public void Adder(String fileName)
	{
		File aFile = new File(fileName);
		FileInputStream inFile = null;
		try
		{
			inFile = new FileInputStream(aFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}

		try
		{
			BufferedReader inStream = new BufferedReader(new InputStreamReader(inFile));
			String line;
			while ((line = inStream.readLine()) != null)	
			{
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens())
					dic.addWord(st.nextToken());
			}
			inFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
			System.exit(0);
		}
	}

	public void SaveDic(String fileName)
	{
		ObjectOutputStream objout = null;
		try
		{
			objout = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
			objout.writeObject(dic);
			objout.close();
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	public static void main(String[] args) 
	{
		DicAdderTest adder = new DicAdderTest();
		adder.Adder("test1.txt");
		adder.SaveDic("dic1.dat");		
	}
};
