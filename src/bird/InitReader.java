/**
	InitReader.java: Read Initial Settings 
**/

import java.util.*;
import java.io.*;

public class InitReader 
{
	public static void
	readInit(double[] arr, String fileDir) {
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// split currentLine by '\t'
				String[] parts = currentLine.split("\t");
				arr[index] = Double.parseDouble(parts[0]);
				index += 1;
				if (index == arr.length) break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static void
	readInit(double[][] arr, String fileDir) {
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// split currentLine by '\t'
				String[] parts = currentLine.split("\t");
				for (int k = 0; k < Main.K; k++)
					arr[index][k] = Double.parseDouble(parts[k]);
				index += 1;
				if (index == arr.length) break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}


	public static void
	readInit(
		String fileDir, 
		Map<String, String> res							// receiver 
	) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				String a = currentLine.split("=")[0].trim();
				String b = currentLine.split("=")[1].trim();
				res.put(a, b);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	// Initialization and Read Data 
	public static int
	init(
		String[] args,
		SparseMatrix<Integer> trainData, SparseMatrix<Integer> testData, 
		SparseMatrix<Integer> trainDataNeg, SparseMatrix<Integer> testDataNeg,
		Map<String, Integer> dict, Map<Integer, String> invDict,
		Set<Integer> candidates,
		int option
	) {
		// Read Data
		String num = "", rel2 = "";
		if (option > 0) {							// option = 1: default values 
			num = "3k";
			rel2 = args[0];
		} else {								// option = 0: read settings.ini 
			Map<String, String> info = new HashMap<String, String>();
			readInit("settings.ini", info);
			for (Map.Entry<String, String> e: info.entrySet()) {
				String a = e.getKey();
				String b = e.getValue();
				System.out.println(a + " = " + b);
				switch (a) {
					case "relation":				// relation {friend, mention, retweet} 
					case "rel":
						rel2 = b;
						break;
					case "reg":					// regularization coefficient
						int reg = Integer.parseInt(b);
						break;
					case "init":					// 1: use init/*
						int useInit = Integer.parseInt(b);	// 0: use random init
						break;
					case "num":					// "3k" || "40k" 
						num = b;
						break;
					case "iter":
						int max_iter = Integer.parseInt(b);
						break;
				}
			}
		}

		String candidateDir = "../../data/dict/merge_id_list";
		String dictDir = "../../data/" + num + "_all/all_dict_" + num;
		FileParser.readVocabulary(dictDir, dict);
		FileParser.readInverseVocabulary(dictDir, invDict);

//		ArrayList<String> allRel = new ArrayList<String>();
//		allRel.add("friend"); allRel.add("mention"); allRel.add("retweet");
//		for (String rel: allRel) {
		for (String rel = rel2; true; ) {
			String trainDataDir = "../../data/" + num + "_" + rel + "_bipartite/" + rel + "_list_" + num + ".train";
			String testDataDir = "../../data/" + num + "_" + rel + "_bipartite/" + rel + "_list_" + num + ".test";
			String trainDataDirNeg = "../../data/" + num + "_" + rel + "_bipartite/n_" + rel + "_list_" + num + ".train.3";
			String testDataDirNeg = "../../data/" + num + "_" + rel + "_bipartite/n_" + rel + "_list_" + num + ".test.3";
			FileParser.readData(trainData, trainDataDir, dict); 
			FileParser.readData(testData, testDataDir, dict);
			FileParser.readData(trainDataNeg, trainDataDirNeg, dict); 
			FileParser.readData(testDataNeg, testDataDirNeg, dict);
			break;
		}
		FileParser.readCandidates(candidateDir, dict, candidates);
		int N = dict.size();
//		System.out.println("Size of Dictionary = " + N);

		return N;
	}

	public static void
	init(
		double[][] p, double[][] q, double[] alpha, double[] beta,
		Map<String, Integer> dict,
		int option,
		String[] args
	) {
		Random rand = new Random(0);
		int N = p.length;
		String seed = args[1];

		if (true) {
			// use (trackable) random init
			readInit(p, "../../data/init/init_p_" + seed);
			readInit(q, "../../data/init/init_q_" + seed);
			readInit(alpha, "../../data/init/init_alpha_" + seed);
			readInit(beta, "../../data/init/init_beta_" + seed);
//			readInit(p, "../../data/tmp_init_5");			// TODO - tmp init, to be removed
//			readInit(q, "../../data/tmp_init_6");
		}

		return;
	}
}
