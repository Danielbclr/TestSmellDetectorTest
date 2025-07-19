package com.test.aadetector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.javaparser.ast.CompilationUnit;

public class ConstructorInitialization extends AbstractSmell {

	ArrayList<TestSmell> listTestSmells;
	TestSmellDescription cadaTestSmell;	
	private List<SmellyElement> smellyElementList;
	private List<MethodUsage> methodConditional;

	String className;
	String filePath;

	private boolean flag = false;
	private ArrayList<MethodUsage> instanceIgnored;

	@Override
	public String getSmellName() {
		return "Constructor Initialization";
	}


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

	public ConstructorInitialization(String name, String path) {
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
	public List<TestSmell> runAnalysis(List<String> textContent) throws FileNotFoundException {

		listTestSmells = new ArrayList<>();

		int constructorPosition = detectConstructorPosition(textContent, detectClassName(textContent));	
		if(constructorPosition != -1) {
			if(!hasCodeInConstructor(textContent, constructorPosition) || hasSetupMethod(textContent)) {
				return listTestSmells;
			}
			int endPosition = Util.findClosingBracket(textContent, constructorPosition);
//			System.out.println(endPosition);
			listTestSmells.add(new TestSmell("Constructor Initialization",
					detectClassName(textContent) + "() \n" ,
					constructorPosition + 1, 
					endPosition + 1,
					"Refactor to setUp()",
					"Refactor option 2"));
		}

		return listTestSmells;
	}

	public static String detectClassName(List<String> lines) {
		String class_name = null;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).trim().startsWith("public class ")) {
				class_name = lines.get(i).split(" ")[2];
				break;
			}
		}
		return class_name;
	}
	
	private static boolean hasCodeInConstructor(List<String> code, int position) {
		int endPosition = Util.getStatementEndIndex(code, position);
		for(int i = position + 1; i < endPosition; i ++) {
			String line = code.get(i).trim();
			if(!line.startsWith("super") && !line.equals("")) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasSetupMethod(List<String> code) {
		for(int i = 0; i < code.size(); i ++) {
			String line = code.get(i).trim().toLowerCase();
			if(line.contains("setup") && line.startsWith("public")){
				return true;
			}
		}
		return false;
	}

	public static int detectConstructorPosition(List<String> lines, String class_name) {
		int constructor_position = -1;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).trim().startsWith("public " + class_name + "(") ) {
				constructor_position = i;
				break;
			}
		}
		return constructor_position;
	}


	/**
	 * Returns the set of analyzed elements (i.e. test methods)
	 */
	@Override
	public List<SmellyElement> getSmellyElements() {
		return smellyElementList;
	}

	@Override
	public List<TestSmellDescription> runAnalysis(CompilationUnit testFileCompilationUnit,
			CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName)
					throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int contSmell() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setClassName(String string) {
		// TODO Auto-generated method stub

	}


	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}
}
