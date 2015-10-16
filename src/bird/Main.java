/**
	03/30/2015 
	Main.java: implement the unified model with two multinomial mixtures

	The first mixture is background (multinomial: \theta_ik * \beta_kj);
	The second mixture is ideal point (softmax - multinomial logit: exp(p_i * q_j) then normalize);

	If we want to build the model purely based on background, set USE_BKG to true and USE_IPM to false;
	If we want to build the model purely based on ideal point, set USE_BKG to false and USE_IPM to true

	If we want to use the bias term in ideal point model, set USEB to true; otherwise, set USEB to false

	04/20/2015
	In order to use settings.ini, we can create a global HashMap variable and parse it into every class 

	04/26/2015
	Modified input source for p and q in InitReader.java
**/

import java.util.*;
import java.io.*;

public class Main
{
	// Configuration
	public static int N;						// Number of Users
	public static final int K = 5;					// number of latent dimension 
	public static final double THRESHOLD = Math.pow(10,-5);		// stopping criterion 
	public static final boolean USEB = true;			// true if we use p[i]*q[j]+b[j]; fase if we use p[i]*q[j] 
	public static final boolean UpdateW = true;			// automatically update weights 
	public static double LR = 0.5;
	public static final double REG = 0.000005;

	public static SparseMatrix<Integer> trainData, testData;
	public static SparseMatrix<Integer> trainDataNeg, testDataNeg;
	public static Map<String, Integer> dict;
	public static Map<Integer, String> invDict;
	public static Set<Integer> candidates;

	// Model Parameters
	public static double[][] p, q;
	public static double[] alpha, beta;
	public static double gamma = 1;

	public static void
	init(String[] args, int option) {
		String seed = args[1];
//		System.out.println("Using seed " + seed);

		// Initialize Data 
		trainData = new SparseMatrix<Integer>(); testData = new SparseMatrix<Integer>();
		trainDataNeg = new SparseMatrix<Integer>(); testDataNeg = new SparseMatrix<Integer>();
		dict = new HashMap<String, Integer>(); invDict = new HashMap<Integer, String>();
		candidates = new HashSet<Integer>();

		// Initialize Parameters 
		N = InitReader.init(args, trainData, testData, trainDataNeg, testDataNeg, dict, invDict, candidates, option);

		p = new double[N][K]; q = new double[N][K];
		alpha = new double[N]; beta = new double[N];
		InitReader.init(p, q, alpha, beta, dict, option, args);
//		System.out.println("Size of Candidates = " + candidates.size());

//		System.out.println("Size of invDict = " + invDict.size());

		return;
	}

	// Train
	public static void
	train(String[] args) {
		String seed = args[1];
		double likelihood = Update.update(trainData, trainDataNeg, p, q, alpha, beta, gamma, REG);

		FileParser.output("./saved_param/p_" + seed, p, invDict);

//		System.out.println("OUTER ITER -- objective = " + likelihood + ", rate = " + rate);
	//	Evaluation.printExactObj(trainData, gamma, theta, beta, p, q, b, Update.reg);
		System.out.printf("%f", likelihood);

		return;
	}

	// Test
	public static void
	test() {
//		System.out.println("pi[0] = " + pi[0] + " pi[1] = " + pi[1]);
		// todo
//		System.out.println("\nTraining (all):");
		Evaluation.auroc(trainData, trainDataNeg, p, q, alpha, beta, gamma, 1);
		Evaluation.auprc(trainData, trainDataNeg, p, q, alpha, beta, gamma, 1);

//		System.out.println("\nTesting (all):");
		Evaluation.auroc(testData, testDataNeg, p, q, alpha, beta, gamma, 2);
		Evaluation.auprc(testData, testDataNeg, p, q, alpha, beta, gamma, 2);

//		System.out.println("");

/*		System.out.println("Training (aver):");
		Evaluation.auroc2(trainData, trainDataNeg, alpha, beta, pi, p, q, b, gamma, phi, varphi, 1);
		Evaluation.auprc2(trainData, trainDataNeg, alpha, beta, pi, p, q, b, gamma, phi, varphi, 1);
		System.out.println("Testing (aver):");
		Evaluation.auroc2(testData, testDataNeg, alpha, beta, pi, p, q, b, gamma, phi, varphi, 2);
		Evaluation.auprc2(testData, testDataNeg, alpha, beta, pi, p, q, b, gamma, phi, varphi, 2);
*/
//		System.out.println("Classification:");

//		Evaluation.partyClassify(p, q, invDict);
	}

	// Entry
	public static void
	main(String[] args) {
		if (args.length >= 3) {
			System.out.println("Usage: java Main <relation> <seed>");
			System.out.println("Example: java Main friend 5");
			System.out.println("If using java Main, settings.ini will be used as input");
			System.exit(0);
		}

		if (args.length == 1) {
			File f = new File("settings.ini");
			if (f.exists()) {
				System.out.println("Reading settings.ini ...");
			}
			else {
				System.out.println("settings.ini does not exist!");
				System.out.println("Usage: java Main <relation>");
				System.out.println("Example: java Main friend");
				System.exit(0);
			}	
		}

		if (args.length == 0) {
			System.out.println("Please specify seeds.");
			System.out.println("Usage: java Main <relation> <seed>");
			System.out.println("Example: java Main friend 5");
			System.out.println("If using java Main, settings.ini will be used as input");
			System.exit(0);
		}

		init(args, args.length);

		train(args);
		test();
	}
}
