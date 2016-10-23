package toy.language;

import java.util.LinkedList;

public class CodeGeneratorV2 {

	private static final boolean DEBUG = true;
	private static final boolean TIMING = true;
	
	static LinkedList<Instr> instructions = new LinkedList<Instr>();
	static boolean assign = false;
	static int elseBlockEndGlobal = 0;
	static int ifFalseGlobal = 0;
	static int doBlockEndGlobal = 0;
	static int doBlockEndJMPGlobal = 0;

	public static void main(String[] args) {
		Parser.main(null);
		long startTime = System.currentTimeMillis();
		codegen(Parser.pt);	// TRUE > 0 		FALSE < 0
		long endTime = System.currentTimeMillis();
		if (TIMING) System.out.println("Time taken to Generate code: " + (endTime - startTime));
		if (DEBUG) System.out.println("Final instructions: "+instructions.toString());
		if (DEBUG) System.out.println("Values: "+Instr.values.toString());
		if (DEBUG) System.out.println("CodePtr: "+Instr.codePtr.toString());
	}
	
	public static void codegen(ParseTree p) {
		//For each remaining node, moving from right to left of ParseTree
		boolean checked = false;
		int elseBlockEnd = 0;
		int doBlockEnd = 0;
		int doBlockEndJMP = 0;
		while (p.nodes.size() != 0) {
			if (DEBUG) System.out.println(instructions.toString());
			if (!checked)	{
				for (ParseTree pt: p.nodes) {
					if (pt.s == Token.ASSIGN && p.nodes.size() == 3) {
						ParseTree var = p.nodes.removeFirst();
						ParseTree val = p.nodes.removeLast();
						p.nodes.addLast(var); p.nodes.addFirst(val);
						assign = true; checked = true;
						break;
					}
					if (pt.s == NonTerminal.OPa || pt.s == NonTerminal.OPr || pt.s == NonTerminal.OPb) {
						int index = p.nodes.indexOf(pt);
						ParseTree opa = p.nodes.remove(index);
						if (p.nodes.size() < 3) p.nodes.addLast(opa);
						else p.nodes.add(2, opa);
						checked = true;
						break;
					}
					if (pt.s == Token.ELSE) {
						elseBlockEnd = instructions.size();
						elseBlockEndGlobal = elseBlockEnd;
						checked = true;
						break;
					}
					if (pt.s == Token.DO) {
						doBlockEnd = instructions.size();
						doBlockEndJMP = Instr.codePtr.size();
						doBlockEndGlobal = doBlockEnd;
						doBlockEndJMPGlobal = doBlockEndJMP;
						instructions.addFirst(Instr.JMP);
						checked = true;
						break;
					}
				}
			}
			ParseTree child = p.nodes.removeLast();
			if (child.s instanceof NonTerminal) {
				if (DEBUG) System.out.println("NonTerminal: "+child.s);
				codegen(child);
			}
			else /*lastChild.s instanceof Token*/ {
				if (DEBUG) System.out.println("Token: "+child.s);
				decode(child);
				continue;	// decode() will do appropriate checks and add instructions to list
			}
		} // The ParseTree had no children and should evaluate to null string
	}
	
	public static void decode(ParseTree pt) {// Needs to be ParseTree so we can get variable name/vale from tree when decoding
		Token t = (Token) pt.s;
		switch (t) {
			case NUM:	instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(pt.in);
						break;
			case VAR: 	if (assign) { // Variable should return STORE instruction
							instructions.addFirst(Instr.STORE);
							Instr.values.addFirst(pt.st);
						}
						else {
							instructions.addFirst(Instr.LOAD);
							Instr.values.addFirst(pt.st);
						}
						break;
			case PLUS: 	instructions.addFirst(Instr.ADD);
						break;
			case MULT:	instructions.addFirst(Instr.MUL);
						break;
			case AND: 	instructions.addFirst(Instr.AND);
						break;
			case OR: 	instructions.addFirst(Instr.OR);
						break;
			case NOT: 	instructions.addFirst(Instr.NOT);
						break;
			case ASSIGN:assign = false;
						break;
			case LT:	instructions.addFirst(Instr.SUB);
						break;
			case GT:	instructions.addFirst(Instr.MUL);
						instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(-1);	//To convert subtraction into positive number and make statement true
						instructions.addFirst(Instr.SUB);
						break;
			case EQ: 	instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(1);	//To return true
						instructions.addFirst(Instr.JMP);
						Instr.codePtr.addFirst(2);	//PUSH -1 already executed, from current cp add 2
						instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(-1);	//To return false
						instructions.addFirst(Instr.JZ);
						Instr.codePtr.addFirst(3);	//Current cp at JZ, add 3 to this and go to PUSH 1 instruction
						instructions.addFirst(Instr.SUB);
						break;
			case TRUE:	instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(1);
						break;
			case FALSE: instructions.addFirst(Instr.PUSH);
						Instr.values.addFirst(-1);
						break;
			case IF:	break;
			case THEN:	Instr.codePtr.addFirst(instructions.size() - ifFalseGlobal + 1);
						instructions.addFirst(Instr.JM);
						break;
			case ELSE:	ifFalseGlobal = instructions.size();
						Instr.codePtr.addFirst(instructions.size() - elseBlockEndGlobal + 1);
						instructions.addFirst(Instr.JMP);
						break;
			case WHILE:	int temp1 = Instr.codePtr.size(); //Add codePtr for jump instruction at end of Do loop
						int temp2 = doBlockEndJMPGlobal;
						if (Instr.codePtr.size() == temp1-temp2) Instr.codePtr.addLast(doBlockEndGlobal-instructions.size() + 1);
						else Instr.codePtr.set(Instr.codePtr.size()-doBlockEndJMPGlobal,doBlockEndGlobal-instructions.size() + 1);
						break;
			case DO:	Instr.codePtr.addFirst(instructions.size() - doBlockEndGlobal + 1);
						instructions.addFirst(Instr.JM);
						break;
			case $:		instructions.add(Instr.HALT);
						break;
			default:	break;	//All other Tokens: skip, semi, braces, paren
		}
	}
}