package util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import models.Tag;
import models.TagStatValueObject;

public class Chart {
	
	private static final int FACTOR = 10;
	
	public static String implode(String delim, List<? extends Object> args){
		StringBuffer sb = new StringBuffer();
		int i = 0;
		
		for(Object o : args){
			if (i++ > 0)
				sb.append(delim);
			
			sb.append(o.toString());
			i++;
		}
		
		return sb.toString();
	}

	
	private static double convertToChm(double moyenne) {
		return moyenne/FACTOR/100;
	}
	
	public static String generateBudgetBarImg(TagStatValueObject tagSatObj) {
		
		Map<Date, BigDecimal> budgets = tagSatObj.getStatMap();
		List<String>labels = new ArrayList<String>();
		List<Integer>values = new ArrayList<Integer>();
		//List<String>colors = new ArrayList<String>();
		
		Calendar cal = Calendar.getInstance();
		
		for(Entry<Date, BigDecimal> entry : budgets.entrySet()) {
			cal.setTime(entry.getKey());
			labels.add(""+cal.get(Calendar.MONTH));
			double val = (Math.abs((entry.getValue().doubleValue()/FACTOR)));
			values.add(Integer.valueOf((int)val));
			//colors.add(entry.getKey().color.replace("#", ""));
		}
		
		
		
		String chart_src = "http://chart.apis.google.com/chart?cht=bvs&chs=600x225&chd=t:"
			+implode(",",values)
			+"&chm=h,999999,0,"+convertToChm(Math.abs(tagSatObj.getMoyenne().doubleValue()))+",2"
			+"|h,CCCCCC,0,"+convertToChm(Math.abs(tagSatObj.getMoyenne().doubleValue()+2*tagSatObj.getEcartType()))+",1"
			+"|h,CCCCCC,0,"+convertToChm(Math.abs(tagSatObj.getMoyenne().doubleValue()-2*tagSatObj.getEcartType()))+",1"
			+"&chxl=0:|"+implode("|",labels);//+"&chco=999999";
		
		String res = "<img src=\""+chart_src+"\"/>";
		
		return res;
	}
	
	public String nonStaticgenerateBudgetBarImg(TagStatValueObject tagSatObj) {
		return Chart.generateBudgetBarImg(tagSatObj);
	}
	
	
	public static String generateBudgetChartImg(Map<Tag, Double> budgets) {
		
		List<String>labels = new ArrayList<String>();
		List<Integer>values = new ArrayList<Integer>();
		List<String>colors = new ArrayList<String>();
		
		for(Entry<Tag, Double> entry : budgets.entrySet()) {
			labels.add(entry.getKey().name);
			values.add((new Integer(Math.abs(entry.getValue().intValue()))/50));
			colors.add(entry.getKey().color.replace("#", ""));
		}
		
		
		
		String chart_src = "http://chart.apis.google.com/chart?cht=p3&chs=320x200&chd=s:result&chd=t:"
			+implode(",",values)+"&chdl="+implode("|",labels)+"&chco="+implode("|",colors);
		
		String res = "<img src=\""+chart_src+"\"/>";
		
		return res;
	}
}
