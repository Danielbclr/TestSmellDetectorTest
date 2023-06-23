
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class AADetector {

	public static void main(String[] args) throws FileNotFoundException {
		String nameClass = "test1";
		String filePath = System.getProperty("user.dir") + "\\testFiles\\testFile1.java";
		AssertionRoulette assertionRoulette = new AssertionRoulette(nameClass, filePath);
		
		System.out.println(filePath);
		
		FileInputStream testFileInputStream, productionFileInputStream;
		testFileInputStream = new FileInputStream(filePath);
		
		JavaParser javaParser = new JavaParser();
		
		CompilationUnit testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		 
		assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
		System.out.println(nameClass + assertionRoulette.getHasSmell());
		
		nameClass = "test2";
		filePath = System.getProperty("user.dir") + "\\testFiles\\testFile2.java";
		assertionRoulette = new AssertionRoulette(nameClass, filePath);
		
		System.out.println(filePath);
		
		testFileInputStream = new FileInputStream(filePath);
		
		javaParser = new JavaParser();
		
		testFileCompilationUnit = javaParser.parse(testFileInputStream).getResult().get();  
		 
		assertionRoulette.runAnalysis(testFileCompilationUnit, nameClass);
		System.out.println(nameClass + assertionRoulette.getHasSmell());
		
		AADetector object = new AADetector();
		String packageName = object.getClass().getPackage().getName();
        System.out.println("Package Name = " + packageName + " " + object.getClass().getName());
	}

}
