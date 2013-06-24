package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;

/**
 * 根据不同的queryid取出其对应的documents
 * 
 * @author ChenJie
 *
 */
public class QueryResource {
	
	private static String indexPath = "/data/webtrec/index/";

	public static List<Document> getByQueryId(int qid){
		List<Document> docs=new ArrayList<Document>();
		try {
			MMapDirectory dir=new MMapDirectory(new File(indexPath));
			IndexReader ir=IndexReader.open(dir, true);
			IndexSearcher searcher=new IndexSearcher(ir);
			Query q=new TermQuery(new Term("qid",qid+""));
			
			
			TopDocs topDocs=searcher.search(q, 10000);
			
			ScoreDoc[] scoreDocs=topDocs.scoreDocs;
			
			Document doc=null;
			for(ScoreDoc sdoc:scoreDocs){
				int id=sdoc.doc;
				doc=searcher.doc(id);
				docs.add(doc);
			}
			searcher.close();
			ir.close();
			dir.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docs;
	}
	public static void main(String[] args) {
		
	}

}
