/**
	Read data
**/

import java.util.*;
import java.io.*;

public class FileParser
{
	public static void
	readData(SparseMatrix<Integer> data, String fileDir, Map<String, Integer> dict) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// parse line here
				// for followers/friends: each line contains 2 ids (x,y) 
				// for mention and retweet: each line contains 3 ids (x,t,y) 
				String[] tokens = currentLine.split("\t");
				if (tokens.length == 2) {
					int x = dict.get(tokens[0]);
					int y = dict.get(tokens[1]);
//					data.set(x, y, 1.0);
					data.addTo(x, y, 1.0);
				}
				else if (tokens.length == 3) {
					int x = dict.get(tokens[0]);
					int y = dict.get(tokens[2]);
					data.addTo(x, y, 1.0);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		data.update(dict);

		return;
	}

	// Read Vocabulary: String -> Integer
	public static void 
	readVocabulary(String fileDir, Map<String, Integer> res) {
		int lineID = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			// Each Line: <newID> \t <rawID> \n 
			while ((currentLine = br.readLine()) != null) {
				String rawID = currentLine.split("\t")[1];
				res.put(rawID, lineID);
				lineID += 1;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	// Read Inverse Vocabulary: Integer -> String
	public static void
	readInverseVocabulary(String fileDir, Map<Integer, String> res) {
		int lineID = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			// Each Line: <newID> \t <rawID> \n 
			while ((currentLine = br.readLine()) != null) {
				String rawID = currentLine.split("\t")[1];
				res.put(lineID, rawID);
				lineID += 1;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	/// get candidates (authority users)
	public static void
	readCandidates(String fileDir, Map<String, Integer> dict, Set<Integer> candidates) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
			String currentLine;
			// Each Line: <full_name> \t <rawID> \t <user_name> \t <party> \n 
			while ((currentLine = br.readLine()) != null) {
				String rawID = currentLine.split("\t")[1];
				if (dict.containsKey(rawID)) {
					int newID = dict.get(rawID);
					candidates.add(newID);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}


	/// write to file
	public static void
	output(String fileDir, Map<String, ?> arr) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (Map.Entry<String, ?> e: arr.entrySet()) {
				if (e.getValue() instanceof Double) {
					writer.printf("%s\t%f\n", e.getKey(), e.getValue());
				}
				else if (e.getValue() instanceof Integer) {
					writer.printf("%s\t%d\n", e.getKey(), e.getValue());
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	outputNum(String fileDir, Map<Integer, ?> arr) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (Map.Entry<Integer, ?> e: arr.entrySet()) {
				if (e.getValue() instanceof Double) {
					writer.printf("%f\n", e.getValue());
				}
				else if (e.getValue() instanceof Integer) {
					writer.printf("%d\n", e.getValue());
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	outputArray(String fileDir, Map<String, double[]> arr) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (Map.Entry<String, double[]> e: arr.entrySet()) {
				writer.printf("%s", e.getKey());
				double[] vs = e.getValue();
				for (double v: vs) 
					writer.printf("\t%f", v);
				writer.printf("\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	output(
		String fileDir,
		double[][] arr
	) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[0].length; j++) {
					writer.printf("%f\t", arr[i][j]);
				}
				writer.printf("\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	output(
		String fileDir,
		double[][] arr,
		Map<Integer, String> voc
	) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (int i = 0; i < arr.length; i++) {
				String newline = voc.get(i);
				for (int k = 0; k < arr[i].length; k++) {
					newline += "\t";
					newline += Double.toString(arr[i][k]);
				}
				writer.printf("%s\n", newline);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	output(
		String fileDir,
		double[] arr,
		Map<Integer, String> voc
	) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (int i = 0; i < arr.length; i++) {
				String newline = voc.get(i);
				newline += "\t";
				newline += Double.toString(arr[i]);
				writer.printf("%s\n", newline);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void
	output(
		String fileDir,
		double[] arr
	) {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileDir)))) {
			for (int i = 0; i < arr.length; i++) {
				writer.printf("%f\n", arr[i]);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
