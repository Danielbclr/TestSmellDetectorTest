package com.test.aadetector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.Gson;
import com.test.aadetector.ExceptionHandling.TryCatchInfo;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in
 * a test method that have no explanation. If one of the assertions fails, you
 * do not know which one it is. A. van Deursen, L. Moonen, A. Bergh, G. Kok,
 * â€œRefactoring Test Codeâ€�, Technical Report, CWI, 2001.
 */

public class EmptyTest extends AbstractSmell {

	private ArrayList<MethodUsage> instanceEmpty;

	private boolean flag = false;

	List<TestSmell> listTestSmells;
	TestSmell cadaTestSmell;
	private List<SmellyElement> smellyElementList;

	String className;
	String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public EmptyTest(String name, String path) {
		setClassName(name);
		setFilePath(path);
		smellyElementList = new ArrayList<>();
	}

	@Override
	public String getSmellName() {
		return "Empty Test";
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
		return smellyElementList.stream().filter(x -> x.getHasSmell()).count();
	}

	/**
	 * Analyze the test file for test methods for multiple assert statements without
	 * an explanation/message
	 * 
	 * @return
	 */
	@Override
	public List<TestSmell> runAnalysis(CompilationUnit testFileCompilationUnit, String testFileName)
			throws FileNotFoundException {

		listTestSmells = new ArrayList<TestSmell>();

		EmptyTest.ClassVisitor classVisitor;
		classVisitor = new EmptyTest.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);

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
		TestClass testClass;
		TestMethod testMethod;

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			testMethod = new TestMethod(n.getNameAsString());
			if (Util.isValidTestMethod(n)) {
				// method should not be abstract
				if (!n.isAbstract()) {
					if (n.getBody().isPresent()) {
						// get the total number of statements contained in the method
						if (n.getBody().get().getStatements().size() == 0) {
							// instanceEmpty.add(new
							// MethodUsage(n.getNameAsString(),"",n.getRange().get().begin.line + "-" +
							// n.getRange().get().end.line));
							insertTestSmell(n);
							return;
						}
					}
				}
			}
		}

	}

	public void insertTestSmell(MethodDeclaration methodDeclaration) {
		cadaTestSmell = new TestSmell("Empty Test", methodDeclaration.getName() + "()",
				methodDeclaration.getRange().get().begin.line + 1, methodDeclaration.getRange().get().end.line,
				"Remove Method", "Refactoring option 2");

		listTestSmells.add(cadaTestSmell);

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

	public static void refactor(int beginLine) {
		// Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + "\\output.java";

		// Read the test file
		List<String> lines = Util.readFile(filePath);

		int endLine = Util.findClosingBracket(lines, beginLine);
		int commentLine;
		
		beginLine--;
		String leadingSpace = Util.getLeadingSpaces(lines.get(beginLine));
		if (!lines.get(beginLine - 1).trim().startsWith("//") && !lines.get(beginLine - 1).trim().isEmpty()) {
			lines.set(beginLine - 1, leadingSpace +  "// Method removed due to Empty Test");
			commentLine = beginLine - 1;
		}
		else {
			lines.set(beginLine, leadingSpace + "// Method removed due to Empty Test");
			commentLine = beginLine;
			beginLine++;
		}

		for (int i = endLine; i >= beginLine; i--) {
			lines.remove(i);
		}

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);
		
		List<String> response = Arrays.asList(result, Integer.toString(commentLine));
		
		Gson gson = new Gson();
		String json = gson.toJson(response);
		
		System.out.println(json);
	}

}
