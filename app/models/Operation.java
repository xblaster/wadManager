package models;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import services.TagService;

@Entity
public class Operation extends Model {

	public String name;
	public String importName;
	
	public String comment;
	
	public Date date;

	public String description;
	
	public double amount;
	
	public boolean fictive;
	
	
	@ManyToOne
	public BankAccount bankAccount;
	
	public String toString() {
		return bankAccount+":"+name+"|"+amount;
	}
	
	@ManyToMany(cascade= CascadeType.PERSIST)
	public Set<Tag> tags;
	
	public Operation() {
		super();
		tags = new TreeSet<Tag>();
		fictive = false;
	}
	
	public boolean hasTag(String name) {
		TagService tagService = new TagService();
		Tag t = tagService.getByName(name);
		
		if (t==null) {
			return false;
		}
		
		return tags.contains(t);
	}
}
