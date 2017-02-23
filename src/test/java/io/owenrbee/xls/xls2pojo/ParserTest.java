package io.owenrbee.xls.xls2pojo;

import io.owenrbee.xls.xls2pojo.model.BusinessUnit;
import io.owenrbee.xls.xls2pojo.observer.AbstractPlaceholder;
import io.owenrbee.xls.xls2pojo.observer.InvalidPlaceholder;
import io.owenrbee.xls.xls2pojo.observer.InvalidSheetName;
import io.owenrbee.xls.xls2pojo.observer.Placeholder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class ParserTest {

	private static final String TEST_DATA_FILENAME = "SampleData.xls";
	
	@Test
	public void bestCase() throws ParserException {
		
		InputStream src = ParserTest.class.getResourceAsStream(TEST_DATA_FILENAME);
		
		Parser parser = new Parser(src);
		
		Placeholder result = parser.read(Placeholder.class);
		
		Assert.assertNotNull(result);
		
		List<BusinessUnit> businessUnits = result.getBusinessUnits();
		Assert.assertNotNull(businessUnits);
		Assert.assertFalse(businessUnits.isEmpty());
		
	}
	
	@Test(expected = FileNotFoundException.class)
	public void fileNotFound() throws FileNotFoundException {
		new Parser(new File("invalid.xls"));
	}

	@Test
	public void invalidPlaceholder() throws ParserException {

		InputStream src = ParserTest.class.getResourceAsStream(TEST_DATA_FILENAME);
		
		Parser parser = new Parser(src);
		
		InvalidPlaceholder result = parser.read(InvalidPlaceholder.class);
		
		Assert.assertNull(result);
		
	}

	@Test(expected = ParserException.class)
	public void invalidSheetName() throws ParserException {

		InputStream src = ParserTest.class.getResourceAsStream(TEST_DATA_FILENAME);
		
		Parser parser = new Parser(src);
		
		parser.read(InvalidSheetName.class);
		
	}
	

	@Test(expected = ParserException.class)
	public void abstractObserver() throws ParserException {

		InputStream src = ParserTest.class.getResourceAsStream(TEST_DATA_FILENAME);
		
		Parser parser = new Parser(src);
		
		parser.read(AbstractPlaceholder.class);
	}
}
