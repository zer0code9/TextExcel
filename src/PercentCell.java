// PercentCell: Object Class
// -> RealCell: Superclass
// The Cell for percent values

public class PercentCell extends RealCell {

    // constructor
    public PercentCell(String percent) {
        super(percent); // RealCell
    }

    // Gets the integer of the double with %, cut or with remaining spaces for 10 characters
    // Return String
    @Override
    public String abbreviatedCellText() {
        return ((getDoubleValue()) + "          ").substring(0, 10);
    }

    // Gets the percent but as a double
    // Return double
    @Override
    public double getDoubleValue() {
        String text = super.fullCellText(); // RealCell
        return Double.parseDouble(text.substring(0, text.indexOf("%"))) / 100;
    }

}
