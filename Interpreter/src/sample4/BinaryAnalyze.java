package sample4;

import java.io.PrintStream;
import java.util.HashMap;

import sample4.MyEnum.AdressingType;
import sample4.MyEnum.OperandType;
import sample4.MyEnum.RegisterName;
import sample4.Operation.OperationInfo;

public class BinaryAnalyze {
	
	private boolean /*flgN = false, flgZ = false, */flgV = false, flgC = false;
	
	private byte[] buf;
//	private byte[] textBuf, dataBuf;
	
	private int textSize, dataSize;
	private int pos, textPos, dataPos;
	private int pc;
	private int psw;
//	private final int HEADERSIZE = 32;
	
	private HashMap<Integer, OperationInfo> map;
	
	private Memory memory;
	
	private final String[] HEADEREXPLAIN = {
			"magic", "text", "data", "bss", 
			"syms", "entry", "trsize", "drsize"		
	};
	
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
		
//		textBuf = new byte[4096];
//		dataBuf = new byte[4096];
		
		textSize = headerNum[1];
		dataSize = headerNum[2];
		
		for(int i = 0; i < textSize; i++) {
//			textBuf[i] = buf[pos];
			memory.writeMemory(i, buf[pos++]);
		}
		
		dataPos = ((textSize + 0x200 - 1) / 0x200) * 0x200;
		
		for(int i = 0; i < dataSize; i++) {
//			dataBuf[i] = buf[pos];
			memory.writeMemory(dataPos + i, buf[pos++]);
		}
		
//		System.out.print("\n*** text ***\n");
//		for(int i = 0; i < textSize; i++) {
//			System.out.print(String.format("%02X ", textBuf[i]));
//			if(i % 16 == 15) {
//				System.out.print("\n");
//			}
//		}
		System.out.print("\n*** memorytext ***\n");
		for(int i = 0; i < textSize; i++) {
			System.out.print(String.format("%02X ", memory.readMemory(i, 1)));
			if(i % 16 == 15) {
				System.out.print("\n");
			}
		}
		
//		System.out.print("\n\n*** data ***\n");
//		for(int i = 0; i < dataSize; i++) {
//			System.out.print(String.format("%02X ", dataBuf[i]));
//			if(i % 16 == 15) {
//				System.out.print("\n");
//			}
//		}
		System.out.println("***memorydata***");
		for(int i = 0; i < dataSize; i++) {
			System.out.printf("%02X ",memory.readMemory(dataPos + i, 1));
			if(i % 16 == 15) {
				System.out.print("\n");
			}
		}
		
		System.out.print("\n*** header ***\n\n\n");
		
		memory.setRegisterValue(0x100000 - 5, RegisterName.SP);
	}
	
	private void operationReg() {
		System.out.println("***operationReg***");
		for(OperationInfo info : OperationInfo.values()) {
			map.put(info.getCode(), info);
			System.out.printf("Code : %02X Name : %S\n", info.getCode(), info.toString());
		}
		System.out.println("***operationReg finish***\n\n");
	}
	
	public void analyze() {
		do {
			System.out.println("\n***");
			
			pc = memory.getRegisterValue(RegisterName.PC);
			System.out.printf("pc : %08X textBuf[pc] : %02X\n", pc, memory.readMemory(pc, 1));
			OperationInfo opInfo = map.get((int)(memory.readMemory(pc, 1) & 0xFF));
			textPos++;
			Operation ope = new Operation((int)(opInfo.getCode()), opInfo);
			
			operationAnalyze(ope);
			
			System.out.println("\n***\n");
		}while(textPos < textSize);
	}
	
	private void operationAnalyze(Operation ope) {
		OperandType[] type = {OperandType.None, OperandType.None, OperandType.None,
				OperandType.None, OperandType.None, OperandType.None};
		
		int val1, val2;
		int val;
		
		System.out.println(ope.getOpeInfo().toString());
		
		for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
			type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
			ope.setOperand(i, readOperand(type[i]));
		}
		
		switch(ope.getOpeInfo().getOpeType()) {
		case HALT:
			break;
		
		case MOV:
//			for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
//				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
//				ope.setOperand(i, readOperand(type[i]));
//			}
//			OperandType type1 = ope.getOpeInfo().getOperandInfo().getOperandType(0);
//			ope.setOperand(0, readOperand(type1));
			val1 = ope.getOperand(0).getVal();
			System.out.printf("mov val : %X\n", val1);
//			OperandType type2 = ope.getOpeInfo().getOperandInfo().getOperandType(1);
//			ope.setOperand(1, readOperand(type2));
			memory.setValue(val1, ope.getOperand(1), type[1]);
//			memory.setRegisterValue(val1, MyEnum.registerName(ope.getOperand(1).getOperand()));
			memory.setFlag(val1 < 0, val1 == 0, false, flgC);
			break;
		
		case CHM:
//			type[0] = ope.getOpeInfo().getOperandInfo().getOperandType(0);
//			ope.setOperand(0, readOperand(type[0]));
			val1 = ope.getOperand(0).getCode();
			System.out.printf("val : %d\n", val1);
			systemCall(val1);
			break;
			
		case SUB:
//			for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
//				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
//				ope.setOperand(i, readOperand(type[i]));
//			}
			switch(ope.getOpeInfo().getOperandNum()) {
			case 2://SUBX2
				val1 = ope.getOperand(0).getVal();
				System.out.printf("val1 : %X\n", val1);
				val2 = memory.getRegisterValue(ope.getOperand(1).getRegName());
				System.out.printf("Register : %S val2 : %X\nsetValue : %X\n", ope.getOperand(1).getRegName().toString(),val2, val2 - val1);
				val2 -= val1;
				memory.setRegisterValue(val2, ope.getOperand(1).getRegName());
//				memory.setRegisterValue(val, reg);//まだわからない
				memory.setFlag(val2 < 0, val2 == 0, flgV/*わからない*/, flgC/*わからない*/);
				
			default:
				
			}
			
			break;
			
		case MOVA:
//			for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
//				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
//				ope.setOperand(i, readOperand(type[i]));
//			}
			val1 = ope.getOperand(0).getAddr();
			System.out.printf("val1 : %X\n", val1);
			memory.setRegisterValue(val1, ope.getOperand(1).getRegName());
//			System.out.printf("setRegister : %S\n", MyEnum.registerName(ope.getOperand(1).getOperand()).toString());
			System.out.printf("%S val : %X\n", ope.getOperand(1).getRegName().toString(), memory.getRegisterValue(ope.getOperand(1).getRegName()));
			memory.setFlag(val1 < 0, val1 == 0, false, flgC);
			break;
			
		case TST:
//			for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
//				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
//				ope.setOperand(i, readOperand(type[i]));
//			}
			val1 = ope.getOperand(0).getVal();
			System.out.printf("val1 : %X\n", val1);
			memory.setFlag(val1 < 0, val1 == 0, false, false);
			System.out.printf("N : %S,Z : %S,V : %S,C : %S\n", memory.getFlagN(), memory.getFlagZ(), memory.getFlagV(), memory.getFlagC());
			break;
			
		case B:
//			for(int i = 0; i < ope.getOpeInfo().getOperandNum(); i++) {
//				type[i] = ope.getOpeInfo().getOperandInfo().getOperandType(i);
//				ope.setOperand(i, readOperand(type[i]));
//			}
			val1 = ope.getOperand(0).getVal();
			System.out.printf("val1 : %X\n", val1);
			System.out.printf("BName %S\n", ope.getOpeInfo().toString());
			switch(ope.getOpeInfo().toString()) {
			case "BNEQ":
				if(memory.getFlagZ()) {
					System.out.println("Z = true");
					memory.setRegisterValue(val1, RegisterName.PC);
					textPos = memory.getRegisterValue(RegisterName.PC);
				}
				break;
				
			case "BLSS":
				if(memory.getFlagN()) {
					System.out.println("N = true");
					memory.setRegisterValue(val1, RegisterName.PC);
					textPos = memory.getRegisterValue(RegisterName.PC);
				}
				break;
				
			case "BCC":
				if(!memory.getFlagC()) {
					System.out.println("C = false");
					memory.setRegisterValue(val1, RegisterName.PC);
					textPos = memory.getRegisterValue(RegisterName.PC);
				}
				break;
				
			default:
				System.out.println("B Undefined.");
			}
			
			break;
			
		case CMP:
			val1 = ope.getOperand(0).getVal();
			val2 = ope.getOperand(1).getVal();
			System.out.printf("val1 : %X  val2 : %X\n", val1, val2);
			memory.setFlag(val1 < val2, val1 == val2, false, (val1 & 0xffffffffL) < (val2 & 0xffffffffL));
			break;
			
		case CALLS:
			System.out.println("calls");
			val1 = ope.getOperand(0).getVal();
			System.out.printf("val1 : %X\n", val1);
			val2 = ope.getOperand(1).getAddr();
			System.out.printf("val2 : %X\n", val2);
			memory.push(val1);
//			memory.setRegisterValue(memory.getRegisterValue(RegisterName.SP) - 4, RegisterName.SP);
//			//SP - 4してmemory[SP]にval1を書き込む
//			memory.writeMemory(memory.getRegisterValue(RegisterName.SP), val1);
			val = memory.getRegisterValue(RegisterName.SP);
			System.out.printf("val : %X\n", val);
			byte last = (byte)(val & 0x3);
			System.out.printf("last : %X\n", last);
			val &= 0xFFFFFFFC;
			memory.setRegisterValue(val, RegisterName.SP);
			memory.setRegisterValue(val2, RegisterName.PC);
			int entryMask = memory.readMemory(memory.getRegisterValue(RegisterName.PC), 2);
			memory.setRegisterValue(val2 + 2, RegisterName.PC);
			System.out.printf("entryMask : %X\n", entryMask);
			
			int mask = entryMask;
			for(int i = 11; i >= 0; i--) {
				mask <<= (i + 1);
				if((mask & 0x8000) != 0) {
					memory.push(memory.getRegisterValue(i));
				}
			}
			System.out.printf("nextPC : %X\n", textPos);
			memory.push(textPos);
			memory.push(memory.getRegisterValue(RegisterName.FP));
			memory.push(memory.getRegisterValue(RegisterName.AP));
			memory.setRegisterValue(val, RegisterName.AP);
			
			memory.setFlag(false, false, false, false);
			
			int maskInfo = 0x20000000 | (last << 30);
			maskInfo |= (entryMask << 16) & 0x0FFF0000;
			maskInfo |= psw & 0xFFFF;
			System.out.printf("maskInfo : %X\n", maskInfo);
			
			memory.push(maskInfo);
			memory.push(0);
			memory.setRegisterValue(memory.getRegisterValue(RegisterName.SP), RegisterName.FP);
			
			System.out.printf("PC : %X\n", memory.getRegisterValue(RegisterName.PC));
			textPos = memory.getRegisterValue(RegisterName.PC);
			break;
			
		case PUSHL:
			val1 = ope.getOperand(0).getVal();
			System.out.printf("val1 : %X\n", val1);
			memory.push(val1);
			memory.setFlag(val1 < 0, val1 == 0, false, flgC);
			break;
			
		case RET:
			memory.setRegisterValue(memory.getRegisterValue(RegisterName.FP) + 4, RegisterName.SP);
			entryMask = memory.pop();
			last = (byte)((entryMask >> 30) & 0x3);
//			boolean flg = ((entryMask >> 29) & 0x1) == 1;
			psw &= 0xFFFF0000;
			psw |= (entryMask & 0xFFFF);
			
			memory.setRegisterValue(memory.pop(), RegisterName.AP);
			memory.setRegisterValue(memory.pop(), RegisterName.FP);
			int retPC = memory.pop();
			memory.setRegisterValue(retPC, RegisterName.PC);
			textPos = retPC;
			
			mask = entryMask;
			for(int i = 0; i <= 11; ++i) {
				mask >>= i;
				if((mask & 1) == 1) {
					memory.setRegisterValue(memory.pop(), i);
				}
			}
			
			memory.setRegisterValue(memory.getRegisterValue(RegisterName.SP) + last, RegisterName.SP);
			
			
			
			break;
		
		default:
			System.out.println("Not define.");
		}
		memory.setRegisterValue(textPos, RegisterName.PC);
	}

	private Operand readOperand(OperandType type) {
		Operand ope = new Operand();
		int val, addr;
		//b = b1b2
//		byte b = textBuf[textPos++];
		byte b = (byte)memory.readMemory(textPos++, 1);
		byte b1 = (byte)((b >> 4) & 0xF);
		byte b2 = (byte)(b & 0xF);
		
		ope.setCode(b);
		
		if(type.equals(OperandType.Brb)) {
			System.out.printf("PC : %X\n", memory.getRegisterValue(RegisterName.PC));
			val = memory.getRegisterValue(RegisterName.PC) + b + 2;
			System.out.printf("val : %X\n", val);
			ope.setAddr(val);
			ope.setVal(val);
			return ope;
		}

		switch(b1) {
		case 0:
		case 1:
		case 2:
		case 3: 
			ope.setAdressingType(AdressingType.Literal);
			ope.setVal(b);
			return ope;
			
		case 4:
			ope.setAdressingType(AdressingType.Index);
//			ope.setRegName(MyEnum.registerName(i));
//			ope.setRegVal(memory.getRegisterValue(MyEnum.registerName(b)));
//			switch(type) {
//			
//			}
			return ope;
			
		case 5:
			System.out.println("レジスタモード");
			ope.setAdressingType(AdressingType.Reg);
			ope.setRegName(MyEnum.registerName(b2 & 0xF));
			ope.setAddr(memory.getRegisterValue(ope.getRegName()));
			ope.setVal(memory.getRegisterValue(ope.getRegName()));
			return ope;
			
		case 6:
			System.out.println("レジスタディファードモード");
			ope.setAdressingType(AdressingType.RegDef);
			ope.setRegName(MyEnum.registerName(b2 & 0xF));
			ope.setAddr(memory.getRegisterValue(ope.getRegName()));
			ope.setVal(memory.getRegisterValue(ope.getRegName()));
			return ope;
			
		case 7:
			ope.setAdressingType(AdressingType.AutoDec);
			System.out.println("オートデクリメント");
			ope.setRegName(MyEnum.registerName(b2 & 0xF));
			addr = memory.getRegisterValue(ope.getRegName()) - type.getByte();
			System.out.printf("addr : %X\n", addr);
			ope.setAddr(addr);
			val = memory.readMemory(addr, type.getByte());
			System.out.printf("val : %X\n", val);
			ope.setVal(val);
			return ope;
			
		case 8:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.Immediate);
				ope.setVal(readNum(type));
				
			} else {
				ope.setAdressingType(AdressingType.AutoInc);
				ope.setRegName(MyEnum.registerName(b2 & 0xF));
				ope.setAddr(memory.getRegisterValue(ope.getRegName()));
				ope.setVal(memory.getRegisterValue(ope.getRegName()));
				val = memory.getRegisterValue(ope.getRegName());
				System.out.printf("Inc val : %X\n", type.getByte());
				System.out.printf("val : %X\n", val);
			}
			return ope;
			
		case 9:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.Abs);
			} else {
				ope.setAdressingType(AdressingType.AutoIncDef);
			}
			return ope;
			
		case 0xA:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.ByteRel);
			} else {
				System.out.println("バイトディスプレイメント");
				ope.setAdressingType(AdressingType.ByteDisp);
				ope.setRegName(MyEnum.registerName(b2 & 0xF));
				addr = memory.getRegisterValue(MyEnum.registerName(b2)) + readNum(OperandType.Byte);
				ope.setAddr(addr);
				System.out.printf("ope.setVal(memory.readMemory(%X, %d))\n", ope.getAddr(), type.getByte());
				ope.setVal(memory.readMemory(ope.getAddr(), type.getByte()));
				System.out.printf("Register : %S\n", MyEnum.registerName(b2).toString());
				System.out.printf("readVal : %X\n", ope.getVal());			
			}
			return ope;
			
		case 0xB:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.ByteRelDef);
			} else {
				ope.setAdressingType(AdressingType.ByteDispDef);
				ope.setRegName(MyEnum.registerName(b2 & 0xF));
				addr = memory.getRegisterValue(MyEnum.registerName(b2)) + readNum(OperandType.Byte);
				ope.setAddr(addr);
				val = memory.readMemory(addr, 4);
				System.out.printf("addr : %X val : %X\n", addr, val);
			}
			return ope;
			
		case 0xC:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.WordRel);
			} else {
				ope.setAdressingType(AdressingType.WordDisp);
			}
			return ope;
			
		case 0xD:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.WordRelDef);
			} else {
				ope.setAdressingType(AdressingType.WordDispDef);
			}
			return ope;
			
		case 0xE:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.LongRel);
				System.out.println("ロングリラティブディファード");
				addr = readNum(OperandType.Long);
				addr += textPos;
				System.out.printf("addr : %X\n", addr);
				ope.setAddr(addr);
				val = memory.readMemory(addr, type.getByte());
				System.out.printf("val : %X\n", val);
				ope.setVal(val);
			} else {
				ope.setAdressingType(AdressingType.LongDisp);
			}
			return ope;
			
		case 0xF:
			if(b2 == 0xF) {
				ope.setAdressingType(AdressingType.LongRelDef);
			} else {
				ope.setAdressingType(AdressingType.LongDispDef);
			}
			return ope;
			
		default: return ope;
				
		}
	}
	
	private int readNum(OperandType type) {
//		byte b1, b2, b3, b4;
		int num;
		switch(type) {
		case Byte:
//			num = memory.readMemory(textPos++, 1);
//			b1 = textBuf[textPos++];
//			num = (b1 & 0xFF);
			return memory.readMemory(textPos++, 1);
			
		case Word:
//			b1 = (byte)memory.readMemory(textPos++, 1);
//			b2 = (byte)memory.readMemory(textPos++, 1);
//			b1 = textBuf[textPos++];
//			b2 = textBuf[textPos++];
			num = memory.readMemory(textPos, 2);
			textPos += 2;
			return num;
			
		case Long:
//			b1 = (byte)memory.readMemory(textPos++, 1);
//			b2 = (byte)memory.readMemory(textPos++, 1);
//			b3 = (byte)memory.readMemory(textPos++, 1);
//			b4 = (byte)memory.readMemory(textPos++, 1);
//			b1 = textBuf[textPos++];
//			b2 = textBuf[textPos++];
//			b3 = textBuf[textPos++];
//			b4 = textBuf[textPos++];
//			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
//					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
			num = memory.readMemory(textPos, 4);
			textPos += 4;
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
			System.out.printf("len : %d\n", len);
			memory.setRegisterValue(len, RegisterName.R0);
			System.out.printf("R0 : %X\n", memory.getRegisterValue(RegisterName.R0));
			break;
		default:
			return;
		}
	}
	
//	private int readNum(int index, int byteNum) {
//		byte b1, b2, b3, b4;
//		int num;
//		switch(byteNum) {
//		case 4:
//			b1 = buf[index++];
//			b2 = buf[index++];
//			b3 = buf[index++];
//			b4 = buf[index++];
//			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
//					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
//			return num;
//		}
//		return -1;
//	}
	
	private int readDataNum(int index, int byteNum) {
//		byte b1, b2, b3, b4;
		int num;
		System.out.printf("index : %X\n", index);
//		index -= dataPos;
//		System.out.printf("index : %X\n\n", index);
		switch(byteNum) {
		case 4:
			num = memory.readMemory(index, 4);
			
			System.out.printf("dataNum : %X index : %X\n", num, index);
			
			
//			b1 = dataBuf[index++];
//			b2 = dataBuf[index++];
//			b3 = dataBuf[index++];
//			b4 = dataBuf[index++];
//			num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
//					((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
//			System.out.printf("dataNum : %X\n", num);
			
			
			return num;
		}
		return -1;
	}
	
	private int sysWrite(int field, int offset, int size) {
		System.out.println("sysWrite");
//		offset -= dataPos;
		PrintStream ps;
		switch(field) {
		case 1: //標準出力
			ps = System.out;
//			ps.write(dataBuf, offset, size); //ここ？
			ps.write(memory.getMemory(), offset, size);
			return size;
		default: return -1;
		}
	}

}
