package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.BankAccount;
import models.Operation;
import models.Tag;
import models.User;
import services.BankAccountService;
import services.TagService;
import services.UserService;

import common.AuthController;

public class Banks extends AuthController{
	
	@Inject	static BankAccountService bankAccountService;
	@Inject	static UserService userService;
	@Inject	static TagService tagService;
	
	public static void index() {
		User u = Application.getAuthUser();
		List<BankAccount> bankAccountList = BankAccount.find("byUser",u).fetch();
		//List<BankAccount> bankAccountList = u.bankAccountList;
		
		renderArgs.put("tags", userService.getAllTags());
		renderArgs.put("bankAccountList", bankAccountList);
		
		render();
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
		
		renderArgs.put("tags", userService.getAllTags());
		
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
	
	public static void save(List<Long> selected) {
		String res = "";
		for(Long  id : selected) {
			res+=id+"\n";
		}
		
		renderText(res);
		
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
	
	public static void importBNPCSV(Long bankId, File importFile) {
		
		//find bank
		BankAccount bAccount = bankAccountService.getById(bankId);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
		
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(importFile));
		} catch (FileNotFoundException e) {
			flash.error("Can't open file");
			show(bankId);
			return;
		}
		
		String line = null;
		try {
			bufRdr.readLine();
			while((line = bufRdr.readLine()) != null)
			{
				String[] res = line.split("\t");
				
				//create operation
				Operation op = new Operation();
				res[2] = res[2].replace(",", ".");
				op.amount = Double.valueOf(res[2]);
				op.name = res[1];
				
				try {
					op.date = (Date)simpleDateFormat.parse(res[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					flash.error("Can't parse date format %s",res[0]);
					show(bankId);
				}
				
				op.bankAccount = bAccount;
				if (!bankAccountService.operationExists(op)) {
					
					//bankAccountService.saveOperation(op);
					
					if (op.name.contains("CARTE")) {
						op.tags.add(tagService.getOrCreateByName("CB"));
					}
					
					
					if (op.name.contains("DAB")) {
						op.tags.add(tagService.getOrCreateByName("DAB"));
					}
					
					if (op.name.contains("PRELEVEMENT")) {
						Tag t = tagService.getOrCreateByName("PRELEVEMENT");
						op.tags.add(t);
					}
					
					//op.save();
					
					bankAccountService.saveOperation(op);
					
				}
				
			}
		} catch (IOException e) {
			flash.error("Can't read the file");
			show(bankId);
			return;
		}
		
		try {
			bufRdr.close();
		} catch (IOException e) {
			flash.error("Can't close the file");
			show(bankId);
			return;
		}
		
		flash.success("Import successfull");
		show(bankId);
	}
	
	public static void batch(List<Long> selected, String jsAction, String jsParam) {
		if (selected == null) {
			return;
		}
		
		for (Long l : selected) {
			System.out.println("retrieve "+l);
			Operation op = bankAccountService.getOperationById(l);
			
			if (jsAction.equals("addTag")) {
				
				System.out.println("change");
				op.tags.add(tagService.getOrCreateByName(jsParam));
				bankAccountService.saveOperation(op);
			} else if (jsAction.equals("delTag")) {
				op.tags.remove(tagService.getOrCreateByName(jsParam));
				bankAccountService.saveOperation(op);
			}
		}
		/*System.out.println(jsAction);
		System.out.println(jsParam);
		System.out.println(selected.size());*/
	}
	
	
}
