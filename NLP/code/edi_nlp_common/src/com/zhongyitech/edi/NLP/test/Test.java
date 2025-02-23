package com.zhongyitech.edi.NLP.test;

import java.util.List;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.util.IoUtil;
import com.zhongyitech.edi.NLP.util.OpMiningUtil;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		String str = IoUtil.readTxt("E:\\ttttttttttttttttttttttttt");
		
		long s = System.currentTimeMillis();
		List<Opinion> oplist = OpMiningUtil.doSa("充电慢，电池耐用，续航好，待机时间长，大小合适，拍照好，屏幕分辨率高，前置摄像头好，后置好，设计好，外形好，外观做工好，性价比高，价格低，耗电大", "iPhone");
		long e = System.currentTimeMillis();
		
		for (int i = 0; i < oplist.size(); i++) {
			System.out.println("op" + i + ":" + "\r\n\t产品：" + oplist.get(i).getProduct().getContent()
					+ "\r\n\t评论对象：" + oplist.get(i).getAspect().getContent()
					+ "\r\n\t评论对象start：" + oplist.get(i).getAspect().getStart_index()
					+ "\r\n\t评论对象end：" + oplist.get(i).getAspect().getEnd_index()
					+ "\r\n\t评论对象属性：" + oplist.get(i).getAttribute().getContent()
					+ "\r\n\t评论对象种类：" + oplist.get(i).get_aspe()
					+ "\r\n\t情感词：" + oplist.get(i).getSentiment().getContent()
					+ "\r\n\t否定词：" + oplist.get(i).getNeg_words()
					+ "\r\n\t情感分类：" + oplist.get(i).getSentiment().getSentiment_category()
					+ "\r\n\t观点句位置：[" + oplist.get(i).getOp_start_index() + "," + oplist.get(i).getOp_end_index() + "]"
					+ "\r\n\t观点句：" + oplist.get(i).getOp_sent()
					);
		}
		
		System.out.println(e-s);
	}
}
