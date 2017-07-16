package sample2;

public class Display {

	static void binaryShow(byte[] buf, int len) {

		for(int i = 0; i < len; i ++) {

			if(i % 16 == 0) {
				System.out.print(String.format("%07X : ", i));
			}

			System.out.print(String.format("%02X ", buf[i]));

			if(i % 16 == 15) {
				System.out.print("\n");
			}
		}

		System.out.println("\n**********************************************************\n\n");

	}

}
