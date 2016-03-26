

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: </p>
 * @author cedar
 * @version 1.0
 */

import java.io.*;

public class FileAppender {
  String filename;
  String word;

  public FileAppender(String fn) {
    filename=fn;
    word=new String();
  }

  public void loadFile(){
    try{
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String s;
      while ( (s = in.readLine()) != null) {
        word=word.concat(s+"\n");
        //System.out.println(s);
      }

    }catch(IOException e){
      System.out.println(e);
    }
    //System.out.println(word);
  }

  public void append(String w){
    loadFile();
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter(filename));
      word=word.concat(w);
      out.write(word,0,word.length());
      out.flush();
    }catch(IOException e){
      System.out.println(e);
    }
    word="";
  }

}
