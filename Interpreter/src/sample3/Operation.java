package sample3;

import sample3.OperandInfo.OperandType;

public class Operation {
	
	enum OperationInfo {
		HALT(0x00),
		MOVL(0xD0, new OperandInfo(OperandType.Long, OperandType.Long)),
		CHMK(0xBC, new OperandInfo(OperandType.Word));
		
		private int code;
		private OperandInfo opeInfo;
		
		OperationInfo(int code) {
			this.code = code;
			this.opeInfo = new OperandInfo();
		}
		
		OperationInfo(int code, OperandInfo opeInfo) {
			this.code = code;
			this.opeInfo = opeInfo;
		}
		
		public int getCode() {
			return code;
		}
		
		public OperandInfo getOperandInfo() {
			return opeInfo;
		}
		
	}
	
	private int opCode = -1;
	
	private OperationInfo opeInfo;
	
	private Operand[] operand;
	
	
	public Operation(int opCode, OperationInfo opeInfo) {
		this.opCode = opCode;
		this.opeInfo = opeInfo;
		operand = new Operand[6];
	}
	
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
