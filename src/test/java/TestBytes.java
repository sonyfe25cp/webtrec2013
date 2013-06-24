import junit.framework.TestCase;

import org.junit.Test;


public class TestBytes extends TestCase{

	@Test
	public void testByte(){
		String url="http://www.baidu.com";
		byte[] b=url.getBytes();
		for(byte bb:b){
			System.out.println(bb);
		}
	}
	
	
}
