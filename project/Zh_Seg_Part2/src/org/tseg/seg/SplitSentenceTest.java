package org.tseg.seg;

import java.util.*;

import org.tseg.tools.NLPTools;
import org.tseg.tools.SplitSentence;


public class SplitSentenceTest {
	SplitSentence gf;
	
	String keys[] = {
			"王小丫 老公",
			"姚明  火箭",
			"姚明ur2.09 火箭hu",
			"王小丫 天天向上",
			"dota显血下载",
			"亚里士多德,论伦理",
			"开店小项目",
			"非常好看的f4 car",
			"av女优激情图片",
			"骑士 女优名 上马宏",
			"6号周日阳西沙扒湾泡清澈的山泉海水 蜒粑魃？ 海水 北咎?最后由 随风cwf 于 2009-8-30 13:04 编辑 ？？俺乔湾位于阳江地区",
			"td class=r1> 您是不是要找：<b><a href=?q=%C2%ED%C2%AC%B4%EF&py=1>马卢达",
			"(你好呀！23）",
			"6段。。。 装备到是比我好了不直一倍。。。 林严 风衣 斩影下3路 ？？竹 神鬼 贤哲。。",
			"铁道部用日本技术",
			"老牛家的战争",
			"广东 英语四、六级 公务员考试 代考 枪手",
			"你是我的人了，嫁给我吧！",
			"（\"夏勇\" \"\"国家保密局\" \"\"智囊\"）OR（\"夏勇\" \"\"中办\"",
			"tt+ny"
			
			
	};
	
	
	
	public void getPartsOfSegTest(){
		String lines[] = {
			"av女优激情图片",
			"骑士 女优名 上马宏",
			"6号周日阳西沙扒湾泡清澈的山泉海水 蜒粑魃？ 海水 北咎?最后由 随风cwf 于 2009-8-30 13:04 编辑 ？？俺乔湾位于阳江地区",
			"td class=r1> 您是不是要找：<b><a href=?q=%C2%ED%C2%AC%B4%EF&py=1>马卢达",
			"(你好呀！23）",
			"6段。。。 装备到是比我好了不直一倍。。。 林严 风衣 斩影下3路 ？？竹 神鬼 贤哲。。",
			"铁道部用日本技术",
			"老牛家的战争",
			"广东 英语四、六级 公务员考试 代考 枪手",
			"你是我的人了，嫁给我吧！",
			"（\"夏勇\" \"\"国家保密局\" \"\"智囊\"）OR（\"夏勇\" \"\"中办\"",
			"tt+ny",
		};
		SplitSentence_seg ss = new SplitSentence_seg();
//		UnigramSeg seg = new UnigramSeg();
		UnigramSeg seg = UnigramSeg.getSeg();
		for(int i = 0; i < lines.length; i++){
			ArrayList<String> words = seg.getSegWordList(lines[i]);
			System.out.println("words: "+words);
			ArrayList<String> feas = ss.getAllFeatures_seg(lines[i], seg);
			System.out.println("feas: "+feas);
		}
	}
	
	public void getPhrasesFromOneLineWithOutUselessSignTest(){
		SplitSentence_seg ss = new SplitSentence_seg();
//		UnigramSeg seg = new UnigramSeg();
		UnigramSeg seg = UnigramSeg.getSeg();
//		seg.setSplitSign(UnigramSeg.splitSign);
		
		for(int i = 0; i < keys.length; i++){
			String segLine = seg.segment(keys[i]);
			System.out.println("segLine: "+segLine);
			System.out.println(ss.getPhrasesFromOneLineWithOutUselessSign(segLine));
		}
		
	}
	
	
	
	
	
	public void getAllFeatures_posTest(){
		SplitSentence ss = new SplitSentence();
		for(int i = 0; i < keys.length; i++){
			ArrayList<String> intMap = ss.getAllSubkeysMergeNumLetter(keys[i]);
			System.out.println(keys[i]+"\n"+intMap);
		}
	}
	
	public void getSegPosTest(){
		String lines[] = {
				"av女优激情图片",
				"骑士 女优名 上马宏",
				"6号周日阳西沙扒湾泡清澈的山泉海水 蜒粑魃？ 海水 北咎?最后由 随风cwf 于 2009-8-30 13:04 编辑 ？？俺乔湾位于阳江地区",
				"td class=r1> 您是不是要找：<b><a href=?q=%C2%ED%C2%AC%B4%EF&py=1>马卢达",
				"(你好呀！23）",
				"6段。。。 装备到是比我好了不直一倍。。。 林严 风衣 斩影下3路 ？？竹 神鬼 贤哲。。",
				"铁道部用日本技术",
				"老牛家的战争",
				"广东 英语四、六级 公务员考试 代考 枪手",
				"你是我的人了，嫁给我吧！",
				"（\"夏勇\" \"\"国家保密局\" \"\"智囊\"）OR（\"夏勇\" \"\"中办\"",
				"tt+ny",
			};
		SplitSentence_seg ss = new SplitSentence_seg();
//			UnigramSeg seg = new UnigramSeg();
			UnigramSeg seg = UnigramSeg.getSeg();
			for(int i = 0; i < lines.length; i++){
				Set<Integer> list = ss.getSegPos(lines[i], seg);
				ArrayList<String> parts = seg.getSegWordList(lines[i]);
				System.out.println(lines[i]);
				System.out.println(parts);
				System.out.println("list: "+list);
				
			}
	}
	
	public void test(){
//		double a = 0.99;
//		double b = 0.999;
//		
//		double max = 0;
//		double maxc = 0;
//		
//		for(int i = 0; i < 100000; i++){
//			double c = i*0.01;
//			double div = Math.pow(b, c)-Math.pow(a, c);
//			System.out.println("c/div: "+c+"/"+div);
//			if(div > max){
//				max = div;
//				maxc = c;
//			}
//		}
//		System.out.println("maxc/max: "+maxc+"/"+max);
//		System.out.println(Math.pow(a, maxc));
//		System.out.println(Math.pow(b, maxc));
		
		String line = "wa[(nihao";
		
		String ss[] = line.split(NLPTools.SENTENCEENDS_log);
		
		System.out.println(ss[0]);
		
	}
	
	public void getAllFeatures_segTest(){
//		UnigramSeg seg = new UnigramSeg();
		UnigramSeg seg = UnigramSeg.getSeg();
		seg.maxUnseenWordLen = 1;
		SplitSentence_seg ss = new SplitSentence_seg();
		for(int i = 0; i < this.keys.length; i++){
			System.out.println(this.keys[i]+"\n"+ss.getAllFeatures_seg(this.keys[i], seg));
		}
	}
	
	public void getAllPartsTest(){
//		UnigramSeg seg = new UnigramSeg();
		UnigramSeg seg = UnigramSeg.getSeg();
		seg.maxUnseenWordLen = 1;
		SplitSentence_seg ss = new SplitSentence_seg();
		for(int i = 0; i < this.keys.length; i++){
			System.out.println(this.keys[i]+"\n"+ss.getAllParts(this.keys[i], seg));
		}
	}
	
	public void getFeaMapTest(){
//		UnigramSeg seg = new UnigramSeg();
		UnigramSeg seg = UnigramSeg.getSeg();
		seg.maxUnseenWordLen = 1;
		SplitSentence_seg ss = new SplitSentence_seg();
		for(int i = 0; i < this.keys.length; i++){
			System.out.println(this.keys[i]+"\n"+ss.getFeaMap(this.keys[i], seg));
		}
	}
	
	public void splitTest(){
		Set<Character> set = new HashSet<Character>();
		
		set.add(' ');
		
		for(int i = 0; i < keys.length; i++){
			ArrayList<String> lines = SplitSentence.splitWithSignInCharSet(keys[i], set);
			
			System.out.println("keys[i]: "+keys[i]);
			System.out.println("lines: "+lines);
		}
		 
	}
	
	public void getPartsWithOutUselessSignTest(){
		for(int i = 0; i < keys.length; i++){
			Set<String> lines = new HashSet<String>();
			SplitSentence_seg.getPartsWithOutUselessSign(lines, keys[i]);
			
			System.out.println("keys[i]: "+keys[i]);
			System.out.println("lines: "+lines);
		}
	}
	
	
	
	public static void main(String args[]){
		SplitSentenceTest sst = new SplitSentenceTest();
//		sst.getAllFeturesTest();
//		sst.getPartsOfSegTest();
//		sst.test();
//		sst.getPhrasesFromOneLineWithOutUselessSignTest();
//		sst.splitTest();
//		sst.getPartsWithOutUselessSignTest();
//		sst.getAllFeatures_MapTest();
//		sst.getAllFeatures_Map_fastTest();
//		sst.getAllFeatures_posTest();
//		sst.getSegPosTest();
//		sst.getAllFeatures_segTest();
//		sst.getFeaMapTest();
//		sst.getAllPartsTest();
	}
}
