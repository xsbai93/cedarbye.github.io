

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: </p>
 * @author cedar
 * @version 1.0
 */
import java.util.*;
import java.io.*;

public class Sample {
  Dictionary dic;
  HashMap<String, Float> wordTable= new HashMap<String, Float>();
  int totleWord;
  int vocNum;

  public Sample() {
  }

  public void init(Dictionary newDic,HashMap<String, Float> hm,int n) {
    dic = newDic;
    totleWord=0;
    wordTable.putAll(hm);
    vocNum=n;
  }

  public int wordSegment(String Sentence) { //中文分词
    int senLen = Sentence.length();
    int i = 0, j = 0;
    int M = 12;
    String word;
    boolean bFind = false;

      while (i < senLen) {
        int N = i + M < senLen ? i + M : senLen + 1;
        bFind = false;
        for (j = N - 1; j > i; j--) {
          word = Sentence.substring(i, j);
          if (dic.Find(word)) {
            if (j > i + 1) {
              totleWord++;  //统计总词汇数
              if (wordTable.containsKey(word)) {
                float c=wordTable.get(word).floatValue()+1;
                wordTable.put(word,new Float(c));
               //统计每个在单词在文本中出现的次数
              }
            }
            bFind = true;
            i = j;
            break;
          }
        }
        if (bFind == false) {
          i = j + 1;
        }
      }
    return 1;
  }

  public void getP(){
    try {
      BufferedReader in = new BufferedReader(
          new FileReader("vocabulary.txt"));
      String s;
      while ( (s = in.readLine()) != null) {
        //System.out.println(s);
        if(wordTable.containsKey(s)){
          float nk=wordTable.get(s).floatValue();
          float p=(nk+1)/(vocNum+totleWord);
          //System.out.println(s+" "+new Float(p));
          wordTable.put(s,new Float(p));
        }
      }
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }


  public void countFreq(String fileName) { //按行读入
    try {
      BufferedReader in = new BufferedReader(
          new FileReader(fileName));
      String s;
      while ( (s = in.readLine()) != null) {
        wordSegment(s);
      }
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }


}