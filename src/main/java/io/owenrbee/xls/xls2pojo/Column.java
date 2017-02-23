package io.owenrbee.xls.xls2pojo;

import org.apache.poi.hssf.util.CellReference;

/**
 * The spreadsheet column reference object.
 * 
 * @author owenrbee@gmail.com
 *
 */
public class Column {
	
	private String name;

	/**
	 * Default constructor
	 * 
	 * @param name
	 */
	public Column(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "[" + name + "]";
	}
	
	/**
	 * Convert the current column name to its 0-base index value.
	 * 
	 * @return the index value
	 */
	public int getIndex() {
		return CellReference.convertColStringToIndex(name);
	}

	/**
	 * Get the column name value.
	 * 
	 * @return the column name
	 */
	public String getName() {
		return name;
	}

}
