// EmptyCell: Object Class
// -> Cell: Interface

public class EmptyCell implements Cell {

    // Gets 10 spaces
    // Returns String
    @Override
    public String abbreviatedCellText() {
        return "          ";
    }

    // Gets an empty String
    // Return String
    @Override
    public String fullCellText() {
        return "";
    }

}
