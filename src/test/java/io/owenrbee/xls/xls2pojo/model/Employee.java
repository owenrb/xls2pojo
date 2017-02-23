package io.owenrbee.xls.xls2pojo.model;

import io.owenrbee.xls.xls2pojo.annotation.Cell;
import io.owenrbee.xls.xls2pojo.annotation.Marker;
import io.owenrbee.xls.xls2pojo.annotation.Row;

import java.util.Date;

/**
 * It is an employee if the value at column "C" is equals to "Employee"
 * 
 * @author owenrbee@gmail.com
 *
 */
@Row(
	@Marker(value="Employee", cell = @Cell("C"))
)
public class Employee {
	
	@Cell("A")
	private String id;

	@Cell("B")
	private String name;
	
	@Cell(value = "D", format="dd-MMM-yyyy")
	private Date assigmentStartDate;

	@Cell(value = "E", format="dd-MMM-yyyy")
	private Date assigmentEndDate;
	
	@Override
	public String toString() {
		return "Employee: " + id + " - " + name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getAssigmentStartDate() {
		return assigmentStartDate;
	}

	public Date getAssigmentEndDate() {
		return assigmentEndDate;
	}

}
