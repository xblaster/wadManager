package controllers;

import models.User;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;

import common.AuthController;

public class Login extends AuthController {

	public static void index() {
	    Application.index();
	}
	     
	public static void login() {
	    render();
	}
	
	public static void refuse() {
	    render();
	}
	    
	public static void authenticate(String user) {
		
	    if(OpenID.isAuthenticationResponse()) {
	        UserInfo verifiedUser = OpenID.getVerifiedID();
	        if(verifiedUser == null) {
	            flash.put("error", "Oops. Authentication has failed");
	            login();
	        } 
	        
	        storeInDb(verifiedUser);
	        index();
	    } else {
	    	 if(!OpenID.id("https://www.google.com/accounts/o8/id").
	    			 	required("email","http://axschema.org/contact/email").
	    			 	required("firstname","http://axschema.org/namePerson/first").
	    			 	required("lastname","http://axschema.org/namePerson/lastname").
	    			 	required("language","http://axschema.org/pref/language")
	    			 	.verify()
	    			 	) { 
	    	               flash.put("error", "Oops. Cannot contact google"); 
	    	               index(); 
	    	         } 
	    }
	    
	}

	private static void storeInDb(UserInfo verifiedUser) {
		//User u = new User();
		session.put("user", verifiedUser.id);
		
		
		User u = User.find("urlid = ?",verifiedUser.id).first();
		if (u ==null) {
			u = new User();
			u.urlid = verifiedUser.id;
			u.email = verifiedUser.extensions.get("email");
			u.firstname = verifiedUser.extensions.get("firstname");
			u.lastname = verifiedUser.extensions.get("lastname");
			u.language = verifiedUser.extensions.get("language");
			u.save();
			
			session.put("userid", u.id);
			
			flash.success("account for %s has been created",u.firstname);
			Application.index();
		}
		session.put("userid", u.id);
	}
	
	public static void disconnect() {
		session.remove("userid");
		session.remove("user");
		
		flash.put("success","You are now disconnected");
		
		Application.index();
	}
	
	

}
