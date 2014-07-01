package com.taobao.feng.play.biz;

import static com.taobao.feng.tools.PrintUtil.*;

import java.util.regex.Pattern;

public class RegTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		p(Pattern.matches(".*[\\.]{1}.+taobao.+[\\.]{1}.*", "abc.ataobaoc.org"));
		p(Pattern.matches(".*[\\.]taobao.+[\\.]{1}.*", "abc.taobaoc.com"));
		p(Pattern.matches(".*[\\.]{1}.+taobao[\\.]{1}.*", "abc.taobao.com"));
		p(Pattern.matches(".*[\\.]{1}.+(taobao)[\\.]{1}.*", "abc.taobao.com"));
		String url = "https://mapi.alipay.com/gateway.do?_input_charset=GBK&notify_url=hello&body=%A3%A8ID%3A+12805327%A3%A9%CD%F8%C9%CF%B3%E4%D6%B5&defaultbank=CCB&%2Fmy.jjwxc.net%2Fpay%";
		p(Pattern.matches(".*mapi.alipay.com\\/gateway\\.do.*notify_url=(?!(hello)|(you)|(ok)).*", url));
		p(Pattern.matches("((abc)|(bcd)|(def))", "def"),"xxx");
		p(Pattern.matches(".*((1783:true)|(1984:true)|(1808:true)).*", "asdf1808:truealdfj"),"2");
		p(Pattern.matches("。*好啊(?!(我们))", "我们好啊"));
		

	}

}
