/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package au.ltl.utils;

public class LTLFormula {
	
	public static String getFormulaByTemplate(DeclareTemplate template, String act_1, String act_2){
		String formula = "";
		switch(template){
		case Absence:
			formula = "!( <> ( \"" + act_1 + "\" ) )";
			break;
		case Absence2 :
			formula = "! ( <> ( ( \"" + act_1 + "\" /\\ X(<>(\"" + act_1 + "\")) ) ) )";
			break;
		case Absence3 :
			formula = "! ( <> ( ( \"" + act_1 + "\" /\\  X ( <> ((\"" + act_1 + "\" /\\  X ( <> ( \"" + act_1 + "\" ) )) ) ) ) ))";
			break;
		case Alternate_Precedence :
			formula = "(((( !(\"" + act_2 + "\") U \"" + act_1 + "\") \\/ []( !(\"" + act_2 + "\"))) /\\ []((\"" + act_2 + "\" ->( (!(X(\"" + act_1 + "\")) /\\ !(X(!(\"" + act_1 + "\"))) ) \\/ X((( !(\"" + act_2 + "\") U \"" + act_1 + "\") \\/ []( !(\"" + act_2 + "\")))))))) /\\ !(\"" + act_2 + "\"))";
			break;
		case Alternate_Response :
			formula = "( []( ( \"" + act_1 + "\" -> X(( (! ( \"" + act_1 + "\" )) U \"" + act_2 + "\" ) )) ) )";
			break;
		case Alternate_Succession :
			formula = "( []((\"" + act_1 + "\" -> X(( !(\"" + act_1 + "\") U \"" + act_2 + "\")))) /\\ (((( !(\"" + act_2 + "\") U \"" + act_1 + "\") \\/ []( !(\"" + act_2 + "\"))) /\\ []((\"" + act_2 + "\" ->( (!(X(\"" + act_1 + "\")) /\\ !(X(!(\"" + act_1 + "\"))) ) \\/ X((( !(\"" + act_2 + "\") U \"" + act_1 + "\") \\/ []( !(\"" + act_2 + "\")))))))) /\\ !(\"" + act_2 + "\")))";
			break;
		case Chain_Precedence :
			formula = "[]( ( X( \"" + act_2 + "\" ) -> \"" + act_1 + "\") )/\\ ! (\"" + act_2 + "\" )";
			break;
		case Chain_Response :
			formula = "[] ( ( \"" + act_1 + "\" -> X( \"" + act_2 + "\" ) ) )";
			break;
		case Chain_Succession :
			formula = "([]( ( \"" + act_1 + "\" -> X( \"" + act_2 + "\" ) ) )) /\\ ([]( ( X( \"" + act_2 + "\" ) ->  \"" + act_1 + "\") ) /\\ ! (\"" + act_2 + "\" ))";
			break;
		case Choice :
			formula = "(  <> ( \"" + act_1 + "\" ) \\/ <>( \"" + act_2 + "\" )  )";
			break;
		case CoExistence :
			formula = "( ( <>(\"" + act_1 + "\") -> <>( \"" + act_2 + "\" ) ) /\\ ( <>(\"" + act_2 + "\") -> <>( \"" + act_1 + "\" ) )  )";
			break;
		case Exactly1 :
			formula = "(  <> (\"" + act_1 + "\") /\\ ! ( <> ( ( \"" + act_1 + "\" /\\ X(<>(\"" + act_1 + "\")) ) ) ) )";
			break;
		case Exactly2 :
			formula = "( <> (\"" + act_1 + "\" /\\ (\"" + act_1 + "\" -> (X(<>(\"" + act_1 + "\"))))) /\\  ! ( <>( \"" + act_1 + "\" /\\ (\"" + act_1 + "\" -> X( <>( \"" + act_1 + "\" /\\ (\"" + act_1 + "\" -> X ( <> ( \"" + act_1 + "\" ) ))) ) ) ) ) )";
			break;
		case Exclusive_Choice :
			formula = "(  ( <>( \"" + act_1 + "\" ) \\/ <>( \"" + act_2 + "\" )  )  /\\ !( (  <>( \"" + act_1 + "\" ) /\\ <>( \"" + act_2 + "\" ) ) ) )";
			break;
		case Existence :
			formula = "( <> ( \"" + act_1 + "\" ) )";
			break;
		case Existence2 :
			formula = "<> ( ( \"" + act_1 + "\" /\\ X(<>(\"" + act_1 + "\")) ) )";
			break;
		case Existence3 :
			formula = "<>( \"" + act_1 + "\" /\\ X(  <>( \"" + act_1 + "\" /\\ X( <> \"" + act_1 + "\" )) ))";
			break;
		case Init :
			 formula = "( \"" + act_1 + "\" )";
			break;
		case Not_Chain_Succession :
			 formula = "[]( ( \"" + act_1 + "\" -> !(X( \"" + act_2 + "\" ) ) ))";
			break;
		case Not_Chain_Response :
			 formula = "[]( ( \"" + act_1 + "\" -> !(X( \"" + act_2 + "\" ) ) ))";
			break;
		case Not_Chain_Precedence :
			 formula = "[]( ( \"" + act_1 + "\" -> !(X( \"" + act_2 + "\" ) ) ))";
			break;
		case Not_CoExistence :
			formula = "(<>(\"" + act_1 + "\")) -> (!(<>( \"" + act_2 + "\" )))";
			break;
		case Not_Succession :
			formula = "[]( ( \"" + act_1 + "\" -> !(<>( \"" + act_2 + "\" ) ) ))";
			break;
		case Not_Precedence :
			formula = "[]( ( \"" + act_1 + "\" -> !(<>( \"" + act_2 + "\" ) ) ))";
			break;
		case Not_Response :
			formula = "[]( ( \"" + act_1 + "\" -> !(<>( \"" + act_2 + "\" ) ) ))";
			break;
		case Precedence :
			formula = "( ! (\"" + act_2 + "\" ) U \"" + act_1 + "\" ) \\/ ([](!(\"" + act_2 + "\"))) /\\ ! (\"" + act_2 + "\" )";
			break;
		case Responded_Existence :
			formula = "(( ( <>( \"" + act_1 + "\" ) -> (<>( \"" + act_2 + "\" ) )) ))";
			break;
		case Not_Responded_Existence :
			formula = "(( ( <>( \"" + act_1 + "\" ) -> !(<>( \"" + act_2 + "\" ) )) ))";
			break;
		case Response :
			formula = "( []( ( \"" + act_1 + "\" -> <>( \"" + act_2 + "\" ) ) ))";
			break;
		case Succession :
			formula = "(( []( ( \"" + act_1 + "\" -> <>( \"" + act_2 + "\" ) ) ))) /\\ (( ! (\"" + act_2 + "\" ) U \"" + act_1 + "\" ) \\/ ([](!(\"" + act_2 + "\"))) /\\ ! (\"" + act_2 + "\" ))";
			break;		
		
		}
		return formula;
	}
	

}
