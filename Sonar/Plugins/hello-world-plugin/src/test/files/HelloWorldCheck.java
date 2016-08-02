class MyClass {
	
	MyClass(MyClass mc) { }
	
	int foo1() { return 0; }
	
	void foo2(int value) { }
	
	// Noncompliant@+1 [[startColumn=6;endLine=+0;endColumn=10;effortToFix=4]] {{Never do that!}}
	int foo3(int value) { return 0; }
	
	Object foo4(int value) { return null; }
	
	// Noncompliant@+1 [[startColumn=10;endLine=+0;endColumn=14;effortToFix=4]] {{Never do that!}}
	MyClass foo5(MyClass value) { return null; }
 
}
