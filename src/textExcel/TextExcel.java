package textExcel;
import java.util.Scanner;

// TextExel: Client Class
// -> Object: Superclass

public class TextExcel
{

	// main, where everything starts :o
	public static void main(String[] args)
	{
	    Spreadsheet sheet = new Spreadsheet();
	    Scanner input = new Scanner(System.in);
	    String command = input.nextLine();
	    while (!command.equalsIgnoreCase("quit")) {
	    	System.out.println(sheet.processCommand(command));
	    	command = input.nextLine();
	    }
	    
	    System.out.println("You lost everything, noooo");
	    input.close();
	}
	
}
