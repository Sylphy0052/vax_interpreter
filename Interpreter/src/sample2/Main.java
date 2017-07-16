package sample2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Main {

	public static void main(String[] args) {
		
		String fileName = "/Users/Kento/fkd/vaxsetup/testset/asm/a.out";
		
		File file = new File(fileName);
		
		byte[] buf = new byte[4096];
		
		int len = 0;
		
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			len = in.read(buf);
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Display.binaryShow(buf, len);
		
		BinaryAnalyze ba = new BinaryAnalyze(buf);
		
		ba.analyze();
		
		System.out.println("\nfinish.");
		
	}
	
}
