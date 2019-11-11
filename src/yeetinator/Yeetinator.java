package yeetinator;
import java.util.List;
import java.util.ArrayList;

public class Yeetinator {
	
	private class Pair{
		String key, value;
		public void Pair(String k, String v) {
			key = k;
			value = v;
		}
	}
	
	public static void main(String[] args) {
		ArrayList<String> input = readInFile();
		removeComments(input);
		
		// Find and perform original macros
		ArrayList<Pair> originalMacros = stripMacros(input);
		performMacros(input, originalMacros);
		
		// Tokenize, then find and "unperform" new yeet macros
		ArrayList<String> tokens = tokenize(input);
		ArrayList<Pair> newMacros = assignYeetIDs(tokens);
		unperformMacros(input, newMacros);
		
	}
	
	// Reads file from stdin and returns an ArrayList where index 0 is the 0th line, 1 is the 1st line etc...
	// If the line ends with a '\' (Line splice), the nth and n+1th lines are combined
	public static ArrayList<String> readInFile(){
		return null;
	}
	
	// Removes all comments (// and /*) from input
	public static void removeComments(ArrayList<String> input){
		return;
	}
	
	// Finds all macros in #define statements, strips them from input, and returns the macros as key, value pairs
	public static ArrayList<Pair> stripMacros(ArrayList<String> input){
		return null;
	}
	
	// Performs macros given by substitutions (A list of key, value pairs) and returns the changed file as 
	// 	and ArrayList
	public static void performMacros(ArrayList<String> input, ArrayList<Pair> substitutions){
		return;
	}
	
	// Finds all tokens present in input and outputs them as a list of strings
	public static ArrayList<String> tokenize(ArrayList<String> input){
		return null;
	}
	
	// Assigns each token a unique Yeet ID (Unique capitalization of the word yeet)
	public static ArrayList<Pair> assignYeetIDs(ArrayList<String> tokens){
		return null;
	}
	
	// Does the reverse of performMacros (Replaces value with key and re-inserts #define statements at the
	//	top of the file.
	public static void unperformMacros(ArrayList<String> input, ArrayList<Pair> substitutions) {
		return;
	}
	
	
	
}
