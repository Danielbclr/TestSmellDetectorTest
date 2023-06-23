package com.test.aadetector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class AADetector {

	public static void main(String[] args) throws FileNotFoundException {
		String nameClass = "test1";
		String filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRoulette assertionRoulette = new AssertionRoulette(nameClass, filePath);
		
		FileInputStream testFileInputStream, productionFileInputStream;
		testFileInputStream = new FileInputStream(filePath);
		
		JavaParser javaParser = new JavaParser();
		
		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		 
		assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
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
		
		nameClass = "test2";
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);
		
		testFileInputStream = new FileInputStream(filePath);	
		
		javaParser = new JavaParser();
		
		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();
		
		List<TestSmellDescription> test3List = conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
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
		 
		List<TestSmellDescription> assertionRouletteList = assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
//		System.out.println(nameClass + ": " + assertionRoulette.getHasSmell());
//		System.out.println(nameClass + ": " + assertionRoulette.contSmell());
//		
		assertionRoulette.listTestSmells.add(null);
		
		assertionRouletteList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.testSmellType + " test smell detected in method " + smell.methodName + " in line " + smell.linePositionBegin);
//				System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring + " " + smell.beginMethod);
			}
		});
		
		conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);
		
		List<TestSmellDescription> conditionalTestList = conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
		conditionalTestList.forEach((smell) -> {
			if(smell != null) {
				System.out.println( smell.testSmellType + " test smell detected in method " + smell.methodName + " in line " + smell.linePositionBegin);
//				System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring + " " + smell.beginMethod);
			}
		});
		
		//
		//	TEST REFACTOR
		//
		///////////////////////////////////////////////////////////////////////
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRouletteRefactor assertionRouletteRefactor = new AssertionRouletteRefactor(filePath);
		assertionRouletteRefactor.run();
		
	}

}
