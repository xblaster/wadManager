package services;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import models.Tag;
import models.TagStatValueObject;
import controllers.Application;

public class TagService {
	
	private static String getRandomColor() {
		String colorList[] = {
				"#FF0000",
				"#00FF00",
				"#0000FF",
				"#666600",
				"#006600",
				"#660066"
		};
		Random r = new Random();
		return colorList[r.nextInt(colorList.length)];
	}
	
	
	public Tag getById(Long id) {
		return Tag.findById(id);
	}
	
	public Tag getOrCreateByName(String name) {
		
		Tag t = getByName(name);
		if (t==null) {
			t = new Tag();
			t.name = name;
			t.user = Application.getAuthUser();
			t.color = getRandomColor();
			
			//invisible tag
			if (name == "needCheck") {
				t.visible = false;
			}
			
			t.save();
		}
		
		return t;
	}
	
	public Tag getByName(String name) {
		return Tag.find("name = ? and user = ?",name, Application.getAuthUser()).first();
	}
}
