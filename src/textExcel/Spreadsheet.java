package textExcel;
import java.util.ArrayList;

// Anselme Sorin
// Spreadsheet: Object Class
// -> Grid: Interface
// Does pretty much everything related to the Spreadsheet: commands, sheet, grid, getting a cell

public class Spreadsheet implements Grid {
	
	private Cell[][] sheet; // The big guy
	private int rows = 20; // Nope, it's not like Google Spreadsheet; there is a range
	private int cols = 12;
	private ArrayList<String> history; // History has it that...
	private boolean haveHistory;
	private int historyLength;
	
	// constructor
	public Spreadsheet() {
		this.sheet = new Cell[getRows()][getCols()];
		empty(this.sheet);
		this.haveHistory = false;
	}

	// Does the commands: Assignment to a specific cell, clearing of the whole sheet or only a specific Cell,
	// viewing of a Cell's value, and history moderating
	// Also sees if the commands are correctly formatted for less errors
	// Takes String & returns String
	@Override
	public String processCommand(String command) {
		if (this.haveHistory == true && !command.contains("history")) {
			this.history.add(0, command);
			if (this.history.size() == this.historyLength + 1) this.history.remove(this.history.size() - 1);
		}
		if (command.length() == 0 || command.trim().equals("")) return ""; // Checkpoint 1 thingy
		String[] parts = command.split(" ", 3);
		
		if (command.contains("=")) {
			String cell = parts[0].toUpperCase();
			if (cell.equals("") || cell.equals("=")) return "ERROR: No Specified Cell";
			if (!invalidCell(cell).equals("")) return invalidCell(cell);
			if (cell.contains("=")) return "ERROR: Weird Cell";
			if (parts.length <= 2 || parts[2].equals("")) return "ERROR: No Value Assigned";
			
			Location loc = new SpreadsheetLocation(cell);
			String value = parts[2];
			
			if (parts[2].startsWith("\"") && parts[2].endsWith("\"")) // Text Cell
				this.sheet[loc.getRow()][loc.getCol()] = new TextCell(value);
			else if (parts[2].endsWith("%")) { // Percent Cell
				if (!isNum(parts[2].substring(0, parts[2].length() - 2))) return "ERROR: Not A Number";
				this.sheet[loc.getRow()][loc.getCol()] = new PercentCell(value);
			}
			else if (parts[2].startsWith("(") && parts[2].endsWith(")")) { // Formula Cell
					if (parts[2].toUpperCase().contains(cell)) return "ERROR: Self Reference";
					
					if (parts[2].toLowerCase().contains("sum") || parts[2].toLowerCase().contains("avg")) {
						String equation = parts[2].substring(6, parts[2].length() - 2);
						String fromCell = equation.substring(0, equation.indexOf("-")).toUpperCase();
						String toCell = equation.substring(equation.indexOf("-") + 1).toUpperCase();
						
						if (!invalidCell(fromCell).equals("")) return invalidCell(fromCell); // Though you don't know which you got wrong
						if (!invalidCell(toCell).equals("")) return invalidCell(toCell);     // the from Cell or to Cell?
					}
					
					this.sheet[loc.getRow()][loc.getCol()] = new FormulaCell(value, this);
			}
			else if ("1234567890".contains(parts[2].charAt(parts[2].length() - 1)+"")) { // Value Cell
				if (!isNum(parts[2])) return "ERROR: Not A Number";
				this.sheet[loc.getRow()][loc.getCol()] = new ValueCell(value);
			}
			else
				return "ERROR: Assignement Not Possible";
			return getGridText();
		}
		
		else if (parts[0].toLowerCase().contains("clear")) {
			if (command.equalsIgnoreCase("clear")) {
				empty(this.sheet);
			} else if (parts.length == 2) {
				String cell = parts[1].toUpperCase();
				if (!invalidCell(cell).equals("")) return invalidCell(cell);
				Location clearLoc = new SpreadsheetLocation(cell);
				this.sheet[clearLoc.getRow()][clearLoc.getCol()] = new EmptyCell();
			} else
				return "ERROR: Unclear Clear Command\n";
			return getGridText();
		}
		
		else if (parts[0].length() <= 3 && parts[0].length() >= 2) {
			String cell = parts[0].toUpperCase();
			if (!invalidCell(cell).equals("")) return invalidCell(cell);
			Location cellLoc = new SpreadsheetLocation(cell);
			return getCell(cellLoc).fullCellText();
		}
		
		else if (parts[0].equalsIgnoreCase("history")) {
			String commands = "";
			if (parts[1].equalsIgnoreCase("start")) {
				if (this.haveHistory == true) return "ERROR: History Is Already On";
				if (parts.length == 2) return "ERROR: No Argument (int)";
				if (Integer.parseInt(parts[2]) < 0) return "ERROR: Length Is Negative";
				
				this.historyLength = Integer.parseInt(parts[2]);
				history = new ArrayList<String>();
				this.haveHistory = true;
			}
			else if (parts[1].equalsIgnoreCase("display")) {
				if (this.haveHistory == false) return "ERROR: History Is Off";
				for (int i = 0; i < this.history.size(); i++) {
					commands += this.history.get(i) + "\n";
				}
			}
			else if (parts[1].equalsIgnoreCase("clear")) {
				if (this.haveHistory == false) return "ERROR: History Is Off";
				if (parts.length == 2) return "ERROR: No Argument (int)";
				if (Integer.parseInt(parts[2]) < 0) return "ERROR: Length Is Negative";
				
				for (int i = Integer.parseInt(parts[2]); i != 0; i--) {
					try {
						this.history.remove(this.history.size() - 1);
					}catch (IndexOutOfBoundsException e) {
						System.out.print("");
					}
				}
			}
			else if (parts[1].equalsIgnoreCase("stop")) { // History is gone
				if (this.haveHistory == false) return "ERROR: History Is Already Off";
				history = null;
				this.haveHistory = false;
			}
			return commands;
		}
		return "ERROR: Command Not Known";
	}

	// Gets the number of rows
	// Returns int
	@Override
	public int getRows() {
		return this.rows;
	}

	// Gets the number of columns
	// Returns int
	@Override
	public int getCols() {
		return this.cols;
	}

	// Gets the cell at a location
	// Takes Location & returns Cell
	@Override
	public Cell getCell(Location loc) {
		return this.sheet[loc.getRow()][loc.getCol()];
	}

	// Makes the grid and adds the Cells' values inside
	// Returns String
	@Override
	public String getGridText() {
		String text = "   ";
		for (char C = 'A'; C < 'L' + 1; C++) {
			text += "|" + C + "         ";
		}
		text += "|\n";
		for (int i = 0; i < getRows(); i++) {
			if (i + 1 < 10) text += (i + 1) + "  ";
			else text += (i + 1) + " ";
			for (int c = 0; c < getCols(); c++) {
				text += "|" + this.sheet[i][c].abbreviatedCellText();
			}
			text += "|\n";
		}
		return text;
	}
	
	// Sees if the Cell is an actual Cell with a letter and a number inside ranges
	// Takes String & returns String
	public String invalidCell(String cell) {
		if (cell.length() > 3) return "ERROR: Cell Is Gigantic, only 2 or 3 chars max";
		if ("1234567890.-".contains(cell.charAt(0)+"") || !"1234567890.-".contains(cell.charAt(1)+"")) return "ERROR: Unclear Cell";
		if (cell.charAt(0) < 'A' || cell.charAt(0) > 'L') return "ERROR: Invalid Cell Column";
		if (Integer.parseInt(cell.substring(1)) < 1 || Integer.parseInt(cell.substring(1)) > 20) return "ERROR: Invalid Cell Row";
		return "";
	}
	
	// Makes all Cells inside the sheet EmptyCells
	// Takes Cell[][]
	public void empty(Cell[][] sheet) {
		for (int i = 0; i < this.sheet.length; i++) {
			for (int j = 0; j < this.sheet[i].length; j++) {
				this.sheet[i][j] = new EmptyCell(); // Make 'em all empty
			}
		}
	}
	
	// Sees if a value only contains numbers
	// Takes String & returns boolean
	public boolean isNum(String value) {
		for (int i = 0; i < value.length(); i++) {
			if (!"1234567890.-".contains(value.charAt(i)+"")) return false;
		}
		return true;
	}

}
