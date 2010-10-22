package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.db.jpa.Model;

@Entity
public class User extends Model{
	public String urlid;
	public String firstname;
	public String lastname;
	public String email;
	public String language;
	
	//@OrderBy("name ASC")
	//@OneToMany
	//public List<BankAccount> bankAccounts;
	
	@OneToMany
	public List<Tag> tags;
	
	public String toString() {
		return this.firstname;
	}
}
