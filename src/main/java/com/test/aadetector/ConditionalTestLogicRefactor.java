package com.test.aadetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalTestLogicRefactor {

//    // Method to refactor test code with Conditional Test Logic smell
//    public String refactorConditionalTestLogic(String testMethodCode) {
//        // Check if the test method contains an IF structure
//        if (testMethodCode.contains("if")) {
//            // Extract the content of every IF and create separate test methods
//            String[] testMethods = extractIFStatements(testMethodCode);
//            StringBuilder refactoredCode = new StringBuilder();
//            for (int i = 0; i < testMethods.length; i++) {
//                refactoredCode.append("void testMethod").append(i).append("() {")
//                        .append(testMethods[i])
//                        .append("}\n");
//            }
//            return refactoredCode.toString();
//        } else if (testMethodCode.contains("for")) {
//            // Extract the content of the FOR loop and create a new test method with RepeatedTest tag
//            String extractedContent = extractForLoopContent(testMethodCode);
//            String refactoredCode = "@RepeatedTest(n) void testMethodRepeated() {\n" + extractedContent + "\n}";
//            return refactoredCode;
//        } else {
//            // No Conditional Test Logic smell found, return the original test method code
//            return testMethodCode;
//        }
//    }
//
//    // Helper method to extract the content of every IF and return them as an array
//    private String[] extractIFStatements(String testMethodCode) {
//        String[] ifs = testMethodCode.split("if\\s*\\(.*?\\)\\s*\\{");
//        for (int i = 0; i < ifs.length; i++) {
//            if (!ifs[i].contains("assert")) {
//                // If the IF block does not contain an assertion message, discard it
//                ifs[i] = "";
//            } else {
//                // Remove any trailing or leading whitespace
//                ifs[i] = ifs[i].trim();
//                // Add closing brace if it's missing
//                if (!ifs[i].endsWith("}")) {
//                    ifs[i] += "}";
//                }
//            }
//        }
//        return ifs;
//    }
//
//    // Helper method to extract the content of the FOR loop and return it
//    private String extractForLoopContent(String testMethodCode) {
//        // Assuming the FOR loop is correctly formatted, find the body of the loop
//        int startIndex = testMethodCode.indexOf("for");
//        int endIndex = testMethodCode.lastIndexOf("}");
//        return testMethodCode.substring(startIndex, endIndex + 1);
//    }
//    
//    public static List<String> extractMethods(String junitTestClass) {
//        List<String> result = new ArrayList<>();
//        Pattern methodPattern = Pattern.compile("@Test\\s+public\\s+void\\s+(\\w+)\\(\\)\\s+\\{([^}]*)\\}");
//        Matcher matcher = methodPattern.matcher(junitTestClass);
//
//        while (matcher.find()) {
//            String methodName = matcher.group(1);
//            String methodContent = matcher.group(2).trim();
//            methodContent = extractConditionalBlocks(methodContent);
//
//            String newMethod = "@Test\npublic void new" + methodName + "() {\n" + methodContent + "\n}";
//            result.add(newMethod);
//        }
//
//        return result;
//    }
//
//    private static String extractConditionalBlocks(String methodContent) {
//        Pattern ifPattern = Pattern.compile("if\\s*\\([^)]*\\)\\s*\\{([^}]*)\\}");
//        Matcher ifMatcher = ifPattern.matcher(methodContent);
//
//        while (ifMatcher.find()) {
//            String ifBlockContent = ifMatcher.group(1).trim();
//            String newMethodName = "condition" + (int) (Math.random() * 1000000); // To generate a unique method name
//
//            methodContent = methodContent.replace(ifMatcher.group(), newMethodName + "();");
//            methodContent += "\n\nprivate void " + newMethodName + "() {\n" + ifBlockContent + "\n}";
//        }
//
//        return methodContent;
//    }
//    
//    public static void runExtraction(List<String> testClassLines) {
//    	
//    }
	
	private List<String> code;
	private List<String> refactoredCode;
	private int currentIndex;
	private String refactoredLines;
	private List<List<String>> refactoredMethods;
	private List<String> originalCode;
	
	public ConditionalTestLogicRefactor(List<String> codeLines) {
		super();
		// TODO Auto-generated constructor stub
		this.code = codeLines;
		currentIndex = 1;
		refactoredLines = "";
	}
	
	public List<String> extractCode(){		
		while (currentIndex < code.size()) {
			String line = code.get(currentIndex).trim();
			if(line.contains("@Test")) {
				if (line.startsWith("@Test") || line.startsWith("public void")) {
	                // Found the beginning of a method
	                String methodName = extractMethodName(line);
	                int methodStartIndex = currentIndex;
	                int methodEndIndex = findMethodEndIndex(code, currentIndex);
	                refactoredMethods.clear();
	                originalCode.clear();

	                if (methodEndIndex != -1) {
	                    // Successfully found the method's end
	                	boolean hasAssert = false;
	                    for(int i = methodStartIndex; i < methodEndIndex; i++) {
	                    	if(code.get(i).trim().startsWith("assert")) {
	                    		hasAssert = true;
	                    	}
	                    }
	                    if(hasAssert) {
	                    	int index = methodStartIndex;
	                    	int endIndex = methodEndIndex;
	                    	while(index < endIndex) {
	                    		String currentLine = code.get(index).trim();
	                    		if(currentLine.startsWith("if")) {
	                    			List<String> ifMethod;
	                    			int startIfIndex = index;
	                    			int endIfIndex = getStatementEndIndex(code, index);
	                    			
	                    		}
	                    		index++;
	                    	}
	                    }
	                    else {
	                    	currentIndex = methodEndIndex;
	                    }
	                    
	                } else {
	                    // Error in method parsing, exit loop
	                    break;
	                }
	            }
			}
			currentIndex++;
		}
	}
	
	public void extractCode() {
		while (currentIndex < code.size()) {
			String line = code.get(currentIndex).trim();
			if (line.startsWith("@Test") || line.startsWith("public void")) {
				extractMethod();
			}
			currentIndex++;
		}
	}
	
	public void extractMethod() {

        // Found the beginning of a method
		String line = code.get(currentIndex).trim();
        String methodName = extractMethodName(line);
        int methodStartIndex = currentIndex;
        int methodEndIndex = findMethodEndIndex(code, currentIndex);
        refactoredMethods.clear();
        originalCode.clear();

        if (methodEndIndex != -1) {
            // Successfully found the method's end
        	boolean hasCondition = false;
        	boolean hasAssert = false;
        	Stack<Integer> beginStatement = new Stack<Integer>();
        	Stack<Integer> endStatement = new Stack<Integer>();
            for(int i = methodStartIndex; i < methodEndIndex; i++) {
            	if(code.get(i).trim().startsWith("if")) {
            		hasCondition = true;
            		beginStatement.add(i);
            		endStatement.add(getStatementEndIndex(code, i));
            		
            	}
            	if(hasCondition) {
            		if(i == endStatement.peek()) {
            			hasCondition = false;
            			beginStatement.pop();
            			endStatement.pop();
            		}
            		else {
            			if(code.get(i).trim().startsWith("assert")) {
            				hasAssert = true;
            				break;
            			}
            		}
            	}
            	else {
            		originalCode.add(code.get(i));
            	}
            }
            if(hasCondition && hasAssert) {
            	int index = methodStartIndex;
            	int endIndex = methodEndIndex;
            	while(index < endIndex) {
            		String currentLine = code.get(index).trim();
            		if(currentLine.startsWith("if")) {
            			List<String> ifMethod;
            			int startIfIndex = index;
            			int endIfIndex = getStatementEndIndex(code, index);
            			
            		}
            		index++;
            	}
            }
            else {
        		currentIndex = methodEndIndex;
        		return;
        	}
        } 
        else {
            // Error in method parsing, exit loop
            return;
        }
        

	}
	
	public static List<MethodInfo> detectMethods(List<String> codeLines) {
        List<MethodInfo> methodInfos = new ArrayList<>();
        int currentIndex = 0;

        while (currentIndex < codeLines.size()) {
            String line = codeLines.get(currentIndex).trim();

            if (line.startsWith("@Test") || line.startsWith("public void")) {
                // Found the beginning of a method
                String methodName = extractMethodName(line);
                int methodStartIndex = currentIndex;
                int methodEndIndex = findMethodEndIndex(codeLines, currentIndex);

                if (methodEndIndex != -1) {
                    // Successfully found the method's end
                    methodInfos.add(new MethodInfo(methodName, methodStartIndex, methodEndIndex));
                    currentIndex = methodEndIndex;
                } 
                else {
                    // Error in method parsing, exit loop
                    break;
                }
            }

            currentIndex++;
        }

        return methodInfos;
    }

    private static String extractMethodName(String line) {
        // Extract method name from the method declaration line
        Pattern methodNamePattern = Pattern.compile("public void (\\w+)\\s*\\(.*\\)\\s*\\{?");
        Matcher matcher = methodNamePattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }

    private static int findMethodEndIndex(List<String> codeLines, int startIndex) {
        // Similar approach as the previous IfStatementDetector#getIfStatementEndIndex method
        // using a stack to find the method's end
        // You can also consider using a more advanced Java parser for more robust detection.
        // For simplicity, we'll use a simplified approach here.
        // This method assumes that method definitions are well-formed and there are no nested methods.

        int currentIndex = startIndex;
        int braceCount = 1; // Start with 1 to handle the method's opening brace

        while (currentIndex < codeLines.size()) {
            String line = codeLines.get(currentIndex);

            for (char ch : line.toCharArray()) {
                if (ch == '{') {
                    braceCount++;
                } else if (ch == '}') {
                    braceCount--;

                    if (braceCount == 0) {
                        // Found the corresponding closing brace for the method
                        return currentIndex;
                    }
                }
            }

            currentIndex++;
        }

        // If no matching closing brace is found, return -1 to indicate an error.
        return -1;
    }
    
    public static int getStatementEndIndex(List<String> codeLines, int startIndex) {
        Stack<Character> stack = new Stack<>();
        int currentIndex = startIndex;

        // Start searching from the startIndex and continue until the end of the codeLines
        while (currentIndex < codeLines.size()) {
            String line = codeLines.get(currentIndex);

            for (char ch : line.toCharArray()) {
                if (ch == '{') {
                    stack.push(ch);
                } else if (ch == '}') {
                    if (stack.isEmpty()) {
                        // Found the corresponding closing brace for the if statement
                        return currentIndex + 1;
                    } else {
                        stack.pop();
                    }
                }
            }

            // Move to the next line
            currentIndex++;
        }

        // If no matching closing brace is found, return -1 to indicate an error.
        return -1;
    }
    
    public static String getConditionFromIfStatement( String javaCode) {
    	  int ifStatementStart = javaCode.indexOf("if");
    	  int ifStatementEnd = javaCode.indexOf(")");
    	  String condition = javaCode.substring(ifStatementStart + 2, ifStatementEnd);
    	  return condition;
    	}
    
    public static int checkIfElse(String[] codeLines, int ifStatementStart) {
    	  int elseStatementLine = -1;
    	  for (int index = ifStatementStart + 1; index < codeLines.length; index++) {
    	    if (codeLines[index].startsWith("else")) {
    	      elseStatementLine = index;
    	      break;
    	    }
    	  }
    	  return elseStatementLine;
    	}
    
    public static int findMatchingBracket( List<String> codeLines, int line) {
        Stack<Character> stack = new Stack<>();
        for (int index = line; index < codeLines.size(); index++) {
          char c = codeLines.get(index).charAt(0);
          if (c == '(' || c == '{' || c == '[') {
            stack.push(c);
          } else if (c == ')' || c == '}' || c == ']') {
            if (stack.isEmpty()) {
              return -1;
            }
            char top = stack.pop();
            if (c != matchingBracket(top)) {
              return -1;
            }
          }
        }
        if (!stack.isEmpty()) {
          return -1;
        }
        return line;
      }

      private static char matchingBracket(char c) {
        switch (c) {
          case '(': return ')';
          case '{': return '}';
          case '[': return ']';
          default: return '\0';
        }
      }
      
      private static boolean checkForAsserts( List<String> code, int begin, int end) {
    	  for(int i = begin; i < end; i++) {
    		  if(code.get(i).trim().startsWith("assert")) {
    			  return true;
    		  }
    	  }
    	  return false;
      }

}
