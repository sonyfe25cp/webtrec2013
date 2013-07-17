import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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


public class TestGoogleCrawler {
	public static void main(String[] args) throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8");
		String url = "https://www.google.lt/search?num=100&safe=strict&q=raspberry+pi";
		System.out.println("query url: "+url);
		
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);

		int status = response.getStatusLine().getStatusCode();
		String result = response.getEntity().getContentType().getName()+"  "+response.getEntity().getContentType().getValue();
		String body = "";
		String str = "";
		switch(status)
		{
		case 200:
			System.out.println("status 200");
			//解决乱码
			str = EntityUtils.toString(response.getEntity(), "Utf-8");
			break;
		case 404:
			System.out.println("404 error!");
			break;
		default :
			System.out.println("else error!");
			break;
		}//switch
		System.out.println("str: "+str);
		System.out.println("result: "+result);
	}

}
