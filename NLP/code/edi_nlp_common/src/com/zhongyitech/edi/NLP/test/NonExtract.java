package com.zhongyitech.edi.NLP.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.MergeSegments;

public class NonExtract {

	public static void main(String[] args) {
//		String path = "E:\\testdata\\part-r-00000_2.5w_comms";
		String path = "E:\\testdata\\comms_14w_20151209";
		String str = null;
		try {
			str = IoUtil.readTxt(path);
			System.out.println("字数："+str.length());
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("now do segment...");
		List<Term> list = ToAnalysis.parse(str.replaceAll(" +", " "));
		System.out.println("词数："+list.size());
//		list = MergeSegments.mergeNonEnNum(list);
//		list = MergeSegments.mergeEnNum(list);
//		System.out.println("合并后词数："+list.size());
		Map<String,Integer> map = new HashMap<>();
		map.clear();
//		String tmp = new String();
//		int flag = 0;
		Integer v;
		for(Term t :list){
			if(t.toString().length()>1 && t.toString().indexOf("/")!=-1){
				String[] tempsplit = t.toString().split("/");
//				if(tempsplit[1].equals("en") || tempsplit[1].equals("m")){
//					tmp= tmp+tempsplit[0];
//					flag = 1;
//					continue;
//				}else if(flag == 1){
//					flag = 0;
//					if( (v = map.get(tmp)) !=null){
//						map.put(tmp, v+1);
//					}else{
//						map.put(tmp, 1);
//					}
//					tmp = new String();
//					continue;
//				}
				
				if(tempsplit[1].equals("n")){
					if( (v = map.get(tempsplit[0])) !=null){
						map.put(tempsplit[0], v+1);
					}else{
						map.put(tempsplit[0], 1);
					}
				}
				
//				if(tempsplit[1].equals("UserDefineMerge")){
//					if( (v = map.get(tempsplit[0])) !=null){
//						map.put(tempsplit[0], v+1);
//					}else{
//						map.put(tempsplit[0], 1);
//					}
//				}
			}
		}
		
		List<String> ls = new ArrayList<>();
		List<Integer> li = new ArrayList<>();
		
		for(Entry<String, Integer> e :map.entrySet()){
			int csize = li.size();
			if(csize==0){
				li.add(e.getValue());
				ls.add(e.getKey());
			}else{
				for(int i = 0 ; i< csize; i++){
					if(e.getValue()<li.get(i) && i!=csize-1){
						continue;
					}else if(i==csize-1){
						li.add(e.getValue());
						ls.add(e.getKey());
						break;
					}else{
						li.add(i, e.getValue());
						ls.add(i, e.getKey());
						break;
					}
				}
			}
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<li.size();i++){
			sb.append(ls.get(i));
			sb.append("\t");
			sb.append(li.get(i));
			sb.append("\n");
		}
		
		try {
			IoUtil.writeToText(sb.toString(), "E:\\ttt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("done!");
	}
	
	

}
