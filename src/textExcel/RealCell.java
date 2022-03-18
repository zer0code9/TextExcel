package textExcel;

// RealCell: Object Class
// -> Cell: Interface

public class RealCell implements Cell
{
	private String value;
	
	// constructor
	public RealCell(String number) {
		this.value = number;
	}
	
	// Gets a cut double or whole double with spaces. Return String
	@Override
	public String abbreviatedCellText() {
		return (getDoubleValue() + "          ").substring(0, 10);
	}
	
	// Gets the whole number as a String. Returns String
	@Override
	public String fullCellText() {
		return this.value;
	}
	
	// Gets the whole number as a double. Returns double 
	public double getDoubleValue() {
		return Double.parseDouble(this.value);
	}
	
}