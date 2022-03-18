package textExcel;

// TextCell: Object Class
// -> Cell: Interface

public class TextCell implements Cell
{
	private String value;
	
	// constructor
	public TextCell(String text) {
		this.value = text;
	}
	
	// Gets a cut number or whole number with spaces without double-quotes. Returns String
	@Override
	public String abbreviatedCellText() {
		String text = this.value.substring(1, value.length() - 1);
		if (text.length() == 0) return "          ";
		return (text + "          ").substring(0, 10);
	}
	
	// Returns the whole text. Returns String
	@Override
	public String fullCellText() {
		return this.value;
	}
}
