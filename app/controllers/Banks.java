package controllers;

import java.util.List;

import models.BankAccount;
import models.User;

import common.AuthController;

public class Banks extends AuthController{
	public static void index() {
		User u = Application.getAuthUser();
		List<BankAccount> bankAccountList = BankAccount.find("byUser",u).fetch();
		//List<BankAccount> bankAccountList = u.bankAccountList;
		render(bankAccountList);
	}
	
	public static void show(Long id) {
		BankAccount bankAccount = BankAccount.findById(id);
		render(bankAccount);
	}
	
	/**
	 * 
	 * @param name
	 */
	public static void delete(Long id) {
		User u = Application.getAuthUser();
		
		BankAccount bankAccount = BankAccount.findById(id);
		flash.success("Account %s deleted", bankAccount.name);
		bankAccount.delete();
		
	}
	
	public static void add(String name) {
		User u = Application.getAuthUser();
		BankAccount bankAccount = new BankAccount();
		bankAccount.name = name;
		bankAccount.user= u;
		bankAccount.save();
		
		flash.success("Account %s created", name);
		
		index();
	}
	
	public static void addOperation() {
		
	}
}
