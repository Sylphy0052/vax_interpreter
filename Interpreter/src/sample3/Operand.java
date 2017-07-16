package sample3;

import sample3.BinaryAnalyze.AdressingType;

public class Operand {
	
	private AdressingType type;
	
	private int addrVal, operand, regVal;
	
	public Operand() {
		init();
	}
	
	public void init() {
		this.addrVal = 0;
		this.operand = 0;
		this.regVal = 0;
		this.type = AdressingType.None;
	}
	
	public void setAdressingType(AdressingType type) {
		this.type = type;
	}
	
	public void setVal(int addrVal) {
		this.addrVal = addrVal;
	}
	
	public void setOperand(int operand) {
		this.operand = operand;
	}
	
	public void setRegVal(int regVal) {
		this.regVal = regVal;
	}
	
	public int getVal() {
		return this.addrVal;
	}
	
	public int getOperand() {
		return this.operand;
	}
	
	public int getRegVal() {
		return this.regVal;
	}
	
	public AdressingType getAdressingType() {
		return this.type;
	}

}
