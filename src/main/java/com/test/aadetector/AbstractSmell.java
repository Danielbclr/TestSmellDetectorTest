package com.test.aadetector;

import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public abstract class AbstractSmell {
	
    public abstract String getSmellName();

    public abstract boolean getHasSmell();
    
    public abstract int contSmell();
    
    public abstract long getCountSmell(String string);

    public abstract List<TestSmellDescription> runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException;

    public abstract List<SmellyElement> getSmellyElements();
    
    public abstract void setClassName(String string);
    
    public abstract String getClassName();

	/**
	 * Analyze the test file for test methods for multiple assert statements without
	 * an explanation/message
	 * @return 
	 */
	public List<TestSmell> runAnalysis(CompilationUnit testFileCompilationUnit, String testFileName) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
