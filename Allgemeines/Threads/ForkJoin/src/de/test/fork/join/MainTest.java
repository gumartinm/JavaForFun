package de.test.fork.join;

import java.io.File;

public class MainTest {

	public static void main(String[] args) {
		ForkJoinTaskExample example = new ForkJoinTaskExample();
		example.doRun(new File("/tmp"));
	}

}
