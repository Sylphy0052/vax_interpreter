package sample4;

import sample4.MyEnum.AdressingType;
import sample4.MyEnum.OperandType;
import sample4.MyEnum.RegisterName;

public class Operand {
	
	private AdressingType type;
	
	private int addrVal, code, addr;
	
	private MyEnum.RegisterName regName;
	
	private MyEnum.OperandType opeType;
	
	public Operand() {
		init();
	}
	
	public void init() {
		this.addrVal = 0;
		this.regName = null;
		this.code = 0;
		this.addr = 0;
		this.type = AdressingType.None;
		this.opeType = null;
	}
	
	public void setAdressingType(AdressingType type) {
		this.type = type;
	}
	
	public void setVal(int addrVal) {
		this.addrVal = addrVal;
	}
	
	public void setRegName(MyEnum.RegisterName regName) {
		this.regName = regName;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public void setAddr(int addr) {
		this.addr = addr;
	}
	
	public void setOpeType(OperandType opeType) {
		this.opeType = opeType;
	}
	
//	public void setRegVal(int regVal) {
//		this.regVal = regVal;
//	}
	
	public int getVal() {
		return this.addrVal;
	}
	
	public RegisterName getRegName() {
		return this.regName;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public int getAddr() {
		return addr;
	}
	
	public OperandType getOpeType() {
		return opeType;
	}
	
//	public int getRegVal() {
//		return this.regVal;
//	}
	
	public AdressingType getAdressingType() {
		return this.type;
	}

}
