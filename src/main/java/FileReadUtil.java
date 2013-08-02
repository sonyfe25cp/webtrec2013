import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileReadUtil {
	public static String readFile(String filePath){
		String content = "";
		BufferedReader br = null;
		File file = null;
		try {
			file = new File(filePath);
			br = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = "";
		try {
			while((str = br.readLine())!= null){
				content+= str;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	public static String readFile(File file){
		String content = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = "";
		try {
			while((str = br.readLine())!= null){
				content+= str;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return content;
	}

}
