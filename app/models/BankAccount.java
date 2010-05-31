package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class BankAccount extends Model {

	@Required
	public String name;
	
	@ManyToOne
	@Required
	public User user;
	
	public BankAccount() {
		
	}
	
	public String toString() {
		String firstName = (user!=null?user.firstname:"");
		return firstName+":"+name;
	}
	
	@OneToMany(fetch = FetchType.EAGER)
	public List<Operation> operations;
}
