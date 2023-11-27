package it.cnr.igg.helper;

import java.math.BigDecimal;

public class MathTools {

	public MathTools() {
	}
	
	public static Double det(Double matrix[][]) {
		Double d = 0d;
		
		BigDecimal[][] bMatrix = MathTools.toBigDecimal(matrix);
		
		BigDecimal bd = MathTools.rDet(bMatrix);
		
		return d = bd.doubleValue();
	}
	
	private static BigDecimal rDet(BigDecimal[][] bm) {
		int dim = bm[0].length;
		BigDecimal rdet = BigDecimal.valueOf(0d);
		if (dim == 2) {
			rdet = bm[0][0].multiply(bm[1][1]);
			rdet = rdet.subtract(bm[0][1].multiply(bm[1][0]));
		}
		else for (int j = 0; j < dim; j++) {
			BigDecimal parziale = bm[0][j].multiply(rDet(minore(bm, 0, j)));
			if (j % 2 == 0) {
				rdet = rdet.add(parziale);
			} else {
				rdet = rdet.add(parziale.multiply(BigDecimal.valueOf(-1d)));
			}
		}
		return rdet;
	}
	
	private static BigDecimal[][] minore(BigDecimal[][] bm, int row, int col) {
		int dim = bm[0].length;
		BigDecimal[][] mn = new BigDecimal[dim - 1][dim - 1];
		int mi = 0;
		int mj = 0;
		for (int i = 0; i < dim; i++) {
			if (i != row) {
				for (int j = 0; j < dim; j++) {
					if (j != col) {
						mn[mi][mj] = bm[i][j];
						mj ++;
					}
				}
				mi ++;
				mj = 0;
			}
			
		}
		return mn;
	}
	
	private static BigDecimal[][] toBigDecimal(Double m[][]) {
		int dim = m[0].length;
		BigDecimal[][] bm = new BigDecimal[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				bm[i][j] = BigDecimal.valueOf(m[i][j]);
			}
		}
		return bm;
	}

	public static void main(String[] args) {
		Double[][] I = {{1d, 0d, 0d}, {0d, 1d, 0d}, {0d, 0d, 1d}};
		Double d = MathTools.det(I);
		System.out.println("det = " + d);
		Double[][] A = {{2d, 1d, 1d}, {1d, 1d, 0d}, {0d, 1d, 4d}};
		d = MathTools.det(A);
		System.out.println("det = " + d);
		Double[][] B = {{-2d, 1d, 1d}, {1d, 1d, 0d}, {0d, 1d, 4d}};
		d = MathTools.det(B);
		System.out.println("det = " + d);
		Double[][] C = {{3d, 1d}, {1d, 1d}};
		d = MathTools.det(C);
		System.out.println("det = " + d);
		Double[][] D = {{2d, 1d, 1d, 0d}, {1d, 1d, 0d, 0d}, {0d, 1d, 4d, 1d}, {1d, 1d, -4d, 3d}};
		d = MathTools.det(D);
		System.out.println("det = " + d);
	}
}
