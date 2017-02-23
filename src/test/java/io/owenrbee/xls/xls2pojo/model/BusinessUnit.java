package io.owenrbee.xls.xls2pojo.model;

import java.util.ArrayList;
import java.util.List;

import io.owenrbee.xls.xls2pojo.annotation.Cell;
import io.owenrbee.xls.xls2pojo.annotation.Marker;
import io.owenrbee.xls.xls2pojo.annotation.Row;

/**
 * It is a business unit if the value at column "C" is equals to "Business Unit"
 * 
 * @author owenrbee@gmail.com
 *
 */
@Row(
	@Marker(value="Business Unit", cell = @Cell("C"))
)
public class BusinessUnit {

	@Cell("A")
	private String id;

	@Cell("B")
	private String name;
	
	private List<Account> accounts = new ArrayList<Account>();
	
	@Override
	public String toString() {
		return "BusinessUnit: " + id + " - " + name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

}
