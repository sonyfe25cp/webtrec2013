import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;


public class GoogleCrawler {
	
	public List<String> readTopics(){
		List<String> topics = new ArrayList<String> ();
		String topicPath = "/home/yulong/store/webtrec/topics";
		File file = new File(topicPath);
		BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new FileReader(file));
			String str = "";
			while((str = reader.readLine()) != null)
			{
				System.out.println(str);
				topics.add(str);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return topics;
	}
	
	public void getSearchResult(List<String> topics) throws ClientProtocolException, IOException{
		for(String topic : topics)
		{
			String[] top = topic.split(":");
			String topicId = top[0];
			System.out.println("topicId: "+topicId);
			String topicContent = top[1];
			//用该方法获取google搜索结果时，提交的检索词之间必须用“+”连接
			String[] queryString = topicContent.split(" ");
			String query = queryString[0];
			for(int i=1;i<queryString.length;i++){
				query+="+"+queryString[i];
			}
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8");
			String url = "https://www.google.lt/search?num=50&safe=strict&q="+query;
			System.out.println("query url: "+url);
			
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			int status = response.getStatusLine().getStatusCode();
			String body = "";
			switch(status)
			{
			case 200:
				body = EntityUtils.toString(response.getEntity(), "UTF-8");
				break;
			case 404:
				System.out.println("404 error!");
				break;
			default :
				break;
			}//switch
			File file = new File("/home/yulong/store/webtrec/searchresult/" + topicId +".txt");
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			fw.write(body);
			fw.close();
			System.out.println("save topic "+topicId+" search result done!");
		}//for
	}
	
	public void getUrlContent(){
		String urlPath="/home/yulong/store/webtrec/url/";
		String urlContentPath = "/home/yulong/store/webtrec/urlcontent/";
		File f = new File(urlPath);
		File[] fList = f.listFiles();
		for(File file : fList)
		{
			String fileName = file.getName();
			fileName = fileName.replaceAll(".txt", "");
			String topic = fileName;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("----------Read Topic "+ fileName +"----------");
			List<String> urls = new ArrayList<String>();
			String str = "";
			try {
				while((str = br.readLine())!= null){
					System.out.println(str);
					urls.add(str);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int i = 1;
			//为每个topic创建文件夹
			File urlDir = new File(urlContentPath + File.separator + fileName);
			if(!urlDir.exists()){
				urlDir.mkdir();
			}
			for(String url : urls)
			{
				//如果topic下的某个url已经爬取过，则跳过本次爬取过程（在程序中断，重新运行后防止重复爬取）
				File urlContentFile = new File(urlDir.getAbsolutePath() + File.separator +  i + ".txt");
				try{
//					FileInputStream fs = new FileInputStream(urlContentFile);
//					long fileSize = fs.available();
//					fs.close();
					if(!urlContentFile.exists()){
						urlContentFile.createNewFile();
					}
					else{
						i++;
						continue;
					}
//					else if(fileSize > 10*1000){
//						i++;
//						continue;
//					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println(fileName + "  " + i +"  -----exception!!!");
				}
				
				CrawlThread crawl = new CrawlThread(urlContentFile, topic, url, i);
				long startTime = System.currentTimeMillis();
				crawl.start();
				//控制每次抓取的时间，若超过60秒，则自动放弃本次抓去，继续下一次
				
				while(crawl.isAlive()){
					long currentTime = System.currentTimeMillis();
					if((currentTime - startTime) > 60*1000){//如果时间超过60秒，则终止当前线程，并跳出while，继续执行下一次for循环
						crawl.stop();
						System.out.println("crawl topic" + fileName + "---" + i + "---" + "  url: " + url + "-----time out!!!");
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				i++;
			}//for
			System.out.println("Crawl topic : "+fileName + "-----------------------------------------------done!");
		}//for
	}

	
	public static void main(String[] args){
		GoogleCrawler gc = new GoogleCrawler();
//		List<String> topics = gc.readTopics();
//		gc.getSearchResult(topics);
		gc.getUrlContent();
	}
	
	
	
}
