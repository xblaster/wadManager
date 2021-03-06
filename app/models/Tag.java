package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

import com.google.gson.annotations.Expose;

@Entity
public class Tag extends Model implements Comparable<Tag>{
	@Expose public String name;
	public String color;
	public boolean visible = true;
	
	public String toString() {
		return name;
	}
	
	@ManyToOne
	public User user;
	
	/*@ManyToOne*/
	@ManyToMany(mappedBy="tags")
	public List<Operation> op;
	
	public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }

	public boolean equals(Object other) {
		
		if (!(other instanceof Tag)) {
			return false;
		}
		
		Tag t = (Tag) other;
		
		if (this.name != t.name) {
			return false;
		}
		
		return true;
	}
	
	
}
