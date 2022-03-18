package textExcel;
import java.util.ArrayList;

// FormulaCell: Object Class
// -> RealCell: Superclass

public class FormulaCell extends RealCell
{
	
	private Spreadsheet sheet;

	// constructor
	public FormulaCell(String formula, Spreadsheet sheet) {
		super(formula); // RealCell
		this.sheet = sheet;
		
	}
	
	// Gets a cut double or whole double with spaces. Return String
	@Override
	public String abbreviatedCellText() {
		return (getDoubleValue() + "          ").substring(0, 10);
	}
	
	// Goes through every RealCell from start to end for SUM and AVG, or
	// Does every operation one by one by order of operation, can have ints, doubles, RealCells, pi.
	// Return double
	@Override
	public double getDoubleValue() {
		String formula = super.fullCellText();
		double output = 0;
		formula = formula.substring(2, formula.length() - 2); // formula.indexOf("(") + 2, formula.indexOf(")") - 1
		String[] array = formula.split(" ");
		ArrayList<String> parts = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			parts.add(array[i]);
		}
		
		if (formula.toLowerCase().contains("sum") || formula.toLowerCase().contains("avg")) {
			String equation = formula.substring(4);
			Location loc1 = new SpreadsheetLocation(equation.substring(0, equation.indexOf("-")));
			Location loc2 = new SpreadsheetLocation(equation.substring(equation.indexOf("-") + 1));
			int cellCount = 0;
			for (int r = loc1.getRow(); r <= loc2.getRow(); r++) {
				for (int c = loc1.getCol(); c <= loc2.getCol(); c++) {
					Location loc = new SpreadsheetLocation(((char) (c + 'A')) + "" + (r + 1));
					double value = 0.0;
					if (this.sheet.getCell(loc) instanceof RealCell)
						value = ((RealCell) this.sheet.getCell(loc)).getDoubleValue();
					else {
						value = 0;
						System.out.println("ERROR: Not A RealCell; Skipped cell " + ((char) (c + 'A')) + "" + (r + 1));
					}
					output += value;
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
					for (int i = p1 + 1; i < p2; i++) subparts.add(parts.get(i));
					while (subparts.size() > 1) doOperations(subparts, number);
					for (int i = (p2 - p1) + 1; i != 0; i--) parts.remove(p1);
					parts.add(p1 ,subparts.get(0));
				}
				doOperations(parts, number);
			}
			output = getElement(parts.get(0));
		}
		return output;
		
	}
	
	// Gets what's inside the specified element, if it's a RealCell, gets what's inside that cell
	// Make the what's inside the element a double
	// Takes String & returns double
	public double getElement(String element) {
		double number = 0;
		if ("pi".contains(element.toLowerCase())) number = Math.PI;
		else if ("abcdefghijkl".contains(element.toLowerCase().substring(0, 1))) {
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
			int min = Math.min(parts.indexOf("*"), parts.indexOf("/"));
			if (min == -1) min = Math.max(parts.indexOf("*"), parts.indexOf("/"));
			double num1 = getElement(parts.get(min - 1));
			double num2 = getElement(parts.get(min + 1));
			if (parts.get(min).equals("*")) number = num1 * num2;
			else number = num1 / num2;
			parts.add(min + 2, number + "");
			for (int i = 3; i != 0; i--) parts.remove(min - 1);
		}
		else if (parts.contains("+") || parts.contains("-")) {
			int min = Math.min(parts.indexOf("+"), parts.indexOf("-"));
			if (min == -1) min = Math.max(parts.indexOf("+"), parts.indexOf("-"));
			double num1 = getElement(parts.get(min - 1));
			double num2 = getElement(parts.get(min + 1));
			if (parts.get(min).equals("+")) number = num1 + num2;
			else {
				if (num2 < 0) number = num1 + (num2 * -1);
				else number = num1 - num2;
			}
			parts.add(min + 2, number + "");
			for (int i = 3; i != 0; i--) parts.remove(min - 1);
		}
		return parts;
	}

}
