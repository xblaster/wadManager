package services;

import java.util.List;

import controllers.Application;
import models.BankAccount;
import models.Tag;
import models.User;

public class UserService {
	public User getById(Long id) {
		return User.findById(id);
	}

	public void save(User u) {
		u.save();
	}
	
	public List<Tag> getAllTags() {
		return Tag.find("byUser",Application.getAuthUser()).fetch();
	}
	
	public List<BankAccount> getAllBankAccountsForUser(User u) {
		return BankAccount.find("byUser", u).fetch();
	}
}
