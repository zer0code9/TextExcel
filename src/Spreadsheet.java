import java.util.ArrayList;
import java.lang.Character;

// Spreadsheet: Object Class
// -> Grid: Interface
// Does pretty much everything related to the Spreadsheet: commands, sheet, grid, getting a cell

public class Spreadsheet implements Grid {

    private final Cell[][] sheet; // The big guy
    private ArrayList<String> history;
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
        if (this.haveHistory && !command.contains("history")) { // History
            this.history.add(0, command);
            if (this.history.size() == this.historyLength + 1) this.history.remove(this.history.size() - 1);
        }
        if (command.isEmpty() || command.trim().isEmpty()) return "ERROR: No Command";
        String[] parts = command.split(" ", 3);

        if (command.contains("=")) { // [cell] = [value]
            String cell = parts[0].toUpperCase();
            if (cell.isEmpty() || cell.equals("=")) return "ERROR: No Specified Cell";
            if (!invalidCell(cell).isEmpty()) return invalidCell(cell);
            if (parts.length <= 2 || parts[2].isEmpty()) return "ERROR: No Value Assigned";

            Location loc = new SpreadsheetLocation(cell);
            String value = parts[2];

            if (parts[2].startsWith("\"") && parts[2].endsWith("\"")) // [cell] = "Hello World"
                this.sheet[loc.getRow()][loc.getCol()] = new TextCell(value);

            else if (parts[2].endsWith("%")) { // [cell] = 1%
                if (!isNum(parts[2].substring(0, parts[2].length() - 2))) return "ERROR: Not A Number";
                this.sheet[loc.getRow()][loc.getCol()] = new PercentCell(value);
            }

            else if (parts[2].startsWith("(") && parts[2].endsWith(")")) { // [cell] = ( 1 + 2 )
                if (parts[2].toUpperCase().contains(cell)) return "ERROR: Self Reference";

                if (parts[2].toLowerCase().contains("sum") || parts[2].toLowerCase().contains("avg")) {
                    String equation = parts[2].substring(6, parts[2].length() - 2);
                    String fromCell = equation.substring(0, equation.indexOf("-")).toUpperCase();
                    String toCell = equation.substring(equation.indexOf("-") + 1).toUpperCase();

                    if (!invalidCell(fromCell).isEmpty()) return invalidCell(fromCell); // Though you don't know which you got wrong
                    if (!invalidCell(toCell).isEmpty()) return invalidCell(toCell);     // the from Cell or to Cell?
                }

                this.sheet[loc.getRow()][loc.getCol()] = new FormulaCell(value, this);
            }

            else if ("1234567890".contains(parts[2].charAt(parts[2].length() - 1)+"")) { // [cell] = 1
                if (!isNum(parts[2])) return "ERROR: Not A Number";
                this.sheet[loc.getRow()][loc.getCol()] = new ValueCell(value);
            }
            else
                return "ERROR: Assignement Not Possible";
            return getGridText();
        }

        else if (parts[0].toLowerCase().contains("clear")) { // clear [cell?]
            if (command.equalsIgnoreCase("clear")) { // clear
                empty(this.sheet);
            } else if (parts.length == 2) { // clear [cell]
                String cell = parts[1].toUpperCase();
                if (!invalidCell(cell).isEmpty()) return invalidCell(cell);
                Location clearLoc = new SpreadsheetLocation(cell);
                this.sheet[clearLoc.getRow()][clearLoc.getCol()] = new EmptyCell();
            } else
                return "ERROR: Unclear Clear Command\n";
            return getGridText();
        }

        else if (parts[0].length() <= 3 && parts[0].length() >= 2) { // [cell]
            String cell = parts[0].toUpperCase();
            if (!invalidCell(cell).isEmpty()) return invalidCell(cell);
            Location cellLoc = new SpreadsheetLocation(cell);
            return getCell(cellLoc).fullCellText();
        }

        else if (parts[0].equalsIgnoreCase("history")) { // history [option] [value?]
            String commands = "";
            if (parts[1].equalsIgnoreCase("start")) { // history start [int]
                if (this.haveHistory) return "ERROR: History Is Already On";
                if (parts.length == 2) return "ERROR: No Argument (int)";
                if (Integer.parseInt(parts[2]) < 0) return "ERROR: Length Is Negative";

                this.historyLength = Integer.parseInt(parts[2]);
                history = new ArrayList<String>();
                this.haveHistory = true;
            }
            else if (parts[1].equalsIgnoreCase("display")) { // history display
                if (!this.haveHistory) return "ERROR: History Is Off";
                for (String s : this.history) {
                    commands += s + "\n";
                }
            }
            else if (parts[1].equalsIgnoreCase("clear")) { // history clear [int]
                if (!this.haveHistory) return "ERROR: History Is Off";
                if (parts.length == 2) return "ERROR: No Argument (int)";
                if (Integer.parseInt(parts[2]) < 0) return "ERROR: Length Is Negative";

                for (int i = Integer.parseInt(parts[2]); i != 0; i--) {
                    try {
                        this.history.remove(this.history.size() - 1);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.print("");
                    }
                }
            }
            else if (parts[1].equalsIgnoreCase("stop")) { // history stop
                if (!this.haveHistory) return "ERROR: History Is Already Off";
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
        return 20;
    }

    // Gets the number of columns
    // Returns int
    @Override
    public int getCols() {
        return 12;
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
        StringBuilder text = new StringBuilder("   ");
        for (char C = 'A'; C < 'L' + 1; C++) {
            text.append("|").append(C).append("         ");
        }
        text.append("|\n");
        for (int i = 0; i < getRows(); i++) {
            if (i + 1 < 10) text.append((i + 1)).append("  ");
            else text.append((i + 1)).append(" ");
            for (int c = 0; c < getCols(); c++) {
                text.append("|").append(this.sheet[i][c].abbreviatedCellText());
            }
            text.append("|\n");
        }
        return text.toString();
    }

    // Sees if the Cell is an actual Cell with a letter and a number inside ranges
    // Takes String & returns String
    public String invalidCell(String cell) {
        if (cell.isEmpty()) return "ERROR: No Specified Cell";
        if (cell.length() > 3) return "ERROR: Cell Is Gigantic, only 2 or 3 chars max";
        if (!Character.isLetter(cell.charAt(0))) return "ERROR: Invalid Cell Column";
        if (!Character.isDigit(cell.charAt(1))) return "ERROR: Invalid Cell Row";
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
