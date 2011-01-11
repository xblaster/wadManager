package services;

import java.util.LinkedList;
import java.util.List;

import models.Operation;

public class CheckService {
	public List<Operation> getUncheckedByPrice(double price) {
		
		//List<Operation> result = new LinkedList<Operation>();
		
		//foreach
		return Operation.find("amount = ?", price).fetch();
		//return Operation.findAll();
	}
}
