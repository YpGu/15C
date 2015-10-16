/**
	Update.java: gives one round of parameter update.
**/

import java.util.*;
import java.io.*;

public class Update
{
	public static final double reg = Main.REG;

	public static final int MAX_ITER_IPM = 1000;
	public static final int CHK = 10;					// check objective function every X times. 

	// Estimate Variational Parameters using EM 
	public static double
	update(
		SparseMatrix<Integer> trainData,
		SparseMatrix<Integer> trainDataNeg,
		double[][] p, double[][] q, double[] alpha, double[] beta,
		double gamma, double reg
	) {
		int N = p.length;

		double oldObj = -1, newObj = 0;

		for (int iter = 0; iter < MAX_ITER_IPM; iter++) {
			System.out.println("    *** Updating p,q,b " + iter + " ***");

			// update P,Q,b 
			newObj = IdealPointInference.update(trainData, trainDataNeg, p, q, alpha, beta, gamma, reg, iter);
			if (true) {
				double ratio = (oldObj-newObj) / oldObj;
//				System.out.println("\t--ratio = " + ratio);
				if (Math.abs(ratio) < 1e-6) {
					// objective function remained stable or even decreased
					break;
				}
				oldObj = newObj;
			}
			System.out.println("\tlikelihood = " + newObj);
		}

		return newObj;
	}
}

