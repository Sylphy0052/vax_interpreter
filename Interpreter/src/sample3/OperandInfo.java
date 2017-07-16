package sample3;

public class OperandInfo {
	
	private int num;
	private OperandType type1;
	private OperandType type2;
	private OperandType type3;
	private OperandType type4;
	private OperandType type5;
	private OperandType type6;
	
	
	enum OperandType {
		None(1), Byte(1), Word(2), Long(4), Float(4);
		
		private int readByte;
		
		OperandType(int readByte) {
			this.readByte = readByte;
		}
		
		public int getByte() {
			return readByte;
		}
	}
	
	public OperandInfo() {
		num = 0;
	}
	
	public OperandInfo(OperandType type1) {
		this.type1 = type1;
		num = 1;
	}
	
	public OperandInfo(OperandType type1, OperandType type2) {
		this(type1);
		this.type2 = type2;
		num = 2;
	}
	
	public OperandInfo(OperandType type1, OperandType type2, OperandType type3) {
		this(type1, type2);
		this.type3 = type3;
		num = 3;
	}
	
	public OperandInfo(OperandType type1, OperandType type2, 
			OperandType type3, OperandType type4) {
		this(type1, type2, type3);
		this.type4 = type4;
		num = 4;
	}
	
	public OperandInfo(OperandType type1, OperandType type2, OperandType type3, 
			OperandType type4, OperandType type5) {
		this(type1, type2, type3, type4);
		this.type5 = type5;
		num = 5;
	}
	
	public OperandInfo(OperandType type1, OperandType type2, OperandType type3, 
			OperandType type4, OperandType type5, OperandType type6) {
		this(type1, type2, type3, type4, type5);
		this.type6 = type6;
		num = 6;
	}
	
	public int getNum() {
		return num;
	}
	
	public OperandType getOperandType(int num) {
		switch(num) {
		case 0: return type1;
		case 1: return type2;
		case 2: return type3;
		default: return null;
		}
	}

}
