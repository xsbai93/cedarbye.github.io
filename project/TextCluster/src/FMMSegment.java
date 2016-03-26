import java.lang.*;
import java.io.*;
import java.util.*;

public class FMMSegment {
  Dictionary dic;
  int totleNumber; //记录文中总共词汇数
  HashMap<String, Float> vocabulary; //记录从文本中获取的中文词

  public FMMSegment() {
  }

  public FMMSegment(Dictionary newDic) {
    dic = newDic;
    totleNumber = 0;
    vocabulary = new HashMap<String, Float>();
  }

  public int wordSegment(String Sentence) { //中文分词
    int senLen = Sentence.length();
    int i = 0, j = 0;
    int M = 12;
    String word;
    boolean bFind = false;
    FileAppender fa=new FileAppender("vocabulary.txt");

      while (i < senLen) {
        int N = i + M < senLen ? i + M : senLen + 1;
        bFind = false;
        for (j = N - 1; j > i; j--) {
          word = Sentence.substring(i, j);
          if (dic.Find(word)) {
            if (j > i + 1) {
              if (!vocabulary.containsKey(word)) {
                vocabulary.put(word, new Float(0));
                totleNumber++; //累加总词汇数
                //将获取的单词写入文件
                fa.append(word);
                //System.out.print(word + " ");
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


  public void fileSegment(String fileName) { //按行读入
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

  public int NumOfVoc() {
    return totleNumber;
  }
}
