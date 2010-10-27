package controllers;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import models.BankAccount;
import models.Tag;
import models.TagStatValueObject;
import models.User;

import services.BankAccountService;
import util.Chart;
import common.AuthController;

public class Stat extends AuthController {
	
	@Inject
	public static BankAccountService bService;
	
	public static void index() {
		User u = Application.getAuthUser();
		List<BankAccount> bankAccountList = BankAccount.find("byUser",u).fetch();
		
		Map<BankAccount,Map<Tag, TagStatValueObject>> stats = new TreeMap<BankAccount, Map<Tag,TagStatValueObject>>();
		
		for (BankAccount ba: bankAccountList) {
			Map<Tag, TagStatValueObject> res = bService.getAllStatForBankAccount(ba);
			stats.put(ba, res);
		}
		
		renderArgs.put("chart", new Chart());
		renderArgs.put("stats", stats);
		
		render();
	}
}
