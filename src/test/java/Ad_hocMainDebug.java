import java.util.List;

import junit.framework.TestCase;
import lemurproject.indri.ParsedDocument;
import lucene.IndexTopKResults;

import org.junit.Test;

import edu.bit.dlde.utils.DLDELogger;

public class Ad_hocMainDebug extends TestCase {
	private DLDELogger logger = new DLDELogger();

	@Test
	public void testAd_hoc() {
		String query = "beijing";
		int queryId = 156;

		/*
		 * 从索引服务器取出索引
		 */
		RawTopKQuery rawQuery = new RawTopKQuery();
		IndexTopKResults indexTopK = new IndexTopKResults();
		try {
			List<ParsedDocument> parsedDocumentList = rawQuery.query(query,
					10);
			/*
			 * 索引该query原始的文档
			 */
			indexTopK.index_for_debug(parsedDocumentList, queryId);

		} catch (Exception e) {
			logger.error("can't parse the query!!! id:" + queryId + " -- "
					+ query);
			e.printStackTrace();
		}

	}
}
