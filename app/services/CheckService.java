package services;

import java.util.LinkedList;
import java.util.List;

import models.Operation;

public class CheckService {
	public List<Operation> getUncheckedByPrice(double price) {
		
		List<Operation> result = new LinkedList<Operation>();
		
		List<Operation> remoteOps = Operation.find("amount = ?", price).fetch();
		
		for(Operation oper : remoteOps) {
			if (oper.hasTag("needCheck")) { //bad way but well !
				result.add(oper);
			}
		}
		return result;
		//return Operation.findAll();
	}

	public void uncheck(Long key) {
		BankAccountServiceImpl bankAccountServiceImpl = new BankAccountServiceImpl();
		TagService tagService = new TagService();
		
		Operation op = bankAccountServiceImpl.getOperationById(key);
		op.tags.remove(tagService.getByName("needCheck"));
		
		bankAccountServiceImpl.saveOperation(op);
	}
}
