import java.util.ArrayList;
import java.util.List;

import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class RawTopKQuery {
//	private static DLDELogger logger = new DLDELogger();
	private QueryEnvironment env;
	
	public RawTopKQuery(){
		env = new QueryEnvironment();
	}
	
	public List<ParsedDocument> query(String query,int topK) throws Exception{
		List<ParsedDocument> list=new ArrayList<ParsedDocument>(servers.length*topK);
		ParsedDocument[] tmpDocs=null;
		for(String server: servers){
			tmpDocs=null;
			tmpDocs=query(server,query,topK);
			if(tmpDocs!=null)
				for(ParsedDocument doc:tmpDocs){
					list.add(doc);
				}
		}
		return list;
	}
	
	/**
	 * 目前还在建索引，所以只开放9872这个
	 */
	String[] servers={
//			"10.1.0.171:9871",// over
//			"10.1.0.127:9872",
//			"10.1.0.127:9873",
//			"10.1.0.171:9874",//over
//			"10.1.0.171:9875",//over
//			"10.1.0.171:9876",//over
//			"10.1.0.171:9877",//over
			"10.1.0.171:9878",
//			"10.1.0.171:9879",
//			"10.1.0.171:9880"
			};
	
	

	private ParsedDocument[] query(String searchServer, String query, int topK) throws Exception {
		ScoredExtentResult[] results;
//		String names[];
		ParsedDocument[] pdocs=null;
		try{
		env.addServer(searchServer);
		}catch(Exception e){
			System.out.println("can't connect to server : "+ searchServer);
			return null;
		}
		results = env.runQuery(query, topK);
//		names = env.documentMetadata(results, "docno");

		int docs[] = new int[results.length];
		for (int i = 0; i < results.length; i++) {
//			logger.info(names[i] + " " + results[i].score + " "
//					+ results[i].begin + " " + results[i].end + " id:"
//					+ results[i].document);
			docs[i] = results[i].document;
		}
		pdocs = env.documents(docs);
		return pdocs;
	}
	
	public void  close(){
		try {
			env.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
