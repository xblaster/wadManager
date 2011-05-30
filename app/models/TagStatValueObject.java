package models;

import java.math.BigDecimal;
import java.math.MathContext;
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


	public int getNonZeroElt() {
		int nonZeroElt = 0;
		for (BigDecimal val: values.values()) {
			if (Math.abs(val.doubleValue()) > 0) {
				nonZeroElt++;
			}
		}
		return nonZeroElt;
	}
	
	public BigDecimal getMoyenne() {
		BigDecimal v = BigDecimal.valueOf(0);
		for (BigDecimal val: values.values()) {
			v =v.add(val);
		}
		int nonZeroElt = getNonZeroElt();
		//avoid divizion by zero
		if (nonZeroElt==0) {
			return BigDecimal.valueOf(0);
		}
		return BigDecimal.valueOf(v.doubleValue()/nonZeroElt); 
	}
	
	public Double getEcartType() {
		Double sum = 0d;
		Double moyenne = getMoyenne().doubleValue();
				
		for (BigDecimal val: values.values()) {
			//only non zero value
			if (Math.abs(val.doubleValue()) > 0) {
				sum += (val.doubleValue()-moyenne)*(val.doubleValue()-moyenne);
			}
		}
		
		int nonZeroElt = getNonZeroElt();
		if (nonZeroElt==0) {
			return 0d;
		}

		sum = sum/nonZeroElt;
		
		return new Double(Math.sqrt(sum)); 
	}

	public BigDecimal getMajoredEstimation() {
		BigDecimal moyenne = BigDecimal.valueOf(getMoyenne().intValue());
		Double ecartType = Double.valueOf(getEcartType().intValue());
		
		if (moyenne.doubleValue() > 0) {
			return moyenne.add(BigDecimal.valueOf(1*ecartType));
		} else {
			return moyenne.add(BigDecimal.valueOf(-1*ecartType));
		}
	}
	
		
	
	
	
}
