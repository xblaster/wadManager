package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;

import models.Operation;
import models.OperationPrevision;
import models.Tag;
import services.BankAccountService;
import services.TagService;
import services.UserService;

import common.AuthController;

import edu.emory.mathcs.backport.java.util.Arrays;

public class OperationCtrl extends AuthController {

	@Inject
	public static BankAccountService bService;
	
	@Inject
	public static UserService uService;
	
	@Inject
	public static TagService tService;
	
	public static void edit(Long id) {
		Operation op = bService.getOperationById(id);
		
		renderArgs.put("operation", op);
		renderArgs.put("tags", uService.getAllTags());
		
		render();
	}
	
	public static void editPrevision(Long id) {
		OperationPrevision op = bService.getOperationPrevisionById(id);
		
		renderArgs.put("operationPrevision", op);
		renderArgs.put("tags", uService.getAllTags());
		
		render();
	}
	
	public static void save(Operation operation) {
		
		System.out.println(operation.name);
		System.out.println(operation.id);
		
		List<String> tagsId;

		if (params.getAll("tag")!=null) {
			tagsId = Arrays.asList(params.getAll("tag"));
		} else {
			tagsId = new ArrayList<String>();
		}


		
		Operation op = bService.getOperationById(operation.id);
		op.name = operation.name;
		op.comment = operation.comment;
		op.tags = new TreeSet();

		if (params.get("fictive")!=null) {
			operation.fictive = true;
		} else {
			operation.fictive = false;
		}
		
		for (String i : tagsId) {
			Tag t = tService.getById(Long.valueOf(i));
			op.tags.add(t);
		}
		
		bService.saveOperation(op);
		
		flash.success("operation '%s' saved", operation.name);
		Banks.show(bService.getByOperation(op).getId(), null, null);
		
		
		//String result = "";
		//System.out.println(vals);
		//System.out.println(tags);
		
		/*for (Map.Entry<String, String[]> e: params.all().entrySet()) {
			result+= e.getKey().toString();
			result+=": ";
			result+= e.getValue()[0].getClass().toString()+"|";
			result+= e.getValue()[0].toString();
			result+= "\n";
		}*/
		//renderText(result);
		
	}
	
public static void savePrevision(OperationPrevision operationPrevision) {
		
		System.out.println(operationPrevision.name);
		System.out.println(operationPrevision.id);
		
		List<String> tagsId;

		if (params.getAll("tag")!=null) {
			tagsId = Arrays.asList(params.getAll("tag"));
		} else {
			tagsId = new ArrayList<String>();
		}


		
		OperationPrevision op = bService.getOperationPrevisionById(operationPrevision.id);
		op.name = operationPrevision.name;
		
		for (String i : tagsId) {
			Tag t = tService.getById(Long.valueOf(i));
			op.tag = t;
		}
		
		bService.saveOperationPrevision(op);
		
		flash.success("operation prevision '%s' saved", operationPrevision.name);
		Banks.show(bService.getByOperationPrevision(op).getId(), null, null);
		
	}
}
