package it.cnr.igg.geomodels;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.MathContext;

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
				setStep(gd.getStep());
				Member[] m = gd.getMembers();
				if (m[0].concentration2 == 0d && m[1].concentration2 == 0d) {
					applyConcentration(gd);
				} else {
					applyIsotope(gd);
				}
			}
		}
	}

	public void applyConcentration(GeoData gd) {
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
				BigDecimal bValue = computeConcentration(BigDecimal.valueOf(m[0].concentration),
						BigDecimal.valueOf(m[1].concentration), w);
				mr.mix.add(new Mix(w.doubleValue(), bValue.doubleValue()));
				w = w.add(increment);
			}

			result.add(mr);
		}
	}

	public void applyIsotope(GeoData gd) {
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
				BigDecimal bValue = computeIsotope(BigDecimal.valueOf(m[0].concentration),
						BigDecimal.valueOf(m[0].concentration2),
						BigDecimal.valueOf(m[1].concentration),
						BigDecimal.valueOf(m[1].concentration2), w);
				mr.mix.add(new Mix(w.doubleValue(), bValue.doubleValue()));
				w = w.add(increment);
			}
			
			result.add(mr);
		}
	}

	public BigDecimal computeIsotope(BigDecimal i1, BigDecimal c1, BigDecimal i2, BigDecimal c2, BigDecimal f1) {
		BigDecimal cm = computeConcentration(c1, c2, f1);
		BigDecimal result = c1;
		result = result.multiply(i1);
		BigDecimal r2 = c2;
		r2 = r2.multiply(i2);
		result = result.subtract(r2);
		result = result.multiply(f1);
		result = result.add(r2);
		result = result.divide(cm, MathContext.DECIMAL64);
		return result;

	}

	public BigDecimal computeConcentration(BigDecimal c1, BigDecimal c2, BigDecimal f1) {
		BigDecimal result = c1;
		result = result.subtract(c2);
		result = result.multiply(f1);
		result = result.add(c2);
		return result;
		// return weightProportion * (endMember1 - endMember2) + endMember2;
		// (peso * endmemberA1 * endmemberA2 + (1 - peso)*endmemberB1*endmemberB2) /
		// endmemberB2
	}

}
