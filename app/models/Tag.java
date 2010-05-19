package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Tag extends Model{
	public String name;
	public String color;
	
	public String toString() {
		return name;
	}
}
