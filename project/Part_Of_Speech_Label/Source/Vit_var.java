package Source;
/**
 * @author cedar
 * @version 1.0
 * @since 12/9/2015
 * @category:viterbi变量 类
 */
public class Vit_var {
	double p;                  //概率 在前当位置出现该词性的概率
	String state;              //状态（词性）
	int index=0;
	public Vit_var(){
		
	}
	public Vit_var(double p,String state){
		this.p=p;
		this.state=state;
	}

}
