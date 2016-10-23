package toy.language;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Lexer {
	
	private static final boolean DEBUG = true;
	private static final boolean TIMING = true;
	
	static List<Token> tokenList = new LinkedList<Token>();		//Stores list of tokens
	static List<Integer> valueList = new LinkedList<Integer>();	//Stores list of NUM values in order of Token NUM order
	static List<String> varList = new LinkedList<String>();		//Stores list of VAR values in order of Token VAR order

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			BufferedReader r = new BufferedReader(new FileReader("C:\\Users\\Akkash\\Desktop\\InputCode.txt"));	//Input file path
			String input = "";
			while (r.ready()) {
				input = input+r.readLine()+" ";
			}
			input = input.replaceAll("\\s+", "");			//Get rid of all whitespace
			lex(input);
			tokenList.add(Token.$);							//Add end of file marker
			r.close();
			if (DEBUG) System.out.println();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found"); 
		} catch (IOException e) {
			System.out.println("IOE");
		}
		long endTime = System.currentTimeMillis();
		if (TIMING) System.out.println("Time taken to Lex: " + (endTime - startTime));
	}
	
	public static void lex(String input) {
		String currentInp = "";
		Token lastAccepting = null;
		boolean skip = false;								//Skip adding another input char for one loop
		while (input.length()>0) {
			if (!skip) {
				String nextSym = input.substring(0, 1);		//Get next char
				currentInp += nextSym;						//Add next char to string under consideration as token
				input = input.substring(1, input.length());	//Reduce input by one char
			}
			skip = false;									//Reset skipping of adding one char
			
			if (currentInp.matches("\\d+")) {				//Matches 1+ digit(s)
				lastAccepting = Token.NUM;
				if (input.length()>0) continue;				//Since no other token can match this
				else {										//End of file
					if (DEBUG) System.out.print("<NUM,"+currentInp.substring(0, currentInp.length())+">");
					tokenList.add(Token.NUM);
					valueList.add(Integer.decode(currentInp.substring(0, currentInp.length())));
					return;
				}
			}
			
			if (lastAccepting != null && lastAccepting == Token.NUM) {
				if (DEBUG) System.out.print("<NUM,"+currentInp.substring(0, currentInp.length()-1)+">");
				tokenList.add(Token.NUM);
				valueList.add(Integer.decode(currentInp.substring(0, currentInp.length()-1)));
				skip = true;								//Skip adding another symbol
				currentInp = currentInp.substring(currentInp.length()-1, currentInp.length());
				lastAccepting = null;
			}
			
			if (currentInp.matches("\\+")) {				//Matches +
				if (DEBUG) System.out.print("<PLUS>");
				tokenList.add(Token.PLUS);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\*")) {				//Matches *
				if (DEBUG) System.out.print("<MULT>");
				tokenList.add(Token.MULT);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches(">")) {					//Matches >
				if (DEBUG) System.out.print("<GT>");
				tokenList.add(Token.GT);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("<")) {					//Matches <
				if (DEBUG) System.out.print("<LT>");
				tokenList.add(Token.LT);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("=")) {					//Matches =
				if (DEBUG) System.out.print("<EQ>");
				tokenList.add(Token.EQ);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\(")) {				//Matches (
				if (DEBUG) System.out.print("<LPAREN>");
				tokenList.add(Token.LPAREN);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\)")) {				//Matches )
				if (DEBUG) System.out.print("<RPAREN>");
				tokenList.add(Token.RPAREN);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\!")) {				//Matches !
				if (DEBUG) System.out.print("<NOT>");
				tokenList.add(Token.NOT);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches(":=")) {					//Matches :=
				if (DEBUG) System.out.print("<ASSIGN>");
				tokenList.add(Token.ASSIGN);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches(";")) {					//Matches ;
				if (DEBUG) System.out.print("<SEMI>");
				tokenList.add(Token.SEMI);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\{")) {				//Matches {
				if (DEBUG) System.out.print("<LBRACES>");
				tokenList.add(Token.LBRACES);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("\\}")) {				//Matches }
				if (DEBUG) System.out.print("<RBRACES>");
				tokenList.add(Token.RBRACES);
				currentInp = ""; continue;
			}
			
			if (currentInp.matches("and")) {				//Matches and
				if (DEBUG) System.out.print("<AND>");
				tokenList.add(Token.AND);
				currentInp = ""; lastAccepting = null;		//If VAR has already set this to be Token.VAR then clearing it 
				continue; 
			}
			
			if (currentInp.matches("or")) {					//Matches or
				if (DEBUG) System.out.print("<OR>");
				tokenList.add(Token.OR);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("true")) {				//Matches true
				if (DEBUG) System.out.print("<TRUE>");
				tokenList.add(Token.TRUE);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("false")) {				//Matches false
				if (DEBUG) System.out.print("<FALSE>");
				tokenList.add(Token.FALSE);
				currentInp = ""; lastAccepting = null; continue;
			}
			if (currentInp.matches("skip")) {				//Matches skip
				if (DEBUG) System.out.print("<SKIP>");
				tokenList.add(Token.SKIP);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("if")) {					//Matches if
				if (DEBUG) System.out.print("<IF>");
				tokenList.add(Token.IF);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("then")) {				//Matches then
				if (DEBUG) System.out.print("<THEN>");
				tokenList.add(Token.THEN);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("else")) {				//Matches else
				if (DEBUG) System.out.print("<ELSE>");
				tokenList.add(Token.ELSE);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("while")) {				//Matches while
				if (DEBUG) System.out.print("<WHILE>");
				tokenList.add(Token.WHILE);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("do")) {					//Matches do
				if (DEBUG) System.out.print("<DO>");
				tokenList.add(Token.DO);
				currentInp = ""; lastAccepting = null; continue;
			}
			
			if (currentInp.matches("[a-zA-Z0-9]+")) {		//Alphanumeric string
				if (lastAccepting == null || lastAccepting == Token.VAR) {
					lastAccepting = Token.VAR;				
					if (input.length()>0) continue;			//More potential chars as variable name
					else {
						if (DEBUG) System.out.println("<VAR,"+currentInp.substring(0, currentInp.length())+">");
						tokenList.add(Token.VAR);
						varList.add(currentInp.substring(0, currentInp.length()));
						return;
					}
				}
			}
			if (lastAccepting != null && lastAccepting == Token.VAR) {
				if (DEBUG) System.out.print("<VAR,"+currentInp.substring(0, currentInp.length()-1)+">");
				tokenList.add(Token.VAR);
				varList.add(currentInp.substring(0, currentInp.length()-1));
				skip = true;								//Skip adding another symbol
				currentInp = currentInp.substring(currentInp.length()-1, currentInp.length());
				lastAccepting = null;
			}
		}
	}
}