package toy.language;

public class LanguageSyntax {
	
	/* Design choice: Do not use and of the predefined text here as a variable e.g. andd will be stored as boolean AND and variable d
	 * True > 0 & False < 0
	 * • x,y (variable identifiers; alphanumerical strings starting with a letter)
	 * • n (POSITIVE integer constants) 
	 * • OPa -> + |* (arithmetic operators) 
	 * • OPb -> and | or (boolean operators) 
	 * • OPr -> > | < | = (relations) 
	 * • Aexp -> n | x | Aexp OPa Aexp | (Aexp) (arithmetic expressions) 
	 * • Bexp -> true | false | !Bexp | Aexp OPr Aexp | Bexp OPb Bexp | (Bexp) (boolean expressions) 
	 * • S -> skip | x := Aexp | S;S | if Bexp then S else S | while Bexp do S | {S} (statements) 
	 */

}
