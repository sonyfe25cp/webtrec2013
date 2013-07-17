

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class WordSplit {
	//分词的主程序，分完词后存放在一个map中，以<word,frequence>的形式存放，如<中国，100>，表示“中国”出现了100次
	public List<Map.Entry<String, Integer>> split(String text){
		Map<String, Integer> wordFrequence = new HashMap<String, Integer>();
		IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
		Lexeme lexeme;
		try {
			while((lexeme = ikSegmenter.next())!= null){
				String word = lexeme.getLexemeText();
				if(word.length() > 1&&(isChinese(word)||isEnglish(word))){
					if(wordFrequence.containsKey(word)){
						wordFrequence.put(word, wordFrequence.get(word)+1);
					}
					else{
						wordFrequence.put(word, 1);
					}	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sortSegmentResult(wordFrequence);
	}
	
	public List<Map.Entry<String, Integer>> sortSegmentResult(Map<String, Integer> wordFrequence){
		System.out.println("排序前： ==================================================================");
		displayMap(wordFrequence);
		
		List<Map.Entry<String, Integer>> wordsList = new ArrayList<Map.Entry<String, Integer>>(wordFrequence.entrySet());
		Collections.sort(wordsList, new Comparator<Map.Entry<String, Integer>>(){
			public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2){
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});
		
		System.out.println("排序后： =================================================================");
		for(Map.Entry<String, Integer> mapList : wordsList){
			System.out.println(mapList.getKey()+" ------" + mapList.getValue());
		}
		
		Map<String, Integer> wordsMap = new HashMap<String, Integer>();
		for(Map.Entry<String, Integer> entry : wordsList){
			wordsMap.put(entry.getKey(), entry.getValue());
		}
		
		return wordsList;
	}

	public String readData(String textPath){
		File file = new File(textPath);
		BufferedReader br = null;
		String text = "";
		try {
			 br = new BufferedReader(new FileReader(file));
			 String str = "";
			 while((str=br.readLine())!= null){
				 text = text + str + " ;";
			 }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
		
	}
	
	public void saveData(File file, List<Map.Entry<String, Integer>> wordsList){
		BufferedWriter bw =null;
		try {
		    if(!file.exists())
				file.createNewFile();
		    bw = new BufferedWriter(new FileWriter(file));
		    if(wordsList == null){
		    	return;
		    }
		    for(Map.Entry<String, Integer> entry : wordsList){
		    	bw.write(entry.getKey() + "     " + entry.getValue());
		    	bw.newLine();
		    }
		    bw.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		
		
	}
	
	public void mutiFilesProcess(String sourceDirectory, String targetDirectory){
		File sourceDir = new File(sourceDirectory);
		if(!sourceDir.exists()||!sourceDir.isDirectory()){
			System.out.println("The Source Directory is not exits!");
			return;
		}
		File[] sourceFiles = sourceDir.listFiles();
		if(sourceFiles==null||sourceFiles.length==0){
			System.out.println("no file!");
			return;
		}
		//建立存放单词及其频率的文件
		File targetDir = new File(targetDirectory);
		if(!targetDir.exists()||!targetDir.isDirectory()){
			targetDir.mkdir();
		}
		
		for(File file : sourceFiles){
			String fileName = file.getName();
			String content = "";
			String temStr = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				while((temStr=br.readLine())!= null){
					content = content + temStr + "  ";
				}
				File wordFreq = new File(targetDirectory + File.separator + fileName +".txt");
				if(!wordFreq.exists()){
					wordFreq.createNewFile();
				}
				else
					continue;
				List<Map.Entry<String, Integer>> wordsList = split(content);
				saveData(wordFreq, wordsList);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void displayMap(Map<String, Integer> map){
		Iterator<Map.Entry<String, Integer>> entryIterator = map.entrySet().iterator();
		while(entryIterator.hasNext()){
			Map.Entry<String, Integer> entry = entryIterator.next();
			System.out.println(entry.getKey() + "-----" + entry.getValue());
		}
	}
	
	/* 以下两个函数都是判断字符串类型的函数，在扩展词的过滤中用到 */

	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/* 准确判断字符串是否为中文 */
	public static final boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}
	
	//判断字符串是否为数字
	public boolean isNumber(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	public boolean isEnglish(String str){
		return str.matches("^[a-zA-Z]*$");
	}
	
	public static void main(String[] args){ 
		WordSplit ws = new WordSplit();
		String sourceDirectory = "/home/yulong/store/webtrec/topiccontent";
		String targetDirectory = "/home/yulong/store/webtrec/topicwords";
		ws.mutiFilesProcess(sourceDirectory, targetDirectory);

	}
	

}
