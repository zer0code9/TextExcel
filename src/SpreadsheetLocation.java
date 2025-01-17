// SpreadsheetLocation: Object Class
// -> Location: Interface
// Everything related to the locations of Cells: getting the row number and column number of a specified cell

public class SpreadsheetLocation implements Location {

    private final int row;
    private final int col;

    // constructor, calculates the row and column of a cell
    // Takes String
    public SpreadsheetLocation(String cellName) {
        this.row = Integer.parseInt(cellName.substring(1)) - 1;
        this.col = Character.toUpperCase(cellName.charAt(0)) - 'A';
    }

    // Gets the row number of a cell
    // Returns int
    @Override
    public int getRow() {
        return this.row;
    }

    // Gets the column number of a cell
    // Returns int
    @Override
    public int getCol() {
        return this.col;
    }

}
