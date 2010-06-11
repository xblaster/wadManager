package services;

import controllers.Application;
import models.Tag;

public class TagService {
	public Tag getById(Long id) {
		return Tag.findById(id);
	}
	
	public Tag getOrCreateByName(String name) {
		Tag t = Tag.find("name = ? and user = ?",name, Application.getAuthUser()).first();
		if (t==null) {
			t = new Tag();
			t.name = name;
			t.user = Application.getAuthUser();
			
			t.save();
		}
		
		return t;
	}
}
