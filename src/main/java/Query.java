import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;
import edu.bit.dlde.utils.DLDELogger;


public class Query {

	private static DLDELogger logger=new DLDELogger();
	
	/**
	 * @param args
	 * Jul 23, 2012
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		QueryEnvironment env= new QueryEnvironment();
//		String myIndex="/data/indri/index2";
		String myServer="127.0.0.1:9872";
//		String myQuery="#combine(\"403b\" #not(<porn pussy>) #not(\"russellbailyn\".url))";
//		String myQuery="\"403b\" #not(<porn pussy>) ";
		String myQuery="\"403b\"";
//		String myQuery="russellbailyn";
		
		ScoredExtentResult[] results;
		
		String names[];
		
//		env.addIndex(myIndex);
		env.addServer(myServer);
		results=env.runQuery(myQuery, 100);
		
		names=env.documentMetadata(results, "docno");
		
		int docs[]=new int[results.length];
		for(int i =0; i < results.length;i++){
			logger.info(names[i]+" "+results[i].score+" "+results[i].begin+" "+results[i].end+" id:"+results[i].document);
			docs[i]=results[i].document;
		}
//		int docs[]={18787164};
		ParsedDocument[] pdocs= env.documents(docs);
		for(ParsedDocument pdoc:pdocs){
//			logger.info(pdoc.text);
//			logger.info(pdoc.content);
//			System.out.println(pdoc.metadata);
			byte[] bytes=(byte[]) (pdoc.metadata.get("url"));
			System.out.println(new String(bytes));
		}
		env.close();
	}

}
