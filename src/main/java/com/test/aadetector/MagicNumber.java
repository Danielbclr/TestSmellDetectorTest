package com.test.aadetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.Gson;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in
 * a test method that have no explanation. If one of the assertions fails, you
 * do not know which one it is. A. van Deursen, L. Moonen, A. Bergh, G. Kok,
 * â€œRefactoring Test Codeâ€�, Technical Report, CWI, 2001.
 */


public class MagicNumber extends AbstractSmell {

	ArrayList<TestSmell> listTestSmells;
	TestSmell cadaTestSmell;	
	private List<SmellyElement> smellyElementList;
	private List<MethodUsage> methodConditional;

	String className;
	String filePath;
	private ArrayList<MethodUsage> instances;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

	public MagicNumber(String name, String path) {
		setClassName(name);
		setFilePath(path);
		smellyElementList = new ArrayList<>();
		methodConditional = new ArrayList<>();
	}
	@Override
	public String getSmellName() {
		return "Magic Number Test";
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

		listTestSmells = new ArrayList<TestSmell>();
		instances = new ArrayList<>();
		MagicNumber.ClassVisitor classVisitor;
		classVisitor = new MagicNumber.ClassVisitor();
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

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg){
			if (Util.isValidTestMethod(n)) {
				currentMethod = n;
				super.visit(n, arg);
				testMethod = new TestMethod(n.getNameAsString());

				//reset values for next method
				currentMethod = null;
				magicCount = 0;
			}
		}

		// examine the methods being called within the test method
		@Override
		public void visit(MethodCallExpr n, Void arg){
			super.visit(n, arg);
			if (currentMethod != null) {
				// if the name of a method being called start with 'assert'
				if (n.getNameAsString().startsWith(("assert"))) {
					// checks all arguments of the assert method

					for (Expression argument : n.getArguments()) {
						// if the argument is a number
						if (Util.isNumber(argument.toString())) {
							MethodUsage verification = new MethodUsage(currentMethod.getNameAsString(),
									"",n.getRange().get().begin.line+"");
							if (!instances.contains(verification)){
								instances.add(verification);
								insertTestSmell(argument.getRange().get(), this.currentMethod);
							}
						}
						// if the argument contains an ObjectCreationExpr (e.g. assertEquals(new Integer(2),...)
						else if (argument instanceof ObjectCreationExpr) {
							checkObject( (ObjectCreationExpr) argument);
						}
						// if the argument contains an MethodCallExpr (e.g. assertEquals(someMethod(2),...)
						else if (argument instanceof MethodCallExpr) {
							checkMethodCall( (MethodCallExpr) argument);
						}
						//if the assertTrue has a number or methodcall with numbers
						else if (argument instanceof BinaryExpr) {
							checkBinary( (BinaryExpr) argument);
						}
					}
				}
			}
		}


		private boolean checkMethodCall(MethodCallExpr argument){
			for (Expression objectArguments : argument.getArguments()) {
				if (Util.isNumber(objectArguments.toString())) {
					MethodUsage verification = new MethodUsage(currentMethod.getNameAsString(), "",
							argument.getRange().get().begin.line+"");
					if (!instances.contains(verification)){
						instances.add(verification);
						insertTestSmell(argument.getRange().get(), this.currentMethod);
						return true;
					}
				}
			}
			return false;
		}

		private boolean checkObject(ObjectCreationExpr argument){
			for (Expression objectArguments : argument.getArguments()) {
				if (Util.isNumber(objectArguments.toString())) {
					MethodUsage verification = new MethodUsage(currentMethod.getNameAsString(), "",
							argument.getRange().get().begin.line+"");
					if (!instances.contains(verification)){
						instances.add(verification);
						insertTestSmell(argument.getRange().get(), this.currentMethod);
						return true;
					}
				}
			}
			return false;
		}

		private boolean checkBinary(BinaryExpr argument) {
			if (Util.isNumber(argument.getLeft().toString()) ||
					Util.isNumber(argument.getRight().toString())) {
				MethodUsage verification = new MethodUsage(currentMethod.getNameAsString(), "",
						argument.getRange().get().begin.line+"");
				if (!instances.contains(verification)){
					instances.add(verification);
					insertTestSmell(argument.getRange().get(), this.currentMethod);
					return true;
				}
			}
			else if (argument.getRight() instanceof MethodCallExpr && checkMethodCall((MethodCallExpr) argument.getRight())) {
				return true;
			}
			else if (argument.getLeft() instanceof MethodCallExpr && checkMethodCall((MethodCallExpr) argument.getLeft())) {
				return true;
			}
			else if (argument.getRight() instanceof ObjectCreationExpr && checkObject((ObjectCreationExpr) argument.getRight())) {
				return true;
			}
			else if (argument.getLeft() instanceof ObjectCreationExpr && checkObject((ObjectCreationExpr) argument.getLeft())) {
				return true;
			}
			else if (argument.getRight() instanceof BinaryExpr && checkBinary((BinaryExpr) argument.getRight())) {
				return true;
			}
			else if (argument.getLeft() instanceof BinaryExpr && checkBinary((BinaryExpr) argument.getLeft())) {
				return true;
			}
			return false;
		}
	}

	//		insertTestSmell(n.getRange().get(), this.testMethod);

	public void insertTestSmell (Range range, MethodDeclaration testMethod) {
		cadaTestSmell = new TestSmell("Magic Number", testMethod.getNameAsString() + "()", range.begin.line, range.end.line, "Create local variable", "Refactoring option 2");
		//		cadaTestSmell = new TestSmellDescription("Ignored Test", 
		//												 "....", 
		//												 getFilePath(), 
		//												 getClassName(),
		//												 testMethod.getName() + "() \n" ,
		//												 range.begin.line + "", 
		//												 range.end.line + "", 
		//												 range.begin.line, 
		//												 range.end.line,
		//												 "",
		//												 null,
		//												 null);	
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
		line--;

		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		String space = Util.getLeadingSpaces(lines.get(line));

		List<String> methodParameters = Util.parseMethodParameters(lines.get(line));

		int randomNum = ThreadLocalRandom.current().nextInt(0, 99999999 + 1);

		String addedLine = "";

		int parameterPos = -1;
		int index = -1;

		for (String str : methodParameters) {
			index++;
			Object value = Util.parseNumber(str);
			String valueType = value.getClass().toString().split("\\.")[2];
			if(value.equals("")){
				continue;
			}

			parameterPos = index;

			addedLine = space + valueType + " magicNumber" + randomNum + " = " + value + "; \t // It is highly advisible to rename this variable to something that better encapsulate its meaning. ";
			//            System.out.println(addedLine);

		}

		if(parameterPos == -1) {
			return;
		}

		String[] methodCall = lines.get(line).trim().split("\\(");
		String[] parameters = methodCall[1].trim().split("\\,");
		parameters[parameterPos] = "magicNumber" + randomNum;

		String argument = String.join(",", parameters);
		methodCall[1] = argument;
		String newLine = space + String.join("(", methodCall);

		//        System.out.println(newLine);

		lines.set(line, newLine);
		lines.add(line, addedLine);

		String dataLine = "" + (line + 1) + "-" + (line + 2) + ",";

		//        System.out.println(dataLine);

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);

		List<String> response = Arrays.asList(result, dataLine);

		Gson gson = new Gson();
		String json = gson.toJson(response);

		System.out.println(json);

	}

	public static void refactorNoPrint(int line) {
		line--;

		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		String space = Util.getLeadingSpaces(lines.get(line));

		List<String> methodParameters = Util.parseMethodParameters(lines.get(line));

		int randomNum = ThreadLocalRandom.current().nextInt(0, 99999999 + 1);

		String addedLine = "";

		int parameterPos = -1;
		int index = -1;

		for (String str : methodParameters) {
			index++;
			Object value = Util.parseNumber(str);
			String valueType = value.getClass().toString().split("\\.")[2];
			if(value.equals("")){
				continue;
			}

			parameterPos = index;

			addedLine = space + valueType + " magicNumber" + randomNum + " = " + value + "; \t // It is highly advisible to rename this variable to something that better encapsulate its meaning. ";
			//        System.out.println(addedLine);

		}

		if(parameterPos == -1) {
			return;
		}

		String[] methodCall = lines.get(line).trim().split("\\(");
		String[] parameters = methodCall[1].trim().split("\\,");
		parameters[parameterPos] = "magicNumber" + randomNum;

		String argument = String.join(",", parameters);
		methodCall[1] = argument;
		String newLine = space + String.join("(", methodCall);

		//    System.out.println(newLine);

		lines.set(line, newLine);
		lines.add(line, addedLine);

		String dataLine = "" + (line + 1) + "-" + (line + 2) + ",";

		//    System.out.println(dataLine);

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);

		List<String> response = Arrays.asList(result, dataLine);

		Gson gson = new Gson();
		String json = gson.toJson(response);

		//	System.out.println(json);

	}
}
