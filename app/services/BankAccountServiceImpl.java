package services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import models.BankAccount;
import models.Operation;
import models.Tag;
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

	public List<BankAccount> getByUser(User u) {
		return BankAccount.find("byUser", u).fetch();
	}

	@Override
	public boolean operationExists(Operation op) {
		long size = Operation.count("amount = ? and date = ?", op.amount, op.date);
		
		if (size ==0) {
			return false;
		}
		
		return true;
	}

	public BankAccount getByOperation(Operation op) {
		//BankAccount ba =  BankAccount.find("byOperation", op).first();
		BankAccount ba = BankAccount.findById(op.bankAccount.getId());
		return ba;
	}

	@Override
	public BigDecimal getAmountAt(BankAccount ba, Date date) {
		List<Operation> operations = Operation.find("bankAccount = ? and date < ? order by date ASC", ba, date).fetch();
		
		BigDecimal result = BigDecimal.valueOf(0d);
		for (Operation op: operations) {
			result = result.add(BigDecimal.valueOf(op.amount));
		}
		return result;
	}
	
	public BigDecimal getBudgetForTag(BankAccount ba, Date dateBegin, Date dateEnd, Tag ta) {
		List<Operation> operations = 
					/*Operation.find("select distinct op from Operation op " +
							"           join op.tags as t" +
							"			where          "+
							"			bankAccount = :bankAccount and (date < :dateBegin and date > :dateEnd) " +
							"			order by date ASC")
										//.bind("tag", ta)
										.bind("bankAccount", ba)
										.bind("dateBegin", dateBegin)
										.bind("dateEnd", dateEnd).fetch();*/
					Operation.find("select op from Operation op " +
							"		join op.tags as t" +
							"		where "+
							"       t in (:tag) and" +
							"       op.bankAccount = (:bankAccount) and "+
							"		(op.date >= :dateBegin and op.date < :dateEnd)")
							.bind("dateBegin", dateBegin)
							.bind("dateEnd", dateEnd)
							.bind("bankAccount", ba)
							.bind("tag", ta)
							.fetch();
		
		BigDecimal result = BigDecimal.valueOf(0d);
		for (Operation op: operations) {
			result = result.add(BigDecimal.valueOf(op.amount));
		}
		return result;
	}
	
	
}
