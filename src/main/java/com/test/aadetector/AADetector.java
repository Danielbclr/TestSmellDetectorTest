package com.test.aadetector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.test.aadetector.JsonGenerationExample.JsonStructure;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class AADetector {

	private static final String IF_REGEX = "\\bif\\s*\\(.*\\)\\s*\\{?";

	public static void test() throws FileNotFoundException {

		String nameClass = "test1";
		String filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRoulette assertionRoulette = new AssertionRoulette(nameClass, filePath);

		FileInputStream testFileInputStream, productionFileInputStream;
		testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  

		List<TestSmell> assertionRouletteList = assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);

		assertionRouletteList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				//				System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring + " " + smell.beginMethod);
			}
		});
		//		System.out.println(nameClass + " " + assertionRoulette.getHasSmell());

		nameClass = "test2";
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		assertionRoulette = new AssertionRoulette(nameClass, filePath);

		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  

		assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);

		//		System.out.println(nameClass + " " + assertionRoulette.getHasSmell());

		//
		// Conditional test logic - test 1
		//
		//////////////////////////////////////////////////////////////

		//		nameClass = "test2";
		//		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		//		ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);
		//
		//		testFileInputStream = new FileInputStream(filePath);	
		//
		//		javaParser = new JavaParser();
		//
		//		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();
		//
		//		List<TestSmellDescription> test3List = conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
		//		System.out.println(nameClass + " Conditional test logic smell: " + conditionalTestLogic.getHasSmell());
		//		System.out.println(nameClass + " Conditional test logic smell: " + test3List.size());

		//
		// Test the output file
		//
		//////////////////////////////////////////////////////////////

		nameClass = "output";
		filePath = System.getProperty("user.dir") + "\\output.java";
		assertionRoulette = new AssertionRoulette(nameClass, filePath);

		//		System.out.println(filePath);

		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  

		assertionRouletteList = assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
		//		System.out.println(nameClass + ": " + assertionRoulette.getHasSmell());
		//		System.out.println(nameClass + ": " + assertionRoulette.contSmell());
		//		
		assertionRoulette.listTestSmells.add(null);

		assertionRouletteList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				//				System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring + " " + smell.beginMethod);
			}
		});

		//		conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);

		//		List<TestSmellDescription> conditionalTestList = conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
		//		conditionalTestList.forEach((smell) -> {
		//			if(smell != null) {
		//				System.out.println( smell.testSmellType + " test smell detected in method " + smell.methodName + " in line " + smell.linePositionBegin);
		//				//				System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring + " " + smell.beginMethod);
		//			}
		//		});

		//
		//	TEST REFACTOR
		//
		///////////////////////////////////////////////////////////////////////
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRouletteRefactor assertionRouletteRefactor = new AssertionRouletteRefactor(filePath);
		assertionRouletteRefactor.run();

		//
		// TEST CONSTRUCTOR INITIALIZATION
		//
		//////////////////////////////////
		nameClass = "testFile4";
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile4.java";
		filePath = System.getProperty("user.dir") + "\\output.java";
		ConstructorInitialization constructorInitialization = new ConstructorInitialization(nameClass, filePath);
		List<String> code = codeToList(filePath);
		assertionRouletteList = constructorInitialization.runAnalysis(code);
		System.out.println(constructorInitialization.getHasSmell());
		assertionRouletteList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				ConstructorInitializationRefactor.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST EXCEPTION HANDLING
		//
		///////////////////////////////////

		List<TestSmell> listExceptionHandling = ExceptionHandling.detect();
		listExceptionHandling.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				ExceptionHandling.refactor(Integer.parseInt(smell.lineBegin) - 1);
				
			}
		});
		
		ExceptionHandling.refactor(Integer.parseInt(listExceptionHandling.get(0).lineBegin) - 1);	
		
		//
		// TEST IGNORED TEST
		//
		////////////////////////////////////
		
		IgnoredTest ignoredTest = new IgnoredTest(nameClass, filePath);
		code = codeToList(filePath);
		
		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		
		List<TestSmell> ignoredTestList= ignoredTest.runAnalysis(testFileCompilationUnit, nameClass);
		System.out.println(ignoredTest.getHasSmell());
		ignoredTestList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				IgnoredTest.refactor(Integer.parseInt(smell.lineBegin));
			}
		});
		
		//
		// TEST EMPTY TEST
		//
		///////////////////////////////////
		EmptyTest emptyTest = new EmptyTest(nameClass, filePath);
		
		List<TestSmell> emptyTestList= emptyTest.runAnalysis(testFileCompilationUnit, nameClass);
		emptyTestList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				EmptyTest.refactor(Integer.parseInt(smell.lineBegin));
			}
		});
		
	}

	public static void main(String[] args) throws FileNotFoundException {

		String resultJson = "";

		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";

		// Check if the required arguments are provided
		if (args.length < 2) {
			System.out.println("Usage: java JsonGenerationExample <testsmell> <refactoring>");
			test();

			return;
		}

		if(args[0].equals("AssertionRoulette") && !args[1].equals("0")) {
			int line =  Integer.parseInt(args[1]);
			
			String result = AssertionRouletteRefactor.addLazyAssertionMessage(line);
			System.out.print(result);
			writeStringToFile(result);
		}

		if(args[0].equals("ConstructorInitialization") && !args[1].equals("0")) {
			int line =  Integer.parseInt(args[1]);
			ConstructorInitializationRefactor.refactor(line);
		}
		
		if(args[0].equals("ExceptionHandling") && !args[1].equals("0")) {
			int line =  Integer.parseInt(args[1]) - 1;
			ExceptionHandling.refactor(line);
		}
		
		if(args[0].equals("IgnoredTest") && !args[1].equals("0")) {
			int line =  Integer.parseInt(args[1]);
			IgnoredTest.refactor(line);
		}
		
		if(args[0].equals("EmptyTest") && !args[1].equals("0")) {
			int line =  Integer.parseInt(args[1]);
			EmptyTest.refactor(line);
		}

		if(args[0].equals("Detection")) {
			List<TestSmell> testSmellList = new ArrayList<>();
			testSmellList.addAll(detectAssertionRoulette());		  		
			testSmellList.addAll(detectConstructorInitialization());
			testSmellList.addAll(ExceptionHandling.detect());
			testSmellList.addAll(detectIgnoredTest());
			testSmellList.addAll(detectEmptyTest());

			resultJson += toJson(testSmellList);

			System.out.println(resultJson);
		}

	}

	private static String toJson(List<TestSmell> testSmellList) {
		Gson gson = new Gson();
		String json = gson.toJson(testSmellList);
		return json;
	}

	public static List<TestSmell> detectAssertionRoulette() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";
		AssertionRoulette assertionRoulette = new AssertionRoulette(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  

		return assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
	}

	public static List<TestSmellDescription> detectConditionalTestLogic() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";
		ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  

		return conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
	}

	public static List<TestSmell> detectConstructorInitialization() throws FileNotFoundException{
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";
		ConstructorInitialization constructorInitialization = new ConstructorInitialization(nameClass, filePath);
		List<String> code = codeToList(filePath);
		return constructorInitialization.runAnalysis(code);

	}
	
	public static List<TestSmell> detectIgnoredTest() throws FileNotFoundException{
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";
		IgnoredTest ignoredTest = new IgnoredTest(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		
		return ignoredTest.runAnalysis(testFileCompilationUnit, nameClass);

	}
	
	public static List<TestSmell> detectEmptyTest() throws FileNotFoundException{
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + "\\output.java";
		EmptyTest emptyTest = new EmptyTest(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		
		return emptyTest.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<String> codeToList(String filePath) {
		List<String> lines = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

	public static String listToCode(List<String> code) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : code) {
			stringBuilder.append(line).append(System.lineSeparator());
		}

		String result = stringBuilder.toString().trim();
		return result;
	}

	public static void writeStringToFile(String content) {
		String filePath = System.getProperty("user.dir") + "\\output.java";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception as needed (e.g., log it, throw it, etc.)
		}
	}

}
