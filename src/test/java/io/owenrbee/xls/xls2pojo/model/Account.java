package io.owenrbee.xls.xls2pojo.model;

import io.owenrbee.xls.xls2pojo.annotation.Cell;
import io.owenrbee.xls.xls2pojo.annotation.Marker;
import io.owenrbee.xls.xls2pojo.annotation.Row;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * It is an account if the value at column "C" is equals to "Account",
 * and the value at the first column is numeric.
 * 
 * @author owenrbee@gmail.com
 *
 */
@Row({
	@Marker(value="Account", cell = @Cell("C")),
	@Marker(value="(\\d+(?:\\.\\d+)?)", regex=true, cell = @Cell("A"))
})
public class Account {

	@Cell("A")
	private Integer id;

	@Cell("B")
	private String name;
	
	@Cell(value = "D", format="dd-MMM-yyyy")
	private Date contractStartDate;

	@Cell(value = "E", format="dd-MMM-yyyy")
	private Date contractEndDate;
	
	private List<Employee> employees = new ArrayList<Employee>();
	
	@Override
	public String toString() {
		return "Account: " + id + " - " + name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public List<Employee> getEmployees() {
		return employees;
	}
	
}
