import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class CrawlThread extends Thread{
	    private String topic;
		private File fileName;
		private String url;
		private int i;
		public CrawlThread(File fileName, String topic, String url, int i){
			this.topic = topic;
			this.fileName = fileName;
			this.url = url;
			this.i = i;
		}		


		public void run(){
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8");
			//System.out.println("query url: "+url);
			
			HttpGet request = new HttpGet(url);
			HttpResponse response = null;;
			try {
				System.out.println("Begin Crawling topic : ---" + topic + "---" + i + "--- url: ---"+url+" ...");
				response = client.execute(request);
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
				
				FileWriter fw = new FileWriter(fileName);
				fw.write(body);
				fw.close();
				System.out.println("Crawl url: "+url + "--- done!");
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(fileName+"  "+i+"  exception!!!!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(fileName+"  "+i+"  exception!!!!!");
			}
		}
		
		
		
		public File getFileName() {
			return fileName;
		}


		public void setFileName(File fileName) {
			this.fileName = fileName;
		}


		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}


		public String getTopic() {
			return topic;
		}


		public void setTopic(String topic) {
			this.topic = topic;
		}
		
		
	}