package textExcel;

// Anselme Sorin
// PercentCell: Object Class
// -> RealCell: Superclass
// The Cell for percent values

public class PercentCell extends RealCell
{
	
	// constructor
	public PercentCell(String percent) {
		super(percent); // RealCell
	}
	
	// Gets the integer of the double with %, cut or with remaining spaces for 10 characters
	// Return String
	@Override
	public String abbreviatedCellText() {
		return (((int) getDoubleValue()) + "%          ").substring(0, 10);
	}
	
	// Gets the whole double divided by 100 as a String
	// Returns String
	@Override
	public String fullCellText() {
		return (getDoubleValue() / 100) + "";
	}
	
	// Gets the number without the %
	// Return double
	@Override
	public double getDoubleValue() {
		String text = super.fullCellText(); // RealCell
		return Double.parseDouble(text.substring(0, text.indexOf("%")));
	}
	
}