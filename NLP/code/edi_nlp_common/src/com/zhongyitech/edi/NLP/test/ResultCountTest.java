package com.zhongyitech.edi.NLP.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

public class ResultCountTest {

	public static void main(String[] args) throws Exception {
		
//		long s = System.currentTimeMillis();
//		FilterModifWord.insertStopWord("但是") ;
//		FilterModifWord.insertStopWord("的") ;
//		FilterModifWord.insertStopWord("了") ;
//		FilterModifWord.insertStopWord("吧") ;
//		List<Term> list = ToAnalysis.parse("苹果的屏幕好，但是价格太贵了吧");
//		list = FilterModifWord.modifResult(list) ;
////		long e = System.currentTimeMillis();
//		
//		System.out.println(list);
//		System.out.println(e-s);
		
//		String st = IoUtil.readTxt("corpus/comms_14w_20151209");
//		String[] c = st.split("\n");
		
		String path = "E:\\comments.txt";
		String product = "iphone";
		String[] c = extractComments(path).split("\n");
//		int n = 10;
//		String str = randomComments(c,n);
//		String[] strs = str.split("\n");
		
		List<Integer> sentNo = new ArrayList<Integer>();
		int t = 0;
		List<Opinion> list = new ArrayList<Opinion>();
		long s = System.currentTimeMillis();
		for(String str :c){
			List<Opinion> templ = OpMiningUtil.doSa(str, product);
			list.addAll(templ);
			t++;
			int tsz = templ.size();
			while(tsz-->0)
				sentNo.add(t);
		}
		long e = System.currentTimeMillis();
		OpRes o = new OpRes();
		List<OpRes> opres = new ArrayList<OpRes>();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("op" + i + ":" + "\r\n\t产品：" + list.get(i).getProduct().getContent()
					+ "\r\n\t评论对象一级分类词：" + list.get(i).get_aspe()
					+ "\r\n\t评论对象start：" + list.get(i).getAspect().getStart_index()
					+ "\r\n\t评论对象end：" + list.get(i).getAspect().getEnd_index()
					+ "\r\n\t评论对象二级分类词：" + list.get(i).getAttribute().getContent()
					+ "\r\n\t评论对象种类：" + list.get(i).get_attr()
					+ "\r\n\t情感词：" + list.get(i).getSentiment().getContent()
					+ "\r\n\t否定词：" + list.get(i).getNeg_words()
					+ "\r\n\t情感分类：" + list.get(i).getSentiment().getSentiment_category()
					+ "\r\n\t观点句位置：[" + list.get(i).getOp_start_index() + "," + list.get(i).getOp_end_index() + "]"
					+ "\r\n\t观点句：" + list.get(i).getOp_sent()
					);
			o = setCountResult(list,sentNo,i);
			opres.add(o);
		}
		String sample_path = "E:\\commentsops.txt";
		List<OpRes> sample = getSample(sample_path);
		ResultCount res = compare(opres,sample);
		System.out.println("recall:"+res.getRecall()+"\nprecision:"+res.getPrecision());
		System.out.println(e-s);
	}

	private static ResultCount compare(List<OpRes> opres, List<OpRes> sample) {
		ResultCount rc = new ResultCount();
		// 发现观点
		int op = 0;
		// 观点和情感都正确
		int count = 0; 
		
		int tag1[]=new int[111];
		int tag2[]=new int[111];
		Arrays.fill(tag1, 999);
		Arrays.fill(tag2, 999);
		
		int jtag = 0;
		boolean noaspflag = false;
		for(int i = 0;i<sample.size();i++){
			for(int j = jtag;j<opres.size();j++){
				if(opres.get(j).getWord()==null)
					noaspflag = true;
				if(sample.get(i).getSentId()==opres.get(j).getSentId()&&(sample.get(i).getWord().equals(opres.get(j).getWord())||(noaspflag&&sample.get(i).getAspect().equals(opres.get(j).getAspect())))){
					jtag=j+1;
					op++;
					tag1[i]=-j;
					tag2[j]=-i;
					if(sample.get(i).getValue()==opres.get(j).getValue()){
						count++;
						tag1[i]=j;
						tag2[j]=i;
					}
					break;
				}
			}
		}
		
		float recall = (float)count/sample.size();
		float precision = (float)count/opres.size();
		
		float oprecall = (float)op/sample.size();
		float opprecision = (float)op/opres.size();
		
		float saprecision = (float)count/op;
		
		float fscore = 2*recall*precision/(recall+precision);
		
		rc.setRecall(recall);
		rc.setPrecision(precision);
		
		return rc;
	}

	private static List<OpRes> getSample(String sample_path) throws Exception {
		List<OpRes> ops = new ArrayList<OpRes>();
		String s = IoUtil.readTxt(sample_path);
		String[] lines = s.split("\n");
		
		for(String l :lines){
			String[] es = l.split("\t");
			OpRes op = new OpRes();
			op.setSentId(Integer.parseInt(es[0]));
			op.setWord(es[1]);
			op.setAspect(es[1]);
			op.setValue(Integer.parseInt(es[2]));
			
			ops.add(op);
		}
		return ops;
	}

	private static OpRes setCountResult(List<Opinion> list, List<Integer> sentNo, int i) {
		OpRes op = new OpRes();
		op.setSentId(sentNo.get(i));
		op.setWord(list.get(i).getAspect().getContent());
//		op.setAspect(list.get(i).get_aspe());
		op.setAspect(list.get(i).get_attr());
		op.setValue(Integer.parseInt(list.get(i).get_opsa()));
		
		return op;
	}

	private static String randomComments(String[] c, int n) {
		StringBuffer res = new StringBuffer();
		Random random = new Random();
		while(n-->0){
			res.append(c[random.nextInt(c.length)]);
			res.append("\n");
		}
		return res.toString();
	}

	private static String extractComments(String path) throws Exception {
		StringBuffer res = new StringBuffer();
		String str = IoUtil.readTxt(path);
		String[] sl = str.split("\n");
		for(String s : sl){
			String[] temp = s.split("[\t]");
			res.append(temp[5]+"\n");
		}
		return res.toString();
	}

}
