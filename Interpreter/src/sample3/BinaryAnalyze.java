package sample3;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import sample3.Memory.RegisterName;
import sample3.OperandInfo.OperandType;
import sample3.Operation.OperationInfo;

public class BinaryAnalyze {
	
	private boolean flgN = false, flgZ = false, flgV = false, flgC = false;
	
	private byte[] buf;
	private byte[] textBuf, dataBuf;
	
	private int textSize, dataSize;
	private int pos, textPos, dataPos;
	private int pc;
	private final int HEADERSIZE = 32;
	
	private HashMap<Integer, OperationInfo> map;
	
	private Memory memory;
	
	private final String[] HEADEREXPLAIN = {
			"magic", "text", "data", "bss", 
			"syms", "entry", "trsize", "drsize"		
	};
	
	enum AdressingType {
		Literal(0), Index(1), Reg(2), RegDef(3),
		AutoDec(4), AutoInc(5), AutoIncDef(6), ByteDisp(7),
		ByteDispDef(8), WordDisp(9), WordDispDef(10), LongDisp(11),
		LongDispDef(12), Immediate(13), Abs(14), ByteRel(15),
		ByteRelDef(16), WordRel(17), WordRelDef(18), LongRel(19),
		LongRelDef(20), None(-1);
		
		int code;
		
		AdressingType(int code) {
			this.code = code;
		}
		
		int getCode() {
			return code;
		}
	}
	
	public BinaryAnalyze(byte[] buf) {
		this.buf = buf;
		pos = 0;
		textPos = 0;
		memory = new Memory(buf);
		map = new HashMap<Integer, OperationInfo>();
		headerAnalyze();
		operationReg();
	}
	
	private void headerAnalyze() {
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
			System.out.print(String.format("%6S : 0x%08X\n", HEADEREXPLAIN[i], headerNum[i]));
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
		
		memory.setText(textBuf);
		memory.setData(dataBuf);
		
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
		
		dataPos = ((textSize + 0x200 - 1) / 0x200) * 0x200;
		
		
		System.out.print("\n*** header ***\n\n\n");
	}
	
	private void operationReg() {
		System.out.println("***operationReg***");
		for(OperationInfo info : OperationInfo.values()) {
			map.put(info.getCode(), info);
			System.out.printf("Code : %d Name : %S\n", info.getCode(), info.toString());
		}
		System.out.println("***operationReg finish***\n\n");
	}
	
	public void analyze() {
		do {
			pc = memory.getRegisterValue(RegisterName.PC);
			System.out.printf("pc : %08X textBuf[pc] : %02X\n", pc, textBuf[pc]);
			OperationInfo opInfo = map.get((int)(textBuf[pc] & 0xFF));
			textPos++;
			Operation ope = new Operation((int)(opInfo.getCode()), opInfo);
			
			operationAnalyze(ope);
			
		}while(textPos < textSize);
	}
	
	private void operationAnalyze(Operation ope) {
		OperandType[] type = {OperandType.None, OperandType.None, OperandType.None,
				OperandType.None, OperandType.None, OperandType.None};
		
		System.out.println(ope.getOpeInfo().toString());
		
		switch(ope.getOpeInfo()) {
		case HALT:
			break;
		
		case MOVL:
			for(int i = 0; i < 2; i++) {
				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
				ope.setOperand(i, readOperand(type[i]));
			}
//			OperandType type1 = ope.getOpeInfo().getOperandInfo().getOperandType(0);
//			ope.setOperand(0, readOperand(type1));
			int setValue = ope.getOperand(0).getVal();
//			OperandType type2 = ope.getOpeInfo().getOperandInfo().getOperandType(1);
//			ope.setOperand(1, readOperand(type2));
			memory.setRegisterValue(ope.getOperand(0).getVal(), registerName(ope.getOperand(1).getOperand()));
			memory.setFlag(setValue < 0, setValue == 0, false, flgC);
			
			break;
		
		case CHMK:
			type[0] = ope.getOpeInfo().getOperandInfo().getOperandType(0);
			ope.setOperand(0, readOperand(type[0]));
			int val = ope.getOperand(0).getOperand();
			System.out.printf("val : %d\n", val);
			systemCall(val);
			break;
		
		default:
			
		}
		memory.setRegisterValue(textPos, RegisterName.PC);
	}

	private Operand readOperand(OperandType type) {
		Operand ope = new Operand();
		//b = b1b2
		byte b = textBuf[textPos++];
		byte b1 = (byte)((b >> 4) & 0xF);
		byte b2 = (byte)(b & 0xF);

		switch(b1) {
		case 0:
		case 1:
		case 2:
		case 3: 
			ope.setAdressingType(AdressingType.Literal);
			ope.setOperand(b);
			return ope;
			
		case 4:
			ope.setAdressingType(AdressingType.Index);
			ope.setOperand(b);
			ope.setRegVal(memory.getRegisterValue(registerName(b)));
//			switch(type) {
//			
//			}
			return ope;
			
		case 5:
			ope.setAdressingType(AdressingType.Reg);
			ope.setOperand(b2 & 0xF);
			ope.setVal(b2 & 0xF);
			return ope;
			
		case 6:
			ope.setAdressingType(AdressingType.RegDef);
			ope.setOperand(b2);
			return ope;
			
		case 7:
			ope.setAdressingType(AdressingType.AutoDec);
			ope.setOperand(b2);
			return ope;
			
		case 8:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.Immediate);
				ope.setVal(readNum(type));
				
			} else {
				ope.setAdressingType(AdressingType.AutoInc);
			}
			return ope;
			
		case 9:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.Abs);
			} else {
				ope.setAdressingType(AdressingType.AutoIncDef);
			}
			return ope;
			
		case 10:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.ByteRel);
			} else {
				ope.setAdressingType(AdressingType.ByteDisp);
			}
			return ope;
			
		case 11:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.ByteRelDef);
			} else {
				ope.setAdressingType(AdressingType.ByteDispDef);
			}
			return ope;
			
		case 12:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.WordRel);
			} else {
				ope.setAdressingType(AdressingType.WordDisp);
			}
			return ope;
			
		case 13:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.WordRelDef);
			} else {
				ope.setAdressingType(AdressingType.WordDispDef);
			}
			return ope;
			
		case 14:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.LongRel);
			} else {
				ope.setAdressingType(AdressingType.LongDisp);
			}
			return ope;
			
		case 15:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.LongRelDef);
			} else {
				ope.setAdressingType(AdressingType.LongDispDef);
			}
			return ope;
			
		default: return ope;
				
		}
	}
	
	private RegisterName registerName(int i) {
		switch(i) {
		case 0: return RegisterName.R0;
		case 1: return RegisterName.R1;
		case 2: return RegisterName.R2;
		case 3: return RegisterName.R3;
		case 4: return RegisterName.R4;
		case 5: return RegisterName.R5;
		case 6: return RegisterName.R6;
		case 7: return RegisterName.R7;
		case 8: return RegisterName.R8;
		case 9: return RegisterName.R9;
		case 10: return RegisterName.R10;
		case 11: return RegisterName.R11;
		case 12: return RegisterName.AP;
		case 13: return RegisterName.FP;
		case 14: return RegisterName.SP;
		case 15: return RegisterName.PC;
		default: return null;
		}
	}
	
	private int readNum(OperandType type) {
		byte b1, b2, b3, b4;
		int num;
		switch(type) {
		case Byte:
			b1 = textBuf[textPos++];
			num = (b1 & 0xFF);
			return num;
			
		case Word:
			b1 = textBuf[textPos++];
			b2 = textBuf[textPos++];
			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8);
			return num;
			
		case Long:
			b1 = textBuf[textPos++];
			b2 = textBuf[textPos++];
			b3 = textBuf[textPos++];
			b4 = textBuf[textPos++];
			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
			return num;
			
		case Float:
			
		default:
			return -1;
		}
	}
	
	private void systemCall(int num) {
		int ap1, ap2, ap3, ap4;
		int val1, val2, val3, val4;
		
		switch(num) {
		case 1: //exit
			ap1 = memory.getRegisterValue(RegisterName.AP);
			val1 = readDataNum(ap1, 4);
			ap2 = memory.getRegisterValue(RegisterName.AP) + 4;
			val2 = readDataNum(ap2, 4);
			System.out.printf("exit(%X)\n", val2);
			System.exit(val2);
			break;
			
		case 4: //write
			ap1 = memory.getRegisterValue(RegisterName.AP);
			val1 = readDataNum(ap1, 4);
			ap2 = memory.getRegisterValue(RegisterName.AP) + 4;
			val2 = readDataNum(ap2, 4);
			ap3 = memory.getRegisterValue(RegisterName.AP) + 8;
			val3 = readDataNum(ap3, 4);
			ap4 = memory.getRegisterValue(RegisterName.AP) + 12;
			val4 = readDataNum(ap4, 4);
			System.out.printf("ap : %08X val1 : %x, val2 : %x, val3 : %x val4 : %x\n", ap1, val1, val2, val3, val4);
			System.out.printf("write(%X, 0x%X, %X)\n", val2, val3, val4);
			int len = sysWrite(val2, val3, val4);
			break;
		default:
			return;
		}
	}
	
	private int readNum(int index, int byteNum) {
		byte b1, b2, b3, b4;
		int num;
		switch(byteNum) {
		case 4:
			b1 = buf[index++];
			b2 = buf[index++];
			b3 = buf[index++];
			b4 = buf[index++];
			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
			return num;
		}
		return -1;
	}
	
	private int readDataNum(int index, int byteNum) {
		byte b1, b2, b3, b4;
		int num;
		System.out.printf("index : %X\n", index);
		index -= dataPos;
		System.out.printf("index : %X\n\n", index);
		switch(byteNum) {
		case 4:
			b1 = dataBuf[index++];
			b2 = dataBuf[index++];
			b3 = dataBuf[index++];
			b4 = dataBuf[index++];
			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
			return num;
		}
		return -1;
	}
	
	private int sysWrite(int field, int offset, int size) {
		offset -= dataPos;
		PrintStream ps;
		switch(field) {
		case 1: //標準出力
			ps = System.out;
			ps.write(dataBuf, offset, size);
			return size;
		default: return -1;
		}
	}

}
