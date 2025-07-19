package com.test.aadetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in
 * a test method that have no explanation. If one of the assertions fails, you
 * do not know which one it is. A. van Deursen, L. Moonen, A. Bergh, G. Kok,
 * â€œRefactoring Test Codeâ€�, Technical Report, CWI, 2001.
 */


public class MysteryGuest extends AbstractSmell {
	
	ArrayList<TestSmell> listTestSmells;
	TestSmell cadaTestSmell;	
	private List<SmellyElement> smellyElementList;
	private ArrayList<MethodUsage> mysteryInstance;
	
	String className;
	String filePath;
	private ArrayList<MethodUsage> instances;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

	public MysteryGuest(String name, String path) {
		setClassName(name);
		setFilePath(path);
		smellyElementList = new ArrayList<>();
	}
	@Override
	public String getSmellName() {
		return "Resource Optimism";
	}

	/**
	 * Returns true if any of the elements has a smell
	 */
	@Override
	public boolean getHasSmell() {
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
	}

	@Override
	public long getCountSmell(String name) {
		setClassName(name);
		JOptionPane.showMessageDialog(null, "Nome da classe: " + getClassName());
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count();
	}

	/**
	 * Analyze the test file for test methods for multiple assert statements without
	 * an explanation/message
	 * @return 
	 */
	@Override
	public List<TestSmell> runAnalysis(CompilationUnit testFileCompilationUnit,
												  String testFileName) throws FileNotFoundException {
		mysteryInstance = new ArrayList<>();
		listTestSmells = new ArrayList<TestSmell>();
		instances = new ArrayList<>();
		MysteryGuest.ClassVisitor classVisitor;
		classVisitor = new MysteryGuest.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);
		
		for (MethodUsage method : instances) {
            TestMethod testClass = new TestMethod(method.getTestMethodName());
            testClass.setRange(method.getRange());
//            testClass.addDataItem("begin", method.getLine());
//            testClass.addDataItem("end", method.getLine()); // [Remover]
            testClass.setHasSmell(true);
            smellyElementList.add(testClass);
        }
		
		return listTestSmells;
	}


	/**
	 * Returns the set of analyzed elements (i.e. test methods)
	 */
	@Override
	public List<SmellyElement> getSmellyElements() {
		return smellyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		
		private MethodDeclaration currentMethod = null;
		TestMethod testMethod;
		private int magicCount = 0;
		
		private List<String> mysteryTypes = new ArrayList<>(
                Arrays.asList(
                        "File",
                        "FileOutputStream",
                        "SQLiteOpenHelper",
                        "SQLiteDatabase",
                        "Cursor",
                        "Context",
                        "HttpClient",
                        "HttpResponse",
                        "HttpPost",
                        "HttpGet",
                        "SoapObject"
                ));
	

     // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                super.visit(n, arg);

                //reset values for next method
                currentMethod = null;
//                mysteryCount = 0;
            }
        }
        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            super.visit(n, arg);
            //Note: the null check limits the identification of variable types declared within the method body.
            // Removing it will check for variables declared at the class level.
            //TODO: to null check or not to null check???
            if (currentMethod != null) {
                boolean hasMystery = false;
                for (String variableType : mysteryTypes) {
                    //check if the type variable encountered is part of the mystery type collection
                	
                    if ((n.getVariable(0).getType().asString().equals(variableType))) {
                        //check if the variable has been mocked
                        for (AnnotationExpr annotation : n.getAnnotations()) {
                            if (annotation.getNameAsString().equals("Mock") || annotation.getNameAsString().equals("Spy"))
                                break;
                        }
                        
                        String[] parts = n.getVariable(0).toString().split("=");
                        String assignmentValue = parts[1].trim().replace(";", "");
//                        System.out.println(assignmentValue);
                        if(!assignmentValue.startsWith(variableType)) {
//                        	System.out.println(true);
                        	break;
                        }
                        // variable is not mocked, hence it's a smell
//                        mysteryCount++;
                        hasMystery = true;
//                        mysteryInstance.add(new MethodUsage(currentMethod.getNameAsString(), "",n.getRange().get().begin.line+""));
                    }
                }
                if (hasMystery) {
                    MethodUsage methodUsage = new MethodUsage(currentMethod.getNameAsString(), "",currentMethod.getRange().get().begin.line + "-" + currentMethod.getRange().get().end.line);
                    if (!mysteryInstance.contains(methodUsage))
//                        mysteryInstance.add(methodUsage);
                    	insertTestSmell(n.getRange().get(), this.currentMethod);
                }
            }
        }
        
    }

//		insertTestSmell(n.getRange().get(), this.testMethod);

	public void insertTestSmell (Range range, MethodDeclaration testMethod) {
		cadaTestSmell = new TestSmell("Mystery Guest", testMethod.getNameAsString(), range.begin.line, range.end.line, "Use Temporary Directory", "Refactoring option 2");
		listTestSmells.add(cadaTestSmell);
		
//		String smellLocation;
//		smellLocation = "Classe " + getClassName() + "\n" + 
//		"M�todo " + testMethod.getName() + "() \n" + 
//		"Begin " + range.begin.line + "\n" +
//		"End " + range.end.line;
//		System.out.println(smellLocation);
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public int contSmell() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<TestSmellDescription> runAnalysis(CompilationUnit testFileCompilationUnit,
			CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void refactor(int line) {

		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		line--;

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		String originalCode = lines.get(line);
		String leadingSpace = Util.getLeadingSpaces(originalCode);
//		System.out.println(originalCode);
		String[] parts = originalCode.split("=");
        String assignmentValue = parts[1].trim().replace(";", "");
//        System.out.println(assignmentValue);
        
        String[] dotSplit = assignmentValue.trim().split("\\.");
//        System.out.println(dotSplit.length);
        String type = dotSplit[0].trim();
        dotSplit[0] = "tempDir";
        
        assignmentValue = " " + String.join(".", dotSplit);
        
        parts[1] = assignmentValue;
        
        String refactoredLine = leadingSpace + String.join("=", parts) + ";";

		lines.set(line, refactoredLine);
		
		for(int i = line; i > 0; i--) {
			String currentLine = lines.get(i);
//			System.out.println(currentLine);	
			if(currentLine.trim().startsWith("public")) {
				leadingSpace = Util.getLeadingSpaces(currentLine);
				String[] declarationParts = currentLine.split("\\(");
				declarationParts[1] = "@TempDir " + type + " tempDir" + declarationParts[1];
				refactoredLine = leadingSpace + String.join("(", declarationParts);
				lines.set(i, refactoredLine);
				break;
			}
		}

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);

		System.out.println(result);

	}
	
	public static void refactorNoPrint(int line) {

		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		line--;

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		String originalCode = lines.get(line);
		String leadingSpace = Util.getLeadingSpaces(originalCode);
//		System.out.println(originalCode);
		String[] parts = originalCode.split("=");
        String assignmentValue = parts[1].trim().replace(";", "");
//        System.out.println(assignmentValue);
        
        String[] dotSplit = assignmentValue.trim().split("\\.");
//        System.out.println(dotSplit.length);
        String type = dotSplit[0].trim();
        dotSplit[0] = "tempDir";
        
        assignmentValue = " " + String.join(".", dotSplit);
        
        parts[1] = assignmentValue;
        
        String refactoredLine = leadingSpace + String.join("=", parts) + ";";

		lines.set(line, refactoredLine);
		
		for(int i = line; i > 0; i--) {
			String currentLine = lines.get(i);
//			System.out.println(currentLine);	
			if(currentLine.trim().startsWith("public")) {
				leadingSpace = Util.getLeadingSpaces(currentLine);
				String[] declarationParts = currentLine.split("\\(");
				declarationParts[1] = "@TempDir " + type + " tempDir" + declarationParts[1];
				refactoredLine = leadingSpace + String.join("(", declarationParts);
				lines.set(i, refactoredLine);
				break;
			}
		}

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);

//		System.out.println(result);

	}
}
