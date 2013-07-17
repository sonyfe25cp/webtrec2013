import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.bit.dlde.extractor.BlockExtractor;
import edu.bit.dlde.extractor.SimpleHtmlExtractor;


public class PagesExtract {
	private String rawPagesPath = "/home/yulong/store/webtrec/urlcontent";
	private String extractPagesPath = "/home/yulong/store/webtrec/extractpages";
	private String topicContentPath = "/home/yulong/store/webtrec/topiccontent";
	public void extract(){
		File rawPages = new File(rawPagesPath);
		if(!rawPages.exists())
			return;
		File[] topics = rawPages.listFiles();
		for(File file : topics)
		{
			//创建存放各个主题的抽取内容的文件夹
			File topicDir = new File(extractPagesPath + File.separator +file.getName());
			if(!topicDir.exists()){
				topicDir.mkdir();
			}
			//创建一个文件存放抽取出的属于该主题下的所有内容，即合并所有page的内容
			File topicContentDir = new File(topicContentPath + File.separator +file.getName());
			if(!topicContentDir.exists()){
				topicContentDir.mkdir();
			}
			File topicContentFile = new File(topicContentPath + File.separator +file.getName() +File.separator + "content.txt");
			if(!topicContentFile.exists()){
				try {
					topicContentFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//content存放该主题下所有pages抽取出来并整合在一起的内容
			String topicContent = "";
			
			File[] pages = file.listFiles();
			for(File pageFile : pages)
			{
				String title = "";
				String body = "";
				String pageContent = "";
				BlockExtractor be = new BlockExtractor();
					try {
						be.setReader(new FileReader(pageFile));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try{
						be.extract();
						title = be.getTitle();
						body = be.getContent();
						pageContent = title + "\n" + body;
						topicContent = topicContent + pageContent +"\n";
					}catch(Exception e){
						e.printStackTrace();
						System.out.println(file.getName() + " -- " + pageFile.getName() + " --block extract Exception--");
						BufferedReader br = null;
						try {
							br = new BufferedReader(new FileReader(pageFile));
							SimpleHtmlExtractor sh = new SimpleHtmlExtractor();
							sh.extract();
							title = sh.getTitle();
							body = sh.getContent();
							pageContent = title + "\n" + body;
							topicContent = topicContent + pageContent +"\n";
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				File contentFile = new File(topicDir.getAbsolutePath() + File.separator + pageFile.getName());
				try{
					if(!contentFile.exists()){
						contentFile.createNewFile();
					} 
					FileWriter pageFW = new FileWriter(contentFile);
					pageFW.write(pageContent);
					pageFW.close();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//for 1
			FileWriter topicFW = null;
			try {
				topicFW = new FileWriter(topicContentFile);
				topicFW.write(topicContent);
				topicFW.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//for 2
	
	}
	
	public static void main(String[] args){
//		String pagePath = "/home/yulong/store/webtrec/urlcontent/201/88.txt";
//		File file = new File(pagePath);
//		BlockExtractor be = new BlockExtractor();
//		try {
//			be.setReader(new FileReader(file));
//			be.extract();
//			System.out.println("Title: " + be.getTitle());
//			System.out.println("body: " + be.getContent());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		PagesExtract pe = new PagesExtract();
		pe.extract();
		
	}

}
