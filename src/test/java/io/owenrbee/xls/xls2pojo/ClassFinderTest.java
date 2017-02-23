package io.owenrbee.xls.xls2pojo;

import java.io.IOException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class ClassFinderTest {

	@Test
	public void bestCase() throws ClassNotFoundException, IOException {
		
		Set<Class<?>> classes = ClassFinder.find("io.owenrbee.xls.xls2pojo");
		
		Assert.assertNotNull(classes);
		Assert.assertFalse(classes.isEmpty());
		
		Assert.assertTrue(classes.contains(ClassFinder.class));
		Assert.assertTrue(classes.contains(Column.class));
		Assert.assertTrue(classes.contains(Parser.class));
		
	}

}
