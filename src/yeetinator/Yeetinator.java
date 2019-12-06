package yeetinator;
import java.util.List;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public static final String DELIMS_REGEX = "[,;{}() \t]";
	public static final String DELIMS = ",;{}()";
	
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
			System.exit(1);
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
		for(int i = 0; i < DELIMS.length(); i++) {
			tokens.add(Character.toString(DELIMS.charAt(i)));
		}
		
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
			String[] lineTokens = line.split(DELIMS_REGEX, -1);
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
	
	// Returns an ArrayList<Integer> with all indexes of where delimiters occur
	public static ArrayList<Integer> getDelimIndicies(String line){
		ArrayList<Integer> out = new ArrayList<Integer>();
		for(int i = 0; i < line.length(); i++) {
			for(int j = 0; j < DELIMS.length(); j++) {
				if(line.charAt(i) == DELIMS.charAt(j)) {
					out.add(i);
					break;
				}
			}
		}
		return out;
	}
	
	// Returns true if a character is a defined delimiter, false otherwise
	public static boolean isDelim(char in) {
		for(int i = 0; i < DELIMS.length(); i++) {
			if(DELIMS.charAt(i) == in) {
				return true;
			}
		}
		return false;
	}
	
	// Replaces every occurrence of substitution with associated macro
	public static void unperformMacros(ArrayList<String> input, ArrayList<String> substitutions) {
		// Add new #define statement at top of file (But underneath original #includes and #defines)
		int start = 0;
		while(true) {
			if((input.get(start).length() > 0) && (input.get(start).charAt(0) != '#')) {
				break;
			}
			start++;
		}

		for(int i = 0; i < substitutions.size(); i++) {
			String macro = "#define "+  getYeetID(i) + " " + substitutions.get(i);
			input.add(start, macro);
		}
		
		// Quoted substitutions first
		for(int i = 0; i < input.size(); i++) {
			String line = input.get(i);
			if((line.length() > 0) && line.charAt(0) == '#') {
				continue;
			}
			
			for(int j = 0; j < substitutions.size(); j++) {				
				String key = substitutions.get(j);
				String yeetID = getYeetID(j) + " ";
				
				if(isDelim(key.charAt(0))){
					continue; // Skip delims for now
				}
				
				if((key.charAt(0) == '"') || (key.charAt(0) == '\'')) {
					// Quoted replacement
					line = line.replace(key, yeetID);
				}
			}
			input.remove(i);
			input.add(i, line);
		}	
		
		// Unquoted substitutions next	
		for(int i = 0; i < input.size(); i++) {
			String line = input.get(i);
			String original = line;
			if((line.length() > 0) && line.charAt(0) == '#') {
				continue;
			}
			
			for(int j = 0; j < substitutions.size(); j++) {				
				String key = substitutions.get(j);
				String yeetID = getYeetID(j) + " ";
				
				if(isDelim(key.charAt(0))){
					continue; // Skip delims for now
				}
				
				if((key.charAt(0) != '"') && (key.charAt(0) != '\'')) {
					int location = 0;
					int keyIndex = line.indexOf(key, location);
					while(keyIndex != -1) {
						// See if this occurrence is bounded by delimiters
						boolean left, right;
						int strEnd = keyIndex + key.length();
						left = (keyIndex == 0) || (isDelim(line.charAt(keyIndex-1)) || (Character.isWhitespace(line.charAt(keyIndex-1))));
						right = (strEnd >= line.length()) || (isDelim(line.charAt(strEnd))  || (Character.isWhitespace(line.charAt(strEnd))));
						if(left && right) {
							// Valid occurrence! Do the exchange
							line = line.substring(0, keyIndex) + yeetID + line.substring(strEnd);
							location = keyIndex + yeetID.length();
						}else {
							// Invalid occurrence... skip past this
							location = strEnd;
						}
						keyIndex = line.indexOf(key, location);
					}
				}
			}
			input.remove(i);
			input.add(i, line);
		}
		
		// And finally the delimiters
		for(int i = 0; i < input.size(); i++) {
			String line = input.get(i);
			if((line.length() > 0) && line.charAt(0) == '#') {
				continue;
			}
					
			for(int j = 0; j < substitutions.size(); j++) {				
				String key = substitutions.get(j);
				String yeetID = getYeetID(j) + " ";
						
				if(isDelim(key.charAt(0))){
					line = line.replace(key, yeetID);
				}
						
			}
			input.remove(i);
			input.add(i, line);
		}	
	}
	
	public static void writeToFile(ArrayList<String> input, String fileout) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileout));
			for(int i = 0; i < input.size(); i++) {
				writer.write(input.get(i)+"\n");
			}
			writer.close();
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Makes your yeets into a nice block
	public static ArrayList<String> beautify(ArrayList<String> original, int width) {
		ArrayList<String> beauty = new ArrayList<String>();
		String newLine = "";
		for(int i = 0; i < original.size(); i++) {
			String originalLine = original.get(i);
			if(originalLine.length() < 1) {
				continue;
			}else if(originalLine.charAt(0) == '#') {
				beauty.add(originalLine);
			}else {
				originalLine = originalLine.trim();
				while(originalLine.length() > 0) {
					int loc = 0;
					while((loc < originalLine.length()) && !Character.isWhitespace(originalLine.charAt(loc))) {
						loc++;
					}
					
					if((loc+newLine.length() + 1) > width) {
						beauty.add(newLine);
						newLine = originalLine.substring(0, loc)+" ";
					}else {
						newLine = newLine + originalLine.substring(0, loc) + " ";
					}
					
					originalLine = originalLine.substring(loc);
					originalLine = originalLine.trim();
				}
			}
		}
		beauty.add(newLine);		
		return beauty;
	}
	
	public static void main(String[] args) {
		if((args.length < 2) || (args.length > 3)) {
			System.out.println("usage: Yeetinator from_file to_file [page_width]");
			System.exit(0);
		}
		
		String filein = args[0];
		String fileout = args[1];
		int maxWidth = 75;
		if(args.length == 3) {
			try {
				maxWidth = Integer.parseInt(args[2]);
			}catch(NumberFormatException e) {
				System.out.println("usage: Yeetinator from_file to_file [page_width]");
				System.exit(1);
			}
		}
		
		System.out.println("Reading - "+filein);
		ArrayList<String> input = readInFile(filein);	
		System.out.println("Removing comments...");
		removeComments(input);		
		System.out.println("Tokenizing...");
		ArrayList<String> tokens = tokenize(input);		
		Collections.shuffle(tokens);
		System.out.println("Yeetifying...");
		unperformMacros(input, tokens);
		System.out.println("Beautifying...");
		input = beautify(input, maxWidth);
		System.out.println("Writing to file...");
		writeToFile(input, fileout);
		System.out.println("Finished - "+fileout);
	}
	
}
