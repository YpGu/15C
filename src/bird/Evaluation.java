/**
	Evaluation.java: Evaluation and Calculator.
**/

import java.util.*;
import java.lang.*;
import java.io.*;

public class Evaluation
{
	public static final boolean WEIGHTED = true;

	// Logistic Function
	public static double logis(double x) {
		double v = 1;
		if (x < 100)
			v = 1 - 1 / (1 + Math.exp(x));
		return v;
	}

	// l2 square: ||p - q||^2 
	public static double l2s(double[] p, double[] q) {
		double s = 0;
		for (int i = 0; i < p.length; i++) {
			s += (p[i] - q[i]) * (p[i] - q[i]);
		}
		return s;
	}

	// Input: log(a) and log(b); Output: log(a+b)
	public static double logSum(double logA, double logB) {
 		if (logA < logB) return logB + Math.log(1 + Math.exp(logA-logB));
		else return logA + Math.log(1 + Math.exp(logB-logA));
	}

	// Magic. Do not touch. 
	// Calculate log of Gamma Function 
	public static double logGamma(double x) {
		double z = 1/(x*x);
		x = x + 6;
		z = (((-0.000595238095238*z+0.000793650793651)*z-0.002777777777778)*z+0.083333333333333)/x;
		z = (x-0.5)*Math.log(x)-x+0.918938533204673+z-Math.log(x-1)-Math.log(x-2)-Math.log(x-3)-Math.log(x-4)-Math.log(x-5)-Math.log(x-6);
		return z;
	}
	// Calculate the Derivative of log-Gamma (Digamma) Function 
	public static double dLogGamma(double x) {
		if (x == 0) return Math.pow(10,-9);
		double dtmp = (x - 0.5) / (x + 4.5) + Math.log(x + 4.5) - 1;
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
                       + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
                       +  0.00120858003 / (x + 4) -  0.00000536382 / (x + 5);
		double dser = -76.18009173 / (x + 0) / (x + 0)  + 86.50532033 / (x + 1) / (x + 1)
                       - 24.01409822 / (x + 2) / (x + 2) + 1.231739516 / (x + 3) / (x + 3)
                       -  0.00120858003 / (x + 4) / (x + 4) + 0.00000536382 / (x + 5) / (x + 5);
		double res = dtmp + dser / ser;
		if (res != res) {
			System.out.println("dLog error");
			System.out.println("x = " + x);
			Scanner sc = new Scanner(System.in);
			int gu = sc.nextInt(); 
		}
		return res;
	}

	// This is the <Lower Bound> of the log-Likelihood 
	public static double calcLikelihood(
		SparseMatrix<Integer> posData,
		SparseMatrix<Integer> negData,
		double[][] p, double[][] q, double[] alpha, double[] beta, 
		double gamma, double reg
	) {
		int N = p.length;

		double res = 0;
		double networkSize = posData.getSize() + negData.getSize();
		for (int i = 0; i < N; i++) {
			for (int j: posData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(sigma);

				double prob = Math.log(sigma + Double.MIN_VALUE);
				res += posData.getElement(i,j) * prob;
			}
		}
		for (int i = 0; i < N; i++) {
			for (int j: negData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(-sigma);

				double prob = Math.log(sigma + Double.MIN_VALUE);
				res += negData.getElement(i,j) * prob; 
			}
		}
		res /= networkSize;

		double resStamp = res;
		for (int i = 0; i < N; i++) {
			for (int k = 0; k < Main.K; k++)
				res -= 0.5 * reg * p[i][k] * p[i][k];
			res -= 0.5 * reg * beta[i] * beta[i];
		}
		for (int i: Main.candidates) {
			for (int k = 0; k < Main.K; k++)
				res -= 0.5 * reg * q[i][k] * q[i][k];
			res -= 0.5 * reg * alpha[i] * alpha[i];
		}
		System.out.println("reg = " + (resStamp-res));

		return res;
	}

	// auroc 
	public static void 
	auroc(
		SparseMatrix<Integer> posData, SparseMatrix<Integer> negData,
		double[][] p, double[][] q, double[] alpha, double[] beta,
		double gamma,
		int type
	) {
		int K = beta.length, N = p.length;

		Map<Integer, Double> recProbs = new HashMap<Integer, Double>();
		Map<Integer, Double> recProbs1 = new HashMap<Integer, Double>();
		Map<Integer, Double> recProbs2 = new HashMap<Integer, Double>();
		Map<Integer, Double> idFreq = new HashMap<Integer, Double>();
		Set<Integer> posGroundTruth = new HashSet<Integer>();
		Set<Integer> negGroundTruth = new HashSet<Integer>();

		int tupleID = 0;
		double posSamples = 0;
		for (int i = 0; i < N; i++) {
			for (int j: posData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(sigma);

				double prob = sigma;
				recProbs.put(tupleID, prob);
				posGroundTruth.add(tupleID);

				double freq = posData.getElement(i, j);
				idFreq.put(tupleID, freq);
				tupleID += 1;
				posSamples += freq;
			}
		}
		double negSamples = 0;
		for (int i = 0; i < N; i++) {
			for (int j: negData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(sigma);

				double prob = sigma;
				recProbs.put(tupleID, prob);
				negGroundTruth.add(tupleID);

				double freq = negData.getElement(i, j);
				idFreq.put(tupleID, freq);
				tupleID += 1;
				negSamples += freq;
			}
		}

//		System.out.printf("\tSize of +'s = %f Size of -'s = %f", posSamples, negSamples);

		// calculate AUROC
		Map<Integer, Double> sortedProbs = ArrayTools.ValueComparator.sortByValue(recProbs);
//		Map<Integer, Double> sortedProbs1 = ArrayTools.ValueComparator.sortByValue(recProbs1);
//		Map<Integer, Double> sortedProbs2 = ArrayTools.ValueComparator.sortByValue(recProbs2);

		if (type == 1) {
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTrain")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			/*
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTrain_p1")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTrain_p2")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}
		if (type == 2) {
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTest")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
					if (posGroundTruth.contains(e.getKey()))
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			/*
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTest_p1")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
					if (posGroundTruth.contains(e.getKey()))
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/roc/secMixtureTest_p2")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
					if (posGroundTruth.contains(e.getKey()))
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}

//		System.out.println(" sortedProbs size = " + sortedProbs.size());
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
				int curKey = e.getKey();

				if (posGroundTruth.contains(curKey)) {
					newY += idFreq.get(curKey)/posSamples;
				}
				else if (negGroundTruth.contains(curKey)) {
					newX += idFreq.get(curKey)/negSamples;
				}
				else {
					// check key? 
					Scanner sc = new Scanner(System.in);
				}
				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\tUsing unified model: AUROC between %f and %f", lowerAUC, upperAUC);
			System.out.printf("\t%f", lowerAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		/*
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
				Scanner sc = new Scanner(System.in);
				int curKey = e.getKey();

				if (posGroundTruth.contains(curKey)) {
					newY += idFreq.get(curKey)/posSamples;
				}
				else if (negGroundTruth.contains(curKey)) {
					newX += idFreq.get(curKey)/negSamples;
				}
				else {
					// check key? 
					sc = new Scanner(System.in);
				}
				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\t  Using background model: AUROC between %f and %f", lowerAUC, upperAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
				Scanner sc = new Scanner(System.in);
				int curKey = e.getKey();

				if (posGroundTruth.contains(curKey)) {
					newY += idFreq.get(curKey)/posSamples;
				}
				else if (negGroundTruth.contains(curKey)) {
					newX += idFreq.get(curKey)/negSamples;
				}
				else {
					// check key? 
					sc = new Scanner(System.in);
				}
				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\t  Using ideal point model: AUROC between %f and %f", lowerAUC, upperAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		*/

		return;
	}

	// au p-r c
	public static void 
	auprc(
		SparseMatrix<Integer> posData, SparseMatrix<Integer> negData,
		double[][] p, double[][] q, double[] alpha, double[] beta,
		double gamma,
		int type
	) {
		int K = beta.length, N = p.length;

		Map<Integer, Double> recProbs = new HashMap<Integer, Double>();
		Map<Integer, Double> recProbs1 = new HashMap<Integer, Double>();
		Map<Integer, Double> recProbs2 = new HashMap<Integer, Double>();
		Map<Integer, Double> idFreq = new HashMap<Integer, Double>();
		Set<Integer> posGroundTruth = new HashSet<Integer>();
		Set<Integer> negGroundTruth = new HashSet<Integer>();

		int tupleID = 0;
		double posSamples = 0;
		for (int i = 0; i < N; i++) {
			for (int j: posData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(sigma);

				double prob = sigma;
				recProbs.put(tupleID, prob);
				posGroundTruth.add(tupleID);

				double freq = posData.getElement(i, j);
				idFreq.put(tupleID, freq);
				tupleID += 1;
				posSamples += freq;
			}
		}
		double negSamples = 0;
		for (int i = 0; i < N; i++) {
			for (int j: negData.getRow(i)) {
				double sigma = alpha[j] + beta[i] - gamma * l2s(p[i], q[j]);
				sigma = logis(sigma);

				double prob = sigma;
				recProbs.put(tupleID, prob);
				negGroundTruth.add(tupleID);

				double freq = negData.getElement(i, j);
				idFreq.put(tupleID, freq);
				tupleID += 1;
				negSamples += freq;
			}
		}

//		System.out.printf("\tSize of +'s = %f Size of -'s = %f", posSamples, negSamples);

		// calculate AUPRC
		Map<Integer, Double> sortedProbs = ArrayTools.ValueComparator.sortByValue(recProbs);
//		Map<Integer, Double> sortedProbs1 = ArrayTools.ValueComparator.sortByValue(recProbs1);
//		Map<Integer, Double> sortedProbs2 = ArrayTools.ValueComparator.sortByValue(recProbs2);

		if (type == 1) {
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTrain")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			/*
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTrain_p1")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTrain_p2")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}
		if (type == 2) {
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTest")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			/*
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTest_p1")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("./record/prc/secMixtureTest_p2")))) {
				for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
					if (posGroundTruth.contains(e.getKey())) 
						writer.printf("%s\t%f\t1\n", e.getKey(), e.getValue());
					else
						writer.printf("%s\t%f\t-1\n", e.getKey(), e.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}

//		System.out.println(" sortedProbs size = " + sortedProbs.size());
		// x: recall; y: precision
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			double numOfRetrieved = 0, numOfRelevant = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs.entrySet()) {
				int curKey = e.getKey();
				numOfRetrieved += idFreq.get(curKey);

				if (posGroundTruth.contains(curKey)) {
					numOfRelevant += idFreq.get(curKey);
				}
				newY = numOfRelevant/numOfRetrieved;
				newX = numOfRelevant/(double)posSamples;

				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\tUsing unified model: AUPRC between %f and %f", lowerAUC, upperAUC);
			System.out.printf("\t%f", lowerAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		/*
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			double numOfRetrieved = 0, numOfRelevant = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs1.entrySet()) {
				int curKey = e.getKey();
				numOfRetrieved += idFreq.get(curKey);

				if (posGroundTruth.contains(curKey)) {
					numOfRelevant += idFreq.get(curKey);
				}
				newY = numOfRelevant/numOfRetrieved;
				newX = numOfRelevant/(double)posSamples;

				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\t  Using background model: AUPRC between %f and %f", lowerAUC, upperAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			double numOfRetrieved = 0, numOfRelevant = 0;
			for (Map.Entry<Integer, Double> e: sortedProbs2.entrySet()) {
				int curKey = e.getKey();
				numOfRetrieved += idFreq.get(curKey);

				if (posGroundTruth.contains(curKey)) {
					numOfRelevant += idFreq.get(curKey);
				}
				newY = numOfRelevant/numOfRetrieved;
				newX = numOfRelevant/(double)posSamples;

				upperAUC += (newX - oldX) * newY;
				lowerAUC += (newX - oldX) * oldY;

				oldX = newX;
				oldY = newY;
			}
//			System.out.printf("\t  Using ideal point model: AUPRC between %f and %f", lowerAUC, upperAUC);
//			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		*/

		return;
	}

	// Party Affiliation Classification Accuracy
	public static void 
	partyClassify (double[] p, double[] q, Map<Integer, String> invDict) {
		String fileDir = "../../data/dict/merge_id_list";
		Map<String, Integer> party = new HashMap<String, Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// parse line here
				// Each Line: FullName \t rawID \t UserName \t party \n
				String[] tokens = currentLine.split("\t");
				String rawID = tokens[1];
				if (tokens[3].equals("R")) {
					party.put(rawID, 1);
				}
				else if (tokens[3].equals("D")) {
					party.put(rawID, 2);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		int numD = 0, numR = 0;
		Map<String, Double> mapP = new HashMap<String, Double>();
		Map<String, Double> mapQ = new HashMap<String, Double>();
		for (int i = 0; i < p.length; i++) {
			String rawID = invDict.get(i);
			if (party.containsKey(rawID)) {
				mapP.put(rawID, p[i]);
				mapQ.put(rawID, q[i]);
				if (party.get(rawID) == 1) numR += 1;
				if (party.get(rawID) == 2) numD += 1;
			}
		}
		Map<String, Double> sortedP = ArrayTools.ValueComparator.sortByValue(mapP);
		Map<String, Double> sortedQ = ArrayTools.ValueComparator.sortByValue(mapQ);

		System.out.println("\tnumR = " + numR + " numD = " + numD);

		if (true) {
			int count = 0; int cor = 0;
			System.out.println("Threshold = 0:");
			for (Map.Entry<String, Double> e: sortedP.entrySet()) {
				try {
					if (party.get(e.getKey()) == 1 && e.getValue() < 0) 
						cor += 1;
					else if (party.get(e.getKey()) == 2 && count >= 0)
						cor += 1;
				}
				catch (java.lang.NullPointerException f) {}
				if (party.containsKey(e.getKey()))
					count += 1;
			}
			System.out.println("\tP: " + cor + " out of " + count + " correct, accuracy = " + (double)cor/count);
			count = 0; cor = 0;
			for (Map.Entry<String, Double> e: sortedP.entrySet()) {
				try {
					if (party.get(e.getKey()) == 2 && e.getValue() < 0)
						cor += 1;
					else if (party.get(e.getKey()) == 1 && e.getValue() >= 0)
						cor += 1;
				}
				catch (java.lang.NullPointerException f) {} 
				if (party.containsKey(e.getKey()))
					count += 1;
			}
			System.out.println("\tP: " + cor + " out of " + count + " correct, accuracy = " + (double)cor/(numD+numR));

			count = 0; cor = 0;
			for (Map.Entry<String, Double> e: sortedQ.entrySet()) {
				try {
					if (party.get(e.getKey()) == 1 && e.getValue()< 0)
						cor += 1;
					else if (party.get(e.getKey()) == 2 && e.getValue() >= 0)
						cor += 1;
				}
				catch (java.lang.NullPointerException f) {} 
				if (party.containsKey(e.getKey()))
					count += 1;
			}
			System.out.println("\tQ: " + cor + " out of " + count + " correct, accuracy = " + (double)cor/(numD+numR));
			count = 0; cor = 0;
			for (Map.Entry<String, Double> e: sortedQ.entrySet()) {
				try {
					if (party.get(e.getKey()) == 2 && e.getValue() < 0)
						cor += 1;
					else if (party.get(e.getKey()) == 1 && e.getValue() >= 0)
						cor += 1;
				}
				catch (java.lang.NullPointerException f) {} 
				if (party.containsKey(e.getKey()))
					count += 1;
			}
			System.out.println("\tQ: " + cor + " out of " + count + " correct, accuracy = " + (double)cor/(numD+numR));
		}

		System.out.println("AUC:");
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			for (Map.Entry<String, Double> e: sortedP.entrySet()) {
				try {
					if (party.get(e.getKey()) == 1)
						newY += 1.0/numR;
					else if (party.get(e.getKey()) == 2)
						newX += 1.0/numD;
				}
				catch (java.lang.NullPointerException f) {}
				upperAUC += (newX-oldX) * newY;
				lowerAUC += (newX-oldX) * oldY;
				oldX = newX; oldY = newY;
			}
			// if AUC < 0.5: simply reverse the results cause we do not know whether D or R is the correct label
			if (lowerAUC < 0.5) lowerAUC = 1-lowerAUC;
			if (upperAUC < 0.5) upperAUC = 1-upperAUC;
			System.out.printf("\tAUC for P: AUROC between %f and %f", lowerAUC, upperAUC);
			System.out.println(" (newY = " + newY + " newX = " + newX + ")");
		}
		if (true) {
			double newX = 0, newY = 0, oldX = 0, oldY = 0;
			double upperAUC = 0, lowerAUC = 0;
			for (Map.Entry<String, Double> e: sortedQ.entrySet()) {
				try {
					if (party.get(e.getKey()) == 1)
						newY += 1.0/numR;
					else if (party.get(e.getKey()) == 2)
						newX += 1.0/numD;
				}
				catch (java.lang.NullPointerException f) {}
				upperAUC += (newX-oldX) * newY;
				lowerAUC += (newX-oldX) * oldY;
				oldX = newX; oldY = newY;
			}
			// if AUC < 0.5: simply reverse the results cause we do not know whether D or R is the correct label
			if (lowerAUC < 0.5) lowerAUC = 1-lowerAUC;
			if (upperAUC < 0.5) upperAUC = 1-upperAUC;
			System.out.printf("\tAUC for Q: AUROC between %f and %f", lowerAUC, upperAUC);
			System.out.println(" (newY = " + newY + " newX = " + newX + ")\n");
		}

		return;
	}
}

