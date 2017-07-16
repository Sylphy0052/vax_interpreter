package sample2;

abstract class Operation {
	
	protected int opCode;
	private String opName;
	protected OperandType[] opType = {OperandType.BYTE, OperandType.BYTE,OperandType.BYTE,OperandType.BYTE,OperandType.BYTE,OperandType.BYTE};
	protected int[] operand;
	private int operandNum;
	
	public Operation(int opCode, String opName) {
		this.opCode = opCode;
		this.opName = opName;
		opType = null;
		operandNum = 0;
	}
	
	public Operation(int opCode, String opName, OperandType type1) {
		this.opCode = opCode;
		this.opName = opName;
		opType[0] = type1;
		operandNum = 1;
		operand = new int[1];
	}
	
	public Operation(int opCode, String opName, OperandType type1, OperandType type2) {
		this.opCode = opCode;
		this.opName = opName;
		opType[0] = type1;
		opType[1] = type2;
		operandNum = 2;
		operand = new int[2];
	}
	
	public int getCode() {
		return opCode;
	}
	
	public String getName() {
		return opName;
	}
	
	public boolean setOperand(int num, int operand) {
		if(num < operandNum) {
			return false;
		}
		this.operand[num] = operand;
		return true;
	}
	
	abstract void behavior();
	
	enum OperandType {
		BYTE(1), WORD(2), LONG(4), QUAD(8), OCTA(16),
		FLOATF(4, "[F-FLOAT]"), FLOATD(8, "[D-FLOAT]");
		
		public final int readByteNum;
		public final String operandName;
		
		private OperandType(int readByteNum) {
			this.readByteNum = readByteNum;
			this.operandName = null;
		}
		
		private OperandType(int readByteNum, String operandName) {
			this.readByteNum = readByteNum;
			this.operandName = operandName;
		}
	}	
	
}

//class MOV extends Operation {
//	public MOV(int opCode, String opName, OperandType type1, OperandType type2) {
//		super(opCode, opName, type1, type2);
//	}
//	
//	public void behavior() {
//		
//	}
//	
//}
//
//class CHMK extends Operation {
//	public CHMK(int opCode, String opName, OperandType type1) {
//		super(opCode, opName, type1);
//	}
//	
//	public void behavior() {
//		
//	}
//}
//
//class HALT extends Operation {
//	public HALT(int opCode, String opName) {
//		super(opCode, opName);
//	}
//	
//	public void behavior() {
//		
//	}
//}


