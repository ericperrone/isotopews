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
						BigDecimal.valueOf(m[0].concentration2), BigDecimal.valueOf(m[1].concentration),
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
	}

	public void compute() {
		if (data != null) {
			result = new ArrayList<MixingResult>();
			for (GeoData gd : data) {
				setStep(gd.getStep());
				compute(gd);
			}
		}
	}

	public void compute(GeoData gd) {
		ArrayList<BigDecimal> c1 = new ArrayList<BigDecimal>();
		ArrayList<BigDecimal> c2 = new ArrayList<BigDecimal>();
		Member[] members = gd.getMembers();
		for (Member m : members) {
			c1.add(BigDecimal.valueOf(m.concentration));
			c2.add(BigDecimal.valueOf(m.concentration2));
		}
		ArrayList<BigDecimal> cm = new ArrayList<BigDecimal>();
	}

	public ArrayList<ArrayList<BigDecimal>> f(int size) {
		BigDecimal start = BigDecimal.valueOf(startValue);
		BigDecimal end = BigDecimal.valueOf(endValue);
		ArrayList<ArrayList<BigDecimal>> w = new ArrayList<ArrayList<BigDecimal>>();
		BigDecimal increment = BigDecimal.valueOf(step);
		// inizializza
		ArrayList<BigDecimal> row = new ArrayList<BigDecimal>();
		for (int i = 0; i < size - 1; i++) {
			row.add(start);
		}
		row.add(end);
		w.add(row);

		// esegue il calcolo
		int col = size - 2;
		while (col > 0) {
			while (row.get(col - 1).compareTo(end) < 0) {

				ArrayList<BigDecimal> previous = w.get(w.size() - 1);
				BigDecimal sum = previous.get(0);
				for (int i = 1; i < col; i++) {
					sum = sum.add(previous.get(i));
				}
				for (int i = col + 1; i <= size - 2; i++) {
					sum = sum.add(previous.get(i));
				}
				BigDecimal value = start.add(increment);
				while (value.add(sum).compareTo(end) <= 0) {
					row = new ArrayList<BigDecimal>();
					for (int j = 0; j < col; j++) {
						row.add(j, previous.get(j));
					}
					row.add(col, value);
					row.add(size - 1, BigDecimal.valueOf(1).subtract(sum.add(value)));
					w.add(row);
					value = value.add(increment);
				}

				row = new ArrayList<BigDecimal>();
				for (int i = 0; i < col - 1; i++) {
					row.add(i, start);
				}
				row.add(col - 1, previous.get(col - 1).add(increment));
				for (int i = col; i <= size - 2; i++) {
					row.add(i, start);
				}
				row.add(end.subtract(row.get(col - 1)));
				w.add(row);
			}

			col--;
		}

		return w;
	}

	private BigDecimal computeConcentration(ArrayList<BigDecimal> c, ArrayList<BigDecimal> f) {
		BigDecimal cm = BigDecimal.valueOf(0d);
		for (int i = 0; i < c.size(); i++) {
			cm.add(c.get(i).multiply(f.get(i)));
		}
		return cm;
	}

	public static void main(String args[]) {
		Member[] members = { new Member("M01", 53.06d), new Member("M02", 55.98d), new Member("M03", 55.29d) };
		GeoData[] gd = { new GeoData("SIO2", members) };
		Mixing mixing = new Mixing(gd);
		mixing.setStep(0.1d);
//		ArrayList<ArrayList<BigDecimal>> f = mixing.f(members.length);
		ArrayList<ArrayList<BigDecimal>> f = mixing.f(3);
		System.out.println("end");
	}
}
