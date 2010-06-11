package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Tag extends Model implements Comparable<Tag>{
	public String name;
	public String color;
	
	public String toString() {
		return name;
	}
	
	@ManyToOne
	public User user;
	
	@ManyToOne
	public Operation op;
	
	public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }
}
