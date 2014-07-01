package strproc;

import static m.util.PrintUtil.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlProcess {

	public static void main(String[] args) throws UnsupportedEncodingException {
		regReplace();
	}

	public static void whiteUrl() throws UnsupportedEncodingException {
		p(cleanUrl("//www.xx.xx/abc.jsp?"));
		p(cleanUrl("//www.xx.xx?abc.jsp?"));
		p(cleanUrl("http://www.xx.xx/abc.jsp?a=b&c=d"));
		p(cleanUrl("www.xdsdfx.xx?abc.jsp?"));
		p(cleanUrl("www.xfasdgx.xx/abc.jsp?"));
		p(cleanUrl("http://www.xx.xx/"));
		p(Pattern.matches("[0-9|a-z|A-Z]*", "123aB��"));
		p(Pattern.matches("[a-z|A-Z]{3}[0-9]{3}[a-z|A-Z]{3}", "aaa1423aBc"));
		p(Pattern.matches("[1-9|\\.]*", "1.1.1.1"));
		p("--");
		p(Pattern.matches(".*\\.taobao.com", "abc.taobao.com"));
		p(Pattern.matches(".*\\.taobaocdn.com", "abc.taobaocdn.com"));
		p(Pattern.matches(".*\\.alimama.com", ""));
		p(Pattern.matches(".*\\.tmall.com", ""));
		p(Pattern.matches(".*\\.", ""));
		p(Pattern.matches(".*\\.", ""));
		p(Pattern.matches("", ""));
		p(Pattern.matches(".*\\.net\\.tf", "abc.net.tf"));
		p(System.currentTimeMillis() / 1000);
		String urlWihte = "http://shop65187688.taobao.com&nbsp;&lt&amp";
		String[] ends = new String[] { "&nbsp", "&lt", "&nbsp;", "&lt;",
				"&amp", "&amp;" };
		p(URLDecoder
				.decode("http://taodaovs1.%6E%65%74%2E%74%66/member/item.htm?id=00012����",
						"gbk"));

		boolean loop = true;
		while (loop) {
			loop = false;
			for (String end : ends) {
				if (urlWihte.endsWith(end)) {
					urlWihte = urlWihte.substring(0,
							urlWihte.length() - end.length());
					loop = true;
				}
			}
		}
		p(urlWihte);
	}

	public static void regReplace() {

		String patstr = "<[^>]+>";
		Pattern pat = Pattern.compile(patstr, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher("<a>test</aasdf>");
		p(mat.replaceAll(""));
	}

	public static String cleanUrl(String url) {

		if (url == null)
			return null;
		String[] prefixes = new String[] { "http://", "http:", "/" };
		while (true) {
			boolean loop = true;

			for (String prefix : prefixes) {

				loop = false;
				if (url.startsWith(prefix)) {
					url = url.substring(prefix.length());
					loop = true;
				}
			}
			if (!loop)
				break;
		}

		int minIdx = Integer.MAX_VALUE;
		String[] strs = new String[] { "\\", "/", "?" };
		for (String str : strs) {
			int i = url.indexOf(str);
			if (i != -1 && i < minIdx) {
				minIdx = i;
			}
		}

		if (minIdx != Integer.MAX_VALUE) {
			url = url.substring(0, minIdx);
		}

		return url;
	}

	public static Set<String> cleanUrls(Set<String> urls) {

		Set<String> resSet = new HashSet<String>();
		if (urls == null)
			return resSet;
		for (String url : urls) {
			urls.add(cleanUrl(url));
		}

		return urls;
	}

}
