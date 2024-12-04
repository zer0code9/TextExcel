// RealCell: Object Class
// -> Cell: Interface
// The Cell for numeric values

public class RealCell implements Cell {

    private final String value;

    // constructor
    public RealCell(String number) {
        this.value = number;
    }

    // Gets the double cut or with the remaining spaces for 10 characters
    // Return String
    @Override
    public String abbreviatedCellText() {
        return (getDoubleValue() + "          ").substring(0, 10);
    }

    // Gets the whole number as a String
    // Returns String
    @Override
    public String fullCellText() {
        return this.value;
    }

    // Gets the whole number as a double
    // Returns double
    public double getDoubleValue() {
        return Double.parseDouble(this.value);
    }

}
