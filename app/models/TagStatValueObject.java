package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class TagStatValueObject {
	private Map<Date,BigDecimal> values;
	
	public TagStatValueObject() {
		values = new TreeMap<Date, BigDecimal>();
	}
	
	public Map<Date, BigDecimal> getStatMap() {
		return this.values;
	}


	public void setValues(Map<Date, BigDecimal> values) {
		this.values = values;
	}
	
	public void addValue(Date d, BigDecimal budget) {
		this.values.put(d, budget);
	}


	public BigDecimal getMoyenne() {
		BigDecimal v = BigDecimal.valueOf(0);
		for (BigDecimal val: values.values()) {
			v =v.add(val);
		}
		return BigDecimal.valueOf(v.doubleValue()/values.size()); 
	}
	
	public Double getEcartType() {
		Double sum = 0d;
		Double moyenne = getMoyenne().doubleValue();
				
		for (BigDecimal val: values.values()) {
			sum = (val.doubleValue()-moyenne)*(val.doubleValue()-moyenne);
		}
		
		sum = sum/values.size();
		
		return new Double(Math.sqrt(sum)); 
	}
	
		
	
	
	
}
