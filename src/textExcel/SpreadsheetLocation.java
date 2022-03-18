package textExcel;

// SpreadsheetLocation: Object Class
// -> Location: Interface

public class SpreadsheetLocation implements Location
{
	private int row;
	private int col;
	
	// constructor, calculates the row and column of a cell. Takes String
	public SpreadsheetLocation(String cellName)
    {
		this.row = Integer.parseInt(cellName.substring(1)) - 1;
		this.col = Character.toUpperCase(cellName.charAt(0)) - 'A';
    }
	
	// Gets the row number of a cell. Returns int
    @Override
    public int getRow()
    {
        return this.row;
    }

    // Gets the column number of a cell. Returns int
    @Override
    public int getCol()
    {
        return this.col;
    }
    
}
