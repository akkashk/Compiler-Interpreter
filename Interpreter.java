package toy.language;

import java.util.HashMap;
import java.util.LinkedList;

public class Interpreter {
	
	private static final boolean DEBUG = true;
	private static final boolean TIMING = true;
	
	static LinkedList<Instr> instr = new LinkedList<Instr>();
	static LinkedList<Integer> codePt = new LinkedList<Integer>();	
	static LinkedList<Object> instrv = new LinkedList<Object>();
	
	static LinkedList<Integer> stack = new LinkedList<Integer>();
	static HashMap<String,Integer> varBind = new HashMap<String,Integer>();
	
	static int instrCp = 0;
	static int codePtCp = 0;
	static int instrvCp = 0;
	
	public static void main(String[] args) {
		CodeGeneratorV2.main(null);
		long startTime = System.currentTimeMillis();
		instr = CodeGeneratorV2.instructions;
		codePt = Instr.codePtr;
		instrv = Instr.values;
		while(instr.get(instrCp) != Instr.HALT) {
			interpret();
		}
		long endTime = System.currentTimeMillis();
		if (TIMING) System.out.println("Time taken to Interpret: " + (endTime - startTime));
		System.out.println("-----EXECUTION COMPLETED-----");
		System.out.println("Value in stack: "+stack.toString());
		System.out.println("Variable values: "+varBind.toString());
	}
	
	public static void interpret() {
		if (DEBUG) {
			System.out.println("Stack: "+stack.toString());
			System.out.println("Env: "+varBind.toString());
			System.out.println("Instr Pointer: "+instrCp);
			System.out.println("Code Pointer Pointer: "+codePtCp);
			System.out.println("Instr Values Pointer: "+instrvCp);
		}
		Instr i = instr.get(instrCp);
		switch (i) {
			case NOP: 	break;
			case PUSH:	stack.push((int) instrv.get(instrvCp));
						instrCp++; instrvCp++;
						break;
			case POP:	stack.pop();
						instrCp++;
						break;
			case LOAD:	stack.push(varBind.get(instrv.get(instrvCp)));
						instrCp++; instrvCp++;
						break;
			case STORE:	varBind.put((String)instrv.get(instrvCp),stack.pop());
						instrCp++; instrvCp++;
						break;
			case ADD:	int x = stack.pop();
						int y = stack.pop();
						stack.push(x+y);
						instrCp++;
						break;
			case SUB:	int x1 = stack.pop();
						int y1 = stack.pop();
						stack.push(x1-y1);
						instrCp++;
						break;
			case MUL:	int x2 = stack.pop();
						int y2 = stack.pop();
						stack.push(x2*y2);
						instrCp++;
						break;
			case OR:	int x3 = stack.pop();
						int y3 = stack.pop();
						stack.push(x3 | y3);
						instrCp++;
						break;
			case AND:	int x4 = stack.pop();
						int y4 = stack.pop();
						stack.push(x4 & y4);
						instrCp++;
						break;
			case NOT:	int x5 = stack.pop();
						stack.push(x5*-1);
						instrCp++;
						break;
			case JMP:	jumps();
						break;
			case JZ:	if (stack.pop() != 0) {
							instrCp++; codePtCp++;
							break;
						}
						jumps();
						break;
			case JP:	if (stack.pop() < 0) {
							instrCp++; codePtCp++;
							break;
						}
						jumps();
						break;
			case JM:	if (stack.pop() > 0) {
							instrCp++; codePtCp++;
							break;
						}
						jumps();
						break;
			default:	break;
		}
	}
	
	public static void jumps() {
		boolean minus = codePt.get(codePtCp) < 0;
		if (minus) {
			for (int j=codePt.get(codePtCp); j<0; j++) {
				instrCp--;	//When moving backwards, decrement instruction first and then do checks 
				Instr temp = instr.get(instrCp);
				if (temp == Instr.JMP || temp == Instr.JM || temp == Instr.JP || temp == Instr.JZ) 
					codePtCp--;
				if (temp == Instr.PUSH || temp == Instr.POP || temp == Instr.LOAD || temp == Instr.STORE) 
					instrvCp--;
			}
		}
		else {
			for (int j=codePt.get(codePtCp); j>0; j--) {
				Instr temp = instr.get(instrCp);
				if (temp == Instr.JMP || temp == Instr.JM || temp == Instr.JP || temp == Instr.JZ) codePtCp++;
				if (temp == Instr.PUSH || temp == Instr.POP || temp == Instr.LOAD || temp == Instr.STORE) instrvCp++;
				instrCp++;	//When moving forwards, increment instruction first and then do checks
			}
		}
	}
}