

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: </p>
 * @author cedar
 * @version 1.0
 */

import java.io.*;
import java.util.*;

public class TargetText {
  Dictionary newDict;
  float []NB=new float[3];
  int []NBE=new int[3];

  public TargetText() {
  }
  public void init(Dictionary dict){
    newDict=dict;
    for(int i=0;i<3;i++){
      NB[i]=1;
      NBE[i]=0;
    }
  }

  public String categorize(Sample []v,int n,String filename){
    for(int i=0;i<n;i++){
      fileSegment(filename,v[i].wordTable,i);
    }
    int temp=NBE[0];
    int j=0;
    for(int i=1;i<3;i++){
      if(temp>NBE[i]){
        temp=NBE[i];
        j=i;
      }
    }
    for(int i=1;i<3;i++){
      NB[i]=NBE[i]=1;
    }

    if(j==0){
      System.out.println("This text belongs to science");
      return new String("science");
    }else if(j==1){
      System.out.println("This text belongs to sport");
      return new String("sport");
    }else if(j==2){
      System.out.println("This text belongs to history");
      return new String("history");
    }else{
      return null;
    }

    //System.out.println(NB[0]+":"+NBE[0]+" "+NB[1]+":"+NBE[1]+" "+NB[2]+":"+NBE[2]);
  }

  public int wordSegment(String Sentence,HashMap<String, Float> hm,int n) {
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
          if (newDict.Find(word)) {
            if (j > i + 1) {
              if (hm.containsKey(word)) {
                NB[n]=NB[n]*hm.get(word).floatValue();  //计算每一个类别的概率
                while(NB[n]<1){
                  NBE[n]=NBE[n]+1;
                  NB[n]=NB[n]*10;
                }
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


  public void fileSegment(String fileName,HashMap<String, Float> hm,int n) { //按行读入
    try {
      BufferedReader in = new BufferedReader(
          new FileReader(fileName));
      String s;
      while ((s = in.readLine()) != null) {
        wordSegment(s,hm,n);
      }
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }


}