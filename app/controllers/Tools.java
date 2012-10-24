package controllers;

import services.BankAccountService;
import services.BankAccountServiceImpl;
import common.AuthController;

public class Tools extends AuthController {
	public static void index() {
		render();
	}
	
	public static void fixBadTag() {
		BankAccountService bankAccountService = new BankAccountServiceImpl();
		renderText(bankAccountService.fixBadTag());
	}
}
