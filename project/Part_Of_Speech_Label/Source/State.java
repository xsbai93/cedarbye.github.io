package Source;
/**
 * @author cedar
 * @version 1.0
 * @since 12/9/2015
 * @category:状态类
 */
public class State {
      String state;        // 状态 （词性）  如： /n  /v
      int num;             // 该状态词数  如： 动词 1895个
      State(String state){
    	  this.state=state;
    	  this.num=1;
      }
}
