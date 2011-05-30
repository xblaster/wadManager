package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class BankAccount extends Model implements Comparable {

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
	
	//@OneToMany(fetch = FetchType.EAGER)
	//public List<Operation> operations;

	public int compareTo(Object arg0) {
		if (arg0 instanceof BankAccount) {
			BankAccount ba = (BankAccount) arg0;
			return name.compareTo(ba.name);
		}
		return 1;
	}
}
