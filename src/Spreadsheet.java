import java.util.ArrayList;
import java.lang.Character;

// Spreadsheet: Object Class
// -> Grid: Interface
// Does pretty much everything related to the Spreadsheet: commands, sheet, grid, getting a cell

public class Spreadsheet implements Grid {

    private final int rows;
    private final int cols;
    private final Cell[][] sheet; // The big guy
    private ArrayList<String> history;
    private boolean haveHistory;
    private int historyLength;

    // constructor
    public Spreadsheet(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.sheet = new Cell[rows][cols];
        empty();
        this.haveHistory = false;
    }

    // Does the commands: Assignment to a specific cell, clearing of the whole sheet or only a specific Cell,
    // viewing of a Cell's value, and history moderating
    // Also sees if the commands are correctly formatted for fewer errors
    // Takes String & returns String
    @Override
    public String processCommand(String command) {
        if (this.haveHistory && !command.contains("history")) { // History
            this.history.add(0, command);
            if (this.history.size() == this.historyLength + 1) this.history.remove(this.history.size() - 1);
        }
        if (command.isEmpty() || command.trim().isEmpty()) return "ERROR: No Command";
        String[] parts = command.split(" ", 3);

        if (command.contains("=")) { // Cell: [cell] = [value]
            String cell = parts[0].toUpperCase();
            if (cell.isEmpty() || cell.equals("=")) return "ERROR: No Specified Cell";
            if (!isCell(cell)) return invalidCell(cell);
            if (parts.length <= 2 || parts[2].isEmpty()) return "ERROR: No Value Assigned";

            Location loc = new SpreadsheetLocation(cell);

            if (parts[2].startsWith("\"") && parts[2].endsWith("\"")) // TextCell: [cell] = "Hello World"
                this.sheet[loc.getRow()][loc.getCol()] = new TextCell(parts[2]);

            else if (parts[2].endsWith("%")) { // PercentCell: [cell] = 1%
                if (!isDouble(parts[2].substring(0, parts[2].length() - 2))) return "ERROR: Not A Number";
                this.sheet[loc.getRow()][loc.getCol()] = new PercentCell(parts[2]);
            }

            else if ("sumavg".contains(parts[2].toLowerCase().split(" ")[0])) { // FunctionCell: [cell] = SUM A1-B2
                if (parts[2].toUpperCase().contains(cell)) return "ERROR: Self Reference";

                int index = parts[2].indexOf("-");
                String fromCell = parts[2].substring(index - 2, index).toUpperCase();
                String toCell = parts[2].substring(index + 1, index + 3).toUpperCase();

                if (!isCell(fromCell)) return invalidCell(fromCell);
                if (!isCell(toCell)) return invalidCell(toCell);

                this.sheet[loc.getRow()][loc.getCol()] = new FunctionCell(parts[2], this);
            }

            else if (parts[2].startsWith("(") && parts[2].endsWith(")")) { // FormulaCell: [cell] = ( 1 + 2 )
                if (parts[2].toUpperCase().contains(cell)) return "ERROR: Self Reference";

                this.sheet[loc.getRow()][loc.getCol()] = new FormulaCell(parts[2], this);
            }

            else if (isDouble(parts[2])) { // ValueCell: [cell] = 1
                this.sheet[loc.getRow()][loc.getCol()] = new ValueCell(parts[2]);
            }
            else
                return "ERROR: Assignment Not Possible";
            return getGridText();
        }

        else if (parts[0].toLowerCase().contains("clear")) { // Clear: clear [cell?]
            if (command.equalsIgnoreCase("clear")) { // clear
                empty();
            } else if (parts.length == 2) { // clear [cell]
                String cell = parts[1].toUpperCase();
                if (!isCell(cell)) return invalidCell(cell);
                Location clearLoc = new SpreadsheetLocation(cell);
                this.sheet[clearLoc.getRow()][clearLoc.getCol()] = new EmptyCell();
            } else
                return "ERROR: Unclear Clear Command\n";
            return getGridText();
        }

        else if (parts[0].length() <= 3 && parts[0].length() >= 2) { // Value: [cell]
            String cell = parts[0].toUpperCase();
            if (!isCell(cell)) return invalidCell(cell);
            Location cellLoc = new SpreadsheetLocation(cell);
            return getCell(cellLoc).fullCellText();
        }

        else if (parts[0].equalsIgnoreCase("history")) { // History: history [option] [value?]
            StringBuilder commands = new StringBuilder();
            if (parts[1].equalsIgnoreCase("start")) { // History Start: history start [int]
                if (this.haveHistory) return "ERROR: History Is Already On";
                if (parts.length == 2) return "ERROR: No Argument (int)";
                if (Integer.parseInt(parts[2]) < 0) return "ERROR: Length Is Negative";

                this.historyLength = Integer.parseInt(parts[2]);
                history = new ArrayList<>();
                this.haveHistory = true;
            }
            else if (parts[1].equalsIgnoreCase("display")) { // History Display: history display
                if (!this.haveHistory) return "ERROR: History Is Off";
                for (String s : this.history) {
                    commands.append(s).append("\n");
                }
            }
            else if (parts[1].equalsIgnoreCase("clear")) { // History Clear: history clear [int]
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
            else if (parts[1].equalsIgnoreCase("stop")) { // History Stop: history stop
                if (!this.haveHistory) return "ERROR: History Is Already Off";
                history = null;
                this.haveHistory = false;
            }
            return commands.toString();
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
        StringBuilder text = new StringBuilder("   ");
        for (char C = 'A'; C < 'A' + this.cols; C++) {
            text.append("|").append(C).append("         ");
        }
        text.append("|\n");
        for (int i = 0; i < this.rows; i++) {
            if (i + 1 < 10) text.append((i + 1)).append("  ");
            else text.append((i + 1)).append(" ");
            for (int c = 0; c < this.cols; c++) {
                text.append("|").append(this.sheet[i][c].abbreviatedCellText());
            }
            text.append("|\n");
        }
        return text.toString();
    }

    public Boolean isCell(String cell) {
        if (cell.isEmpty() || cell.length() > 3) return false;
        if (!Character.isLetter(cell.charAt(0)) ||!Character.isDigit(cell.charAt(1))) return false;
        if (cell.charAt(0) < 'A' || cell.charAt(0) > 'A' + this.cols) return false;
        return Integer.parseInt(cell.substring(1)) >= 1 && Integer.parseInt(cell.substring(1)) <= this.rows;
    }

    // Sees if the Cell is an actual Cell with a letter and a number inside ranges
    // Takes String & returns String
    public String invalidCell(String cell) {
        if (cell.isEmpty()) return "ERROR: No Specified Cell";
        if (cell.length() > 3) return "ERROR: Cell Is Gigantic, only 2 or 3 chars max";
        if (!Character.isLetter(cell.charAt(0))) return "ERROR: Invalid Cell Column";
        if (!Character.isDigit(cell.charAt(1))) return "ERROR: Invalid Cell Row";
        if (cell.charAt(0) < 'A' || cell.charAt(0) > 'A' + this.cols) return "ERROR: Invalid Cell Column";
        if (Integer.parseInt(cell.substring(1)) < 1 || Integer.parseInt(cell.substring(1)) > this.rows) return "ERROR: Invalid Cell Row";
        return "";
    }

    public Boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // Makes all Cells inside the sheet EmptyCells
    // Takes Cell[][]
    public void empty() {
        for (int i = 0; i < this.sheet.length; i++) {
            for (int j = 0; j < this.sheet[i].length; j++) {
                this.sheet[i][j] = new EmptyCell(); // Make 'em all empty
            }
        }
    }

}
