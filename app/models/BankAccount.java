package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class BankAccount extends Model {

	public String name;
	
	@OneToOne
	public User user;
}
