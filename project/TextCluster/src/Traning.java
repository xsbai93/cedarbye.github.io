

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: </p>
 * @author cedar
 * @version 1.0
 */
import java.util.*;

public class Traning {
  FMMSegment vocabulary;
  Sample []v=new Sample[3];
  Dictionary dict;

  public Traning(Dictionary newDict) {
    vocabulary=new FMMSegment(newDict);
    dict=newDict;
  }



  public void train(){
    for(int i=0;i<3;i++){
      v[i]=new Sample();
      v[i].init(dict,vocabulary.vocabulary,vocabulary.NumOfVoc());
    }
    //扫描文本样例,计算P(wk|vj)
    v[0].countFreq("sample/science.txt");
    v[0].getP();
    v[1].countFreq("sample/sport.txt");
    v[1].getP();
    v[2].countFreq("sample/history.txt");
    v[2].getP();

  }

  public void loadVocabulary(){
    vocabulary.fileSegment("Sample/sport.txt");
    vocabulary.fileSegment("Sample/science.txt");
    vocabulary.fileSegment("Sample/history.txt");
    System.out.println("totle number of vocabulary is:"+vocabulary.NumOfVoc());
    //System.out.println(vocabulary.vocabulary.containsKey("颗粒"));

    train();
  }


}