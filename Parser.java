package toy.language;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Parser {
	
	private static final boolean DEBUG = true;
	private static final boolean TIMING = true;
	
	//static Map<NonTerminal,Map<Token,List<Sentential>>> table;
	static List<Token> inputTokens;
	static List<Integer> inputValues;
	static List<String> inputVars;
	
	static Map<Token,List<Sentential>> rowS = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowSDash = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowBexp = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowBDash  = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowAexp = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowADash = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowOPa = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowOPb = new HashMap<Token,List<Sentential>>();
	static Map<Token,List<Sentential>> rowOPr  = new HashMap<Token,List<Sentential>>();
	
	public static ParseTree pt;
	
	public static void main(String[] args) {
		Lexer.main(null);
		long startTime = System.currentTimeMillis();
		inputTokens = Lexer.tokenList;
		inputValues = Lexer.valueList;
		inputVars = Lexer.varList;
		ini();				//Initialise grammar rules
		// Start node of ParseTree
		ParseTree parseTree = new ParseTree();
		parseTree.s = NonTerminal.Prog;
		
		// S node of ParseTree
		ParseTree parseTreeS = new ParseTree();
		parseTreeS.s = NonTerminal.S;
		parseTreeS = parse(rowS,parseTreeS);
		
		// EOF node of ParseTree
		ParseTree parseTreeEOF = new ParseTree();
		parseTreeEOF.s = Token.$;
		
		parseTree.nodes.add(parseTreeS);
		parseTree.nodes.add(parseTreeEOF);
		
		pt = parseTree;
		
		if (DEBUG) display(parseTree,1);
		long endTime = System.currentTimeMillis();
		if (TIMING) System.out.println("Time taken to Parse: " + (endTime - startTime));
	}
	
	//Outputs ParseTree in textual format. depth.i shows the depth and the nth child node 
	public static void display(ParseTree p, int depth) {
		if (p == null) return;
		System.out.println("Node: "+p.s);
		int i = 1;
		for (ParseTree s: p.nodes) {
			System.out.print("Node number "+depth+"."+i+" ");
			display(s,depth+1);
			i++;
		}
	}
	
	//inputTokens, inputValues, inputVars are the input variables
	public static ParseTree parse(Map<Token,List<Sentential>> rowInput, ParseTree p) {	//We start from Row S
		Map<Token,List<Sentential>> currentRow = rowInput;
		while (!(inputTokens.get(0) == Token.$)) {
			//For given token, return the corresponding production rule
			List<Sentential> rule = currentRow.get(inputTokens.get(0));
			//NULL exists in rule and hence this expression can reduce to nothing
			if (rule == null) {
				return p;
			}
			
			//Invariant: Rule found for particular input token
			for (Sentential s: rule) {
				if (s instanceof Token) {
					if (s == inputTokens.get(0)) {
						//Create a ParseTree for the input token token
						ParseTree tok = new ParseTree();
						tok.s = s;
						//If token is NUM or VAR, store associated value in the node
						if (s == Token.NUM){
							tok.in = inputValues.get(0);
							inputValues.remove(0);
						}
						if (s == Token.VAR){
							tok.st = inputVars.get(0);
							inputVars.remove(0);
						}
						//Add token's ParseTree to input ParseTree
						p.nodes.add(tok);
						inputTokens.remove(0);	//So next token always at 0th index
					}
					else {	//inputToken did not match rule
						if (DEBUG) System.err.println("Input token did not match rule token");
					}
				}
				else {	//Next Sentential in rule is NonTerminal
					//Get possible productions for NonTerminal  
					Map<Token,List<Sentential>> temp = getRow((NonTerminal) s);
					//Create new ParseTree for NonTerminal node 
					ParseTree nextSententialtemp = new ParseTree();
					nextSententialtemp.s = s;	//Add NonTerminal as node
					nextSententialtemp = parse(temp,nextSententialtemp);
					if (nextSententialtemp == null) continue;	//IF NULL was returned, then sentential reduces to null expression so nothing in parse tree
					//At this point, the corresponding expansion of nonterminal would be done
					p.nodes.add(nextSententialtemp);
				}
			}
		}
		return p;
	}
	
	public static Map<Token,List<Sentential>> getRow(NonTerminal s) {
		switch(s) {
		case S: return rowS;
		case SDash: return rowSDash;
		case Bexp: return rowBexp;
		case BexpDash: return rowBDash;
		case Aexp: return rowAexp;
		case AexpDash: return rowADash;
		case OPa: return rowOPa;
		case OPb: return rowOPb;
		case OPr: return rowOPr;
		default: return null;
		}
	}
	
	public static void ini() {
		//For input terminals in rowS and rowSDash
		List<Sentential> skip = new LinkedList<Sentential>();
		List<Sentential> var = new LinkedList<Sentential>();
		List<Sentential> iff = new LinkedList<Sentential>();
		List<Sentential> whilee = new LinkedList<Sentential>();
		List<Sentential> semi = new LinkedList<Sentential>();
		List<Sentential> braces = new LinkedList<Sentential>();
		List<Sentential> semi2 = new LinkedList<Sentential>();

		skip.add(Token.SKIP); skip.add(NonTerminal.SDash);
		var.add(Token.VAR); var.add(Token.ASSIGN); var.add(NonTerminal.Aexp); var.add(NonTerminal.SDash);
		iff.add(Token.IF); iff.add(NonTerminal.Bexp); iff.add(Token.THEN); iff.add(NonTerminal.S); iff.add(Token.ELSE); iff.add(NonTerminal.S); iff.add(NonTerminal.SDash); 
		whilee.add(Token.WHILE); whilee.add(NonTerminal.Bexp); whilee.add(Token.DO); whilee.add(NonTerminal.S); whilee.add(NonTerminal.SDash);
		semi.add(Token.SEMI); semi.add(NonTerminal.S); semi.add(NonTerminal.SDash);
		braces.add(Token.LBRACES); braces.add(NonTerminal.S); braces.add(Token.RBRACES); braces.add(NonTerminal.SDash);
		//Add all lists to rowS
		rowS.put(Token.SKIP, skip); rowS.put(Token.VAR, var); rowS.put(Token.IF, iff); rowS.put(Token.WHILE, whilee); rowS.put(Token.SEMI, semi);  rowS.put(Token.LBRACES, braces);

		//Input terminals in rowS'
		semi2.add(Token.SEMI); semi2.add(NonTerminal.S); semi2.add(NonTerminal.SDash);
		rowSDash.put(Token.SEMI, semi2); rowSDash.put(Token.NULL,new LinkedList<Sentential>());

		//For input terminals in rowBexp and rowBDash
		List<Sentential> truee = new LinkedList<Sentential>();
		List<Sentential> falsee = new LinkedList<Sentential>();
		List<Sentential> not = new LinkedList<Sentential>();
		List<Sentential> lbracket = new LinkedList<Sentential>();
		List<Sentential> num = new LinkedList<Sentential>();
		List<Sentential> var2 = new LinkedList<Sentential>();
		List<Sentential> and = new LinkedList<Sentential>();
		List<Sentential> or = new LinkedList<Sentential>();

		truee.add(Token.TRUE); truee.add(NonTerminal.BexpDash);
		falsee.add(Token.FALSE); falsee.add(NonTerminal.BexpDash);
		not.add(Token.NOT); not.add(NonTerminal.Bexp); not.add(NonTerminal.BexpDash);
		//NEED TO HACK THE NEXT LINE
		lbracket.add(Token.LPAREN); lbracket.add(NonTerminal.Bexp); lbracket.add(Token.RPAREN); lbracket.add(NonTerminal.BexpDash);
		num.add(NonTerminal.Aexp); num.add(NonTerminal.OPr); num.add(NonTerminal.Aexp); num.add(NonTerminal.BexpDash);
		var2.add(NonTerminal.Aexp); var2.add(NonTerminal.OPr); var2.add(NonTerminal.Aexp); var2.add(NonTerminal.BexpDash);
		rowBexp.put(Token.TRUE, truee); rowBexp.put(Token.FALSE, falsee); rowBexp.put(Token.NOT, not); rowBexp.put(Token.LPAREN, lbracket); rowBexp.put(Token.NUM, num); rowBexp.put(Token.VAR, var2);

		//For Bexp'
		and.add(NonTerminal.OPb); and.add(NonTerminal.Bexp);  and.add(NonTerminal.BexpDash);
		or.add(NonTerminal.OPb); or.add(NonTerminal.Bexp);  or.add(NonTerminal.BexpDash);
		rowBDash.put(Token.AND, and); rowBDash.put(Token.OR, or); rowBDash.put(Token.NULL,new LinkedList<Sentential>()); 

		//For Aexp
		List<Sentential> num3 = new LinkedList<Sentential>();
		List<Sentential> var3 = new LinkedList<Sentential>();
		List<Sentential> lparen = new LinkedList<Sentential>();
		List<Sentential> plus = new LinkedList<Sentential>();
		num3.add(Token.NUM); num3.add(NonTerminal.AexpDash);
		var3.add(Token.VAR); var3.add(NonTerminal.AexpDash);
		lparen.add(Token.LPAREN); lparen.add(NonTerminal.Aexp); lparen.add(Token.RPAREN); lparen.add(NonTerminal.AexpDash);
		rowAexp.put(Token.NUM, num3); rowAexp.put(Token.VAR, var3); rowAexp.put(Token.LPAREN, lparen);

		plus.add(NonTerminal.OPa); plus.add(NonTerminal.Aexp); plus.add(NonTerminal.AexpDash);
		rowADash.put(Token.PLUS,plus); rowADash.put(Token.MULT,plus); rowADash.put(Token.NULL,new LinkedList<Sentential>());      

		//Opr OPb OPa
		List<Sentential> plus2 = new LinkedList<Sentential>();
		List<Sentential> mult2 = new LinkedList<Sentential>();
		List<Sentential> and2 = new LinkedList<Sentential>();
		List<Sentential> or2 = new LinkedList<Sentential>();
		List<Sentential> lt= new LinkedList<Sentential>();
		List<Sentential> gt = new LinkedList<Sentential>();
		List<Sentential> eq = new LinkedList<Sentential>();
		plus2.add(Token.PLUS); 
		mult2.add(Token.MULT);
		rowOPa.put(Token.PLUS, plus2); rowOPa.put(Token.MULT, mult2);

		and2.add(Token.AND); 
		or2.add(Token.OR);
		rowOPb.put(Token.AND, and2); rowOPb.put(Token.OR, or2);

		lt.add(Token.LT); 
		gt.add(Token.GT);
		eq.add(Token.EQ);
		rowOPr.put(Token.LT, lt); rowOPr.put(Token.GT, gt); rowOPr.put(Token.EQ, eq);
	}
}
