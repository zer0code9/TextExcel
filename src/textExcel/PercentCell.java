package textExcel;

// PercentCell: Object Class
// -> RealCell: Superclass

public class PercentCell extends RealCell
{
	
	// constructor
	public PercentCell(String percent) {
		super(percent); // RealCell
	}
	
	// Gets the integer of a number with spaces. Return String
	@Override
	public String abbreviatedCellText() {
		return (((int) getDoubleValue()) + "%          ").substring(0, 10);
	}
	
	// Gets the whole double as a String. Returns String
	@Override
	public String fullCellText() {
		return (getDoubleValue() / 100) + "";
	}
	
	// Gets the number divided by 100 instead of %. Return double
	@Override
	public double getDoubleValue() {
		String text = super.fullCellText(); // RealCell
		return Double.parseDouble(text.substring(0, text.indexOf("%")));
	}
	
}