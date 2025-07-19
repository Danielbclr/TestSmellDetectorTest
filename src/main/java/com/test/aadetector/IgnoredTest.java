package com.test.aadetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.Gson;

public class IgnoredTest extends AbstractSmell {

	List<TestSmell> listTestSmells;
	TestSmell cadaTestSmell;	
	private List<SmellyElement> smellyElementList;
	private List<MethodUsage> methodConditional;

	String className;
	String filePath;

	private boolean flag = false;
	private ArrayList<MethodUsage> instanceIgnored;

	@Override
	public String getSmellName() {
		return "Ignored Test";
	}


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

	public IgnoredTest(String name, String path) {
		setClassName(name);
		setFilePath(path);
		smellyElementList = new ArrayList<>();
		methodConditional = new ArrayList<>();
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

		IgnoredTest.ClassVisitor classVisitor;
		classVisitor = new IgnoredTest.ClassVisitor();
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

		@Override
		public void visit(MethodDeclaration n, Void arg) {

			//JUnit 4
			//check if test method has Ignore annotation
			if (n.getAnnotationByName("Test").isPresent()) {
				if (n.getAnnotationByName("Ignore").isPresent() || flag) {
					//                    instanceIgnored.add(new MethodUsage(n.getNameAsString(), "",n.getRange().get().begin.line + "-" + n.getRange().get().end.line));
					insertTestSmell(n.getRange().get(), n);
					return;
				}
			}

			//JUnit 3
			//check if test method is not public
			if (n.getNameAsString().toLowerCase().startsWith("test")) {
				if (!n.getModifiers().contains(Modifier.publicModifier())) {
					//                    instanceIgnored.add(new MethodUsage(n.getNameAsString(), "",n.getRange().get().begin.line + "-" + n.getRange().get().end.line));
					insertTestSmell(n.getRange().get(), n);
					return;
				}
			}
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			if (n.getAnnotationByName("Ignore").isPresent()) {
				testClass = new TestClass(n.getNameAsString());
				flag = true;
			}
			super.visit(n, arg);
		}
	}
	public void insertTestSmell (Range range, MethodDeclaration testMethod) {
		cadaTestSmell = new TestSmell("Ignored Test",
				testMethod.getName() + "()" ,
				range.begin.line + 1, 
				range.end.line,
				"Remove Method",
				"Refactor option 2");
	
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

        // Read the test file
        List<String> lines = Util.readFile(filePath);

        int lineEnd = Util.findClosingBracket(lines, line);
        
        for(int i = lineEnd; i >= line; i--) {
        	lines.remove(i);
        }
        
        String space = Util.getLeadingSpaces(lines.get(line - 2));
        
        lines.remove(line);
        lines.remove(line - 1);
        lines.set(line - 2, space + "// Line removed due to Ignored Test");

        String result = Util.listToCode(lines);
        
        Util.writeStringToFile(result);
        
        List<String> response = Arrays.asList(result, Integer.toString(line - 1));
		
		Gson gson = new Gson();
		String json = gson.toJson(response);
		
		System.out.println(json);
        
	}
	
public static void refactorNoPrint(int line) {
		
        // Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

        // Read the test file
        List<String> lines = Util.readFile(filePath);

        int lineEnd = Util.findClosingBracket(lines, line);
        
        for(int i = lineEnd; i >= line; i--) {
        	lines.remove(i);
        }
        
        String space = Util.getLeadingSpaces(lines.get(line - 2));
        
        lines.remove(line);
        lines.remove(line - 1);
        lines.set(line - 2, space + "// Line removed due to Ignored Test");

        String result = Util.listToCode(lines);
        
        Util.writeStringToFile(result);
        
//        List<String> response = Arrays.asList(result, Integer.toString(line - 1));
//		
//		Gson gson = new Gson();
//		String json = gson.toJson(response);
//		
//		System.out.println(json);
        
	}
}
