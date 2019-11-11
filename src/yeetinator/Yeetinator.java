package yeetinator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		ArrayList<String> input = readInFile("C:\\Users\\walak\\Desktop\\Code\\Files\\lzw - Copy.c");
			
		removeComments(input);		
		/*// Find and perform original macros
		ArrayList<Pair> originalMacros = stripMacros(input);
		performMacros(input, originalMacros);*/
		
		// Tokenize, then find and "unperform" new yeet macros
		ArrayList<String> tokens = tokenize(input);
		
		for(int i = 0; i < tokens.size(); i++) {
			System.out.println(tokens.get(i));
		}	
		
		System.exit(0);
		
		ArrayList<Pair> newMacros = assignYeetIDs(tokens);
		unperformMacros(input, newMacros);
		
	}
	
	// Reads file from stdin and returns an ArrayList where index 0 is the 0th line, 1 is the 1st line etc...
	// If the line ends with a '\' (Line splice), the nth and n+1th lines are combined
	public static ArrayList<String> readInFile(String filename){
		ArrayList<String> output = new ArrayList();
		try {
			File file = new File(filename);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			boolean concat = false;
			int i = 0;
			while((line = reader.readLine()) != null) {
				
				if(concat) {
					String previous = output.remove(i);
					output.add(previous.substring(0, previous.length()-1)+line);
				}else{
					output.add(line);
				}
				
				if(line.length() != 0) {
					concat = (line.charAt(line.length()-1) == '\\');
				}else {
					concat = false;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return output;
	}
	
	// Removes all comments (// and /*) from input
	public static void removeComments(ArrayList<String> input){
		boolean inComment = false; // Used to track if we are in a block comment from the previous line
		for(int i = 0; i < input.size(); i++) {
			String line = input.get(i);			
			
			// Find end of previous block comment before continuing
			if(inComment) {
				int endIndex = line.indexOf("*/");
				if(endIndex == -1) {
					line = "";
				}else{
					line = line.substring(endIndex+2);
					inComment = false;
				}
			}
			
			line = line.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)",""); // CREDIT: http://ostermiller.org/findcomment.html
			
			// See if any block comment extends beyond this line
			int commentStarted = -1;
			for(int j = 0; j < line.length()-1; j++) {
				if(inComment) {
					if((line.charAt(j) == '*') && (line.charAt(j+1) == '/')) {
						inComment = false;
					}
				}else {
					if((line.charAt(j) == '/') && (line.charAt(j+1) == '*')) {
						inComment = true;
						commentStarted = j;
					}
				}
			}
			
			if(inComment && (line.length() != 0)) {
				line = line.substring(0, commentStarted);
			}
			
			input.remove(i);
			input.add(i, line);
		}
		return;
	}
	
	/*
	// Finds all macros in #define statements, strips them from input, and returns the macros as key, value pairs
	public static ArrayList<Pair> stripMacros(ArrayList<String> input){
		return null;
	}
	
	// Performs macros given by substitutions (A list of key, value pairs) and returns the changed file as 
	// 	and ArrayList
	public static void performMacros(ArrayList<String> input, ArrayList<Pair> substitutions){
		return;
	}*/
	
	// Finds all tokens present in input and outputs them as a list of strings
	// Uses linear search to find duplicates (barfs in mouth) for now... implement something else laterer
	public static ArrayList<String> tokenize(ArrayList<String> input){
		ArrayList<String> tokens = new ArrayList<String>();
		for(int i = 0; i < input.size(); i++) {
			String line = input.get(i);
			
			// Ignore lines that start with #
			if((line.length() != 0) && line.charAt(0) == '#') {
				continue;
			}
			
			// Handle strings and chars
			// Assumes that if an open quote or tick mar exist, a closing mark exists as well
			int quoteStart;
			for(int j = 0; j < line.length()-1; j++) {
				if((line.charAt(j) == '"') || (line.charAt(j) == '\'')) {
					char stopChar = line.charAt(j);
					quoteStart = j;
					j++;
					while((j < line.length()) && (line.charAt(j) != stopChar)) {
						if(line.charAt(j) == '\\') {
							j += 2;
						}else {
							j++;
						}
					}
					
					String toAdd = line.substring(quoteStart, j+1);
					if(!tokens.contains(toAdd)) {
						tokens.add(toAdd);
					}
					
					line = line.substring(0, quoteStart) + line.substring(j+1); // Make sure this isn't out of bounds
					j = quoteStart-1;
				}
			}
			
			// All other tokens
			String[] lineTokens = line.split("[,;{}() \t]", -1);
			for(int j = 0; j < lineTokens.length; j++) {
				if(lineTokens[j].length() != 0) {
					String toAdd = lineTokens[j];
					if(!tokens.contains(toAdd)) {
						tokens.add(toAdd);
					}
				}
				
			}
		}
		
		return tokens;
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
