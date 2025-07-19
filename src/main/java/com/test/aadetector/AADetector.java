package com.test.aadetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.gson.Gson;

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
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				// System.out.println("DEBUG: " + smell.field + " " + smell.testSmellRefactoring
				// + " " + smell.beginMethod);
			}
		});
		// System.out.println(nameClass + " " + assertionRoulette.getHasSmell());

		nameClass = "test2";
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		assertionRoulette = new AssertionRoulette(nameClass, filePath);

		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);

		// System.out.println(nameClass + " " + assertionRoulette.getHasSmell());

		//
		// Conditional test logic - test 1
		//
		//////////////////////////////////////////////////////////////

		// nameClass = "test2";
		// filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		// ConditionalTestLogic conditionalTestLogic = new
		// ConditionalTestLogic(nameClass, filePath);
		//
		// testFileInputStream = new FileInputStream(filePath);
		//
		// javaParser = new JavaParser();
		//
		// testFileCompilationUnit =
		// javaParser.parse(testFileInputStream).getResult().get();
		//
		// List<TestSmellDescription> test3List =
		// conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
		// System.out.println(nameClass + " Conditional test logic smell: " +
		// conditionalTestLogic.getHasSmell());
		// System.out.println(nameClass + " Conditional test logic smell: " +
		// test3List.size());

		//
		// Test the output file
		//
		//////////////////////////////////////////////////////////////

		nameClass = "output";
		filePath = System.getProperty("user.dir") + File.separator + "output.java";
		assertionRoulette = new AssertionRoulette(nameClass, filePath);

		// System.out.println(filePath);

		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		assertionRouletteList = assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
		// System.out.println(nameClass + ": " + assertionRoulette.getHasSmell());
		// System.out.println(nameClass + ": " + assertionRoulette.contSmell());
		//
		assertionRoulette.listTestSmells.add(null);

		assertionRouletteList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				String result = AssertionRouletteRefactor
						.addLazyAssertionMessage(Integer.parseInt(smell.lineBegin) - 1);
//				System.out.print(result);
				writeStringToFile(result);
			}
		});

		// conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);

		// List<TestSmellDescription> conditionalTestList =
		// conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
		// conditionalTestList.forEach((smell) -> {
		// if(smell != null) {
		// System.out.println( smell.testSmellType + " test smell detected in method " +
		// smell.methodName + " in line " + smell.linePositionBegin);
		// // System.out.println("DEBUG: " + smell.field + " " +
		// smell.testSmellRefactoring + " " + smell.beginMethod);
		// }
		// });

		//
		// TEST REFACTOR
		//
		///////////////////////////////////////////////////////////////////////
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRouletteRefactor assertionRouletteRefactor = new AssertionRouletteRefactor(filePath);
		assertionRouletteRefactor.run();

		//
		// TEST CONSTRUCTOR INITIALIZATION
		//
		//////////////////////////////////
		filePath = System.getProperty("user.dir") + File.separator + "output.java";
		ConstructorInitialization constructorInitialization = new ConstructorInitialization(nameClass, filePath);
		List<String> code = codeToList(filePath);
		assertionRouletteList = constructorInitialization.runAnalysis(code);
		System.out.println(constructorInitialization.getHasSmell());
		assertionRouletteList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				ConstructorInitializationRefactor.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST EXCEPTION HANDLING
		//
		///////////////////////////////////

		List<TestSmell> listExceptionHandling = ExceptionHandling.detect();
		listExceptionHandling.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				ExceptionHandling.refactor(Integer.parseInt(smell.lineBegin) - 1);

			}
		});

//		ExceptionHandling.refactor(Integer.parseInt(listExceptionHandling.get(0).lineBegin) - 1);	

		//
		// TEST IGNORED TEST
		//
		////////////////////////////////////

		IgnoredTest ignoredTest = new IgnoredTest(nameClass, filePath);
		code = codeToList(filePath);

		testFileInputStream = new FileInputStream(filePath);

		javaParser = new JavaParser();

		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		List<TestSmell> ignoredTestList = ignoredTest.runAnalysis(testFileCompilationUnit, nameClass);
		System.out.println(ignoredTest.getHasSmell());
		ignoredTestList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				IgnoredTest.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST EMPTY TEST
		//
		///////////////////////////////////
		EmptyTest emptyTest = new EmptyTest(nameClass, filePath);

		List<TestSmell> emptyTestList = emptyTest.runAnalysis(testFileCompilationUnit, nameClass);
		emptyTestList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				EmptyTest.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST DUPLICATE ASSERT
		//
		//////////////////////////////////

		DuplicateAssert duplicateAssert = new DuplicateAssert(nameClass, filePath);

		List<TestSmell> duplicateAssertList = duplicateAssert.runAnalysis(testFileCompilationUnit, nameClass);
		duplicateAssertList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				DuplicateAssert.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST UNNECESSARY PRINT
		//
		///////////////////////////////

		UnnecessaryPrint unnecessaryPrint = new UnnecessaryPrint(nameClass, filePath);

		List<TestSmell> unnecessaryPrintList = unnecessaryPrint.runAnalysis(testFileCompilationUnit, nameClass);
		unnecessaryPrintList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				UnnecessaryPrint.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST UNNECESSARY PRINT
		//
		///////////////////////////////

		RedundantAssertion redundantAssertion = new RedundantAssertion(nameClass, filePath);

		List<TestSmell> redundantAssertionList = redundantAssertion.runAnalysis(testFileCompilationUnit, nameClass);
		redundantAssertionList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				RedundantAssertion.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST MYSTERY GUEST
		//
		///////////////////////////////

		MysteryGuest mysteryGuest = new MysteryGuest(nameClass, filePath);

		List<TestSmell> mysteryGuestList = mysteryGuest.runAnalysis(testFileCompilationUnit, nameClass);
		mysteryGuestList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				MysteryGuest.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// TEST MAGIC NUMBER
		//
		///////////////////////////////

		MagicNumber magicNumber = new MagicNumber(nameClass, filePath);

		List<TestSmell> magicNumberList = magicNumber.runAnalysis(testFileCompilationUnit, nameClass);
		magicNumberList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
//				MagicNumber.refactor(Integer.parseInt(smell.lineBegin));
			}
		});

		//
		// GENERAL FIXTURE
		//
		///////////////////////////////

		GeneralFixture generalFixture = new GeneralFixture(nameClass, filePath);

		List<TestSmell> generalFixtureList = generalFixture.runAnalysis(testFileCompilationUnit, nameClass);
		generalFixtureList.forEach((smell) -> {
			if (smell != null) {
				System.out.println(
						smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				GeneralFixture.refactor(Integer.parseInt(smell.lineBegin));
				return;
			}
			return;
		});

	}

	private static String dataLine = "";
	private static int offset;
	private static boolean refactored = false;

	public static void main(String[] args) throws IOException {

//		System.out.println(args[0] + " " + args[1]);

		String resultJson = "";

		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

		// Check if the required arguments are provided
		if (args.length != 2) {
			System.out.println("Usage: java JsonGenerationExample <testsmell> <refactoring>");
//			test();
			refactorAll();

			return;
		}

		if (args[0].equals("AssertionRoulette") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);

			String result = AssertionRouletteRefactor.addLazyAssertionMessage(line);
			System.out.print(result);
			writeStringToFile(result);
		}

		if (args[0].equals("ConstructorInitialization") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			ConstructorInitializationRefactor.refactor(line);
		}

		if (args[0].equals("ExceptionHandling") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]) - 1;
			ExceptionHandling.refactor(line);
		}

		if (args[0].equals("IgnoredTest") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			IgnoredTest.refactor(line);
		}

		if (args[0].equals("EmptyTest") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			EmptyTest.refactor(line);
		}

		if (args[0].equals("DuplicateAssert") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			DuplicateAssert.refactor(line);
		}

		if (args[0].equals("UnnecessaryPrint") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			UnnecessaryPrint.refactor(line);
		}

		if (args[0].equals("RedundantAssertion") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			RedundantAssertion.refactor(line);
		}

		if (args[0].equals("MysteryGuest") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			MysteryGuest.refactor(line);
		}

		if (args[0].equals("MagicNumber") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			MagicNumber.refactor(line);
		}

		if (args[0].equals("GeneralFixture") && !args[1].equals("0")) {
			int line = Integer.parseInt(args[1]);
			GeneralFixture.refactor(line);
		}

		if (args[0].equals("Detection")) {
			List<TestSmell> testSmellList = new ArrayList<>();
			testSmellList.addAll(detectAssertionRoulette());
			testSmellList.addAll(detectConstructorInitialization());
			testSmellList.addAll(ExceptionHandling.detect());
			testSmellList.addAll(detectIgnoredTest());
			testSmellList.addAll(detectEmptyTest());
			testSmellList.addAll(detectDuplicateAssert());
			testSmellList.addAll(detectUnnecessaryPrint());
			testSmellList.addAll(detectRedundantAssertion());
			testSmellList.addAll(detectMysteryGuest());
			testSmellList.addAll(detectMagicNumber());
			testSmellList.addAll(detectGeneralFixture());

			resultJson += toJson(testSmellList);

			System.out.println(resultJson);
		}

		if (args[0].equals("RefactorAll")) {

			refactorAll();

		}

	}

	private static void refactorAll() throws IOException {
		refactored = false;
		List<TestSmell> testSmellList = new ArrayList<>();

		// ASSERTION ROULETTE
		testSmellList.addAll(detectAssertionRoulette());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				List<String> lines = AssertionRouletteRefactor.refactor(Integer.parseInt(smell.lineBegin) - 1);
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
				Util.writeStringToFile(Util.listToCode(lines));
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// CONSTRUCTOR INITIALIZATION
		testSmellList.addAll(detectConstructorInitialization());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				ConstructorInitializationRefactor.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// EXCEPTION HANDLING
		testSmellList.addAll(ExceptionHandling.detect());
		offset = 0;
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				ExceptionHandling.refactorNoPrint(Integer.parseInt(smell.lineBegin) - 1 + offset);
				if (Integer.parseInt(smell.lineBegin) - Integer.parseInt(smell.lineEnd) == -4) {
					dataLine += (Integer.parseInt(smell.lineBegin) + offset) + "-"
							+ (Integer.parseInt(smell.lineBegin) + 2 + offset) + ",";
					offset += Integer.parseInt(smell.lineBegin) - Integer.parseInt(smell.lineEnd) + 1;
				} else {
					dataLine += (Integer.parseInt(smell.lineBegin) + offset) + "-"
							+ (Integer.parseInt(smell.lineBegin) + 2 + offset) + ",";
					offset += Integer.parseInt(smell.lineBegin) - Integer.parseInt(smell.lineEnd) + 6;

				}

			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// IGNORED TEST
		testSmellList.addAll(detectIgnoredTest());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				IgnoredTest.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin) - 1) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// EMPTY TEST
		testSmellList.addAll(detectEmptyTest());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				EmptyTest.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin) - 1) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// DUPLICATE ASSERT
		testSmellList.addAll(detectDuplicateAssert());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				DuplicateAssert.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// UNNECESSARY PRINT
		testSmellList.addAll(detectUnnecessaryPrint());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				UnnecessaryPrint.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// REDUNDANT ASSERTION
		testSmellList.addAll(detectRedundantAssertion());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				RedundantAssertion.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// MYSTERY GUEST
		testSmellList.addAll(detectMysteryGuest());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
				refactored = true;
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				MysteryGuest.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// MAGIC NUMBER
		testSmellList.addAll(detectMagicNumber());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
//				System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				MagicNumber.refactorNoPrint(Integer.parseInt(smell.lineBegin));
				dataLine += (Integer.parseInt(smell.lineBegin) - 1) + "-" + (Integer.parseInt(smell.lineBegin)) + ",";
			}
		});
		if (!testSmellList.isEmpty()) {
//			System.out.println("NOT OK");
		}
		testSmellList.clear();

		// GENERAL FIXTURE
		testSmellList.addAll(detectGeneralFixture());
		testSmellList.forEach((smell) -> {
			if (smell != null) {
//						System.out.println( smell.type + " test smell detected in method " + smell.method + " in line " + smell.lineBegin);
				dataLine += GeneralFixture.refactorNoPrint(Integer.parseInt(smell.lineBegin));
			}
		});
		if (!testSmellList.isEmpty()) {
//					System.out.println("NOT OK");
		}
		testSmellList.clear();

		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		String file = Util.readFileAsString(filePath);
		List<String> response = Arrays.asList(file, dataLine);

		Gson gson = new Gson();
		String json = gson.toJson(response);

		System.out.println(json);

	}

	private static String toJson(List<TestSmell> testSmellList) {
		Gson gson = new Gson();
		String json = gson.toJson(testSmellList);
		return json;
	}

	public static List<TestSmell> detectAssertionRoulette() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		AssertionRoulette assertionRoulette = new AssertionRoulette(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
	}

	public static List<TestSmellDescription> detectConditionalTestLogic() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return conditionalTestLogic.runAnalysis(testFileCompilationUnit, nameClass);
	}

	public static List<TestSmell> detectConstructorInitialization() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		ConstructorInitialization constructorInitialization = new ConstructorInitialization(nameClass, filePath);
		List<String> code = codeToList(filePath);
		return constructorInitialization.runAnalysis(code);

	}

	public static List<TestSmell> detectIgnoredTest() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		IgnoredTest ignoredTest = new IgnoredTest(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return ignoredTest.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectEmptyTest() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		EmptyTest emptyTest = new EmptyTest(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return emptyTest.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectDuplicateAssert() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		DuplicateAssert duplicateAssert = new DuplicateAssert(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return duplicateAssert.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectUnnecessaryPrint() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		UnnecessaryPrint unnecessaryPrint = new UnnecessaryPrint(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return unnecessaryPrint.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectRedundantAssertion() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		RedundantAssertion redundantAssertion = new RedundantAssertion(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return redundantAssertion.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectMysteryGuest() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		MysteryGuest mysteryGuest = new MysteryGuest(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return mysteryGuest.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectMagicNumber() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		MagicNumber magicNumber = new MagicNumber(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return magicNumber.runAnalysis(testFileCompilationUnit, nameClass);

	}

	public static List<TestSmell> detectGeneralFixture() throws FileNotFoundException {
		String nameClass = "output";
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		GeneralFixture generalFixture = new GeneralFixture(nameClass, filePath);

		FileInputStream testFileInputStream = new FileInputStream(filePath);

		JavaParser javaParser = new JavaParser();

		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();

		return generalFixture.runAnalysis(testFileCompilationUnit, nameClass);

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
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception as needed (e.g., log it, throw it, etc.)
		}
	}

}
