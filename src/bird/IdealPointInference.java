/**
	IdealPointInference.java: gives one round of parameter update in the ideal point mixture.
**/

import java.util.*;

public class IdealPointInference
{
	public static double LR = Main.LR;
	public static final boolean BACKTRACK = true;

	public static double
	update(
		SparseMatrix<Integer> trainData,
		SparseMatrix<Integer> trainDataNeg,
		double[][] p, double[][] q, 
		double[] alpha, double[] beta,
		double gamma,
		double reg,									// coefficient of the regularization term 
		int iterRecord
	) {
		int N = p.length;
		int K = Main.K;
		double[][] tmpP = new double[N][K];
		double[] tmpBeta = new double[N];
		double[][] tmpQ = new double[q.length][K];
		double[] tmpAlpha = new double[q.length];
		if (iterRecord%10 == 9) LR *= 1.2;						// provide some chances for LR to increase 

		double[][] gradP = new double[N][K];
		double[] gradBeta = new double[N];
		double[][] gradQ = new double[q.length][K];
		double[] gradAlpha = new double[q.length];

		// calculate gradients 
		double networkSize = trainData.getSize() + trainDataNeg.getSize();
		for (int i = 0; i < N; i++) {
			// existing links
			for (int j: trainData.getRow(i)) {
				double v = trainData.get(i, j) / networkSize;

				double sigma = alpha[j] + beta[i] - gamma * Evaluation.l2s(p[i], q[j]);
				sigma = Evaluation.logis(sigma);

				for (int k = 0; k < K; k++) {
					gradP[i][k] += v * (1-sigma) * q[j][k];
					gradQ[j][k] += v * (1-sigma) * p[i][k];
				}
				gradAlpha[j] += v * (1-sigma);
				gradBeta[i] += v * (1-sigma);
			}
			// non-existing links (sample)
			for (int l: trainDataNeg.getRow(i)) {
				double v = trainDataNeg.get(i, l) / networkSize;

				double sigma = alpha[l] + beta[i] - gamma * Evaluation.l2s(p[i], q[l]);
				sigma = Evaluation.logis(sigma);

				for (int k = 0; k < K; k++) {
					gradP[i][k] -= v * sigma * q[l][k];
					gradQ[l][k] -= v * sigma * p[i][k];
				}
				gradAlpha[l] -= v * sigma;
				gradBeta[i] -= v * sigma;
			}
		}

		// Backtracking Line Search 
		double newObj = 0;
		double oldObj = Evaluation.calcLikelihood(trainData, trainDataNeg, p, q, alpha, beta, gamma, reg);
		if (BACKTRACK) {
			int count = 0;
			double blsAlpha = 0.2;	// decay factor
			double blsBeta = 0.001;	// slope: should be between 1e-4 and 0.3
			double lr = LR;
			double p22 = 0;			// ||p||_2^2, where p = grad(J) 
			while (true) {
				for (int i = 0; i < N; i++) {
					for (int k = 0; k < K; k++) {
						tmpP[i][k] = p[i][k] + lr * gradP[i][k] - lr * reg * p[i][k];
						p22 += gradP[i][k] * gradP[i][k];
					}
					tmpBeta[i] = beta[i] + lr * gradBeta[i] - lr * reg * beta[i];
					p22 += gradBeta[i] * gradBeta[i];
				}
				for (int i: Main.candidates) {
					for (int k = 0; k < K; k++) {
						tmpQ[i][k] = q[i][k] + lr * gradQ[i][k] - lr * reg * q[i][k];
						p22 += gradQ[i][k] * gradQ[i][k];
					}
					tmpAlpha[i] = alpha[i] + lr * gradAlpha[i] - lr * reg * alpha[i];
					p22 += gradAlpha[i] * gradAlpha[i];
				}

				newObj = Evaluation.calcLikelihood(trainData, trainDataNeg, tmpP, tmpQ, tmpAlpha, tmpBeta, gamma, reg);
				double increaseInObj = newObj - oldObj;
//				System.out.println("\tIncrease = " + increaseInObj + ", learning rate = " + lr);
//				System.out.println("\t  p22 = " + p22);
				if (increaseInObj > lr * blsBeta * p22) break;
//				else System.out.println("\t  p22 = " + p22 + " threshold = " + lr * blsBeta * p22);

				lr *= blsAlpha;
				count += 1;
				if (count == 3) return oldObj;
			}
			if (count != 0) LR *= 0.5;
		}

		// Update 
//		if (count != 5) {
		if (true) {
			for (int i = 0; i < N; i++) {
				for (int k = 0; k < K; k++) {
					p[i][k] = tmpP[i][k]; 
					q[i][k] = tmpQ[i][k];
				}
				beta[i] = tmpBeta[i];
				alpha[i] = tmpAlpha[i];
			}
		}

		System.out.println("\tnewObj = " + newObj);
		return newObj;
	}
}
