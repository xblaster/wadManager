package controllers;

import javax.inject.Inject;

import services.BankAccountService;
import services.CheckService;

import common.AuthController;

public class Check extends AuthController  {
	
	 @Inject static CheckService checkService;
	
	 public static void index() {
		 render();
	 }
	 
	 public static void check(double price) {
		 renderArgs.put("operations", checkService.getUncheckedByPrice(Double.valueOf(price))); 
		 render();
	 }
	 
	 public static void uncheck(Long key) {
		 checkService.uncheck(key);
		 flash.success("uncheck OK");
		 index();
	 }
}
