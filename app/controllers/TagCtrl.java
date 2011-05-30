package controllers;

import models.Tag;

import common.AuthController;

public class TagCtrl extends AuthController {
	public static void edit(String id, Long tagid, String value) {
		  Tag t = Tag.findById(tagid);
		  t.name = value;
		  t.save();
		  
		  renderText(value);
	}
}
