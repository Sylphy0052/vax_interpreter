package sample4;

import sample4.MyEnum.OperandType;

public class Operation {
	
	enum OperationType {
		HALT, MOV, CHM, SUB, MOVA, TST, B, CMP, CALLS, PUSHL, RET;
	}
	
	enum OperationInfo {
		HALT(0x00, OperationType.HALT),
		MOVL(0xD0, OperationType.MOV, new OperandInfo(OperandType.Long, OperandType.Long)),
		CHMK(0xBC, OperationType.CHM, new OperandInfo(OperandType.Word)),
		SUBL2(0xC2, OperationType.SUB, new OperandInfo(OperandType.Long, OperandType.Long)),
		MOVAB(0x9E, OperationType.MOVA, new OperandInfo(OperandType.Byte, OperandType.Long)),
		TSTL(0xD5, OperationType.TST, new OperandInfo(OperandType.Long)),
		BNEQ(0x12, OperationType.B, new OperandInfo(OperandType.Brb)),
		CMPL(0xD1, OperationType.CMP, new OperandInfo(OperandType.Long, OperandType.Long)),
		BLSS(0x19, OperationType.B, new OperandInfo(OperandType.Brb)),
		CALLS(0xFB, OperationType.CALLS, new OperandInfo(OperandType.Long, OperandType.Byte)),
		PUSHL(0xDD, OperationType.PUSHL, new OperandInfo(OperandType.Long)),
		BCC(0x1E, OperationType.B, new OperandInfo(OperandType.Brb)),
		RET(0x04, OperationType.RET)
		;
		
		private int code;
		private OperationType opeType;
		private OperandInfo opeInfo;
		private int opeNum;
		
		OperationInfo(int code, OperationType opeType) {
			this.code = code;
			this.opeType = opeType;
			this.opeInfo = new OperandInfo();
			this.opeNum = 0;
		}
		
		OperationInfo(int code, OperationType opeType, OperandInfo opeInfo) {
			this.code = code;
			this.opeType = opeType;
			this.opeInfo = opeInfo;
			this.opeNum = opeInfo.getNum();
		}
		
		public int getCode() {
			return code;
		}
		
		public OperandInfo getOperandInfo() {
			return opeInfo;
		}
		
		public OperationType getOpeType() {
			return this.opeType;
		}
		
		public int getOperandNum() {
			return opeNum;
		}
		
	}
	
	private int opCode = -1;
	
	private OperationInfo opeInfo;
	
	private Operand[] operand;
	
	
	public Operation(int opCode, OperationInfo opeInfo) {
		this.opCode = opCode;
		this.opeInfo = opeInfo;
		operand = new Operand[6];
//		setOperandType();
	}
	
//	private void setOperandType() {
//		for(int i = 0; i < opeInfo.opeNum; i++) {
//			operand[i].setOpeType(opeInfo.getOperandInfo().getOperandType(i));
//		}
//	}
	
	public OperationInfo getOpeInfo() {
		return opeInfo;
	}
	
	public int getCode() {
		return opCode;
	}
	
	public void init() {
		opCode = -1;
		opeInfo = null;
		for(int i = 0; i < 6; i++) {
			operand[i].init();
		}
	}
	
	public Operand getOperand(int i) {
		return operand[i];
	}
	
	public void setOperand(int i, Operand operand) {
		this.operand[i] = operand;
	}

}
