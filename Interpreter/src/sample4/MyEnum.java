package sample4;

public class MyEnum {

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
	
	enum RegisterName {
		R0(0), R1(1), R2(2), R3(3),
		R4(4), R5(5), R6(6), R7(7),
		R8(8), R9(9), R10(10), R11(11),
		AP(12), FP(13), SP(14), PC(15);
		
		private int num;
		
		RegisterName(int num) {
			this.num = num;
		}
		
		public int getNum() {
			return this.num;
		}
	}
	
	enum OperandType {
		None(1), Byte(1), Word(2), Long(4), Float(4), Brb(1);
		
		private int readByte;
		
		OperandType(int readByte) {
			this.readByte = readByte;
		}
		
		public int getByte() {
			return readByte;
		}
	}
	
	public static RegisterName registerName(int i) {
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
	
	
	
}
