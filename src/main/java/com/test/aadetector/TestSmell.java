package com.test.aadetector;

public class TestSmell {
	public String type;
	public String method;
	public String lineBegin;
	public String lineEnd;
	public String refactor1;
	public String refactor2;

	public TestSmell(String type, String method, String lineBegin, String lineEnd) {
		super();
		this.type = type;
		this.method = method;
		this.lineBegin = lineBegin;
		this.lineEnd = lineEnd;
		this.refactor1 = "Refactoring Strategy 1";
		this.refactor2 = "Refactoring Strategy 2";
	}

	public TestSmell(String type, String method, String lineBegin, String lineEnd, String refactor1, String refactor2) {
		super();
		this.type = type;
		this.method = method;
		this.lineBegin = lineBegin;
		this.lineEnd = lineEnd;
		this.refactor1 = refactor1;
		this.refactor2 = refactor2;
	}

	public TestSmell(String type, String method, int lineBegin, int lineEnd, String refactor1, String refactor2) {
		super();
		this.type = type;
		this.method = method;
		this.lineBegin = Integer.toString(lineBegin);
		this.lineEnd = Integer.toString(lineEnd);
		this.refactor1 = refactor1;
		this.refactor2 = refactor2;
	}

	// Add constructors, getters, and setters
	// ...
}