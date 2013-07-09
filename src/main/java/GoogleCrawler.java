import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class GoogleCrawler {

	
	public static void main(String[] args) throws ClientProtocolException, IOException{
		HttpClient client= new DefaultHttpClient();

		String word = "pi";
		String url = "https://www.google.lt/search?num=100&safe=strict&q="+word;
		
		HttpGet request = new HttpGet(url);
//		request.setHeader("URIEncoding", "UTF-8");
		HttpResponse response = client.execute(request);
		int status = response.getStatusLine().getStatusCode();
		String body = "";
		try {
		switch(status){
		case 200:
			InputStream input =response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line = "";
				while((line = br.readLine()) !=null){
					body += line ;
					
				}
			break;
		case 404:
			break;
			
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(body);
		
	}
	
	
}
