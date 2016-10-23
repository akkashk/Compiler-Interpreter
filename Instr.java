package toy.language;

import java.util.LinkedList;

public enum Instr {
	
	NOP, PUSH, POP, LOAD, STORE, ADD, SUB, MUL, OR, AND, NOT, JMP, JZ, JP, JM, HALT;
	
	//codePtr stores relative value to add to current code pointer
	public static LinkedList<Integer> codePtr = new LinkedList<Integer>();
	
	//values store the numeric/string values for appropriate instructions 
	public static LinkedList<Object> values = new LinkedList<Object>();
	
}
