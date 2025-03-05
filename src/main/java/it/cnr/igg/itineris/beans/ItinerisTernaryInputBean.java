package it.cnr.igg.itineris.beans;

import java.util.ArrayList;

class TernaryElement {
	public String element;
	ArrayList<Double> value;
}

public class ItinerisTernaryInputBean {
	public TernaryElement a;
	public TernaryElement b;
	public TernaryElement c;
	
	public ItinerisTernaryInputBean() {
		a = new TernaryElement();
		b = new TernaryElement();
		c = new TernaryElement();
	}
	
	public void setA(String element, ArrayList<Double> value) {
		a.element = element;
		a.value = value;
	}
	
	public void setB(String element, ArrayList<Double> value) {
		b.element = element;
		b.value = value;
	}
	
	public void setC(String element, ArrayList<Double> value) {
		c.element = element;
		c.value = value;
	}
}
