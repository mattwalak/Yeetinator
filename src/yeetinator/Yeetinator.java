package yeetinator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class Yeetinator {
	
	private class Pair{
		String key, value;
		public void Pair(String k, String v) {
			key = k;
			value = v;
		}
	}
	
	public static final String DELIMS = "[,;{}() \t]";
	
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
	
	// Finds all tokens present in input and outputs them as a list of strings
	public static ArrayList<String> tokenize(ArrayList<String> input){
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(",");
		tokens.add(";");
		tokens.add("{");
		tokens.add("}");
		tokens.add("(");
		tokens.add(")");
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
					
					line = line.substring(0, quoteStart) + line.substring(j+1);
					j = quoteStart-1;
				}
			}
			
			// All other tokens
			String[] lineTokens = line.split(DELIMS, -1);
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
	
	// Generates a Yeet ID from a string based on the super cool Yeet ID assigning algorithm
	public static String getYeetID(int index) {
		int n = 4;
		for(int i = index; i > 15; i -= Math.pow(2, n)) {
			n++;
		}
		
		int yeetValue = index;
		for(int i = 4; i < n; i++) {
			yeetValue -= Math.pow(2, i);
		}
		
		String id = Integer.toBinaryString(yeetValue);
		while(id.length() < n) {
			id = "0" + id;
		}
		
		String ee = id.substring(1, id.length()-1);
		ee = ee.replace('0', 'e');
		ee = ee.replace('1', 'E');
		
		char y;
		if(id.charAt(0) == '0') {
			y = 'y';
		}else {
			y = 'Y';
		}
		
		char t;
		if(id.charAt(id.length()-1) == '0') {
			t = 't';
		}else {
			t = 'T';
		}
		
		return y + ee + t;
	}
	
	// Replaces every occurrence of substitution with associated macro
	public static void unperformMacros(ArrayList<String> input, ArrayList<String> substitutions) {
		for(int i = 0; i < substitutions.size(); i++) {
			// Add new #define statement at top of file
			String macro = "#define "+getYeetID(i) + " " + substitutions.get(i);
			input.add(0, macro);
			
			for(int j = 0; j < input.size(); j++) {
				String line = input.get(j);
				if((line.length() != 0) && (line.charAt(0) == '#')) {
					continue;
				}
				
				String key = substitutions.get(i);
				if((key.charAt(0) == '"') || (key.charAt(0) == '\'')) {
					line = line.replace(substitutions.get(i), getYeetID(i));
					input.remove(j);
					input.add(j, line);
				}else {
					line = line.replaceAll(DELIMS+"\\b"+substitutions.get(i)+"\\b"+DELIMS, getYeetID(i));
					line = line.replaceAll("^\\b"+substitutions.get(i)+"\\b"+DELIMS, getYeetID(i));
					line = line.replaceAll(DELIMS+"\\b"+substitutions.get(i)+"\\b$", getYeetID(i));
					input.remove(j);
					input.add(j, line);
				}
			}
		}	
	}
	
	public static void main(String[] args) {
		
		String line = "a this,(";
		System.out.println(line.replaceAll(DELIMS+"\\bthis\\b"+DELIMS, "crazy"));
		
		System.exit(0);
		
		ArrayList<String> input = readInFile("C:\\Users\\walak\\Desktop\\Code\\Files\\lzw - Copy.c");			
		removeComments(input);		
		
		ArrayList<String> tokens = tokenize(input);		
		Collections.shuffle(tokens);
		unperformMacros(input, tokens);
		
		for(int i = 0; i < input.size(); i++) {
			System.out.println(input.get(i));
		}
	}
	
}
