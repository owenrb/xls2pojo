package io.owenrbee.xls.xls2pojo;

import org.junit.Assert;
import org.junit.Test;

public class ColumnTest {

	@Test
	public void bestCase() {
		
		Column a = new Column("A");
		
		Assert.assertEquals("A", a.getName());
		Assert.assertEquals(0, a.getIndex());
		
		Assert.assertEquals("[A]", a.toString());
		
		// try lower case
		Column b = new Column("b");
		
		Assert.assertEquals("b", b.getName());
		Assert.assertEquals(1, b.getIndex());
		
		
	}
	
	@Test
	public void invalidCase() {

		Column unk = new Column("1");
		
		Assert.assertEquals("1", unk.getName());
		Assert.assertFalse(unk.getIndex() >= 0);
	}
}
