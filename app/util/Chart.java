package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Tag;

public class Chart {
	
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
