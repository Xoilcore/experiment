package com.taobao.feng.play.ctu;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.taobao.kfc.core.KfcException;
import com.taobao.kfc.core.dic.node.DartsManager;
import com.taobao.kfc.core.search.SearchResult;
import com.taobao.kfc.core.search.SearcherService;
import com.taobao.kfc.core.search.query.FuzzyIndexOf;
import com.taobao.kfc.core.search.query.Query;
import com.taobao.kfc.fasttext.psoriasis.SkipDarts;

import static com.taobao.feng.tools.PrintUtil.*;

public class KfcLocal {

	private static final Log logger = LogFactory.getLog("script");

	public boolean compare(String content, String words) throws KfcException {
		if (content == null || words == null || content.isEmpty()
				|| words.isEmpty())
			return false;
		String storeKey = "" + words.hashCode();
		build(words, storeKey);
		Query query = new FuzzyIndexOf();

		query.setFirsApply(storeKey);
		query.setContent(content);
		SearchResult sr = searcherService.search(query);
		return sr.isMatchedKeyword();
	}

	private void build(String words, String storeKey) {

		Optional<SkipDarts> dartObj = DartsManager.getDartsManager().getDarts(
				storeKey);
		if (dartObj.isPresent())
			return;

		String[] keywords = words.split("\n");
		Set<String> wordsSet = Sets.newHashSet();
		for (String keyword : keywords) {
			wordsSet.add(keyword);
		}

		logger.warn(String.format("KFCLOCAL:构建[%s]", storeKey));
		// p(String.format("KFCLOCAL:构建[%s]", storeKey));
		DartsManager.getDartsManager().build(storeKey, wordsSet);
	}

	static SearcherService searcherService = new SearcherService();

	static String text = "cc78414d8a0fbf2b630bf679bbc455e13\nc00000000000000000000000017296192\ncf3aa190307b76a0a9ae2f72c170b3bf6\ncca00a759e526854656055f006075699f\nc539882ec8ef1ff3098636aea9694b33d\ncd77f1b238ab61482430dc4c71f1153ec\nc2339b3d372c8a154603e745fb05a4ad9\nc69c3ddb460961bb0d38644528277b4cb\ncd273531dcff58dc1269d87f4a6d8132d\nc2514ab5fb8bf4fb3aa2881233623ba00\nc92942b92edb7116bef2384718f9318ce\nc34e245d3098eb7e645fbf0938cc3a629\nc00000000000000000000000001978343\ncf5e1f2bafa1d27e6afb73af5b4b14649\nc6914731437460c686f49b0f1d92ffdce\nccd4e1f3e10bbdb7d9c6fd631d8c32b7d\nc25ca422ab81e6eef9feec538b3ae7f79\nc905f8ce1246cd82166fc33ede047f4a3\ncb4442f01a17c75196fd9112e3696b080\nc3f12b5b4c9ef6342674fcd0d38837347\nc92bbe7f5ea1681ec1c8de55cce99617b\ncd9fcc3ea03420189c400e2c5a187f436\nc00000000000000000000000032104317\nc00000000000000000000000001978343\nced680407e6071780d977a1371646dabb\nc2ea22941a14ef383b9446aa5ec798bf5\nc68366ba7434b510cbe51402054bd1751\nc337df80207240c1f143a6fabb886c056\nc9124e7c4312aa43e93cee8769c7b73ed\ncbd90c737ec9bce966b587b26ecd117cd\nc98acdb94481cfbd6e52cfae403d66ee2\nccd28dfdc87637ffc23fc1f2fe067c0b8\nc67e8858ab7a3914cd25e404ea76b6b88\ncbd90c737ec9bce966b587b26ecd117cd\np00000000000000000000000032592447\nc2eee486578890eade1359a2d892fbd94\nc00000000000000000000000000663273\ncd405de33f30b313174756886b8d0d841\nc8ca926ade76831bf59f33664f61b603a\nc00000000000000000000000024635543\nc3abb86e992467cf8dc4e37e710814707\ncd2880b70e86b2a35c7169703c0ccd804\ncb891292754d98f80856d20e5e615c417\nv00000000000000000000000008525527\nv1dad83f976605b3bb2d7bd9ec221605b\nvaeb1ba7e796af84a651d1fa00dad0be1\nv00000000000000000000000020061708\nv1d0649ed436a25c9a9284a04ec5bea19\ncabcd73a763f3cbf5e8de6e478f7d4ab3\nccaef493ed78a14ee08222ffc4370c433\nc9124e7c4312aa43e93cee8769c7b73ed\nc00000000000000000000000017296192\ncf3aa190307b76a0a9ae2f72c170b3bf6\ncc78414d8a0fbf2b630bf679bbc455e13\nc2339b3d372c8a154603e745fb05a4ad9\ncc78414d8a0fbf2b630bf679bbc455e13\nc539882ec8ef1ff3098636aea9694b33d\ncca00a759e526854656055f006075699f\nc00000000000000000000000001978343\nc274934c73d1682d65f3894c1f1de4390\nc46ff7e22bf024578b0090273f34888fe\ncf3aa190307b76a0a9ae2f72c170b3bf6\nc1774193dfb78429d49f340e6de83c099\nc00000000000000000000000011675548\nc1774193dfb78429d49f340e6de83c099\nc0edf3a9147ce70ad193792d8e7e12019\nc75e79d310de33ff4896845987c8deef8\ncf7e05fb84dccb6f3ec835c0478f42fa5\nc5575b752dd72a850d2130bda491ca041\nc978eedc45ce8b4602ce537e19f15c20b\nce0329ef79f5aa5bed1ebcaf0d8288f35\nc1c7b561550c8633f810719f2df001d95\ncd62aeb2c458df0f01921918664bf3910\nc679c9caa97002c57f672f545b33d066a\nc00000000000000000000000017296192\ncdf4f8d87e2a3845a6ae6544a3c3aee77\ncd77f1b238ab61482430dc4c71f1153ec\nc6b7d97dbc4bced0cb20f8ac19bec1259\nc00000000000000000000000017296192\nc4451f2a79b1fa657887abd6fcb4337e8\nc00000000000000000000000011998190\nc1d8790c03bd5f74718ccad78c1834961\nc00000000000000000000000011998190\ncdf4f8d87e2a3845a6ae6544a3c3aee77\ncf498bacad8d6b971795324bc91c3d6f2\nc2339b3d372c8a154603e745fb05a4ad9\ncd77f1b238ab61482430dc4c71f1153ec\nc00000000000000000000000017296192\ncb16797b4721fc10d2fb6dc0a93d4e2fb\nc58ada8ec7f6ad35a09a1439fb3d11eed\nc9feaf4166c2473c9954a79206915adf8\ncd77f1b238ab61482430dc4c71f1153ec\nc51e219b5f193f575cf12969693ec3f5a\nc9124e7c4312aa43e93cee8769c7b73ed\nc4aced70889cccf446d838e01eed4bbe4\ncc78414d8a0fbf2b630bf679bbc455e13\nc11cd6b868831ec28d15e4deabab6bf84\nc235b615454e4e513a42b4c4011fc6e81\nc8e32f75194306c083f109d489ad73a32\nc274934c73d1682d65f3894c1f1de4390\nc33c4b0f2be64d1b35a106adf2a78f61d\nc8e32f75194306c083f109d489ad73a32\ncc6e73c7aec74e086c212d58bb7811999\nc274934c73d1682d65f3894c1f1de4390\nc0d51988d733e31629680d7b5cf7cfc94\nc978eedc45ce8b4602ce537e19f15c20b\ncf3aa190307b76a0a9ae2f72c170b3bf6\nc140b8fef05b7d4f199f96d30b41baf37\nc00000000000000000000000018056309\nc112ca5f6a3560f24b551074fe85337e6\nc84b6b18d26e210b41717694aaa2ab5d0\nc3b96caedac48d72dd3013ff8202ddf3d\ncf7e05fb84dccb6f3ec835c0478f42fa5\nce0329ef79f5aa5bed1ebcaf0d8288f35\nc01d360658eb14b97abadfaace4b32251\ncc6e73c7aec74e086c212d";

	private boolean compare2(String content, String text) {
		String[] arr = text.split("\n");
		Set<String> set = Sets.newHashSet();
		for (String str : arr) {
			set.add(str);
		}
		for (String str : arr) {
			if (content.indexOf(str) > -1)
				return true;
		}
		return false;
	}

	public static void main(String[] args) throws KfcException {

		KfcLocal local = new KfcLocal();
		int tryTimes = 1000;
		long tmis = System.currentTimeMillis();
		for (int i = 0; i < tryTimes; i++) {
			local.compare("cf3aa190307b76a0a9ae2f72c170b3bf6", text);
		}
		pf("timeuser=%d\n", System.currentTimeMillis() - tmis);
		tmis = System.currentTimeMillis();
		for (int i = 0; i < tryTimes; i++) {
			local.compare2("cf3aa190307b76a0a9ae2f72c170b3bf6", text);
		}
		pf("timeuser=%d\n", System.currentTimeMillis() - tmis);
	}

}