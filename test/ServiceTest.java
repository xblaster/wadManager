import injection.WadGuiceModule;

import java.util.Date;
import java.util.List;

import models.BankAccount;
import models.Operation;
import models.Tag;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import services.BankAccountService;
import services.TagService;
import services.UserService;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class ServiceTest extends UnitTest{
	
	private static Injector injector = Guice.createInjector(new WadGuiceModule());
	private BankAccountService bService;
	private UserService uService;
	private TagService tService;
	
	/**
	 * create a user on the fly
	 * @return
	 */
	private User fetchOnTheFlyCreatedUser() {
		User u = new User();
		u.firstname = "bob";
		u.lastname = "morane";
		
		uService.save(u);
		
		return u;
	}
	
	@Before
	public void setUp() {
		 bService = injector.getInstance(BankAccountService.class);
		 uService = injector.getInstance(UserService.class);
		 tService = injector.getInstance(TagService.class);
	}
	
	@Test
	public void userCreateTest() {
		User u = fetchOnTheFlyCreatedUser();
		
		assertNotNull(u.getId());
		
		User u2 = uService.getById(u.getId());
		
		assertEquals(u.firstname, u2.firstname);
		assertEquals(u.lastname, u2.lastname);
	}
	
	@Test
	public void bankCreateTest() {
		User u = fetchOnTheFlyCreatedUser();
		
		BankAccount bAccount = new BankAccount();
		bAccount.user = u;
		bAccount.name = "bank1";
		
		bService.save(bAccount);
		
		BankAccount bAccount2 = new BankAccount();
		bAccount2.user = u;
		bAccount2.name = "bank2";
		
		bService.save(bAccount2);
		
		
		List<BankAccount> blist = uService.getAllBankAccountsForUser(u);
		assertEquals(2, blist.size());
	}
	
	@Test
	public void operationExistsTest() {
		User u = fetchOnTheFlyCreatedUser();
		
		BankAccount bAccount = new BankAccount();
		bAccount.user = u;
		bAccount.name = "bank1";
		
		bService.save(bAccount);
		
		Operation op = new Operation();
		op.name = "test";
		op.amount = 234;
		op.date = new Date();
		op.bankAccount = bAccount;
		
		bService.saveOperation(op);
		
		assertEquals(true, bService.operationExists(op));
		
		Operation op2 = new Operation();
		op2.name = "test";
		op2.amount = 7879;
		op2.date = new Date();
		op2.bankAccount = bAccount;
		
		//we don't save it
		
		assertEquals(false, bService.operationExists(op2));
		
	}

	@Test
	public void tagAddTest() {
		User u = fetchOnTheFlyCreatedUser();
		
		BankAccount bAccount = new BankAccount();
		bAccount.user = u;
		bAccount.name = "bankAddTest";
		
		bService.save(bAccount);
		
		Operation op = new Operation();
		op.name = "test";
		op.amount = 234;
		op.date = new Date();
		op.bankAccount = bAccount;
		
		bService.saveOperation(op);
		
		Tag t = tService.getOrCreateByName("test1");
		
		System.out.println("**** TAG ***"+t.getId());
		
		op.tags.add(t);
		op.save();
	}
}
