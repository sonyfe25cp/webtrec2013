import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaserLinkFromXml {
	private String xmlHeader = "<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.6/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=http://www.mediawiki.org/xml/export-0.6/ http://www.mediawiki.org/xml/export-0.6.xsd\" version=\"0.6\" xml:lang=\"en\">";
	private static String xmlDirectory = "/home/yulong/store/xml";
	private static String targetDirectory = "/home/yulong/store/xmlParse";
	
	public List<String> extractLinks(String xmlContent) throws Exception{
		List<String> links = new ArrayList<String>();
		int start = xmlContent.indexOf("<text xml:space=\"preserve\">");
		int end = xmlContent.indexOf("</text>");
		if(start==-1||end==-1){
			return null;
		}
		String content = xmlContent.substring(start, end);
		//String regex = "\\[\\[[^\\]]+\\]\\]";
		String regex = "\\[\\[((?!\\]{2,}).)+\\]\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = null;
		matcher = pattern.matcher(content);
		while(matcher!= null && matcher.find()){
			String link = matcher.group();
			link = link.replaceAll("\\[\\[", "");
			link = link.replaceAll("\\]\\]", "");
			links.add(link);
		}
		return links;
	}
	
	public String extractTitle(String xmlContent){
		//System.out.println(xmlContent);
		String title = "";
		String redirectTitle = "";
		String regex = "<\\s*redirect\\s*title=((?!/>).)*/>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = null;
		matcher = pattern.matcher(xmlContent);
		while(matcher!= null && matcher.find()){
			redirectTitle += matcher.group();
		}
		if(!redirectTitle.equals("")){
			redirectTitle = redirectTitle.replaceAll("<(\\s)*redirect(\\s)*title(\\s)*=(\\s)*", "");
			redirectTitle = redirectTitle.replaceAll("(\\s)*/>", "");
			redirectTitle = redirectTitle.replaceAll("\"", "");
//			int length = title.length();
//			title.substring(1, length-1);
			title = "<title>" + redirectTitle + "</title>"; 
		}
		else{
			String regex1 = "<\\s*title>((?!/title).)*</title>";
			Pattern pattern1 = Pattern.compile(regex1);
			Matcher matcher1 = pattern1.matcher(xmlContent);
			while(matcher1!= null && matcher1.find()){
				title += matcher1.group();
			}
		}
		return title;
	}
	
	public void parse(String xmlHome, String targetDir){
		File homeDir = new File(xmlHome);
		if(!homeDir.exists()){
			return;
		}
		File[] dirList = homeDir.listFiles();
		int i = 0;
		for(File dir : dirList)
		{
			String dirName = dir.getName();
			File[] xmlFiles = dir.listFiles();
			File xmlDir = new File(targetDir + File.separator + dirName);
			if((!xmlDir.exists())||(!xmlDir.isDirectory())){
				xmlDir.mkdir();
			}
			for(File xmlFile: xmlFiles)
			{
				String xmlName = xmlFile.getName();
				File targetXml = new File(targetDir + File.separator + dirName + File.separator + xmlName);
				if(targetXml.exists()){
					FileInputStream fs;
					try {
						fs = new FileInputStream(targetXml);
						long size = fs.available();
						fs.close();
						if(size>0)
							continue;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				String xmlContent = FileReadUtil.readFile(xmlFile);
				String title = extractTitle(xmlContent);
				List<String> links = null; 
				try {
					links = extractLinks(xmlContent);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("============"+dirName + "===========" + xmlName+"===========exception");
					e1.printStackTrace();
				}
				String resultContent = xmlHeader + "\n" + "  <page>\n" + "    " + title +"\n";
				if(links!=null){
					resultContent += "    " + "<text xml:space=\"preserve\">\n";
					for(String link : links){
						resultContent += "      <a href=\"" +link +"\">" +link +"</a>\n";
					}
					resultContent += "    </text>\n";
				}
				else
				{
					resultContent += "    " + "<text xml:space=\"preserve\"></text>\n";
				}
				resultContent += "  </page>\n" + "</mediawiki>";
				try {
					if(!targetXml.exists()){
						targetXml.createNewFile();
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				FileWriter fw =null;
				try {
					fw = new FileWriter(targetXml);
					fw.write(resultContent);
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(i%1000 == 0){
					System.out.println(i + "--------dir: " + dirName + "--------" + "file: " + xmlName);
				}
				i++;
			}// for
		}//for
	}
	
	
	public static void main(String[] args){
		PaserLinkFromXml plfx = new PaserLinkFromXml();
		plfx.parse(xmlDirectory, targetDirectory);
	}

}
