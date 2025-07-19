package com.test.aadetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in
 * a test method that have no explanation. If one of the assertions fails, you
 * do not know which one it is. A. van Deursen, L. Moonen, A. Bergh, G. Kok,
 * â€œRefactoring Test Codeâ€�, Technical Report, CWI, 2001.
 */


public class UnnecessaryPrint extends AbstractSmell {

	ArrayList<TestSmell> listTestSmells;
	TestSmell cadaTestSmell;	
	private List<SmellyElement> smellyElementList;
	private ArrayList<MethodUsage> mysteryInstance;

	String className;
	String filePath;
	private ArrayList<MethodUsage> instances;
	private List<MethodUsage> methodPrints;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

	public UnnecessaryPrint(String name, String path) {
		setClassName(name);
		setFilePath(path);
		smellyElementList = new ArrayList<>();
	}
	@Override
	public String getSmellName() {
		return "Print Statement";
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
		methodPrints = new ArrayList<>();
		listTestSmells = new ArrayList<TestSmell>();
		instances = new ArrayList<>();
		UnnecessaryPrint.ClassVisitor classVisitor;
		classVisitor = new UnnecessaryPrint.ClassVisitor();
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
		int countPrint = 0;

		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			if (Util.isValidTestMethod(n)) {
				currentMethod = n;
				super.visit(n, arg);

				//reset values for next method
				currentMethod = null;
				countPrint = 0;
			}
		}
		// examine the methods being called within the test method
		@Override
		public void visit(MethodCallExpr n, Void arg) {
			super.visit(n, arg);
			if (currentMethod != null) {
				// if the name of a method being called is 'print' or 'println' or 'printf' or 'write'
				if (n.getNameAsString().equals("print") || n.getNameAsString().equals("println") || n.getNameAsString().equals("printf") || n.getNameAsString().equals("write")) {
					//check the scope of the method & proceed only if the scope is "out"
					if ((n.getScope().isPresent() &&
							n.getScope().get() instanceof FieldAccessExpr &&
							(((FieldAccessExpr) n.getScope().get())).getNameAsString().equals("out"))) {

						FieldAccessExpr f1 = (((FieldAccessExpr) n.getScope().get()));

						//check the scope of the field & proceed only if the scope is "System"
						if ((f1.getScope() != null &&
								f1.getScope() instanceof NameExpr &&
								((NameExpr) f1.getScope()).getNameAsString().equals("System"))) {
							//a print statement exists in the method body
							countPrint++;
							methodPrints.add(new MethodUsage(currentMethod.getNameAsString(), "",n.getRange().get().begin.line+""));
							insertTestSmell(n.getRange().get(), this.currentMethod);
						}
					}
				}
			}
		}

	}

	//		insertTestSmell(n.getRange().get(), this.testMethod);

	public void insertTestSmell (Range range, MethodDeclaration testMethod) {
		cadaTestSmell = new TestSmell("Unnecessary Print", testMethod.getNameAsString() + "()", range.begin.line, range.end.line, "Remove line", "Refactoring option 2");
		//		cadaTestSmell = new TestSmellDescription("Ignored Test", 
		//				 "....", 
		//				 getFilePath(), 
		//				 getClassName(),
		//				 testMethod.getName() + "() \n" ,
		//				 range.begin.line + "", 
		//				 range.end.line + "", 
		//				 range.begin.line, 
		//				 range.end.line,
		//				 "",
		//				 null,
		//				 null);	
		listTestSmells.add(cadaTestSmell);

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

		String leadingSpace = Util.getLeadingSpaces(lines.get(line));
		String originalCode = lines.get(line).trim();

		lines.set(line, leadingSpace + "// Line removed due to Unnecessary Print: " + originalCode);

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

		String leadingSpace = Util.getLeadingSpaces(lines.get(line));
		String originalCode = lines.get(line).trim();

		lines.set(line, leadingSpace + "// Line removed due to Unnecessary Print: " + originalCode);

		String result = Util.listToCode(lines);

		Util.writeStringToFile(result);

//		System.out.println(result);

	}

}
