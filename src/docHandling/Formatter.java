package docHandling;
/**
 * 
 * @author igorpieters
 *
 */

public class Formatter {
	private int size;
	
	public Formatter(int dividerWidth) { 
		this.size = dividerWidth;
	};

	private String performIndentation(int n) {
		String indentation = "";
		for (int i = 0; i < n; i++) {
			indentation += "\t";
		}
		return indentation;
	}

	public void printDivider(char character) {
		for(int i = 0; i < size; i++)
			System.out.print(character);
		System.out.println();
	}
	
	public void indentString(String text, int indents) {
		System.out.println(performIndentation(indents) + text);
	}
}
