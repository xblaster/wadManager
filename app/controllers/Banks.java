package controllers;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import services.BankAccountService;
import services.BankAccountServiceImpl;

import models.BankAccount;
import models.Operation;
import models.User;

import common.AuthController;

public class Banks extends AuthController{
	
	@Inject	static BankAccountService bankAccountService;
	
	public static void index() {
		User u = Application.getAuthUser();
		List<BankAccount> bankAccountList = BankAccount.find("byUser",u).fetch();
		//List<BankAccount> bankAccountList = u.bankAccountList;
		render(bankAccountList);
	}
	
	public static void show(Long id) {
		BankAccount bankAccount = BankAccount.findById(id);
		List <Operation> operations = Operation.find("bankAccount = ? order by date ASC", bankAccount).fetch();
		renderArgs.put("bankAccount", bankAccount);
		renderArgs.put("operations", operations);
		
		
		
		Double somme = 0d;
		for (Operation op : operations ) {
			somme+= op.amount;
		}
		
		renderArgs.put("somme", somme);
		
		render();
	}
	
	/**
	 * 
	 * @param name
	 */
	public static void delete(Long id) {
		bankAccountService.delete(id);
		//flash.success("Account %s deleted", bankAccount.name);
	}
	
	/**
	 * 
	 * @param name
	 */
	public static void deleteOperation(Long id) {
		flash.success("Operation %s deleted", bankAccountService.getOperationById(id).name);
		bankAccountService.deleteOperation(id);
	}
	
	public static void addOperation(String name, Date date, Double amount, Long bankId) {
		
		Operation operation = new Operation();
		operation.name = name;
		operation.amount = amount;
		operation.date = date;
		
		operation.bankAccount = BankAccount.findById(bankId);
		
		bankAccountService.saveOperation(operation);
		
		flash.success("Operation %s created", name);
		
		show(bankId);
	}
	
	public static void add(String name) {
		User u = Application.getAuthUser();
		BankAccount bankAccount = new BankAccount();
		bankAccount.name = name;
		bankAccount.user= u;
		
		bankAccountService.save(bankAccount);
		
		flash.success("Account %s created", name);
		
		index();
	}
	
	
}
