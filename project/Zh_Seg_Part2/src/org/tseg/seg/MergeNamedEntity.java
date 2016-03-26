package org.tseg.seg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class MergeNamedEntity {
	
	
	//isPrint用来控制是否打印，供调试用
	//通常情况下，isPrint是false，isPrint1是ture
	//如果要打印某个东西，就将其放在if(isPrint1)条件分支内
	//如果不再打印，就将isPrint1改为isPrint
	//如果不打印任何东西，就将isPrint1改为false
    boolean isPrint = false;
    boolean isPrint1 = true;

	//用来合并机构名、组织名、地名、人名
	//如果输入为“[广播/vn  电影/n  电视/n  部/n]nt”
	//则输出为“广播电影电视部/nt”
	//这样是为了便于统计实体
	//对于“[人民/n  大会堂/n]/ns”这样的地名可以合并，但是
	//注意：对于地名“天津/ns  塘沽/ns”这样的字串尚没有合并
	public String mergeN(String sentn) {
		sentn = this.mergeNr(sentn);
		return this.mergeNzNs(sentn);
	}
	
	public void mergeNFile(String filePath,String wPath){
		this.mergeNEInFile(filePath, wPath, false);
	}
	
    public void mergeNrFile(String filePath,String wPath){
		this.mergeNEInFile(filePath, wPath, true);
	}
	
	//这里合并的是一个文件中的名词实体
	//而上面的mergeN是指针对一行文本
	public void mergeNEInFile(String filePath, String wPath, boolean isNr) {
		try {
			BufferedReader buff = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(wPath),"UTF-8"));
			
			
			String line;
			while((line = buff.readLine()) != null) {
				if(isNr) {
					line = this.mergeNr(line);
				} else {
					line = this.mergeN(line);
				}
				writer.write(line+"\n");
			}
			buff.close();
			writer.close();
			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	//这里合并以“nz，ns，nt”标识的命名实体
	public String mergeNzNs(String sentn){
		String ret = "";
		
		int index_end = sentn.lastIndexOf("]");
		while(index_end >=0) {
			if(isPrint1){
				System.out.println(index_end);
			}
			
			ret = sentn.substring(index_end) + ret;
			
			if(sentn.substring(index_end+1).matches("ns.*|nt.*|nz.*")){
				int index_start = sentn.lastIndexOf("[");
				if(index_start >= 0) {
					String temp = sentn.substring(index_start +1, index_end);
					if(isPrint1) {
						System.out.println("temp1:"+temp);
					}
					temp = temp.replaceAll("/[a-zA-Z]+|[ ]", "");
					
					if(isPrint1) {
						System.out.println("temp2:"+temp);
					}
					
					ret = temp +"/"+ret.substring(1);
					sentn = sentn.substring(0,index_start);
				} else {
					
					ret = sentn.substring(0,index_end) +ret;
					sentn = "";
				}
			} else {
				sentn = sentn.substring(0,index_end);
			}
			
			index_end = sentn.lastIndexOf("]");
		}
		
		
		ret = sentn + ret;
		
		
		
		return ret;
	}
	
	//这里合并人名中的姓氏和名字
	//如“张/nr  三/nr”可以被合并为“张三/nr”
	public String mergeNr(String sentn) {
		String orig = "";
		
		String nrs[] = sentn.split("/nr  ");
		
			if(isPrint1) {
				System.out.println(orig);
			}
			
			int t = 0;
			for(int k = 0; k < nrs.length-1; k ++){
				if((k + t)%2 == 1) {
					if(nrs[k].contains("/")) {
						nrs[k] =  "/nr  "+nrs[k];
						t++;
					} else {
						nrs[k] = nrs[k] + "/nr  ";
					}
					
				}
				orig += nrs[k];
			}
			
			if(sentn.matches(".*/nr  ")) {
				orig += nrs[nrs.length -1] + "/nr  ";
			} else {
				if(nrs.length != 2) {
					orig += nrs[nrs.length -1];
				} else {
					orig += "/nr  " + nrs[nrs.length -1];
				}
			}
			
			if(isPrint1) {
				System.out.println("after:"+orig);
			}
			
		
		
		return orig;
		
	}
	
	//下面的方法是对mergeNr方法的一般化，如果type="nr"，就可以合并人名了
	//注意：下面这个方法不能扩展到地名，因为地名可能有三个以上地名组合而成
	//而人名都只由两个地名组合而成
	@SuppressWarnings("unused")
	private String mergeNrs_type(String sentn, String type) {
		String orig = "";
		String typeWithTwoSpace = "/"+ type +"  ";
		
		String nrs[] = sentn.split(typeWithTwoSpace);
		
			if(isPrint1) {
				System.out.println("\norig:"+sentn);
			}
			
			int t = 0;
			for(int k = 0; k < nrs.length-1; k ++){
				if(isPrint1) {
					System.out.println(nrs[k]+"-----1"+"---"+nrs.length);
				}
				if((k + t)%2 == 1) {
					
					
					
					if(nrs[k].contains("/")) {
						nrs[k] =  typeWithTwoSpace + nrs[k];
						t++;
					} else {
						nrs[k] = nrs[k] + typeWithTwoSpace;
					}
					
					if(isPrint1) {
						System.out.println(nrs[k]+"-----2");
					}
					
				}
				orig += nrs[k];
				
				if(isPrint1) {
					System.out.println("middle:"+orig);
				}
				
				
			}
			
			if(sentn.length() >=4 &&
					sentn.substring(sentn.length() - 4).compareTo(".*"+typeWithTwoSpace) == 0) {
				orig += nrs[nrs.length -1] + typeWithTwoSpace;
			} else {
				if(nrs.length != 2) {
					orig += nrs[nrs.length -1];
				} else {
					orig += typeWithTwoSpace + nrs[nrs.length -1];
				}
			}
			
			if(isPrint1) {
				System.out.println("after:"+orig);
			}
			
		
		
		return orig;
		
	}
	
	

	
}
