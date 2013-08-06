import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class GenerateIndriQueryText {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws Exception {

		String fileName = "2001.txt";
		String filePath = "/home/coder/workspace/mavenSpace/webtrack/doc/topicwords/";
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath + fileName)));
		String count = br.readLine();
		int total = Integer.parseInt(count);
		String line = count;
		while(line != null){
			line = br.readLine();
			String[] tmp = line.split(" ");
//			System.out.println(tmp.length);
			float num = Float.parseFloat(tmp[5]);
			float ratio = num/total;
			
			System.out.print(ratio+" "+tmp[0]+" ");
		}
		br.close();
		
	}

}
