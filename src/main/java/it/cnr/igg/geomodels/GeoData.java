package it.cnr.igg.geomodels;

class Member {
	public String member;
	public double value;
	
	public Member(String member, double value) {
		this.member = member;
		this.value = value;
	}
}

public class GeoData {
	private String element;
	private Member[] members;
	
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

	public void setElement(String element) {
		this.element = element;
	}

	public void setMembers(Member[] members) {
		this.members = members;
	}
	
	public void setMembers(int size) {
		this.members = new Member[size];
	}
	
	public void setMember(String member, Double value, int index) {
		this.members[index] = new Member(member, value);
	}
}
