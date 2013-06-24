import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lucene.QueryModel;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.Test;

import edu.bit.dlde.feature.FeatureArray;

/**
 * 测试一下FeatureArray好不好使
 * 
 * @author lins
 * @date 2012-8-3
 **/
public class FeatureTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	@Test
	public void testFeatureArray() throws CorruptIndexException, IOException {
		File idxDir = new File("/home/lins/data/index_10docs");
		IndexReader indexReader = IndexReader.open(SimpleFSDirectory
				.open(idxDir));
		Document doc = indexReader.document(1);
		System.out.println(doc.get("qid"));
//		System.out.println(doc.get("anchor"));
//		System.out.println(doc.get("title"));
//		List<Fieldable> a = doc.getFields();
//		System.out.println(a);
		FeatureArray features = FeatureArray.getInstacne(indexReader);
		HashMap<Integer, Double> map = features.processDocument(new QueryModel(1,"grille",""), 1);
		System.out.println(map.get(1));
	}

}
