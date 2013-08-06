import edu.bit.dlde.utils.DLDELogger;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;


public class IndriDocReader {
	private static DLDELogger logger=new DLDELogger();

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String myServer="10.1.0.149:9876";
		QueryEnvironment env= new QueryEnvironment();
		env.addServer(myServer);
		int docs[]={22};
		ParsedDocument[] pdocs= env.documents(docs);
		for(ParsedDocument pdoc:pdocs){
			logger.info(pdoc.text);
			logger.info(pdoc.content);
			System.out.println(pdoc.metadata);
			byte[] bytes=(byte[]) (pdoc.metadata.get("url"));
			System.out.println(new String(bytes));
		}
		env.close();
		
	}

}
