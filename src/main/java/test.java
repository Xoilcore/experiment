import static m.util.PrintUtil.*;

import java.io.StreamTokenizer;
import java.net.InetAddress;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int userUmidLoginDays = 0;
		String line = "C55d7094ecb169ddde368aa7d1eae6d4a,C6b3e49ac94a12d166b72b80e30f2bbe9;C55d7094ecb169ddde368aa7d1eae6d4a,C55d7094ecb169ddde368aa7d1eae6d4a,C8f927b4afe1c9e79ad319bc51cc10a82,C8f927b4afe1c9e79ad319bc51cc10a82;C447542186b396aa4fde63e9b433f51b2,C447542186b396aa4fde63e9b433f51b2;C55d7094ecb169ddde368aa7d1eae6d4a,C447542186b396aa4fde63e9b433f51b2;C55d7094ecb169ddde368aa7d1eae6d4a,C55d7094ecb169ddde368aa7d1eae6d4a;";
		String[] list = line.split(";");
		String umid = "C55d7094ecb169ddde368aa7d1eae6d4a";
		// if(System.currentTimeMillis()%50==0){//for beta
		for (String item : list) {
			if (item != null && item.indexOf(umid) > -1) {
				userUmidLoginDays++;
			}
		}
		p(userUmidLoginDays);
		
		StreamTokenizer stk;
	}

}
