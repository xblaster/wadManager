package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import models.BankAccount;
import models.OperationPrevision;
import models.Tag;
import services.BankAccountService;
import services.TagService;
import services.UserService;

import common.AuthController;



public class PrevisionCtrl extends AuthController{
	
	@Inject	static BankAccountService bankAccountService;
	@Inject	static UserService userService;
	@Inject	static TagService tagService;
	
	public static void add(String name, Long tagId, Long bankId, double amount, String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		OperationPrevision op = new OperationPrevision();
		op.tag = tagService.getById(tagId);
		op.name = name;
		op.bankAccount = bankAccountService.getById(bankId);
		op.amount = amount;
		try {
			op.date = dateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			flash.error("bad date format %s", date);
			Banks.show(bankId, null, null);
			return;
		}
		
		bankAccountService.saveOperationPrevision(op);
		
		flash.success("Operation prevision %s created", name);
		Banks.show(bankId, null, null);
		
	}
	
	public static void delete(Long id) {
		OperationPrevision op = OperationPrevision.findById(id);
		
		BankAccount ba = op.bankAccount;
		
		flash.success("Operation prevision %s deleted", op.name);
		
		op.delete();
		
		Banks.show(ba.id, null, null);
	}
}
