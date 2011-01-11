package services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import play.db.jpa.Model;

import models.BankAccount;
import models.Operation;
import models.OperationPrevision;
import models.Tag;
import models.TagStatValueObject;
import models.User;

public interface BankAccountService {
	public void delete(Long id);
	public void save(BankAccount ba);
	
	public BankAccount getById(Long id);
	
	public List<BankAccount> getByUser(User u);
	
	public BankAccount getByOperation(Operation op);
	
	public BigDecimal getAmountAt(BankAccount ba, Date date);
	
	
	/**
	 * operation part
	 */
	
	public void saveOperation(Operation op);
	public void deleteOperation(Long id);
	
	public Operation getOperationById(Long id);
	
	public boolean operationExists(Operation op);
	
	public BigDecimal calculateBudgetForTag(BankAccount ba, Date dateBegin, Date dateEnd, Tag ta);
	public BigDecimal calculateEstimation(BankAccount ba, Tag ta);
	
	/**
	 * operation previsions part
	 */
	public Collection<OperationPrevision> getAllOperationPrevisions(Date begin, Date end);
	public void saveOperationPrevision(OperationPrevision op);
	public OperationPrevision getOperationPrevisionById(Long id);
	public BankAccount getByOperationPrevision(OperationPrevision op);
	
	public TagStatValueObject getStatForTag(Tag t, BankAccount ba);
	public Map<Tag, TagStatValueObject>getAllStatForBankAccount(BankAccount ba);
	
}
