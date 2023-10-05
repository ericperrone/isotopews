package it.cnr.igg.geomodels;

class Member {
	public String member;
	public double concentration;
	public double isotope;
	
	public Member(String member, double concentration) {
		this.member = member;
		this.concentration = concentration;
		this.isotope = 0d;
	}

	public Member(String member, double concentration, double isotope) {
		this.member = member;
		this.concentration = concentration;
		this.isotope = isotope;
	}
}

public class GeoData {
	private String element;
	private Member[] members;
	private double step;
	
	public GeoData() {
	}

	public GeoData(String element, Member[] members) {
		super();
		this.element = element;
		this.members = members;
	}

	public String getElement() {
		return element;
	}

	public Member[] getMembers() {
		return members;
	}
	
	public double getStep() {
		return step;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void setMembers(Member[] members) {
		this.members = members;
	}
	
	public void setMembers(int size) {
		this.members = new Member[size];
	}
	
	public void setMember(String member, Double concentration, int index) {
		this.members[index] = new Member(member, concentration);
	}
	
	public void setStep(double step) {
		this.step = step;
	}

	public void setMember(String member, Double concentration, Double isotope, int index) {
		this.members[index] = new Member(member, concentration, isotope);
	}
}
