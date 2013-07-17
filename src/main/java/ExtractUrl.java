import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;

public class ExtractUrl {

	public List<String> parseUrl(String body){
		//首先对body做预处理，只保留<ol> </ol>之间的内容
		int start = body.indexOf("<ol>");
		int end = body.indexOf("</span><br></div></li></ol>");
		body=body.substring(start, end);
		List<String> urls = new ArrayList<String>();
		//String regex = "<\\s*[aA]\\s*(href\\s*=[^>]+)>";
		String regex = "<\\s*[aA]\\s*(href\\s*=[^&]+)&";
		Pattern pattern = Pattern.compile(regex);
		//String test = "这是测试<a href=http://www.ba*****idu.cn>www.goog[]e.cn</a>真的是测试我试下<a href='http://www.google.cn'>www.google.cn</a>了";
		Matcher matcher = null;
		matcher = pattern.matcher(body);
		//System.out.println("count: " + matcher.groupCount());
		while(matcher!= null && matcher.find())
		{
			URLDecoder decode = new URLDecoder();
			String url = decode.decode(matcher.group());
			String regex2 = "https?:[^&]+&";
			Pattern pattern2 = Pattern.compile(regex2);
			Matcher matcher2 = pattern2.matcher(url);
			if(matcher2!= null && matcher2.find()){
				String temUrl = matcher2.group(0);
				url = temUrl.replaceAll("&", "");
			}
			if(!urls.contains(url)){
				if(url.startsWith("http") && !url.contains("google")){
					urls.add(url);
					System.out.println(url);
				}
			}
			
		}//if
		return urls;
	}
	
	public void extractUrl() throws IOException{
		String searchResultPath = "/home/yulong/store/webtrec/searchresult";
		String urlPath = "/home/yulong/store/webtrec/url/";
		File f1 = new File(searchResultPath);
		File[] fList1 = f1.listFiles();
		if(fList1!= null)
		{
			for(File file : fList1)
			{
				String urlFileName = file.getName();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String body = "";
				String temStr = "";
				while((temStr = br.readLine())!= null){
					body+= temStr;
				}
				br.close();
				List<String> urls =parseUrl(body);
				File urlFile = new File(urlPath+urlFileName);
				urlFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(urlFile));
				for(String url : urls){
					bw.write(url);
					bw.newLine();
				}
				bw.close();
				System.out.println(urlFileName+" done!");
			}//for
		}//if
	}
	
	public static void main(String[] args) throws IOException{
		ExtractUrl exu = new ExtractUrl();
//		BufferedReader br = new BufferedReader(new FileReader(new File("/home/yulong/store/webtrec/searchresult/201.txt")));
//		String body = "";
//		String temStr = "";
//		while((temStr = br.readLine())!= null){
//			body += temStr;
//		}
//		List<String> urls = exu.parseUrl(body);
//		System.out.println(urls.size());
		exu.extractUrl();
	}
}
