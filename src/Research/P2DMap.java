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
	static ArrayList<Integer> unPatAlgo1 = new ArrayList<Integer>();

	static TreeMap<Integer, TreeMap<Double, Integer>> globalBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();
	static TreeMap<Integer, TreeMap<Double, Integer>> localBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();
	static TreeMap<Integer, TreeMap<Double, Integer>> currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>();

	static double globalBestDist = 0;
	static double localBestDist = Integer.MAX_VALUE;
	static double currentBestDist = 0;

	public static TreeMap<Integer, TreeMap<Double, Integer>> algo1(MedMap m) {
		// Algorithm 1: This algorithm maps nearest doctors to each patient
		TreeMap<Integer, TreeMap<Double, Integer>> tm1 = new TreeMap<Integer, TreeMap<Double, Integer>>();
		int p[] = new int[100010];
		Collections.shuffle(patCount);
		for (int i = 0; i < m.docCap.length; i++) {
			p[i] = m.docCap[i];
		}
		for (int i = 0; i < m.patientcount; i++) {
			unPatAlgo1.add(i);
		}
		algo1Dist = 0;
		for (int i = 0; i < m.patientcount; i++) {
			// System.out.println("Patient number: "+patCount.get(i)+"
			// Co-ordinates: "+m.patients[patCount.get(i)][0]+"
			// "+m.patients[patCount.get(i)][1]);

			// int d = map2Doc(patCount.get(i), m, p);

			int p1 = patCount.get(i);

			TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
			// this function takes coordinates of patients and returns the index
			// of
			// the nearest doctor
			for (int j = 0; j < m.doctorcount; j++) {
				if (p[j] != 0) {
					// System.out.println("searching on doctor: " +
					// m.doctors[i][0]
					// + " " + m.doctors[i][1]);
					Double d = Haversine.haversine(m.patients[p1][0], m.patients[p1][1], m.doctors[j][0],
							m.doctors[j][1]);
					// System.out.println("Distance: " + d);
					tm.put(d, j);
				}
			}
			if (tm.isEmpty())
				break;
			else {
				// System.out.println("Shortest distance found:" +
				// tm.firstKey());
				int index = tm.get(tm.firstKey());
				double dist = tm.firstKey();
				if (!tm1.containsKey(tm.get(tm.firstKey())))
					tm1.put(index, new TreeMap<Double, Integer>());
				tm1.get(index).put(dist, patCount.get(i));
				algo1Dist += tm.firstKey();
				p[index]--;
				unPatAlgo1.remove(patCount.get(i));
				// System.out.println("Patient num: "+p+" is assigned to doctor
				// number "+index);
			}

		}
		globalBesttm = DeepClone.deepClone(tm1);
		localBesttm = DeepClone.deepClone(tm1);
		globalBestDist = algo1Dist;// initializing the static variable
		localBestDist = globalBestDist;
		currentBestDist = globalBestDist;
		return tm1;
	}

	public static TreeMap<Integer, TreeMap<Double, Integer>> algo2(MedMap m) {
		// Algorithm 2: This algorithm maps nearest patients to each doctor
		TreeMap<Integer, TreeMap<Double, Integer>> tm2 = new TreeMap<Integer, TreeMap<Double, Integer>>();
		boolean isPatientAssigned[] = new boolean[1000010];
		int p[] = new int[100010];
		Collections.shuffle(docCount);
		algo2Dist = 0;

		for (int i = 0; i < m.docCap.length; i++) {
			p[i] = m.docCap[i];
		}
		// System.out.println("Algo 2 starts!");

		//int matrix[][] = new int[m.doctorcount][100];
		if (m.doctorcount == 0)
			System.out.println("No doctors available at the moment!");
		else {
			for (int i = 0; i < m.doctorcount; i++) {
				//matrix[docCount.get(i)] = map2Pat(docCount.get(i), m, p, isPatientAssigned);
				
				int d=docCount.get(i);
				int patientIndex[] = new int[100];
				TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
				int count = 0;

				if (p[d] == 0)
					continue;

				else {
					for (int j = 0; j < m.patientcount; j++) {
						if (p[d] != 0 && (!isPatientAssigned[j])) {
							Double dist = Haversine.haversine(m.patients[j][0], m.patients[j][1], m.doctors[d][0],
									m.doctors[d][1]);
							tm.put(dist, j);
						}
					}
					// System.out.println("Paients assigned to doctor: " + d + " With
					// capacity: " + arr[d]);
					tm2.put(d, new TreeMap<Double,Integer>());
					for (Map.Entry<Double, Integer> entry : tm.entrySet()) {
						if (p[d] == 0) {
							// System.out.println("Assignment done!");
							break;
						}
						Integer value = entry.getValue();
						Double key = entry.getKey();
						tm2.get(d).put(key, value);
						//patientIndex[count] = value;
						// dist[value]=key;
						//count++;
						p[d]--;
						isPatientAssigned[value] = true;
						// System.out.println(value + " Distance: " + key);
						algo2Dist += key;
					}
				}
			}
		}
		globalBesttm = DeepClone.deepClone(tm2);
		localBesttm = DeepClone.deepClone(tm2);
		globalBestDist = algo2Dist;// initializing the static variable
		localBestDist = globalBestDist;
		currentBestDist = globalBestDist;
		return tm2;
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

		/*
		 * for (int i=0;i<m.doctorcount;i++){ if(m.docCap[i]==0) continue;
		 * TreeMap<Double, Integer> unpattm = new TreeMap<Double, Integer>();
		 * TreeMap<Double, Integer> pattm = tm3.get(i); boolean flag=true; for
		 * (int j=0;j<m.patientcount;j++){ if(!isPatientAssigned[j]){
		 * unpattm.put((Haversine.haversine(m.patients[j][0], m.patients[j][1],
		 * m.doctors[i][0],m.doctors[i][1])),j); } } while(flag){
		 * if(unpattm.firstKey()<pattm.lastKey()){ System.out.println(
		 * "Repalcing: "+pattm.firstKey()+" with "+unpattm.lastKey());
		 * pattm.remove(pattm.lastKey());
		 * pattm.put(unpattm.firstKey(),unpattm.get(unpattm.firstKey()));
		 * unpattm.remove(unpattm.firstKey()); } else flag=false; } }
		 */

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
		localBestDist = globalBestDist;
		currentBestDist = globalBestDist;
		//System.out.println("Total distance from method " + (int) globalBestDist + " KM");
		return tm3;
	}
	
	public static TreeMap<Integer, TreeMap<Double, Integer>> tabuSearch1(TreeMap<Integer, TreeMap<Double, Integer>> tm,
			MedMap m) {
		// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>> (tm);
		double difference = 0;
		double difference2 = 0;
		currentBesttm = DeepClone.deepClone(tm);
		// currentBesttm = tm;
		// localBestDist = Double.MAX_VALUE;
		HashMap<Integer, Integer> tabulist = new HashMap<Integer, Integer>();
		// System.out.println("Tabu search starts, " + currentBesttm.size() + "
		// " + tm.size());
		for (int iter = 0; iter < 700; iter++) {
			boolean b = true;
			boolean first1 = true;
			boolean first2 = true;
			boolean first11 = false;
			boolean first22 = false;
			int pat1 = 0;
			int pat2 = 0;
			boolean makeTabu = false;
			// System.out.println("Entered for block");
			for (int i = 0; i < currentBesttm.size() - 1; i++) {
				if (m.docCap[i] == 0)
					continue;
				boolean flag_i = false;
				double olddist_i;

				boolean isTabu1 = false;
				TreeMap<Double, Integer> locpattm1 = currentBesttm.get(i);
				try{
					olddist_i = locpattm1.lastKey();
				}catch(Exception ex){
					continue;
				}
				pat1 = locpattm1.get(olddist_i);
				if (olddist_i > 1000)
					System.out.println("First i :" + olddist_i + " Patient number:" + pat1);
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
				if (flag_i) {
					//System.out.println("First continue");

					if (!tabulist.isEmpty()) {// initial tabu operations
						// System.out.println("inside if");
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

					continue;
				}

				for (int j = i + 1; j <= currentBesttm.size() - 1; j++) {
					boolean flag_j = false;
					if (m.docCap[j] == 0)
						continue;
					// System.out.println("Entered for block i=" + i + " j=" +
					// j);

					double olddist_j=0;

					boolean isTabu2 = false;

					TreeMap<Double, Integer> locpattm2 = currentBesttm.get(j);
					
					try{
						olddist_j = locpattm2.lastKey();
					}
					catch(Exception ex){
						//System.out.println("Doc num: "+j+" DocCap: "+m.docCap[j]);
						continue;
					}
					

					pat2 = locpattm2.get(olddist_j);

					if (olddist_j > 1000)
						System.out.println("First j :" + olddist_j + " Patient number" + pat2);

					if (tabulist.containsKey(pat2))
						isTabu2 = true; // should not consider pat 2 for swap

					while (isTabu2) {// finds the next tabu-free pat2 to swap
						// System.out.println("Entered while tabu1 block");
						try {
							olddist_j = locpattm2.lowerKey(olddist_j);
							pat2 = locpattm2.get(olddist_j);
							if (!tabulist.containsKey(pat2)) {
								if (olddist_j > 1000)
									System.out.println("Second j :" + olddist_j);
								break;
							}
						} catch (Exception ex) {
							flag_j = true;
							break;
						}
					}
					if (flag_j) {
						//System.out.println("Second Continue");

						if (!tabulist.isEmpty()) {// initial tabu operations
							//System.out.println("inside if");
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
							 * tabulist.entrySet()) { int value =
							 * entry2.getValue(); int key = entry2.getKey();
							 * value--; if (value == 0){ System.out.println(
							 * "Entered if"); tabulist.remove(key); } else{
							 * System.out.println( "Entered else");
							 * tabulist.put(key, value); } }
							 */
						}

						continue;
					}

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

					// System.out.println("Cuur dist:" + currentBestDist
					// +"Global dist:" + globalBestDist);
					if ((newdist_i + newdist_j) - (olddist_i + olddist_j) < 0) {

						// System.out.println("new dist: i: " + i + ", j: " + j+
						// " " + (newdist_i + newdist_j) +
						// " olddist:" +olddist_i + " " + olddist_j +
						// " Difference: " +
						// ((newdist_i + newdist_j) - (olddist_i + olddist_j)));

						if (first1) {
							difference = (newdist_i + newdist_j) - (olddist_i + olddist_j);
							first1 = false;
							first11 = true;
						}
					} else if (first2) {
						difference2 = (newdist_i + newdist_j) - (olddist_i + olddist_j);
						first2 = false;
						first22 = true;
					}
					if ((((newdist_i + newdist_j) - (olddist_i + olddist_j)) < difference) || first11) {

						//System.out.println("Old diffetene: "+difference);

						difference = (newdist_i + newdist_j) - (olddist_i + olddist_j);

						//System.out.println("New diffetene: "+difference);

						// System.out.println("First Swapped"+" i: "+i+"
						// j:"+j+"pat1: "+pat1+
						// " pat2: "+pat2+" old dist i:"+olddist_i+
						// " old dist j: "+olddist_j);

						double currbestdist_dupl = currentBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);

						// System.out.println(currentBestDist);
						// globalBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (currentBesttm);
						// globalBesttm = DeepClone.deepClone(currentBesttm);
						// globalBesttm = currentBesttm;
						localBesttm = DeepClone.deepClone(currentBesttm);
						// System.out.println("localBestDist: "+localBestDist+"
						// currbestdist: "+currbestdist_dupl);
						localBestDist = currbestdist_dupl;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);
						// System.out.println("Map
						// equality:"+currentBesttm.equals(globalBesttm));"+
						// "make pat1 and pat2 tabu now
						// localBestDist = globalBestDist;
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>> (globalBesttm);
						// localBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (globalBesttm);
						// localBesttm = DeepClone.deepClone(globalBesttm);
						b = false;
						if (first11)
							first11 = false;
						makeTabu = true;
					} else if (b && (((newdist_i + newdist_j) - (olddist_i + olddist_j) < difference2) || first22)) {
						//System.out.println("Old Difference: "+ difference2);
						difference2 = (newdist_i + newdist_j) - (olddist_i + olddist_j);
						//System.out.println("Old Difference: "+ difference2);
						// System.out.println("Second Swapped"+" i: "+i+"j:"+j+
						// " pat1: "+pat1+" pat2: "+pat2+" old dist
						// i:"+olddist_i+
						// " old sit j: "+olddist_j);

						double currbestdist_dupl = currentBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);
						// localBesttm = (TreeMap<Integer, TreeMap<Double,
						// Integer>>)currentBesttm.clone();
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>>(currentBesttm);
						localBesttm = DeepClone.deepClone(currentBesttm);
						//System.out.println("localBestDist: "+localBestDist+"currbestdist: "+currbestdist_dupl);
						localBestDist = currbestdist_dupl;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);

						// (localBesttm.get(i)).put(23456.64326, pat2);
						// make pat1 and pat2 tabu now
						// System.out.println("Map equality:
						// "+currentBesttm.equals(localBesttm));
						if (first22)
							first22 = false;
						makeTabu = true;
					}
				}
				if (unPatAlgo1.size()==0) continue;
				int unPat = unPatAlgo1.get(unPatAlgo1.size()-1);
				double newDist = Haversine.haversine(m.doctors[i][0], m.doctors[i][1], m.patients[unPat][0],
						m.patients[unPat][1]); 
				if ((newDist-olddist_i)<0 && (newDist-olddist_i)<difference){
					//System.out.println("Algo 1 new code if");
					difference = newDist-olddist_i;
					double currbestdist_dupl = currentBestDist + newDist - olddist_i;
					localBesttm = DeepClone.deepClone(currentBesttm);
					localBestDist = currbestdist_dupl;
					(localBesttm.get(i)).remove(olddist_i);
					(localBesttm.get(i)).put(newDist, unPat);
					unPatAlgo1.remove(unPatAlgo1.size()-1);
					unPatAlgo1.add(pat1);
				}
				else if ((newDist-olddist_i) < difference2){
					//System.out.println("Algo 1 new code else");
					difference2 = newDist-olddist_i;
					double currbestdist_dupl = currentBestDist + newDist - olddist_i;
					localBesttm = DeepClone.deepClone(currentBesttm);
					localBestDist = currbestdist_dupl;
					(localBesttm.get(i)).remove(olddist_i);
					(localBesttm.get(i)).put(newDist, unPat);
					unPatAlgo1.remove(unPatAlgo1.size()-1);
					unPatAlgo1.add(pat1);
				}
			}
			// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>
			// (localBesttm);
			if (makeTabu) {
				tabulist.put(pat1, 17);
				tabulist.put(pat2, 17);
			}
			currentBesttm = DeepClone.deepClone(localBesttm);
			currentBestDist = localBestDist;
			if (currentBestDist < globalBestDist) {
				globalBesttm = DeepClone.deepClone(currentBesttm);
				//System.out.println("globalBestDist changed: " + globalBestDist + " to currbestdist: " + currentBestDist);
				globalBestDist = currentBestDist;
			}
			double tabudist = 0;
			/*for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : currentBesttm.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				// System.out.println("Patients asssigned to Doctor: " + key);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					// System.out.println("Patient number :" + key2 +
					// "Distance: " + value2 + " KMS");
					tabudist += value2;
				}
			}*/
			//System.out.println("Result befor search: " + (int) tabudist + "KM");
			//System.out.println("Next search");
			// currentBesttm = localBesttm;

			// System.out.println("localBestDist: "+localBestDist);
		}
		System.out.println("Tabu search results: " + (int) globalBestDist + " KM");
		return globalBesttm;
	}

	public static TreeMap<Integer, TreeMap<Double, Integer>> tabuSearch2(TreeMap<Integer, TreeMap<Double, Integer>> tm,
			MedMap m) {
		// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>> (tm);
		double difference = 0;
		double difference2 = 0;
		currentBesttm = DeepClone.deepClone(tm);
		// currentBesttm = tm;
		// localBestDist = Double.MAX_VALUE;
		HashMap<Integer, Integer> tabulist = new HashMap<Integer, Integer>();
		// System.out.println("Tabu search starts, " + currentBesttm.size() + "
		// " + tm.size());
		for (int iter = 0; iter < 700; iter++) {
			boolean b = true;
			boolean first1 = true;
			boolean first2 = true;
			boolean first11 = false;
			boolean first22 = false;
			int pat1 = 0;
			int pat2 = 0;
			boolean makeTabu = false;
			// System.out.println("Entered for block");
			for (int i = 0; i < currentBesttm.size() - 1; i++) {
				if (m.docCap[i] == 0)
					continue;
				boolean flag_i = false;
				double olddist_i;

				boolean isTabu1 = false;
				TreeMap<Double, Integer> locpattm1 = currentBesttm.get(i);
				try{
					olddist_i = locpattm1.lastKey();
				}catch(Exception ex){
					continue;
				}
				pat1 = locpattm1.get(olddist_i);
				if (olddist_i > 1000)
					System.out.println("First i :" + olddist_i + " Patient number:" + pat1);
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
				if (flag_i) {
					//System.out.println("First continue");

					if (!tabulist.isEmpty()) {// initial tabu operations
						// System.out.println("inside if");
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

					continue;
				}

				for (int j = i + 1; j <= currentBesttm.size() - 1; j++) {
					boolean flag_j = false;
					if (m.docCap[j] == 0)
						continue;
					// System.out.println("Entered for block i=" + i + " j=" +
					// j);

					double olddist_j=0;

					boolean isTabu2 = false;

					TreeMap<Double, Integer> locpattm2 = currentBesttm.get(j);
					
					try{
						olddist_j = locpattm2.lastKey();
					}
					catch(Exception ex){
						//System.out.println("Doc num: "+j+" DocCap: "+m.docCap[j]);
						continue;
					}
					

					pat2 = locpattm2.get(olddist_j);

					if (olddist_j > 1000)
						System.out.println("First j :" + olddist_j + " Patient number" + pat2);

					if (tabulist.containsKey(pat2))
						isTabu2 = true; // should not consider pat 2 for swap

					while (isTabu2) {// finds the next tabu-free pat2 to swap
						// System.out.println("Entered while tabu1 block");
						try {
							olddist_j = locpattm2.lowerKey(olddist_j);
							pat2 = locpattm2.get(olddist_j);
							if (!tabulist.containsKey(pat2)) {
								if (olddist_j > 1000)
									System.out.println("Second j :" + olddist_j);
								break;
							}
						} catch (Exception ex) {
							flag_j = true;
							break;
						}
					}
					if (flag_j) {
						//System.out.println("Second Continue");

						if (!tabulist.isEmpty()) {// initial tabu operations
							//System.out.println("inside if");
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
							 * tabulist.entrySet()) { int value =
							 * entry2.getValue(); int key = entry2.getKey();
							 * value--; if (value == 0){ System.out.println(
							 * "Entered if"); tabulist.remove(key); } else{
							 * System.out.println( "Entered else");
							 * tabulist.put(key, value); } }
							 */
						}

						continue;
					}

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

					// System.out.println("Cuur dist:" + currentBestDist
					// +"Global dist:" + globalBestDist);
					if ((newdist_i + newdist_j) - (olddist_i + olddist_j) < 0) {

						// System.out.println("new dist: i: " + i + ", j: " + j+
						// " " + (newdist_i + newdist_j) +
						// " olddist:" +olddist_i + " " + olddist_j +
						// " Difference: " +
						// ((newdist_i + newdist_j) - (olddist_i + olddist_j)));

						if (first1) {
							difference = (newdist_i + newdist_j) - (olddist_i + olddist_j);
							first1 = false;
							first11 = true;
						}
					} else if (first2) {
						difference2 = (newdist_i + newdist_j) - (olddist_i + olddist_j);
						first2 = false;
						first22 = true;
					}
					if ((((newdist_i + newdist_j) - (olddist_i + olddist_j)) < difference) || first11) {

						//System.out.println("Old diffetene: "+difference);

						difference = (newdist_i + newdist_j) - (olddist_i + olddist_j);

						//System.out.println("New diffetene: "+difference);

						// System.out.println("First Swapped"+" i: "+i+"
						// j:"+j+"pat1: "+pat1+
						// " pat2: "+pat2+" old dist i:"+olddist_i+
						// " old dist j: "+olddist_j);

						double currbestdist_dupl = currentBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);

						// System.out.println(currentBestDist);
						// globalBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (currentBesttm);
						// globalBesttm = DeepClone.deepClone(currentBesttm);
						// globalBesttm = currentBesttm;
						localBesttm = DeepClone.deepClone(currentBesttm);
						// System.out.println("localBestDist: "+localBestDist+"
						// currbestdist: "+currbestdist_dupl);
						localBestDist = currbestdist_dupl;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);
						// System.out.println("Map
						// equality:"+currentBesttm.equals(globalBesttm));"+
						// "make pat1 and pat2 tabu now
						// localBestDist = globalBestDist;
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>> (globalBesttm);
						// localBesttm =new TreeMap<Integer,
						// TreeMap<Double,Integer>> (globalBesttm);
						// localBesttm = DeepClone.deepClone(globalBesttm);
						b = false;
						if (first11)
							first11 = false;
						makeTabu = true;
					} else if (b && (((newdist_i + newdist_j) - (olddist_i + olddist_j) < difference2) || first22)) {
						//System.out.println("Old Difference: "+ difference2);
						difference2 = (newdist_i + newdist_j) - (olddist_i + olddist_j);
						//System.out.println("Old Difference: "+ difference2);
						// System.out.println("Second Swapped"+" i: "+i+"j:"+j+
						// " pat1: "+pat1+" pat2: "+pat2+" old dist
						// i:"+olddist_i+
						// " old sit j: "+olddist_j);

						double currbestdist_dupl = currentBestDist - (olddist_i + olddist_j) + (newdist_i + newdist_j);
						// localBesttm = (TreeMap<Integer, TreeMap<Double,
						// Integer>>)currentBesttm.clone();
						// localBesttm = new TreeMap<Integer, TreeMap<Double,
						// Integer>>(currentBesttm);
						localBesttm = DeepClone.deepClone(currentBesttm);
						//System.out.println("localBestDist: "+localBestDist+"currbestdist: "+currbestdist_dupl);
						localBestDist = currbestdist_dupl;
						(localBesttm.get(i)).remove(olddist_i);
						(localBesttm.get(i)).put(newdist_i, pat2);
						(localBesttm.get(j)).remove(olddist_j);
						(localBesttm.get(j)).put(newdist_j, pat1);

						// (localBesttm.get(i)).put(23456.64326, pat2);
						// make pat1 and pat2 tabu now
						// System.out.println("Map equality:
						// "+currentBesttm.equals(localBesttm));
						if (first22)
							first22 = false;
						makeTabu = true;
					}
				}
			}
			// currentBesttm = new TreeMap<Integer, TreeMap<Double, Integer>>
			// (localBesttm);
			if (makeTabu) {
				tabulist.put(pat1, 17);
				tabulist.put(pat2, 17);
			}
			currentBesttm = DeepClone.deepClone(localBesttm);
			currentBestDist = localBestDist;
			if (currentBestDist < globalBestDist) {
				globalBesttm = DeepClone.deepClone(currentBesttm);
				//System.out.println("globalBestDist changed: " + globalBestDist + " to currbestdist: " + currentBestDist);
				globalBestDist = currentBestDist;
			}
			double tabudist = 0;
			/*for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : currentBesttm.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				// System.out.println("Patients asssigned to Doctor: " + key);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					// System.out.println("Patient number :" + key2 +
					// "Distance: " + value2 + " KMS");
					tabudist += value2;
				}
			}*/
			//System.out.println("Result befor search: " + (int) tabudist + "KM");
			//System.out.println("Next search");
			// currentBesttm = localBesttm;

			// System.out.println("localBestDist: "+localBestDist);
		}
		System.out.println("Tabu search results: " + (int) globalBestDist + " KM");
		return globalBesttm;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MedMap m = new MedMap();

		m.circleGenerator(4, 6, 3);

		/*try {
			out = new PrintStream(new FileOutputStream("C://Users/Srikiran Sistla/Desktop/output.txt"));
			System.setOut(out);
		} catch (Exception e) {
			System.out.println(e);
		}*/

		System.out.println("\nAlgorithm 1 ");
		TreeMap<Integer, TreeMap<Double, Integer>> map = new TreeMap<Integer, TreeMap<Double, Integer>>();
		TreeMap<Integer, TreeMap<Double, Integer>> algo3tm = new TreeMap<Integer, TreeMap<Double, Integer>>();
		TreeMap<Integer, TreeMap<Double, Integer>> algo4tm = new TreeMap<Integer, TreeMap<Double, Integer>>();
		for (int i = 0; i < m.doctorcount; i++) {
			docCount.add(i);
		}
		for (int i = 0; i < m.patientcount; i++) {
			patCount.add(i);
		}

		for (int k = 0; k < 1; k++) {
			double algo1dist = 0;
			map = algo1(m);
			// out1.println("Final results of algo 1:");
			/*
			 * int matrix[][] = new int[m.doctorcount][100]; int matrixCount[] =
			 * new int[m.doctorcount]; int c = 0; for (Map.Entry<Integer,
			 * Integer> entry : map.entrySet()) { int key = entry.getKey(); int
			 * value = entry.getValue(); // out1.println(key + " => " + value +
			 * " Doctor Capacity: " + // m.docCap[value]);
			 * matrix[value][matrixCount[value]++] = key; } for (int i = 0; i <
			 * m.doctorcount; i++) { // System.out.println(
			 * "Patients assigned to doctor: " + i + " // with capacity: " +
			 * m.docCap[i] // + " Co-ordinates" + m.doctors[i][0] + " " +
			 * m.doctors[i][1]); for (int j = 0; j < matrixCount[i]; j++) { int
			 * d = matrix[i][j]; if (d == 0) c++; if (d == 0 && c >= 2) break;
			 * // System.out.println(matrix[i][j] + " Distance: " + // dist[d]);
			 * algo1Dist_loc += dist[d]; } }
			 */

			for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : map.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				//System.out.println("Patients asssigned to Doctor: " + key);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					//System.out.println("Patient number :" + key2 +
					//" Distance: " + value2 + " KMS");
					algo1dist += value2;
				}
			}

			System.out.println("Total distance in run " + k + ": " + (int) algo1Dist + " KM");
			
			tabuSearch1(map,m);
			
			double percentage = ((algo1Dist - globalBestDist) / algo1Dist) * 100;
			System.out.println("Percentage optimized: " + percentage + " % \n");
			
			tabuSearch2(map,m);
			
			percentage = ((algo1Dist - globalBestDist) / algo1Dist) * 100;
			System.out.println("Percentage optimized: " + percentage + " % \n");
			
		}

		System.out.println("\nAlgorithm 2 ");
		for (int i = 0; i < 1; i++) {
			map=algo2(m);
			for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : map.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				//System.out.println("Patients asssigned to Doctor: " + key+" DocCap: "+m.docCap[key]);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					//System.out.println("Patient number :" + key2 +
					//"Distance: " + value2 + " KMS");
					//algo3dist += value2;
				}
			}
			System.out.println("Total distance in run " + i + ": " + (int) algo2Dist + " KM");
			tabuSearch2(map,m);
			double percentage = ((algo2Dist - globalBestDist) / algo2Dist) * 100;
			System.out.println("Percentage optimized: " + percentage + " % \n");
		}
		System.out.println("\nAlgorithm 3 ");
		for (int i = 0; i < 6; i++) {
			algo3tm = algo3(m);
			double algo3dist = 0;
			double algo4dist = 0;
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
					// System.out.println("Patient number :" + key2 +
					// "Distance: " + value2 + " KMS");
					algo3dist += value2;
				}
			}
			System.out.println("Total distance in run " + i + ": " + (int) algo3dist + " KM");
			// tabuSearch(algo3tm, m);
			algo4tm = tabuSearch2(algo3tm, m);
			for (Map.Entry<Integer, TreeMap<Double, Integer>> entry : algo4tm.entrySet()) {
				int key = entry.getKey();
				TreeMap<Double, Integer> value = entry.getValue();
				// out1.println(key + " => " + value + " Doctor Capacity: "
				// +m.docCap[value]);
				// System.out.println("Patients asssigned to Doctor: " + key);
				for (Map.Entry<Double, Integer> entry2 : value.entrySet()) {
					int key2 = entry2.getValue();
					double value2 = entry2.getKey();
					// out.println(key + " => " + value);
					// System.out.println("Patient number :" + key2 +
					// "Distance: " + value2 + " KMS");
					algo4dist += value2;
				}
			}
			double percentage = ((algo3dist - globalBestDist) / algo3dist) * 100;
			System.out.println("Percentage optimized: " + percentage + " % \n");
			//System.out.println("Total distance in run " + i + ": " + (int) algo3dist + " KM");
		}
		System.out.println("\nProgram Terminated!");
	}

}
