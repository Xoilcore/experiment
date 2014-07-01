package com.taobao.feng.play.lucene;

import static com.taobao.feng.tools.PrintUtil.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.taobao.feng.play.biz.TextSample;


public class LuceneTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		new LuceneTest().run();
	}

	public void run() throws Exception {
		p("build start");
		buildIndex();
		p("build end");
		search();
		p("search finish");

	}

	public Analyzer getAnalyzer() {
		Analyzer analyzer = null;
		analyzer = new IKAnalyzer();
		return analyzer;
	}

	public void buildIndex() throws Exception {

		directory = new RAMDirectory();

		Analyzer analyzer = getAnalyzer();

		parser = new QueryParser(Version.LUCENE_CURRENT, "content", analyzer);

		IndexWriter writer = new IndexWriter(directory, analyzer,
				new MaxFieldLength(20));

		Document doc = null;
		List<String> texts = TextSample.getPart(TextSample.WWDIALOG, 1000);
		for (String str : texts) {
			doc = new Document();
			doc.add(new Field("content", str, Store.YES, Index.ANALYZED));
			writer.addDocument(doc);
		}
		writer.close();
	}

	private Directory directory = null;
	private QueryParser parser;

	public void search() throws CorruptIndexException, IOException,
			ParseException {
		IndexSearcher searcher = new IndexSearcher(directory);
		Query query = parser.parse("店铺 旺旺");

		TopDocs res = searcher.search(query, 10);
		IndexReader indexReader = IndexReader.open(directory);
		for (ScoreDoc doc : res.scoreDocs) {
			Document curDoc = indexReader.document(doc.doc);
			p(curDoc.toString());
		}
	}

}
