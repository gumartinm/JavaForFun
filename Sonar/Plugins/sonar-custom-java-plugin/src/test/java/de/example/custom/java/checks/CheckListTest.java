package de.example.custom.java.checks;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CheckListTest {

	  @Test
	  public void whenRetrievingJavaChecksThenGetRightNumberOfClasses() {
	    int count = 0;
	    Collection<File> files = FileUtils.listFiles(
	    		new File("src/main/java/de/example/custom/java/checks/"),
	    		new String[] {"java"},
	    		true);
	    for (File file : files) {
	      if (file.getName().endsWith("Check.java")) {
	        count++;
	      }
	    }
	    assertThat(CheckList.getChecks().size(), is(count));
	  }

}
