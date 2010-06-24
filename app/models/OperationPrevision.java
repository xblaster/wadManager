package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class OperationPrevision extends Model {
	@ManyToOne
	public BankAccount bankAccount;
	
	public String name;
	public Date date;
	
	@ManyToOne
	public Tag tag;
	
	public double amount;
	
	public String toString() {
		return bankAccount+":"+name;
	}
}
