package services;

import java.util.List;

import models.BankAccount;
import models.Operation;
import models.User;

public interface BankAccountService {
	public void delete(Long id);
	public void save(BankAccount ba);
	
	public BankAccount getById(Long id);
	
	public List<BankAccount> getByUser(User u);
	
	public BankAccount getByOperation(Operation op);
	
	
	/**
	 * operation part
	 */
	
	public void saveOperation(Operation op);
	public void deleteOperation(Long id);
	
	public Operation getOperationById(Long id);
	
	public boolean operationExists(Operation op);
	
}
