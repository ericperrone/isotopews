package it.cnr.igg.geomodels;

import java.util.ArrayList;

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
	
	public ArrayList<MixingResult> getResult() {
		return this.result;
	}
	
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}

	public void setEndValue(double endValue) {
		this.endValue = endValue;
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
					for (double w = startValue; w <= endValue; w+= step) {
						double value = compute(m[0].value, m[1].value, w);
						System.out.println("" + value);
						mr.mix.add(new Mix(w, value));
					}
					result.add(mr);
				}
			}
		}
	}

	public double compute(double endMember1, double endMember2, double weightProportion) {
		return weightProportion * (endMember1 - endMember2) + endMember2;
	}
	
	public static void main(String[] args) {
		GeoData geo = new GeoData();
		geo.setElement("Ba");
		Member[] members = { new Member("EA136", 554d), new Member("EA185", 1143d) };
		geo.setMembers(members);
		GeoData[] geodata = { geo };
		Mixing mixing = new Mixing(geodata);
		mixing.apply();
		ArrayList<MixingResult> result = mixing.getResult();
	}

}
