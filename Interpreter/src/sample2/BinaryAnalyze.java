package sample2;

import java.util.HashMap;

import sample2.Operation.OperandType;

/*
 * pos : binaryの位置
 * header / text / data
 */

public class BinaryAnalyze {
	
	private byte[] buf;
	private byte[] textBuf;
	private byte[] dataBuf;
	
	private boolean flg_N; //NegativeFlag
	private boolean flg_Z; //ZeroFlag
	private boolean flg_V; //OverFlowFlag
	private boolean flg_C; //CarryFlag
	
	private HashMap map;
	
	private int[] register;
	
	private int pos, index, textSize, dataSize;
	private final int HEADERSIZE = 32;
	
	private final String[] headerExplain = {
			"magic", "text", "data", "bss", 
			"syms", "entry", "trsize", "drsize"};
	
	enum Reg {
			R0(0), R1(1), R2(2), R3(3), 
			R4(4), R5(5), R6(6), R7(7), 
			R8(8), R9(9), R10(10), R11(11), 
			AP(12), FP(13), SP(14), PC(15);
		private int code;
		Reg(int code){
			this.code = code;
		}
		public int getCode() {
			return code;
		}
	};
	
	public BinaryAnalyze(byte[] buf) {
		this.buf = buf;
		register = new int[16];
		map = new HashMap<Integer, Operation>();
		regist();
	}
	
	public void analyze() {
		header();
		textAnalyze();
	}
	
	private void header() {
		byte buf1, buf2, buf3, buf4;
		int[] headerNum = new int[8];
		
		System.out.print("*** header ***\n");
		
		for(int i = 0; i < 8; i++) {
			buf1 = buf[pos++];
			buf2 = buf[pos++];
			buf3 = buf[pos++];
			buf4 = buf[pos++];
			headerNum[i] = (buf1 & 0xFF) | ((buf2 & 0xFF) << 8) | 
					((buf3 & 0xFF) << 16) | ((buf4 & 0xFF) << 24);
			System.out.print(String.format("%6S : 0x%08X\n", headerExplain[i], headerNum[i]));
		}
		
		textBuf = new byte[4096];
		dataBuf = new byte[4096];
		
		textSize = headerNum[1];
		dataSize = headerNum[2];
		
		for(int i = 0; i < textSize; i++) {
			textBuf[i] = buf[pos++];
		}
		
		for(int i = 0; i < dataSize; i++) {
			dataBuf[i] = buf[pos++];
		}
		
		System.out.print("\n*** text ***\n");
		for(int i = 0; i < textSize; i++) {
			System.out.print(String.format("%02X ", textBuf[i]));
			if(i % 16 == 15) {
				System.out.print("\n");
			}
		}
		
		System.out.print("\n\n*** data ***\n");
		for(int i = 0; i < dataSize; i++) {
			System.out.print(String.format("%02X ", dataBuf[i]));
			if(i % 16 == 15) {
				System.out.print("\n");
			}
		}
		
		System.out.print("\n*** header ***\n\n\n");
		
	}
	
	private void textAnalyze() {
		pos = 0;
		do {
			int opeCode = textBuf[pos++] & 0xFF;
//			System.out.println("opeCode : " + String.format("%02X", opeCode));
			Operation ope = (Operation) map.get(opeCode);
			ope.behavior();
		}while(pos < textSize);
	}
	
	private int readOperand(OperandType type) {
		byte b = textBuf[pos++];
		byte b1 = (byte)((b >> 4) & (byte)0xf);
		byte b2 = (byte)(b & (byte)0xf);
		
//		System.out.println(String.format("%02X", b));
		
		byte buf1, buf2, buf3, buf4;
		int num;
		
		if(b2 == (byte)0xF & b1 >= (byte)0x08) {
			switch(b1) {
			case 0x8: 
			case (byte)0x9: 
			case (byte)0xA: 
			case (byte)0xB: 
			case (byte)0xC: 
			case (byte)0xD: 
			case (byte)0xE: 
			case (byte)0xF:
				switch(type) {
				case LONG:
					buf1 = textBuf[pos++];
					buf2 = textBuf[pos++];
					buf3 = textBuf[pos++];
					buf4 = textBuf[pos++];
					num = ((buf1 & 0xFF) | ((buf2 & 0xFF) << 8) | ((buf3 & 0xFF) << 16) | ((buf4 & 0xFF) << 24));
					return num;
				}
			default: 
			}
		} else {
			switch(b1) {
			case (byte)0x4: 
			case (byte)0x5:
				return -(b2);
			case (byte)0x6: 
			case (byte)0x7: 
			case (byte)0x8: 
			case (byte)0x9: 
			case (byte)0xA: 
			case (byte)0xB: 
			case (byte)0xC: 
			case (byte)0xD: 
			case (byte)0xE: 
			case (byte)0xF: 
			default: 
				return b;
			}
		}
		return -1;
	}
	
	private void regist() {
		HALT halt = new HALT(0x00, "HALT");
		MOV movl = new MOV(0xD0, "MOVL", OperandType.LONG, OperandType.LONG);
		CHM chmk = new CHM(0xBC, "CHMK", OperandType.WORD);
		
		map.put(halt.getCode(), halt);
		map.put(movl.getCode(), movl);
		map.put(chmk.getCode(), chmk);
	}
	
	class MOV extends Operation {
		public MOV(int opCode, String opName, OperandType type1, OperandType type2) {
			super(opCode, opName, type1, type2);
		}
		
		public void behavior() {
			operand[0] = readOperand(opType[0]);
			System.out.println("MOV do.\nOperand1 = " + String.format("%08X", operand[0]));
			operand[1] = readOperand(opType[1]);
			System.out.println("\nOperand2 = " + String.format("%08X", operand[1]));
			if(operand[1] < 0) {
				register[-operand[1]] = operand[0];
				System.out.println("AP = " + String.format("%08X",register[Reg.AP.getCode()]));
			}
			flg_N = false;
			flg_Z = false;
			flg_V = false;
			//flg_C はそのまま
			if(operand[0] < 0) {
				flg_N = true;
			} else if(operand[0] == 0) {
				flg_Z = true;
			}
			
		}
		
	}

	class CHM extends Operation {
		public CHM(int opCode, String opName, OperandType type1) {
			super(opCode, opName, type1);
		}
		
		public void behavior() {
			System.out.println("CHM do.");
			operand[0] = readOperand(opType[0]);
			System.out.println(operand[0]);
			switch(operand[0]) {
			case 1: //exit
				
				break;
			case 4: //write(int fildes, const void *buf, size_t nbyte) : 
				
				break;
			}
		}
	}

	class HALT extends Operation {
		public HALT(int opCode, String opName) {
			super(opCode, opName);
		}
		
		public void behavior() {
			System.out.println("HALT do.");
		}
	}

}
