package controllers;

import javax.inject.Inject;

import models.Operation;
import services.BankAccountService;

import common.AuthController;

public class OperationCtrl extends AuthController {

	@Inject
	public static BankAccountService bService;
	
	public static void edit(Long id) {
		Operation op = bService.getOperationById(id);
		
		renderArgs.put("operation", op);
		//renderArgs.put("tags", arg1);
		
		render();
	}
	
	public static void save() {
		
	}
}
