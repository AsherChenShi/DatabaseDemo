import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Compile the code with:
 *
 * javac MinHashingExercise.java
 *
 * To execute the program you then have to use:
 *
 * java MinHashingExercise <File1> <File2>
 *
 * For the exercise write the code of the methods jaccard, similaritykHash, and
 * similaritykValues.
 *
 */
public class MinHashingExercise {

    public static void main(String... args) throws IOException {
        System.out.println("Min Hashing exercise:");
        System.out.println("============================================");
        if (args.length < 2) {
            System.out.println("Error: Two files required as input!");
            System.exit(-1);
        }
        List<String> file1 = readFile(args[0]);
        List<String> file2 = readFile(args[1]);

        System.out.println("============================================");
        System.out.println("Calculating Jaccard similarity:");
        System.out.println(jaccard(file1, file2));
        System.out.println("============================================");
        for (int i = 1; i <= 6; i++) {
            System.out.println("Calculating similarity for k = " + i + " hash functions:");
            System.out.println(similaritykHash(i, file1, file2));
        }
        System.out.println("============================================");
        for (int i = 1; i <= 6; i++) {
            System.out.println("Calculating similarity for k = " + i + " hash values:");
            System.out.println(similaritykValues(i, file1, file2));
        }
        System.out.println("============================================");
    }

    private static double jaccard(List<String> lhs, List<String> rhs) {
    	ArrayList<String> a = new ArrayList<>();
    	ArrayList<String> b = new ArrayList<>();
		List<String> union = new ArrayList<>();
		List<String> intersection = new ArrayList<>();
		a.addAll(lhs);
		b.addAll(rhs);
		intersection.addAll(rhs);
		union.addAll(lhs);
		intersection.retainAll(a); //store the intersection of this two list
		union.removeAll(b);
		b.addAll(union); //store the union string but not duplicate
		System.out.println(intersection.size());
		double jaccard_coefficient = (double)intersection.size() / b.size();
        return jaccard_coefficient;
    }

    private static double similaritykHash(int k, List<String> lhs, List<String> rhs) {
    	List<String> shingles = new ArrayList<>();  //create a list of all words
    	List<String> a = new ArrayList<>();
    	shingles.addAll(lhs);
    	a.addAll(rhs);
    	a.removeAll(lhs);
    	shingles.addAll(a);
    	
    	List<HashMap<String,Integer>> hashvalues = new ArrayList<>();//create a 2 dimensional List to store the hash value from different hash function
    	for(int i=0; i<k-1;i++) {
    		HashMap<String,Integer> map = new HashMap<String,Integer>();
    		for(int j=0; j<shingles.size(); j++) {
    			int hashvalue = hash(i,shingles.get(j));    	//the i-th hash function		
    			map.put(shingles.get(j), hashvalue);
    		}
    		hashvalues.add(map);
    	}
    	
    	List<Integer> minLhsList = new ArrayList<>();  //create two lists to store the minimum hash values from each hash function
    	List<Integer> minRhsList = new ArrayList<>();
    	for(int i=0; i<k-1;i++) {
    		int min = hashvalues.get(i).get(lhs.get(0));
    		for(int j=0; j<lhs.size(); j++) {
    			if(min > hashvalues.get(i).get(lhs.get(j)));{
    				min = hashvalues.get(i).get(lhs.get(j));
    			}
    		}
    		minLhsList.add(min);
    	}

    	for(int i=0; i<k-1;i++) {
    		int min = hashvalues.get(i).get(rhs.get(0));
    		for(int j=0; j<rhs.size(); j++) {
    			if(min > hashvalues.get(i).get(rhs.get(j)));{
    				min = hashvalues.get(i).get(rhs.get(j));
    			}
    		}
    		minRhsList.add(min);
    	}
    	
    	int equalsNum = 0;
    	for(int i=0;i<k-1;i++) {
    		if(minLhsList.get(i)==minRhsList.get(i)) {
    			equalsNum++;
    		}
    	}
    	
    	double similarity = (double)equalsNum / k;
        return similarity;
    }

    private static double similaritykValues(int k, List<String> lhs, List<String> rhs) {
    	List<String> shingles = new ArrayList<>();  //create a list of all words
    	List<String> a = new ArrayList<>();
    	shingles.addAll(lhs);
    	a.addAll(rhs);
    	a.removeAll(lhs);
    	shingles.addAll(a);
    	
    	HashMap<String,Integer> hashvalues = new HashMap<>();//create a hashMap to store the hash value from different hash function
    	for(int j=0; j<shingles.size(); j++) {
    		int hashvalue = hash(1,shingles.get(j)); 
    		hashvalues.put(shingles.get(j), hashvalue);
    	}
 
    	
    	List<List<Integer>> minHashValues = new ArrayList<>();
        HashMap<String,Integer> lhsHashValues = new HashMap<>();
        HashMap<String,Integer> rhsHashValues = new HashMap<>();
    	for(int i=0; i<lhs.size(); i++) {
    		lhsHashValues.put(lhs.get(i),hashvalues.get(lhs.get(i)));
    	}
    	for(int i=0; i<rhs.size(); i++) {
    		rhsHashValues.put(rhs.get(i),hashvalues.get(rhs.get(i)));
    	}
    	List<Map.Entry<String, Integer> > lhsSortedList = 
                new LinkedList<Map.Entry<String, Integer>>(lhsHashValues.entrySet());
    	// Sort the list 
        Collections.sort(lhsSortedList, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        // put the k min values from sorted list to hashmap  
        HashMap<String, Integer> tempLhs = new LinkedHashMap<String, Integer>();
        int num1 = 0;
        for (Map.Entry<String, Integer> aa : lhsSortedList) {
        	if(num1<k-1) {
        		num1++;
                tempLhs.put(aa.getKey(), aa.getValue()); 
        	}
        		
        }
        
        
        List<Map.Entry<String, Integer> > rhsSortedList = 
                new LinkedList<Map.Entry<String, Integer>>(rhsHashValues.entrySet());
    	// Sort the list 
        Collections.sort(rhsSortedList, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        // put the k min values from sorted list to hashmap   
        HashMap<String, Integer> tempRhs = new LinkedHashMap<String, Integer>(); 
        int num2 = 0;
        for (Map.Entry<String, Integer> aa : rhsSortedList) { 
        	if(num2<k-1) {
        		num2++;
        		tempRhs.put(aa.getKey(), aa.getValue()); 
        	}
        	
        } 
    	
    	int equalsNum = 0;
    	for (Entry<String, Integer> entry : tempLhs.entrySet()) {
    		if(tempRhs.containsKey(entry.getKey())) {
    			if(tempRhs.get(entry.getKey())==entry.getValue())
    				equalsNum++;
    		}
    	}
    	
    	
    	double similarity = (double)equalsNum / k;
        return similarity;
    }

    /**
     * Reads a file and returns the words in it as a List
     * 
     * @param file the path to the file to read
     * @return A list of words contained in the file.
     * @throws IOException
     */
    private static List<String> readFile(String file) throws IOException {
        System.out.println("Reading file: " + file);
        String contents = new String(Files.readAllBytes(Paths.get(file)));
        System.out.println(contents);
        return Arrays.asList(contents.split(" "));
    }

    /**
     * Calculates the k-th hash value of str
     * 
     * @param k   The index of the hash function in [0,5]
     * @param str The string to hash
     * @return The k-th hash value of str
     */
    static int hash(int k, String str) {
        int hash = str.hashCode();
        switch (k) {
        case 0:
            return hash % 2012;
        case 1:
            return hash % 1024;
        case 2:
            return hash % 4273;
        case 3:
            return hash % 582;
        case 4:
            return hash % 8362;
        case 5:
            return hash % 2743;
        default:
            return -1;
        }
    }
}
