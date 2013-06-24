package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 读出所有的查询
 * @author ChenJie
 *
 */
public class QueryReader {


	private static String queryExpanPath="/home/coder/workspace/mavenSpace/webtrack/src/main/resources/query_expan.txt";
	
	public static List<QueryModel> readAll(){
		List<QueryModel> queryList = new ArrayList<QueryModel>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(queryExpanPath)));
			String line=br.readLine();
			QueryModel model=null;
			while(line!=null){//152:angular cheilitis,#1(angular cheilitis)
				if(line.length()<1){
					break;
				}
				int queryNum=Integer.parseInt(line.substring(0,3));
				String query=line.substring(4,line.lastIndexOf(","));
				String queryExpan=line.substring(line.lastIndexOf(",")+1);
				System.out.println(queryNum+ " -- "+query+" -- "+queryExpan);
				model=new QueryModel(queryNum,query,queryExpan);
				queryList.add(model);
				line=br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return queryList;
	}
	
	public static void main(String[] args){
		readAll();
	}
}
