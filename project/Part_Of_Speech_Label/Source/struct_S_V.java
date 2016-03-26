package Source;


import java.io.*;
import java.util.*;



/**
 * @author cedar
 * @version 1.0
 * @since 12/9/2015
 * @category: 主程序
 *            structSV()  构造字符集与状态集
 *            structAB(double delta)  构造状态转移矩阵与字符哈希表，同时实现add delta平滑
 *            Viterbi()   基于隐马尔可夫模型利用viterbi算法实现词类标注
 *            score()    标注效果测评
 */
public class struct_S_V {
	State_set sts = new State_set();        //状态集
	Symbol_set sys=new Symbol_set();        //字符集
	List<String> state_table=new ArrayList<String>();  //储存所有词性，用于计算状态转换表的坐标
	int TOTAL_NUM=0;            //共有词数
	int STATE_NUM=0;            //共有词性数
	int A_STATE[][];           //状态转换次数表
	double A_table[][];         //状态转换概率表
	double Un_Reg[];          //未登陆词特定状态的出现概率
	Map B_map =new HashMap();  //词表
	
	int total_words=0;      //共标注词数
	int correct_words=0;    //标注正确数
	double correct_rate=0.0; //正确率
	
	
	File infile = new File ("199801.txt");     //语料文件
	File outfile1 = new File ("ts.txt");       //状态集文件
	File outfile2 = new File ("ty.txt");       //字符集文件
	File outfile3 = new File ("b_table.txt");  //字符＋概率  b表文件
	File outfile6 =new File ("a_table.txt");  //  状态转移矩阵 a表文件
	File outfile4 = new File ("test.txt");    //  加工好的可以用于测试的文档
	File infile2 = new File ("test_S.txt");         // 真正用于测试标注效果的测试文档
	File outfile5 = new File ("result.txt");       // 标注结果
	
	//------------------------------------------------------------------------------
	//构造字符集与状态集  预料的前期加工
	public void structSV()throws IOException{
		
		FileReader in = new FileReader(infile);//语料文件
		FileWriter out1 =new FileWriter(outfile1);//状态集文件
		FileWriter out2 = new FileWriter(outfile2);//字符集文件
		FileWriter out4 = new FileWriter(outfile4);//  加工好的可以用于测试的文档
		State temp1=new State("/v");          // 预先插入一定存在的词性，减少判断次数。
		temp1.num=0;
		sts.state_set.add(temp1);
		state_table.add("/v");
		
		int ch;
		String element="";    //词+词性 串
		String end="";       // 词性
		String symbol="";     // 词
		int tar=1;           //词性是否已经存在与表中的标志
		while ((ch = in.read()) != -1){                     //读文件
			if ((char)(ch)=='\r'||(char)(ch)=='\n'){
				out2.write(ch);
				out4.write(ch);
				continue;
			}
			if ((char)ch==' '||(char)(ch)=='['||(char)(ch)==']'){ //忽略 上述符号
				continue;
			}
			
			element=element+ (char)ch;
			while((char) (ch = in.read())!=' '&&(char)(ch)!=']'&&(char)(ch)!='\n'){ //构造一个词素
				element=element+(char)ch;
				}
				if ((element.length()==21&&element.endsWith("/m"))||element.length()==2||element.length()==1){
					element="";               //词素是日期的话忽略
					continue;
				}
				else{
					int index=element.lastIndexOf("/");      //分离词素的词和词性部分
					if (index<element.length()&&index!=0){
						end=element.substring(element.lastIndexOf("/"), element.length()); 
						symbol=element.substring(0,element.lastIndexOf("/"));
					}
					
					tar=1;
					
						for (int i=0;i<sts.state_set.size();i++){  // 判断词性是否已经存在于表中
							if(sts.state_set.get(i).state.equals(end)){  //存在
								sts.state_set.get(i).num++;			//该词性出现次数++					
								out2.write(element+" ");
								out4.write(symbol+" ");
								element="";
								symbol="";
								end="";
								tar=0;
								break;
							}
							
						}
						if (tar==1){       //不存在  相词性表（状态表中）插入新元素
							State temp=new State(end);
							state_table.add(end);
							sts.state_set.add(temp);
							out2.write(element+"  ");						
							out4.write(symbol+" ");
							element="";
							symbol="";
							end="";
						}
						continue;
					}
				}
		for (int i=0;i<sts.state_set.size();i++){//计算预料中词的总数
			out1.write(state_table.get(i)+"  ");
			TOTAL_NUM=TOTAL_NUM+sts.state_set.get(i).num; 
		}
		
		STATE_NUM=sts.state_set.size();  //状态总数（词性总数）
	//	System.out.println(sts.state_set.size());
	//	System.out.println(TOTAL_NUM);
		in.close();
		out1.close();
		out2.close();
		out4.close();
		
	}
	
	//--------------------------------------------------------------------------------------------------
	// 构造状态转移矩阵 构造字符哈希表  求隐马尔可夫模型的参数  
	public void structAB(double delta)throws IOException{           //add delta 平滑
		A_STATE=new int [STATE_NUM][STATE_NUM];  //状态转化表 （出现次数）
		A_table=new double [STATE_NUM][STATE_NUM]; // 状态转化表 (频率)
		FileWriter out = new FileWriter(outfile3);  
		FileWriter out1 = new FileWriter (outfile6);
		for (int i=0;i<STATE_NUM;i++){             //初始化
			for (int j=0;j<STATE_NUM;j++){
				A_STATE[i][j]=0;
			}
		}
			
			
		String stateF="";  //前词串
		String stateB="";  //后词串
		String endF="";    //前词词性
		String endB="";    //后词词性
		int indexF=0;      //前词词性在词性表中的坐标
		int indexB=0;      //后词词性在词性表中的坐标
		FileReader in = new FileReader(outfile2);
		int ch=0;	
		while ((char)(ch=in.read())!=' '){ //取得第一个要插入哈希表的词串
			stateF=stateF+(char) ch; 
		}
		endF=stateF.substring(stateF.indexOf("/"),stateF.length()); //将词串插入哈希表
		Symbol temp=new Symbol(stateF);
		temp.end=endF;
		temp.p=(double)temp.num/(double)TOTAL_NUM;
		B_map.put(stateF, temp);		
		while ((ch=in.read())!=-1){
			if ((char)(ch)==' '){
				continue;
			}
			
			stateB=stateB+(char) ch;
			while ((char) (ch = in.read())!=' '){
				stateB=stateB+(char) ch;              //取得一个新词串
			}
				endB=stateB.substring(stateB.indexOf("/"),stateB.length());
				indexF=state_table.indexOf(endF);        
				indexB=state_table.indexOf(endB);
				A_STATE[indexF][indexB]++;           //更新状态转移矩阵(转移次数)
				if (B_map.containsKey(stateB)){     
					temp=(Symbol) B_map.get(stateB);  //更新哈希表   如果词串已经存在于哈希表
					temp.num++;           //词串出现次数更新
					temp.p=(double)temp.num/(double)TOTAL_NUM;//词串出现频率更新。
					B_map.put(stateB, temp);
				}
				else{                         //如果词串不存在于哈希表中
					temp=new Symbol(stateB);
					temp.end=endB;
					temp.p=(double)temp.num/(double)TOTAL_NUM;
					B_map.put(stateB, temp);
				}
				stateF=stateB;
				endF=endB;
				stateB="";
				endB="";
	}
		Collection setv=B_map.values();  //遍历哈希表，将值打印到文件中
		Iterator iterator=setv.iterator();
		while(iterator.hasNext()){
			Symbol temp3=(Symbol)iterator.next();
			out.write(temp3.symbol+" "+temp3.num+" "+temp3.end+"    "+temp3.p+'\r'+'\n');
		}
		out.close();
		in.close();
		Un_Reg = new double [STATE_NUM];   //处理未登陆词 平滑。
		for (int i=0;i<STATE_NUM;i++){
			Un_Reg[i]=delta/(double) TOTAL_NUM;
			out1.write(Un_Reg[i]+"       ----");
			for (int j=0;j<STATE_NUM;j++){
				A_table[i][j]=(double) A_STATE[i][j]/(double) TOTAL_NUM;
				out1.write("  "+A_table[i][j]);
			}
			out1.write(""+'\r'+'\n');
		}
		
		out1.close();
	//	System.out.println("2");
	}
//------------------------------------------------------------------------------------------------------------
//            viterbi算法 实现标注	
	public void Viterbi()throws IOException{
		FileReader in = new FileReader(infile2);
		FileWriter out = new FileWriter(outfile5);
		int ch;
		int n=0;//句子中词素的个数
		String sentence="";         // 一个待标注句子
		while ((ch=in.read())!=-1){
			if ((char)ch==' '||(char)ch=='\r'||(char)ch=='\n'){  
				continue;
			}
			n=0;
			sentence=sentence+(char) ch; 
			while ((char)(ch=in.read())!='。'&&(char)(ch)!='？'&&(char)(ch)!='！'&&(char)(ch)!='：'&&(char)(ch)!='；'&&(char)(ch)!='\n'&&(char)(ch)!='\r'&&ch!=-1){
				sentence=sentence+(char) ch;
				if ((char)ch==' '){
					n++;
				}
			}
			//
		    if (ch!=-1){
		    	sentence=sentence+(char) ch;
		    }
			if (sentence.endsWith("。")||sentence.endsWith("？")||sentence.endsWith("！")||sentence.endsWith("：")||sentence.endsWith("；")){
				n++;
				sentence=sentence+" ";
			}
			String [] words=sentence.split(" ");	//数组项中存放句子的词
			//------------ viterbi算法开始
			String word_state="";   //汉语词＋词性 串
			Symbol temp=new Symbol();
			Vit_var vit_array[][]= new Vit_var[n][STATE_NUM]; //维特比变量数组
			Vit_path path_array[][]=new Vit_path[n][STATE_NUM]; //路径数组 path_array[i][j]表示第i+1个词是j词性的时候第i个词的词性。
			for (int i=0;i<STATE_NUM;i++)  //初始化维特比算法，处理开始词
			{
				vit_array[0][i]=new Vit_var();
				vit_array[0][i].index=0;
				vit_array[0][i].p=((double)sts.state_set.get(i).num)/((double)TOTAL_NUM);
				
				vit_array[0][i].state=sts.state_set.get(i).state;
				word_state=words[0]+vit_array[0][i].state;  //构成词串
				
				if((temp=(Symbol)B_map.get(word_state))!=null) //词串在哈希表中
				{
					vit_array[0][i].p=vit_array[0][i].p*temp.p;
				}
				else         //词串是未登陆词
				{
					vit_array[0][i].p=vit_array[0][i].p*Un_Reg[i];
				}
			}
			//--------------------------------------------
			double p_max=0.0;  //概率
			String s_now="";   //词性
			String s_max="";
			double p_now=0.0;
			for (int i=1;i<n;i++) //针对每个词的循环
			{
				for (int j=0;j<STATE_NUM;j++) //针对每种词性的循环
				{
					vit_array[i][j]=new Vit_var();
					word_state=words[i]+sts.state_set.get(j).state;
					p_max=0.0;
					for (int k=0;k<STATE_NUM;k++)    //寻找概率最大的选择
					{
						p_now=vit_array[i-1][k].p*A_table[k][j];
						s_now=vit_array[i-1][k].state;
						if ((temp=(Symbol)B_map.get(word_state))!=null) //哈希表中的词串
						{
							p_now=p_now*temp.p;
						} 
						else   //未登陆词串
						{
							p_now=p_now*Un_Reg[j];
						}
						if (p_max<p_now)
						{
							p_max=p_now;
							s_max=s_now;
						}
						/////////////////////////////////////
						
					}
					vit_array[i][j].p=p_max;  //处理一个vit_array和path_array项
					vit_array[i][j].index=i;
					vit_array[i][j].state=sts.state_set.get(j).state;
					path_array[i-1][j]=new Vit_path();
					path_array[i-1][j].index=i-1;
					path_array[i-1][j].state_Front=s_max;
				}
			}
			//***** 构建词性向量
			String []state_result=new String[n]; //存储词性标注的结果
			for(int i=0;i<n;i++)
			{
				state_result[i]="";
			}
			p_max=0.0;
			int index=0;
			for (int i=0;i<STATE_NUM;i++)         //寻找概率最大的结果
			{
				if (vit_array[n-1][i].p>p_max)
				{
					p_max=vit_array[n-1][i].p;
					index=i;
				}
			}
			state_result[n-1]=sts.state_set.get(index).state;
			for (int i=n-2;i>=0;i--)  //开始循环，倒序构造词性标注结果数组
			{
				state_result[i]=path_array[i][index].state_Front;
				for (int j=0;j<sts.state_set.size();j++)
				{
					if (sts.state_set.get(j).state==path_array[i][index].state_Front)
					{
						index=j;
						break;
					}
				}
			}
						//-------------------- viterbi算法结束
			for (int i=0;i<n;i++){ //向文件中输出结果
				out.write(words[i]+state_result[i]+" ");
			}
			out.write(""+'\r'+'\n');
			sentence="";
			
		}
		out.close();
		in.close();
	}
	
	//----------------------------------------------------------------------------------
	//  测试算法效果
	public void score()throws IOException{
		FileReader in_source=new FileReader(outfile2); //源文件
		FileReader in_result=new FileReader(outfile5); //结果文件
		String source="";//源中的一个词串
		String result="";//我标注结果的一个词串
		
		int ch_s;
		int ch_r=0;
		
		while (ch_r!=-1){
			while ((char)(ch_r=in_result.read())==' '||(char)ch_r=='\r'||(char)ch_r=='\n'){
				continue;
			}
			if(ch_r==-1){
				 correct_rate=(double)((double)correct_words)/((double)total_words);
					in_source.close();
					in_result.close();
					System.out.print("准确率为：");
					System.out.println(correct_rate);
					System.out.println("正确标注词数："+correct_words+"  "+"共计标注词数："+total_words);
				return;
			}
			result=result+(char) ch_r;
			while ((char)(ch_s=in_source.read())==' '||(char)ch_s=='\r'||(char)ch_s=='\n'){
				continue;
			}
			source=source+(char) ch_s;
			
			while((char)(ch_r = in_result.read()) != ' '){
				result=result+(char) ch_r;
			}
			while ((char)(ch_s=in_source.read())!=' '){
				source=source+(char)ch_s;
			}
			
			
			if (source.equals(result)){
				correct_words++;
			}
			total_words++;
			result="";
			source="";			
		}		
	}
	
	
	//----------------------------------------------------------------------------------
	//主程序
	public static void main (String[] args)throws IOException {
		struct_S_V test = new struct_S_V();
		test.structSV();
		test.structAB(0.0001);
		test.Viterbi();
		test.score();
		System.out.println("完成");
	}
}
