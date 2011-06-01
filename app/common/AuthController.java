package common;

import models.Tag;
import controllers.Application;
import controllers.Login;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Before;
import play.mvc.Controller;

public class AuthController extends Controller {
	
	@Before(unless={"Login.login", "Login.authenticate","Login.refuse","Banks.showJSON","Banks.jsonAdd"})
	static void checkAuthenticated() {
	    if(!session.contains("user")) {
	        Login.login();
	    } else {
	    	renderArgs.put("authenticated",true);
	    }
	    
	    if (Application.getAuthUser()==null) {
	    	Login.login();
	    }
	}
	
	@Before
	static void populate() {
		renderArgs.put("user", Application.getAuthUser());
		renderArgs.put("tags", Tag.findAll());
	}
	 

}
