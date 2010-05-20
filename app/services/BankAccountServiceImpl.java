package services;

import models.BankAccount;
import models.Operation;
import models.User;
import controllers.Application;

public class BankAccountServiceImpl implements BankAccountService {

	public void delete(Long id) {
		User u = Application.getAuthUser();
		//@TODO verify he have rights to do what
		
		BankAccount bankAccount = BankAccount.findById(id);
		bankAccount.delete();
	}

	public void save(BankAccount ba) {
		ba.save();
	}

	public BankAccount getById(Long id) {
		return BankAccount.findById(id);
	}

	public void deleteOperation(Long id) {
		Operation.findById(id).delete();
	}

	public Operation getOperationById(Long id) {
		return Operation.findById(id);
	}

	public void saveOperation(Operation op) {
		op.save();
	}
	
	
}
