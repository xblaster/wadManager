package models;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import services.TagService;

import com.google.gson.annotations.Expose;

@Entity
public class Operation extends Model {

	
	@Expose public String name;
	public String importName;
	
	@Expose public String comment;
	
	@Expose public Date date;

	@Expose public String description;
	
	@Expose public double amount;
	
	public boolean fictive;
	
	
	@ManyToOne
	public BankAccount bankAccount;
	
	public String toString() {
		return bankAccount+":"+name+"|"+amount;
	}
	
	@ManyToMany(cascade= CascadeType.PERSIST)
	@Expose public Set<Tag> tags;
	
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
