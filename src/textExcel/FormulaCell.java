package textExcel;
import java.util.ArrayList;

// Anselme Sorin
// FormulaCell: Object Class
// -> RealCell: Superclass
// The Cell for formula values

public class FormulaCell extends RealCell
{
	
	private Spreadsheet sheet; // Cheap copies

	// constructor
	public FormulaCell(String formula, Spreadsheet sheet) {
		super(formula); // RealCell
		this.sheet = sheet;
		
	}
	
	// Gets double value of the formula, cut or with remaining spaces for 10 characters
	// Return String
	@Override
	public String abbreviatedCellText() {
		return (getDoubleValue() + "          ").substring(0, 10);
	}
	
	// Goes through every RealCell from start to end for SUM and AVG, or
	// Does every operation one by one by order of operation, can have ints, doubles, RealCells, and now parenthesis.
	// Returns double
	// THIS IS HARD TO UNDERSTAND :(
	@Override
	public double getDoubleValue() {
		String formula = super.fullCellText(); // RealCell
		double output = 0.0;
		formula = formula.substring(2, formula.length() - 2);
		String[] array = formula.split(" ");
		ArrayList<String> parts = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) parts.add(array[i]); // Makes Array an ArrayList
		
		if (formula.toLowerCase().contains("sum") || formula.toLowerCase().contains("avg")) {
			String expression = formula.substring(4);
			Location loc1 = new SpreadsheetLocation(expression.substring(0, expression.indexOf("-")));
			Location loc2 = new SpreadsheetLocation(expression.substring(expression.indexOf("-") + 1));
			int cellCount = 0;
			for (int r = loc1.getRow(); r <= loc2.getRow(); r++) {
				for (int c = loc1.getCol(); c <= loc2.getCol(); c++) {
					output += getElement(((char) (c + 'A')) +""+ (r + 1));
					cellCount++;
				}
			}
			if (formula.toLowerCase().contains("avg")) output /= cellCount;
			
		} else {
			
			while (parts.size() > 1) {
				double number = 0;
				while (parts.contains("(") && parts.contains(")")) {
					int p1 = parts.indexOf("(");
					int p2 = parts.indexOf(")");
					ArrayList<String> subparts = new ArrayList<String>();
					
					for (int i = p1 + 1; i < p2; i++) // Getting the expression inside the parenthesis
						subparts.add(parts.get(i));
					
					while (subparts.size() > 1)
						doOperations(subparts, number);

					expressionToResultSwitch(parts, (p2 - p1) + 1, p1, subparts.get(0));
				}
				doOperations(parts, number); // For the resulting expression that doesn't have parenthesis
			}
			output = getElement(parts.get(0)); // The last one standing
		}
		return output;
	}
	
	// Gets what's inside the specified element, if it's a RealCell, gets what's inside that cell
	// Make the what's inside the element a double
	// Takes String & returns double
	public double getElement(String element) {
		double number = 0;
		if ("abcdefghijkl".contains(element.toLowerCase().substring(0, 1))) {
			Location loc = new SpreadsheetLocation(element.toUpperCase());
			if (this.sheet.getCell(loc) instanceof RealCell)
				number = ((RealCell) this.sheet.getCell(loc)).getDoubleValue();
			else {
				number = 0;
				System.out.println("ERROR: Not A RealCell; Skipped cell " + element.toUpperCase());
			}
		}
		else number = Double.parseDouble(element);
		return number;
	}
	
	// Sees if there are any * or /, if so get the numbers next to the operator, do the operators with those numbers,
	// add the resultant after the second number, and remove the numbers and operator
	// if not, do the same steps for any + or -
	// Takes ArrayList<String>, double & returns ArrayList<String> (no return needed anyway)
	public ArrayList<String> doOperations(ArrayList<String> parts, double number) {
		if (parts.contains("*") || parts.contains("/")) {
			double[] expression = getExpression(parts, "*", "/");
			int operatorIndex = (int) expression[1];
			double operand1 = expression[0];
			double operand2 = expression[2];
			
			if (parts.get(operatorIndex).equals("*")) number = operand1 * operand2; // Multiplication
			else number = operand1 / operand2; // Division
			
			expressionToResultSwitch(parts, 3, operatorIndex - 1, number+"");
		}
		else if (parts.contains("+") || parts.contains("-")) {
			double[] expression = getExpression(parts, "+", "-");
			int operatorIndex = (int) expression[1];
			double operand1 = expression[0];
			double operand2 = expression[2];
			
			if (parts.get(operatorIndex).equals("+")) number = operand1 + operand2; // Addition
			else { // Substraction
				if (operand2 < 0) number = operand1 + (operand2 * -1);
				else number = operand1 - operand2;
			}
			
			expressionToResultSwitch(parts, 3, operatorIndex - 1, number+"");
		}
		return parts;
	}
	
	// Get the 2 operands and the index of the operator from the overall expression
	// Takes ArrayList<String>, 2 Strings & returns double[]
	public double[] getExpression(ArrayList<String> parts, String operator1, String operator2) {
		int operatorIndex = Math.min(parts.indexOf(operator1), parts.indexOf(operator2));
		if (operatorIndex == -1) operatorIndex = Math.max(parts.indexOf(operator1), parts.indexOf(operator2));
		double operand1 = getElement(parts.get(operatorIndex - 1));
		double operand2 = getElement(parts.get(operatorIndex + 1));
		double[] expression = {operand1, operatorIndex, operand2};
		return expression;
	}
	
	// Replace the calculated expression of length start to its result
	// Takes ArrayList<String>, 2 ints, String & returns ArrayList<String>
	public ArrayList<String> expressionToResultSwitch(ArrayList<String> parts, int start, int index, String value) {
		for (int i = start; i != 0; i--) // Removing the three elements that made the operation (number(or Cell's value) operator number(or Cell's value))
			parts.remove(index);
		parts.add(index, value); // Add the resultant inside the ArrayList
		return parts;
	}

}
