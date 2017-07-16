package sample4;

//import sample4.MyEnum.AdressingType;
import sample4.MyEnum.OperandType;
import sample4.MyEnum.RegisterName;

public class Memory {
	
	private boolean flgN, flgZ, flgV, flgC;
	
	private byte[] memory;
	
	private int[] register = new int[16];
	
	public Memory(byte[] memory) {
		this.memory = memory;
		init();
	}
	
	private void init() {
		for(int i = 0; i < register.length; i++) {
			register[i] = 0;
		}
		memory = new byte[0x100000];
	}
	
	public int readMemory(int offset, int length) {
		byte b1, b2, b3, b4;
		b1 = b2 = b3 = b4 = 0;
		switch(length) {
		case 4:
			b4 = memory[offset + 3];
		case 3:
			b3 = memory[offset + 2];
		case 2:
			b2 = memory[offset + 1];
		case 1:
			b1 = memory[offset];
		}
		int num = (b1 & 0xFF) | ((b2 & 0xFF) << 8) | 
				((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
		return num;
	}
	
	public void writeMemory(int offset, int data) {
		writeMemory(offset, data, 1);
	}
	
	private void writeMemory(int offset, int data, int length) {
//		System.out.printf("writeMemory(%X, %X, %d)\n", offset, (byte)data, length);
		byte[] buf = new byte[4];
		switch(length) {
		case 4:
			buf[3] = (byte)((data & 0xFF000000) >> 24);
			buf[2] = (byte)((data & 0x00FF0000) >> 16);
			
		case 2:
			buf[1] = (byte)((data & 0x0000FF00) >> 8);
		case 1:
			buf[0] = (byte)((data & 0x000000FF));
		default:
			
		}
		
		for(int i = offset; i < offset + length; i++) {
			memory[i] = buf[i - offset];
			}
	}
	
	public int getRegisterValue(RegisterName reg) {
		return register[reg.getNum()];
	}
	
	public int getRegisterValue(int num) {
		return register[num];
	}
	
	public void setValue(int val, Operand ope, OperandType type) {
		switch(ope.getAdressingType()) {
		case Reg:
			setRegisterValue(val, ope.getRegName());
			break;
			
		case RegDef:
			System.out.printf("writeMemory(%X, %X, %d)\n", ope.getAddr(), val, type.getByte());
			writeMemory(ope.getAddr(), val, type.getByte());
			break;
			
		case ByteDisp:
			System.out.printf("writeMemory(%X, %X, %d)\n", ope.getAddr(), val, type.getByte());
			writeMemory(ope.getAddr(), val, type.getByte());
			break;
			
		case LongRel:
			writeMemory(ope.getVal(), val, type.getByte());
			break;
			
		default:
			System.out.println("Undefined. RegType : " + ope.getAdressingType().toString() + "\n\n");
		}
		
//		if(ope.getAdressingType() == AdressingType.Reg) {
//			setRegisterValue(val, ope.getRegName());
//		} else {
//			writeMemory(, val,);
//		}
	}
	
	public void setRegisterValue(int val, RegisterName reg) {
		this.register[reg.getNum()] = val;
	}
	
	public void setRegisterValue(int val, int num) {
		this.register[num] = val;
	}
	
	public void setFlag(boolean flgN, boolean flgZ, boolean flgV, boolean flgC) {
		this.flgN = flgN;
		this.flgZ = flgZ;
		this.flgV = flgV;
		this.flgC = flgC;
	}
	
	public boolean getFlagN() {
		return flgN;
	}
	
	public boolean getFlagZ() {
		return flgZ;
	}
	
	public boolean getFlagV() {
		return flgV;
	}
	
	public boolean getFlagC() {
		return flgC;
	}
	
	
	public void memoryShow(int length) {
		System.out.println("\n***memoryShow***\n");
		for(int i = 0; i < length; i++) {
			if(i % 16 == 0) {
				System.out.printf("\n%08X : ", i);
			}
			System.out.printf("%02X ", memory[i]);
		}
	}
	
	public void push(int val) {
		this.register[RegisterName.SP.getNum()] -= 4;
		writeMemory(this.register[RegisterName.SP.getNum()], val, 4);
	}
	
	public int pop() {
		int addr = this.register[RegisterName.SP.getNum()];
		int val = readMemory(addr, 4);
		setRegisterValue(addr + 4, RegisterName.SP);
		return val;
	}
	
	public byte[] getMemory() {
		return memory;
	}

}
