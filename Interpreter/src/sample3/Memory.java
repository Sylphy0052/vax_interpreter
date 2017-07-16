package sample3;

public class Memory {
	
	private boolean flgN, flgZ, flgV, flgC;
	
	private byte[] memory;
	private byte[] textMemory;
	private byte[] dataMemory;
	
	private int index = 0;
	private int[] register = new int[16];
	
	enum RegisterName {
		R0(0), R1(1), R2(2), R3(3),
		R4(4), R5(5), R6(6), R7(7),
		R8(8), R9(9), R10(10), R11(11),
		AP(12), FP(13), SP(14), PC(15);
		
		private int num;
		
		RegisterName(int num) {
			this.num = num;
		}
	};
	
	public Memory(byte[] memory) {
		this.memory = memory;
		init();
	}
	
	private void init() {
		for(int i = 0; i < register.length; i++) {
			register[i] = 0;
		}
	}
	
	public void setText(byte[] textMemory) {
		this.textMemory = textMemory;
	}
	
	public void setData(byte[] dataMemory) {
		this.dataMemory = dataMemory;
	}
	
	public int getRegisterValue(RegisterName reg) {
		return register[reg.num];
	}
	
	public void setRegisterValue(int val, RegisterName reg) {
		this.register[reg.num] = val;
	}
	
	public void setFlag(boolean flgN, boolean flgZ, boolean flgV, boolean flgC) {
		this.flgN = flgN;
		this.flgZ = flgZ;
		this.flgV = flgV;
		this.flgC = flgC;
	}

}
