// equationCell: Object Class
// -> RealCell: Superclass
// The Cell for equation values

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionCell extends RealCell {

    private final Spreadsheet sheet; // Cheap copies

    // constructor
    public FunctionCell(String equation, Spreadsheet sheet) {
        super(equation); // RealCell
        this.sheet = sheet;
    }

    // Gets double value of the equation, cut or with remaining spaces for 10 characters
    // Return String
    @Override
    public String abbreviatedCellText() {
        return (this.getDoubleValue() + "          ").substring(0, 10);
    }

    // Goes through every RealCell from start to end for SUM and AVG
    // Returns double
    @Override
    public double getDoubleValue() {
        String equation = super.fullCellText(); // RealCell
        double output = 0.0;
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(equation.split(" ")));

        int index = parts.get(1).indexOf("-");
        Location loc1 = new SpreadsheetLocation(parts.get(1).substring(0, index));
        Location loc2 = new SpreadsheetLocation(parts.get(1).substring(index + 1));
        int cellCount = 0;
        for (int r = loc1.getRow(); r <= loc2.getRow(); r++) {
            for (int c = loc1.getCol(); c <= loc2.getCol(); c++) {
                output += this.getElement(((char) (c + 'A')) +""+ (r + 1));
                cellCount++;
            }
        }
        if (equation.toLowerCase().contains("avg")) output /= cellCount;
        return output;
    }

    // Gets what's inside the specified element, if it's a RealCell, gets what's inside that cell
    // Make the what's inside the element a double
    // Takes String & returns double
    public double getElement(String element) {
        double number;
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

}
