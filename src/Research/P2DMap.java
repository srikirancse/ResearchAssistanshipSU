package Research;

import java.util.*;

import org.apache.commons.lang3.time.StopWatch;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class P2DMap {

	static PrintStream out;
	static double dist[] = new double[1000010];
	static double algo2Dist = 0;
	static double algo1Dist = 0;

	static ArrayList<Integer> docCount = new ArrayList<Integer>();
	static ArrayList<Integer> patCount = new ArrayList<Integer>();

	static TreeMap<Integer, TreeMap<Double, Integer>> globalBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();
	static TreeMap<Integer, TreeMap<Double, Integer>> localBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();
	static TreeMap<Integer, TreeMap<Double, Integer>> currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();

	static double globalBestDist = 0;
	static double localBestDist = Integer.MAX_VALUE;
	static double currentBestDist = 0;

	public static int map2Doc(int p, MedMap m, int[] arr) {
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		// this function takes coordinates of patients and returns the index of
		// the nearest doctor
		for (int i = 0; i < m.doctorcount; i++) {
			if (arr[i] != 0) {
				// System.out.println("searching on doctor: " + m.doctors[i][0]
				// + " " + m.doctors[i][1]);
				Double d = Haversine.haversine(m.patients[p][0], m.patients[p][1], m.doctors[i][0], m.doctors[i][1]);
				// System.out.println("Distance: " + d);
				tm.put(d, i);
			}
		}
		if (tm.isEmpty())
			return -1;
		else {
			// System.out.println("Shortest distance found:" + tm.firstKey());
			int index = tm.get(tm.firstKey());
			dist[p] = tm.firstKey();
			algo1Dist += tm.firstKey();
			arr[index]--;
			// System.out.println("Patient num: "+p+" is assigned to doctor
			// number "+index);
			return index;
		}

	}

	public static int[] map2Pat(int d, MedMap m, int[] arr, boolean[] isPatientAssigned) {
		// this function takes coordinates of doctors and returns
		// the indices of assigned nearest patients in an array
		int patientIndex[] = new int[100];
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		int count = 0;

		if (arr[d] == 0)
			return null;

		else {
			for (int i = 0; i < m.patientcount; i++) {
				if (arr[d] != 0 && (!isPatientAssigned[i])) {
					Double dist = Haversine.haversine(m.patients[i][0], m.patients[i][1], m.doctors[d][0],
							m.doctors[d][1]);
					tm.put(dist, i);
				}
			}
			// System.out.println("Paients assigned to doctor: " + d + " With
			// capacity: " + arr[d]);
			for (Map.Entry<Double, Integer> entry : tm.entrySet()) {
				if (arr[d] == 0) {
					// System.out.println("Assignment done!");
					break;
				}
				Integer value = entry.getValue();
				Double key = entry.getKey();
				patientIndex[count] = value;
				// dist[value]=key;
				count++;
				arr[d]--;
				isPatientAssigned[value] = true;
				// System.out.println(value + " Distance: " + key);
				algo2Dist += key;
			}
			return patientIndex;
		}

	}

	public static int map2Pat3(int p, MedMap m, int[] arr) {
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		// this function takes coordinates of patients and returns the index of
		// the nearest doctor
		for (int i = 0; i < m.patientcount; i++) {
			if (arr[i] != 0) {
				// System.out.println("searching on doctor: " + m.doctors[i][0]
				// + " " + m.doctors[i][1]);
				Double d = Haversine.haversine(m.patients[i][0], m.patients[i][1], m.doctors[p][0], m.doctors[p][1]);
				// System.out.println("Distance: " + d);
				tm.put(d, i);
			}
		}
		if (tm.isEmpty())
			return -1;
		else {
			// System.out.println("Shortest distance found:" + tm.firstKey());
			int index = tm.get(tm.firstKey());
			// dist[p] = tm.firstKey();
			arr[index]--;
			// System.out.println("Patient num: "+p+" is assigned to doctor
			// number "+index);
			return index;
		}

	}

	public static TreeMap<Integer, Integer> algo1(MedMap m) {
		// Algorithm 1: This algorithm maps nearest doctors to each patient
		int p[] = new int[100010];
		Collections.shuffle(patCount);
		for (int i = 0; i < m.docCap.length; i++) {
			p[i] = m.docCap[i];
		}
		TreeMap<Integer, Integer> tm2 = new TreeMap<Integer, Integer>();
		algo1Dist = 0;
		for (int i = 0; i < m.patientcount; i++) {
			// System.out.println("Patient number: "+patCount.get(i)+"
			// Co-ordinates: "+m.patients[patCount.get(i)][0]+"
			// "+m.patients[patCount.get(i)][1]);
			int d = map2Doc(patCount.get(i), m, p);
			if (d == -1) {
				// System.err.println("Doctors unavailable from patient num: " +
				// i);
				break;
			} else {
				tm2.put(patCount.get(i), d);
			}

		}
		return tm2;
	}

	public static int[][] algo2(MedMap m) {
		// Algorithm 2: This algorithm maps nearest patients to each doctor
		boolean isPatientAssigned[] = new boolean[1000010];
		int p[] = new int[100010];
		Collections.shuffle(docCount);
		algo2Dist = 0;

		for (int i = 0; i < m.docCap.length; i++) {
			p[i] = m.docCap[i];
		}
		// System.out.println("Algo 2 starts!");

		int matrix[][] = new int[m.doctorcount][100];
		if (m.doctorcount == 0)
			System.out.println("No doctors available at the moment!");
		else {
			for (int i = 0; i < m.doctorcount; i++) {
				matrix[docCount.get(i)] = map2Pat(docCount.get(i), m, p, isPatientAssigned);
			}
		}
		return matrix;
	}

	public static TreeMap<Integer, TreeMap<Double, Integer>> algo3(MedMap m) {
		int p[] = new int[100010];
		Collections.shuffle(patCount);
		Collections.shuffle(docCount);
		boolean isPatientAssigned[] = new boolean[1000010];
		TreeMap<Integer, TreeMap<Double, Integer>> tm3 = new TreeMap<Integer, TreeMap<Double, Integer>>();
		int max = 0;
		for (int i = 0; i < m.docCap.length; i++) {
			p[i] = m.docCap[i];
			if (p[i] > max)
				max = p[i];
		}
		// System.out.println("Max value is: " + max);
		for (int i = 0; i < max; i++) {
			for (int j = 0; j < m.doctorcount; j++) {
				int n = docCount.get(j);
				TreeMap<Double, Integer> tm1 = new TreeMap<Double, Integer>();
				// System.out.println("Assigning for doctor: " + j + " with
				// capacity: " + p[j]);
				if (p[n] == 0)
					continue;
				for (int k = 0; k < m.patientcount; k++) {
					int l = patCount.get(k);
					if (isPatientAssigned[l])
						continue;
					Double d = Haversine.haversine(m.patients[l][0], m.patients[l][1], m.doctors[n][0],
							m.doctors[n][1]);
					tm1.put(d, l);
				}
				if (tm1.size() == 0)
					continue;
				double dist = tm1.firstKey();
				int pat = tm1.get(tm1.firstKey());
				if (!tm3.containsKey(n))
					tm3.put(n, new TreeMap<Double, Integer>());
				// System.out.println("Patient: " + pat + " is assigned to
				// doctor: " + j);
				(tm3.get(n)).put(dist, pat);
				isPatientAssigned[pat] = true;
				p[n]--;
			}
		}
		// globalBesttm = new TreeMap<Integer, TreeMap<Double, Integer>> (tm3);
		globalBesttm = DeepClone.deepClone(tm3);
		// globalBesttm = tm3;
		// localBesttm = new TreeMap<Integer, TreeMap<Double, Integer>> (tm3);
		localBesttm = DeepClone.deepClone(tm3);
		// localBesttm = tm3;
		// currentBesttm = tm3;
		globalBestDist = 0;// initializing the static variable
		for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : tm3.entrySet()) {
			int key = entry.getKey();
			TreeMap<Double, Integer> value = entry.getValue();
			for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
				int key2 = entry2.getValue();
				double value2 = entry2.getKey();
				globalBestDist += value2;
			}
		}
		// localBestDist = globalBestDist;
		currentBestDist = globalBestDist;
		// System.out.println("Total distance from method " + (int)
		// globalBestDist + " KM");
		return tm3;
	}

	public static TreeMap<Integer, TreeMap<Double, Integer>> tabuSearch(TreeMap<Integer, TreeMap<Double, Integer>> tm,
			MedMap m) {
		localBestDist = Double.MAX_VALUE;
		System.out.println(localBestDist < 10078.289895292493);
		int count = 0;
		currentBesttm = tm;
		HashMap<Integer, Integer> tabulist = new HashMap<Integer, Integer>();
		// System.out.println("Tabu search starts, " + currentBesttm.size() + "
		// " + tm.size());
		for (int iter = 0; iter < 500; iter++) {
			// System.out.println("Entered for block");
			for (int i = 0; i < currentBesttm.size() - 1; i++) {
				// System.out.println("Entered for block");
				if (m.docCap[i] == 0)
					continue;
				boolean flag_i = false;
				double olddist_i;
				int pat1;
				boolean isTabu1 = false;
				TreeMap<Double, Integer> locpattm1 = currentBesttm.get(i);
				olddist_i = locpattm1.lastKey();
				pat1 = locpattm1.get(olddist_i);
				if (tabulist.containsKey(pat1))
					isTabu1 = true; // should not consider pat 1 for swap
				while (isTabu1) {// finds the next tabu-free pat1 to swap
					// System.out.println(count++);
					// System.out.println("Entered while tabu1 block");
					try {
						olddist_i = locpattm1.lowerKey(olddist_i);
						pat1 = locpattm1.get(olddist_i);
						if (!tabulist.containsKey(pat1))
							break;
					} catch (Exception ex) {
						flag_i = true;
						break;
					}
				}
				if (flag_i)
					continue;

				for (int j = i + 1; j <= currentBesttm.size() - 1; j++) {
					boolean flag_j = false;
					if (m.docCap[j] == 0)
						continue;
					// System.out.println("Entered for block i=" + i + " j=" +
					// j);

					double olddist_j;

					int pat2;

					boolean isTabu2 = false;

					TreeMap<Double, Integer> locpattm2 = currentBesttm.get(j);

					olddist_j = locpattm2.lastKey();

					pat2 = locpattm2.get(olddist_j);

					if (tabulist.containsKey(pat2))
						isTabu2 = true; // should not consider pat 2 for swap

					while (isTabu2) {// finds the next tabu-free pat2 to swap
						// System.out.println("Entered while tabu1 block");
						try {
							olddist_j = locpattm2.lowerKey(olddist_j);
							pat2 = locpattm2.get(olddist_j);
							if (!tabulist.containsKey(pat2))
								break;
						} catch (Exception ex) {
							flag_j = true;
							break;
						}
					}
					if (flag_j)
						continue;

					if (!tabulist.isEmpty()) {// initial tabu operations
						Iterator it = tabulist.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();
							int key = (int) pair.getKey();
							int value = (int) pair.getValue();
							value--;
							if (value == 0) {
								// System.out.println("Entered if");
								it.remove();
							} else {
								// System.out.println("Entered else");
								tabulist.put(key, value);
							}
							// it.remove(); // avoids a
							// ConcurrentModificationException
						}
						/*
						 * for (Map.Entry<Integer, Integer> entry2 :
						 * tabulist.entrySet()) { int value = entry2.getValue();
						 * int key = entry2.getKey(); value--; if (value == 0){
						 * System.out.println("Entered if");
						 * tabulist.remove(key); } else{ System.out.println(
						 * "Entered else"); tabulist.put(key, value); } }
						 */
					}

					double newdist_i = Haversine.haversine(m.doctors[i][0], m.doctors[i][1], m.patients[pat2][0],
							m.patients[pat2][1]);
					double newdist_j = Haversine.haversine(m.doctors[j][0], m.doctors[j][1], m.patients[pat1][0],
							m.patients[pat1][1]);
					currentBestDist = globalBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);
					// System.out.println("Cuur dist:" + currentBestDist +
					// "Global dist:" + globalBestDist);

					if (currentBestDist < globalBestDist) {
						System.out.println(currentBestDist);
						globalBesttm = currentBesttm;
						globalBestDist = currentBestDist;
						(globalBesttm.get(i)).remove(olddist_i);
						(globalBesttm.get(i)).put(newdist_i, pat2);
						(globalBesttm.get(j)).remove(olddist_j);
						(globalBesttm.get(j)).put(newdist_j, pat1);
						// make pat1 and pat2 tabu now
						tabulist.put(pat1, 10);
						tabulist.put(pat2, 10);
						localBestDist = globalBestDist;
						localBesttm = globalBesttm;
					} else if (currentBestDist < localBestDist) {
						localBesttm = currentBesttm;
						localBestDist = currentBestDist;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);
						// make pat1 and pat2 tabu now
						tabulist.put(pat1, 10);
						tabulist.put(pat2, 10);
					}
				}
			}
			currentBesttm = localBesttm;
		}
		System.out.println("Tabu search 1 results:" + (int) globalBestDist + " KM");
		return globalBesttm;
	}

	public static TreeMap<Integer, TreeMap<Double, Integer>> tabuSearch2(TreeMap<Integer, TreeMap<Double, Integer>> tm,
			MedMap m) {
		// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>> (tm);
		double difference=0;
		double difference2=0;
		currentBesttm = DeepClone.deepClone(tm);
		// currentBesttm = tm;
		// localBestDist = Double.MAX_VALUE;
		HashMap<Integer, Integer> tabulist = new HashMap<Integer, Integer>();
		// System.out.println("Tabu search starts, " + currentBesttm.size() + "
		// " + tm.size());
		for (int iter = 0; iter < 500; iter++) {
			boolean b = true;
			boolean first = true;
			// System.out.println("Entered for block");
			for (int i = 0; i < currentBesttm.size() - 1; i++) {
				if (m.docCap[i] == 0)
					continue;
				boolean flag_i = false;
				double olddist_i;
				int pat1;
				boolean isTabu1 = false;
				TreeMap<Double, Integer> locpattm1 = currentBesttm.get(i);
				olddist_i = locpattm1.lastKey();
				pat1 = locpattm1.get(olddist_i);
				if (olddist_i > 1000)
					System.out.println("First i :" + olddist_i+" Patient number: "+pat1);
				if (tabulist.containsKey(pat1))
					isTabu1 = true; // should not consider pat 1 for swap
				while (isTabu1) {// finds the next tabu-free pat1 to swap
					// System.out.println("Entered while tabu1 block");
					try {
						olddist_i = locpattm1.lowerKey(olddist_i);
						pat1 = locpattm1.get(olddist_i);
						if (!tabulist.containsKey(pat1)) {
							if (olddist_i > 1000)
								System.out.println("Second i :" + olddist_i);
							break;
						}
					} catch (Exception ex) {
						flag_i = true;
						break;
					}
				}
				if (flag_i)
					continue;

				for (int j = i + 1; j <= currentBesttm.size() - 1; j++) {
					boolean flag_j = false;
					if (m.docCap[j] == 0)
						continue;
					// System.out.println("Entered for block i=" + i + " j=" +
					// j);

					double olddist_j;

					int pat2;

					boolean isTabu2 = false;

					TreeMap<Double, Integer> locpattm2 = currentBesttm.get(j);

					olddist_j = locpattm2.lastKey();
					
					pat2 = locpattm2.get(olddist_j);
					
					if (olddist_j > 1000)
						System.out.println("First j :" + olddist_j+" Patient number"+ pat2);

					if (tabulist.containsKey(pat2))
						isTabu2 = true; // should not consider pat 2 for swap

					while (isTabu2) {// finds the next tabu-free pat2 to swap
						// System.out.println("Entered while tabu1 block");
						try {
							olddist_j = locpattm2.lowerKey(olddist_j);
							pat2 = locpattm2.get(olddist_j);
							if (!tabulist.containsKey(pat2)){
								if (olddist_j > 1000)
									System.out.println("Second j :" + olddist_j);
								break;
							}
						} catch (Exception ex) {
							flag_j = true;
							break;
						}
					}
					if (flag_j)
						continue;

					if (!tabulist.isEmpty()) {// initial tabu operations
						Iterator it = tabulist.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();
							int key = (int) pair.getKey();
							int value = (int) pair.getValue();
							value--;
							if (value == 0) {
								// System.out.println("Entered if");
								it.remove();
							} else {
								// System.out.println("Entered else");
								tabulist.put(key, value);
							}
							// it.remove(); // avoids a
							// ConcurrentModificationException
						}
						/*
						 * for (Map.Entry<Integer, Integer> entry2 :
						 * tabulist.entrySet()) { int value = entry2.getValue();
						 * int key = entry2.getKey(); value--; if (value == 0){
						 * System.out.println("Entered if");
						 * tabulist.remove(key); } else{ System.out.println(
						 * "Entered else"); tabulist.put(key, value); } }
						 */
					}

					double newdist_i = Haversine.haversine(m.doctors[i][0], m.doctors[i][1], m.patients[pat2][0],
							m.patients[pat2][1]);
					double newdist_j = Haversine.haversine(m.doctors[j][0], m.doctors[j][1], m.patients[pat1][0],
							m.patients[pat1][1]);
					currentBestDist = globalBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);
					// System.out.println("Cuur dist:" + currentBestDist
					// +"Global dist:" + globalBestDist);
					if ((newdist_i + newdist_j) - (olddist_i + olddist_j) < 0){
						System.out.println("new dist: i: " + i + ", j: " + j + " " + (newdist_i + newdist_j)
								+ " olddist:" + olddist_i + " " + olddist_j + " Difference: "
								+ ((newdist_i + newdist_j) - (olddist_i + olddist_j)));
						if (first){
							difference=(newdist_i + newdist_j) - (olddist_i + olddist_j);
						}
					}
					else if(first) difference2=(newdist_i + newdist_j) - (olddist_i + olddist_j);
					if (((newdist_i + newdist_j) - (olddist_i + olddist_j))<=difference) {
						
						difference=(newdist_i + newdist_j) - (olddist_i + olddist_j);
						
						System.out.println("First Swapped"+" i: "+i+" j: "+j+" pat1: "+pat1+" pat2: "+pat2+" old dist i: "+olddist_i+" old dist j: "+olddist_j);
						
						// System.out.println(currentBestDist);
						// globalBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (currentBesttm);
						globalBesttm = DeepClone.deepClone(currentBesttm);
						// globalBesttm = currentBesttm;
						globalBestDist = currentBestDist;
						(globalBesttm.get(i)).remove(olddist_i);
						(globalBesttm.get(i)).put(newdist_i, pat2);
						(globalBesttm.get(j)).remove(olddist_j);
						(globalBesttm.get(j)).put(newdist_j, pat1);
						 //System.out.println("Map equality:"+currentBesttm.equals(globalBesttm));"+ "make pat1 and pat2 tabu now
						tabulist.put(pat1, 10);
						tabulist.put(pat2, 10);
						localBestDist = globalBestDist;
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>> (globalBesttm);
						// localBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (globalBesttm);
						localBesttm = DeepClone.deepClone(globalBesttm);
						b = false;
						if (first)
							first = false;
					} else if ((b && (currentBestDist < localBestDist)) || first) {
						System.out.println("Second Swapped"+" i: "+i+" j: "+j+" pat1: "+pat1+" pat2: "+pat2+" old dist i: "+olddist_i+" old sit j: "+olddist_j);
						// localBesttm = (TreeMap<Integer, TreeMap<Double,
						// Integer>>)currentBesttm.clone();
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>>(currentBesttm);
						localBesttm = DeepClone.deepClone(currentBesttm);
						localBestDist = currentBestDist;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);

						//(localBesttm.get(i)).put(23456.64326, pat2);
						//make pat1 and pat2 tabu now
						tabulist.put(pat1, 10);
						tabulist.put(pat2, 10);
						if (first)
							first = false;
						// System.out.println("Map equality:
						// "+currentBesttm.equals(localBesttm));
					}
				}
			}
			// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>
			// (localBesttm);
			currentBesttm = DeepClone.deepClone(localBesttm);
			System.out.println("Next search");
			// currentBesttm = localBesttm;

			// System.out.println("localBestDist: "+localBestDist);
		}
		System.out.println("Tabu search results:" + (int) globalBestDist + " KM");
		return globalBesttm;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MedMap m = new MedMap();

		m.circleGenerator(4, 6, 3);

		try {
			out = new PrintStream(new FileOutputStream("C://Users/Srikiran Sistla/Desktop/output.txt"));
			System.setOut(out);
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("\nAlgorithm 1 ");
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		TreeMap<Integer, TreeMap<Double, Integer>> algo3tm = new TreeMap<Integer, TreeMap<Double, Integer>>();
		for (int i = 0; i < m.doctorcount; i++) {
			docCount.add(i);
		}
		for (int i = 0; i < m.patientcount; i++) {
			patCount.add(i);
		}

		for (int k = 0; k < 6; k++) {
			double algo1Dist_loc = 0;
			map = algo1(m);
			// out1.println("Final results of algo 1:");
			int matrix[][] = new int[m.doctorcount][100];
			int matrixCount[] = new int[m.doctorcount];
			int c = 0;
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				int key = entry.getKey();
				int value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: " +
				// m.docCap[value]);
				matrix[value][matrixCount[value]++] = key;
			}
			for (int i = 0; i < m.doctorcount; i++) {
				// System.out.println("Patients assigned to doctor: " + i + "
				// with capacity: " + m.docCap[i]
				// + " Co-ordinates" + m.doctors[i][0] + " " + m.doctors[i][1]);
				for (int j = 0; j < matrixCount[i]; j++) {
					int d = matrix[i][j];
					if (d == 0)
						c++;
					if (d == 0 && c >= 2)
						break;
					// System.out.println(matrix[i][j] + " Distance: " +
					// dist[d]);
					algo1Dist_loc += dist[d];
				}
			}
			System.out.println("Total distance in run " + k + ": " + (int) algo1Dist + " KM");
		}

		System.out.println("\nAlgorithm 2 ");
		for (int i = 0; i < 6; i++) {
			algo2(m);
			System.out.println("Total distance in run " + i + ": " + (int) algo2Dist + " KM");
		}
		System.out.println("\nAlgorithm 3 ");
		for (int i = 0; i < 6; i++) {
			algo3tm = algo3(m);
			double algo3dist = 0;
			for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : algo3tm.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				// System.out.println("Patients asssigned to Doctor: " + key);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					// System.out.println("Patient number :" + key2 + "
					// Distance: " + value2 + " KMS");
					algo3dist += value2;
				}
			}
			System.out.println("Total distance in run " + i + ": " + (int) algo3dist + " KM");
			// tabuSearch(algo3tm, m);
			tabuSearch2(algo3tm, m);

		}
		System.out.println("\nProgram Terminated!");
	}

}
