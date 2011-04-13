package injection;

import services.BankAccountService;
import services.BankAccountServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;

public class WadGuiceModule extends AbstractModule  {

	@Override
	protected void configure() {
		bind(BankAccountService.class).to(BankAccountServiceImpl.class);
	}

}
