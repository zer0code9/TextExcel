package textExcel;

// Anselme Sorin
// TextCell: Object Class
// -> Cell: Interface
// The Cell for text values

public class TextCell implements Cell
{
	
	private String value;
	
	// constructor
	public TextCell(String text) {
		this.value = text;
	}
	
	// Gets a text without double-quotes, cut or with remaining spaces for 10 characters
	// Returns String
	@Override
	public String abbreviatedCellText() {
		String text = this.value.substring(1, this.value.length() - 1); // removing quotes
		return (text + "          ").substring(0, 10);
	}
	
	// Returns the whole text
	// Returns String
	@Override
	public String fullCellText() {
		return this.value;
	}
	
}
