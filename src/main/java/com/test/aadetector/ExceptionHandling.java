package com.test.aadetector;

import java.util.List;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ExceptionHandling {

	static List<TryCatchInfo> list;

	public static void refactor(int lineNumber) {
		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		TryCatchInfo tryCatchInfo = detectTryCatchInfoOnLine(lines, lineNumber);
		
		if(tryCatchInfo == null) {
			return;
		}
		
		String tabSpace =  Util.getLeadingSpaces(lines.get(lineNumber));
		String singleTab = "	";
		String doubleTab = "		";
		int newEnd = 0;
		
		for(int i = tryCatchInfo.endLine - 1; i >= tryCatchInfo.tryStartLine - 1; i--) {
			lines.remove(i);
			newEnd--;
		}
		
		List<String> newLines = new ArrayList<>();
		newLines.add(tabSpace + "assertThrows(" + tryCatchInfo.exception + ",");
		if(tryCatchInfo.tryContent.size() == 1) {
			String line = tryCatchInfo.tryContent.get(0).trim();
			newLines.add(tabSpace + doubleTab + "() -> " + line.substring(0, line.length() - 1) + ");");
		} else {
			newLines.add(tabSpace + doubleTab + "() -> {");
			for(int i = 0; i < tryCatchInfo.tryContent.size(); i++) {
				newLines.add(tabSpace + doubleTab + singleTab + tryCatchInfo.tryContent.get(i).trim());
			}
			newLines.add(tabSpace + doubleTab + "});");
		}
		for(int i = 0; i < tryCatchInfo.catchContent.size(); i++) {
			if(!tryCatchInfo.catchContent.get(i).trim().toLowerCase().startsWith("assert")) {
				newLines.add(tabSpace + tryCatchInfo.catchContent.get(i).trim());
			}
		}
		
		newEnd += newLines.size();
		
		lines.addAll(lineNumber, newLines);
		
		String result = Util.listToCode(lines);
		
		Util.writeStringToFile(result);
		
		List<String> response = Arrays.asList(result, Integer.toString(newEnd));
		
		Gson gson = new Gson();
		String json = gson.toJson(response);
		
		System.out.println(json);
		
	}
	
	public static void refactorNoPrint(int lineNumber) {
		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);
		
//		System.out.println(lines.get(lineNumber));

		TryCatchInfo tryCatchInfo = detectTryCatchInfoOnLine(lines, lineNumber);
		
		if(tryCatchInfo == null) {
			return;
		}
		
		String tabSpace =  Util.getLeadingSpaces(lines.get(lineNumber));
		String singleTab = "	";
		String doubleTab = "		";
		int newEnd = 0;
		
		for(int i = tryCatchInfo.endLine - 1; i >= tryCatchInfo.tryStartLine - 1; i--) {
			lines.remove(i);
			newEnd--;
		}
		
		List<String> newLines = new ArrayList<>();
		newLines.add(tabSpace + "assertThrows(" + tryCatchInfo.exception + ",");
		if(tryCatchInfo.tryContent.size() == 1) {
			String line = tryCatchInfo.tryContent.get(0).trim();
			newLines.add(tabSpace + doubleTab + "() -> " + line.substring(0, line.length() - 1) + ");");
		} else {
			newLines.add(tabSpace + doubleTab + "() -> {");
			for(int i = 0; i < tryCatchInfo.tryContent.size(); i++) {
				newLines.add(tabSpace + doubleTab + singleTab + tryCatchInfo.tryContent.get(i).trim());
			}
			newLines.add(tabSpace + doubleTab + "});");
		}
		for(int i = 0; i < tryCatchInfo.catchContent.size(); i++) {
			if(!tryCatchInfo.catchContent.get(i).trim().toLowerCase().startsWith("assert")) {
				newLines.add(tabSpace + tryCatchInfo.catchContent.get(i).trim());
			}
		}
		
		newEnd += newLines.size();
		
		lines.addAll(lineNumber, newLines);
		
		String result = Util.listToCode(lines);
		
		Util.writeStringToFile(result);
		
//		List<String> response = Arrays.asList(result, Integer.toString(newEnd));
//		
//		Gson gson = new Gson();
//		String json = gson.toJson(response);
//		
//		System.out.println(json);
		
	}

	public static List<TestSmell> detect() {
		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		list = new ArrayList<>();
		detectTryCatchInfo(lines, 0);

		List<TestSmell> testSmellList = new ArrayList<>();

		list.forEach((item) -> {
			if (item != null) {
//				System.out.println("Method " + item.methodName);
//				System.out.println(item.tryStartLine + " to " + item.endLine  );
//				System.out.println("Try " + item.tryContent);
//				System.out.println("Exception " + item.exception);
//				System.out.println("Catch " + item.catchContent);
				testSmellList.add(new TestSmell("Exception Handling", item.methodName, item.tryStartLine, item.endLine,
						"Change to assertThrows", "Add @ExpectedException Annotation"));
			}
		});
		return testSmellList;
	}

	public static void detectTryCatchInfo(List<String> lines, int position) {
		String methodName = null;
		boolean inTry = false;
		boolean inCatch = false;
		int tryStartLine = -1;
		int catchStartLine = -1;
		int endLine = -1;
		String exceptionToBeCaught = null;
		List<String> tryContent = new ArrayList<>();
		List<String> catchContent = new ArrayList<>();
		boolean hasAssert = false;

		for (int i = position; i < lines.size(); i++) {

			if (i == endLine && hasAssert) {
				list.add(new TryCatchInfo(methodName, tryStartLine, tryContent, catchStartLine, exceptionToBeCaught,
						catchContent, endLine + 1));
				inTry = false;
				inCatch = false;
				tryStartLine = -1;
				catchStartLine = -1;
				endLine = -1;
				exceptionToBeCaught = null;
				tryContent = new ArrayList<>();
				catchContent = new ArrayList<>();
				hasAssert = false;
			}
			if (i >= lines.size()) {
				return;
			}

			String line = lines.get(i).trim();

			if (line.contains("public") && line.contains("(") && line.contains(")") && !line.startsWith("//")) {
				methodName = Util.getMethodName(line);
			}
			if (line.contains("try") && !line.startsWith("//")) {
				inTry = true;
				tryStartLine = i + 1;
			} else if (lines.get(i).contains("catch") && !line.startsWith("//")) {
				inTry = false;
				inCatch = true;
				endLine = Util.findClosingBracket(lines, i);
				catchStartLine = i + 1;
				exceptionToBeCaught = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
			} else if (inTry) {
				tryContent.add(line);
			} else if (inCatch) {
				catchContent.add(line);
				if (line.toLowerCase().contains("assert") && !line.trim().startsWith("//")) {
					hasAssert = true;
				} else {
					hasAssert = false;
					
				}
			}
		}

	}

	public static TryCatchInfo detectTryCatchInfoOnLine(List<String> lines, int position) {
		String methodName = null;
		boolean inTry = false;
		boolean inCatch = false;
		int tryStartLine = -1;
		int catchStartLine = -1;
		int endLine = -1;
		String exceptionToBeCaught = null;
		List<String> tryContent = new ArrayList<>();
		List<String> catchContent = new ArrayList<>();
		boolean hasAssert = false;

		String line = lines.get(position).trim();

		if (!line.startsWith("try")) {
			return null;
		}

		for (int i = position; i < lines.size(); i++) {

			if (i == endLine && hasAssert) {
				return new TryCatchInfo(methodName, tryStartLine, tryContent, catchStartLine, exceptionToBeCaught,
						catchContent, endLine + 1);
			}
			if (i >= lines.size()) {
				return null;
			}

			line = lines.get(i).trim();

			if (line.contains("public") && line.contains("(") && line.contains(")") && !line.startsWith("//")) {
				methodName = Util.getMethodName(line);
			}
			if (line.contains("try") && !line.startsWith("//")) {
				inTry = true;
				tryStartLine = i + 1;
			} else if (lines.get(i).contains("catch") && !line.startsWith("//")) {
				inTry = false;
				inCatch = true;
				endLine = Util.findClosingBracket(lines, i);
				catchStartLine = i + 1;
				exceptionToBeCaught = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
			} else if (inTry) {
				tryContent.add(line);
			} else if (inCatch) {
				catchContent.add(line);
				if (line.toLowerCase().contains("assert") && !line.startsWith("//")) {
					hasAssert = true;
				}
			}
		}

		return null;

	}

	public static class TryCatchInfo {
		String methodName;
		int tryStartLine;
		List<String> tryContent;
		int catchStartLine;
		String exception;
		List<String> catchContent;
		int endLine;

		public TryCatchInfo(String methodName, int tryStartLine, List<String> tryContent, int catchStartLine,
				String exception, List<String> catchContent, int endLine) {
			this.methodName = methodName;
			this.tryStartLine = tryStartLine;
			this.tryContent = tryContent;
			this.catchStartLine = catchStartLine;
			this.exception = exception;
			this.catchContent = catchContent;
			this.endLine = endLine;
		}
	}
}
