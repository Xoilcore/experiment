package com.taobao.feng.play.weka;

import java.io.IOException;
import java.util.Iterator;

import com.taobao.feng.tools.WHelper;

import weka.core.tokenizers.Tokenizer;

public class IkTokenizer extends Tokenizer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3073341662174809268L;

	public String getRevision() {
		return "1.0";
	}

	@Override
	public String globalInfo() {

		return "base on ikAnalyser";
	}

	Iterator<String> itr;

	@Override
	public boolean hasMoreElements() {

		return itr.hasNext();
	}

	@Override
	public Object nextElement() {

		return itr.next();
	}

	@Override
	public void tokenize(String content) {
		try {
			itr = WHelper.ikSeg(content).iterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
