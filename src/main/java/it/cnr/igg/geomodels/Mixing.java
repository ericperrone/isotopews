package it.cnr.igg.geomodels;

import java.util.ArrayList;
import java.math.BigDecimal;

class Mix {
	public double weight;
	public double mix;
	
	public Mix(double weight, double mix) {
		this.weight = weight;
		this.mix = mix;
	}
}

class MixingResult {
	public String element;
	public String memberA, memberB;
	public ArrayList<Mix> mix;
}

public class Mixing {
	private GeoData[] data = null;
	private double step = 0.01d, startValue = 0d, endValue = 1d; 
	private ArrayList<MixingResult> result = null;

	public Mixing() {
		this.data = null;
	}

	public Mixing(GeoData[] data) {
		this.data = data;
	}

	public void setData(GeoData[] data) {
		this.data = data;
	}
	
	public void setStep(double step) {
		this.step = step;
	}
	
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}

	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}

	public ArrayList<MixingResult> getResult() {
		return this.result;
	}
	
	public void apply() {
		if (data != null) {
			result = new ArrayList<MixingResult>();
			for (GeoData gd : data) {
				Member[] m = gd.getMembers();
				if (m.length == 2) {
					MixingResult mr = new MixingResult();
					mr.mix = new ArrayList<Mix>();
					mr.element = gd.getElement();
					mr.memberA = m[0].member;
					mr.memberB = m[1].member;
					BigDecimal w = BigDecimal.valueOf(startValue);
					BigDecimal end = BigDecimal.valueOf(endValue);
					BigDecimal increment = BigDecimal.valueOf(step);
					while (w.compareTo(end) <= 0) {
						BigDecimal bValue = compute(BigDecimal.valueOf(m[0].value), 
								BigDecimal.valueOf(m[1].value), 
								w);
						mr.mix.add(new Mix(w.doubleValue(), bValue.doubleValue()));
//						System.out.println(w + ": " + bValue);
						w = w.add(increment);
					}
					
					result.add(mr);
				}
			}
		}
	}

	public BigDecimal compute(BigDecimal endMember1, BigDecimal endMember2, BigDecimal weightProportion) {
		BigDecimal result = endMember1;
		result = result.subtract(endMember2);
		result = result.multiply(weightProportion);
		result = result.add(endMember2);
		return result;
		// return weightProportion * (endMember1 - endMember2) + endMember2;
	}
	
	public static void main(String[] args) {
		double a = 0.05d, b = 0.01d;		
		System.out.println("a: " + a + ", b: " + b + ", a+b: " + (a+b));
		String aa = "0.05", bb = "0.01";
//		BigDecimal bda = new BigDecimal("" + a);
//		BigDecimal bdb = new BigDecimal("" + b);
		BigDecimal bda = BigDecimal.valueOf(a);
		BigDecimal bdb = BigDecimal.valueOf(b);
		BigDecimal bsum = bda.add(bdb);
		System.out.println("a: " + bda + ", b: " + bdb + ", a+b: " + bsum);
		
		
//		System.out.println("a: " + a + ", b: " + b + ", nextDown(a+b): " + Math.nextDown(a+b));
//		System.out.println("a: " + a + ", b: " + b + ", a+b: " + Math.round(a+b));
//		GeoData geo = new GeoData();
//		geo.setElement("Ba");
//		Member[] members = { new Member("EA136", 554d), new Member("EA185", 1143d) };
//		geo.setMembers(members);
//		GeoData[] geodata = { geo };
//		Mixing mixing = new Mixing(geodata);
//		mixing.apply();
//		ArrayList<MixingResult> result = mixing.getResult();
	}

}
