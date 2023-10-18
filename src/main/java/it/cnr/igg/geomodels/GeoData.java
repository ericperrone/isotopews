package it.cnr.igg.geomodels;

class Member {
	public String member;
	public String element;
	public double concentration;
	public double concentration2;
	
	public Member(String member, String element, double concentration) {
		this.member = member;
		this.element = element;
		this.concentration = concentration;
		this.concentration2 = 0d;
	}

	public Member(String member, String element, double concentration, double concentration2) {
		this.member = member;
		this.element = element;
		this.concentration = concentration;
		this.concentration2 = concentration2;
	}
}

public class GeoData {
//	private String element;
	private Member[] members;
	private double step;
	
	public GeoData() {
	}

	public GeoData(Member[] members) {
		super();
//		this.element = element;
		this.members = members;
	}

	public Member[] getMembers() {
		return members;
	}
	
	public double getStep() {
		return step;
	}

	public void setMembers(Member[] members) {
		this.members = members;
	}
	
	public void setMembers(int size) {
		this.members = new Member[size];
	}
	
	public void setMember(String member, String element, Double concentration, int index) {
		this.members[index] = new Member(member, element, concentration);
	}
	
	public void setStep(double step) {
		this.step = step;
	}

	public void setMember(String member, String element, Double concentration, Double concentration2, int index) {
		this.members[index] = new Member(member, element, concentration, concentration2);
	}
}
