package models;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Operation extends Model {

	public String name;
	public Date date;
	
	public double amount;
	
	@ManyToOne
	public BankAccount bankAccount;
	
	public String toString() {
		return bankAccount+":"+name+"|"+amount;
	}
	
	@OneToMany(cascade= CascadeType.PERSIST)
	public Set<Tag> tags;
	
	public Operation() {
		super();
		tags = new TreeSet<Tag>();
	}
}
