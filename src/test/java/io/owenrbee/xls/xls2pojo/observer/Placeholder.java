package io.owenrbee.xls.xls2pojo.observer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import io.owenrbee.xls.xls2pojo.annotation.Sheet;
import io.owenrbee.xls.xls2pojo.model.Account;
import io.owenrbee.xls.xls2pojo.model.BusinessUnit;
import io.owenrbee.xls.xls2pojo.model.Employee;

@Sheet(value = "BO2017", scanPackage = "io.owenrbee.xls.xls2pojo.model")
public class Placeholder {

	private static final Logger LOG = LoggerFactory.getLogger(Placeholder.class);
	
	private List<BusinessUnit> businessUnits = new ArrayList<BusinessUnit>();
	
	private BusinessUnit currentBusinessUnit;
	private Account currentAccount;
	
	@Subscribe
	public void subscribeBusinessUnit(BusinessUnit businessUnit) {
		
		LOG.debug("got: " + businessUnit);
		
		businessUnits.add(businessUnit);
		
		currentBusinessUnit = businessUnit;
		currentAccount = null; // reset current account
	}
	
	@Subscribe
	public void subscribeAccount(Account account) {

		LOG.debug("got: " + account);
		
		if(currentBusinessUnit == null) {
			LOG.error("Opps, unable to find parent business unit for account: " + account.toString());
		} else {
			currentBusinessUnit.getAccounts().add(account);
			
			currentAccount = account;
		}
	}
	
	@Subscribe
	public void subscribeEmployee(Employee employee) {

		LOG.debug("got: " + employee);
		
		if(currentAccount == null) {
			LOG.error("Opps, unable to find parent account for employee: " + employee.toString());
		} else {	
			currentAccount.getEmployees().add(employee);
		}
	}

	/**
	 * 
	 * @return the final structured results
	 */
	public List<BusinessUnit> getBusinessUnits() {
		return businessUnits;
	}
	
}
