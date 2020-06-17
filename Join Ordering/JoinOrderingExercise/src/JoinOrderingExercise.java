

import java.io.File;
import java.io.IOException;
import java.util.*;


public class JoinOrderingExercise {

    /**
     * Compile the code with:
     *
     *     javac JoinOrderingExercise.java
     *
     * To execute the program you then have to use:
     *
     *     java JoinOrderingExercise data.txt
     *
     * For the exercise write the code of the methods computeGreedy1-3, computeBestPlan, and computeWorstPlan.
     *
     */
    public static void main(String... args) throws IOException {
        System.out.println("Join order exercise:");
        System.out.println("============================================");

        if(args.length == 0) {
            System.out.println("Missing file argument!");
            return;
        }

        File input = new File(args[0]);
        //Read config file
        DummyFileDirectory relations = new DummyFileDirectory(input);
        System.out.println("============================================");

        JoinTree plan = computeGreedy1(relations);
        System.out.println("Greedy 1: \t" + plan.toString() + "\tCost: " + plan.cost(relations));
        plan = computeGreedy2(relations);
        System.out.println("Greedy 2: \t" + plan.toString() + "\tCost: " + plan.cost(relations));
        plan = computeGreedy3(relations);
        System.out.println("Greedy 3: \t" + plan.toString() + "\tCost: " + plan.cost(relations));
        plan = computeBestPlan(relations);
        System.out.println("Best: \t\t" + plan.toString() + "\tCost: " + plan.cost(relations));
        plan = computeWorstPlan(relations);
        System.out.println("Worst: \t\t" + plan.toString() + "\tCost: " + plan.cost(relations));
    }



    /*   
    * The main components of this boilerplate code are the classes 
    *
    *   IRelationDirectory :        Interface representing a connection to a (mock) database, which provides metadata for relations.
    *       DummyFileDirectory :    This class uses a file to read relation information
    *   The function IRelationDirectory.getRelations() returns a list of all relations with their cardinalities.
    *   IRelationDirectory.getSelectivity(String a,String a,String pred) returns the selectivity between the relations a and b. (pred is unused and can be ignores)
    *
    *   JoinTree :      Interface representing a join tree with cost/cardinality calculation functions
    *       Join :      Represents a node in the join tree
    *       Relation :  Represents a leaf in the join tree
    *   You can create/modify a JoinTree by using the constructors:
    *       Relation(String name) : Creates a leaf node with the given relation name
    *       Join(JoinTree left, JoinTree right): Creates a node combining two JoinTrees
    *   The functions JoinTree.cost(IRelationDirectory) and JoinTree.cardinality(IRelationDirectory) 
    *   Can be used to calculate the cost and cardinality of a given JoinTree
    *
    * 
    *   There are additional useful functions, which should be understandable by reading the comments.
    */

    static JoinTree computeGreedy1(IRelationDirectory directory) { 
    	List<Pair<String, Integer>> RelationList = new ArrayList<>();
    	List<Pair<String, Integer>> relations = directory.getRelations();
        while(!relations.isEmpty()) {
        	Pair<String, Integer> min = null;
        	for (Pair<String, Integer> entry : relations) {
        	    if (min == null || min.getValue() > entry.getValue()) {
        	        min = entry;
        	    }
        	}
        	for (int i=0; i<relations.size(); i++) {//eliminate minimum relation 
        		if(min==relations.get(i)) {
        			relations.remove(i);
        			
        		}       		
        	}
        	RelationList.add(min);		        	
        }
        List<JoinTree> JoinTreeList = new ArrayList<>();
        for(int i=0; i<RelationList.size(); i++) {//generate the join tree
        	if(i==0) {
        		Relation a = new Relation(RelationList.get(i).getKey());
            	Relation b = new Relation(RelationList.get(i+1).getKey());
            	JoinTreeList.add(new Join(a,b));
        	}else if(i==1) {
        		i++;
        	}else {
        		Relation a = new Relation(RelationList.get(i).getKey());
        		JoinTreeList.add(new Join(JoinTreeList.get(i-2),a));
        	}
        	
        }
        JoinTree LeftJoinTree = JoinTreeList.get(JoinTreeList.size());
        return LeftJoinTree;

    }

    static JoinTree computeGreedy2(IRelationDirectory directory) {
    	List<Pair<String, Integer>> RelationSequence = new ArrayList<>();
    	List<Pair<String, Integer>> relations = directory.getRelations();
        while(!relations.isEmpty()) {
        	double min = 0;
        	double Cardinality = 0;
        	Pair<String, Integer> k = null;
        	for (Pair<String, Integer> entry : relations) {
        	    if (min == 0 || RelationSequence.size()==0 || min> entry.getValue()) {
        	    	k = entry;
        	    	min = relations.get(0).getValue();
        	    	continue;
        	    }else if(RelationSequence.size()==1) {
    	    		Relation a = new Relation(RelationSequence.get(0).getKey());
    	    		Relation b = new Relation(entry.getKey());
    	    		Join intermediateResult = new Join(a,b);
    	    		Cardinality = intermediateResult.cardinality(directory);
    	    		if(min>Cardinality) {
    	    			min = Cardinality;
    	    			k = entry;
    	    			Join intermediateSequence = intermediateResult;
    	    			continue;
    	    		}		
    	    	}else {
    	    		Relation Rk = new Relation(entry.getKey());
    	    		Join temp = new Join(intermediateSequence,Rk);
    	    		min = Cardinality;
	    			k = entry;
    	    	}
        	    	
        	    }
        	}
        	relations.remove(k);//eliminate Rk from R 
        	RelationSequence.add(k); //append Rk to S
        	List<JoinTree> JoinTreeList = new ArrayList<>();
        	for(int i=0; i<RelationSequence.size(); i++) {//generate the join tree
            	if(i==0) {
            		Relation a = new Relation(RelationSequence.get(i).getKey());
                	Relation b = new Relation(RelationSequence.get(i+1).getKey());
                	JoinTreeList.add(new Join(a,b));
            	}else if(i==1) {
            		i++;
            	}else {
            		Relation a = new Relation(RelationSequence.get(i).getKey());
            		JoinTreeList.add(new Join(JoinTreeList.get(i-2),a));
            	}
            	
            }
            JoinTree LeftJoinTree = JoinTreeList.get(JoinTreeList.size());
            return LeftJoinTree;
        }	
    

    static JoinTree computeGreedy3(IRelationDirectory directory) {
    	List<Pair<String, Integer>> RelationSequence = new ArrayList<>();
    	List<Pair<String, Integer>> relations = directory.getRelations();
        while(!relations.isEmpty()) {
        	double min = 0;
        	double Cardinality = 0;
        	Pair<String, Integer> k = null;
        	for (Pair<String, Integer> entry : relations) {
        	    if (min == 0 || RelationSequence.size()==0 || min> entry.getValue()) {
        	    	k = entry;
        	    	min = relations.get(0).getValue();
        	    	continue;
        	    }else if(RelationSequence.size()==1) {
    	    		Relation a = new Relation(RelationSequence.get(0).getKey());
    	    		Relation b = new Relation(entry.getKey());
    	    		Join intermediateResult = new Join(a,b);
    	    		Cardinality = intermediateResult.getSelectivity(directory);
    	    		if(min>Cardinality) {
    	    			min = Cardinality;
    	    			k = entry;
    	    			Join intermediateSequence = intermediateResult;
    	    			continue;
    	    		}		
    	    	}else {
    	    		Relation Rk = new Relation(entry.getKey());
    	    		Join temp = new Join(intermediateSequence,Rk);
    	    		min = Cardinality;
	    			k = entry;
    	    	}
        	    	
        	    }
        	}
        	relations.remove(k);//eliminate Rk from R 
        	RelationSequence.add(k); //append Rk to S
        	List<JoinTree> JoinTreeList = new ArrayList<>();
        	for(int i=0; i<RelationSequence.size(); i++) {//generate the join tree
            	if(i==0) {
            		Relation a = new Relation(RelationSequence.get(i).getKey());
                	Relation b = new Relation(RelationSequence.get(i+1).getKey());
                	JoinTreeList.add(new Join(a,b));
            	}else if(i==1) {
            		i++;
            	}else {
            		Relation a = new Relation(RelationSequence.get(i).getKey());
            		JoinTreeList.add(new Join(JoinTreeList.get(i-2),a));
            	}
            	
            }
            JoinTree LeftJoinTree = JoinTreeList.get(JoinTreeList.size());
            return LeftJoinTree;
    }

    static JoinTree computeBestPlan(IRelationDirectory directory) {

        ///////////////
        // Your code //
        ///////////////

        return null;
    }

    static JoinTree computeWorstPlan(IRelationDirectory directory) {

        ///////////////
        // Your code //
        ///////////////

        return null;
    }

    private static List<JoinTree> getAllPlans(IRelationDirectory directory){
        List<JoinTree> allPlans = getAllPlans(directory, directory.getRelations().size());
        List<JoinTree> ret = new ArrayList<>();
        for (JoinTree plan : allPlans) {
            if (plan.getRelations().size() == directory.getRelations().size()){
                ret.add(plan);
            }
        }
        return ret;
    }
    private static List<JoinTree> getAllPlans(IRelationDirectory directory, int i) {

        if (i == 1){
            List<Pair<String, Integer>> relations = directory.getRelations();
            List<JoinTree> ret = new ArrayList<>();
            for (Pair<String, Integer> relation : relations) {
                ret.add(new Relation(relation.getKey()));
            }
            return ret;
        }
        List<JoinTree> plans = getAllPlans(directory, i - 1);
        List<JoinTree> combinedPlans = new ArrayList<>();
        for (JoinTree left : plans) {
            for (JoinTree right : plans) {
                if(!left.shareRelations(right)){
                    Join join = new Join(left, right);
                    if(!join.isCrossProduct(directory)){
                        combinedPlans.add(join);
                    }
                }
            }
        }
        plans.addAll(combinedPlans);
        return plans;
    }


}

/**
 * An interface representing a query plan
 */
interface JoinTree {
    /**
     * Returns the C_out costs of the query plan
     * @param directory A relation directory for selectivity and cardinality lookup.
     * @return The C_out costs of the query plan
     */
    int cost(IRelationDirectory directory);

    /**
     * Returns the cardinality of the query plan
     * @param directory A relation directory for selectivity and cardinality lookup.
     * @return The cardinality of the query plan
     */
    int cardinality(IRelationDirectory directory);

    /**
     * Returns a list of all contained relations
     * @return list of all contained relations
     */
    List<String> getRelations();

    /**
     * Checks if a given relation is contained within the query plan
     * @param relation The name of the relation
     * @return True if the relation is contained within the plan, false if not
     */
    boolean contains(String relation);

    /**
     * Check whether both plans share any relations.
     * @param plan The plan to check against
     * @return True if both plans share relations, false if not
     */
    boolean shareRelations(JoinTree plan);

}


/**
 * The Join class represents the inner nodes of the join tree
 */
class Join implements JoinTree {
    private JoinTree left;
    private JoinTree right;

    /**
     * Creates a join between two sub join trees
     * @param left Left join tree 
     * @param right Right join tree
     */
    public Join(JoinTree left, JoinTree right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int cost(IRelationDirectory directory) {
        return cardinality(directory)+left.cost(directory)+right.cost(directory);
    }

    @Override
    public int cardinality(IRelationDirectory directory) {
        double factor = getSelectivityProduct(directory);

        return Math.toIntExact(Math.round(factor * left.cardinality(directory) * right.cardinality(directory)));
    }


    @Override
    public String toString() {
          return "(" + left.toString() + " \u2A1D " + right.toString() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public List<String> getRelations() {
        List<String> relations = left.getRelations();
        relations.addAll(right.getRelations());
        return relations;
    }

    @Override
    public boolean contains(String relation) {
        return left.contains(relation) || right.contains(relation);
    }

    @Override
    public boolean shareRelations(JoinTree plan) {
        return left.shareRelations(plan) || right.shareRelations(plan);
    }

    /**
     * This function checks whether a cross product has to be computed to perform this join
     * @param directory A directory for relational metadata lookup
     * @return True if a cross product is computed, false if not
     */
    public boolean isCrossProduct(IRelationDirectory directory){
        double factor = getSelectivityProduct(directory);
        return factor == 1;
    }


    /**
     * Returns the selectivity product of this join tree
     * @param directory A directory for relational metadata lookup
     * @return A factor in [0,1]
     */
    private double getSelectivityProduct(IRelationDirectory directory) {
        double factor = 1;
        List<String> leftRelations = left.getRelations();
        List<String> rightRelations = right.getRelations();
        for (String l : leftRelations) {
            for (String r : rightRelations) {
                factor *= directory.getSelectivity(l,r,"");
            }
        }
        return factor;
    }
}

/**
 * This class represents the leafs of the join tree (single relations)
 */
class Relation implements JoinTree {
    private String relation;

    /**
     * Creates a leaf node with the given relation name
     * @param relation Relation name to use
     */
    public Relation(String relation) {
        this.relation = relation;
     }

    @Override
    public int cost(IRelationDirectory directory) {
        return 0;
    }

    @Override
    public int cardinality(IRelationDirectory directory) {
        return directory.getSize(relation);
    }

    @Override
    public String toString() {
        return relation;
    }

    @Override
    public int hashCode() {
        return relation.hashCode();
    }

    @Override
    public List<String> getRelations() {
        ArrayList<String> list = new ArrayList<>();
        list.add(relation);
        return list;
    }

    @Override
    public boolean contains(String relation) {
        return relation.equals(this.relation);
    }

    @Override
    public boolean shareRelations(JoinTree plan) {
        return plan.getRelations().contains(relation);
    }
}

/**
 * This interface is used to retrieve metadata (size/selectivity/...) of relations.
 */
interface IRelationDirectory {

    /**
     * Retrieves all relations with their sizes
     * @return List of pairs containing relations and their sizes
     */
    List<Pair<String,Integer>> getRelations();

    /**
     * Returns the size of the given relation
     * @param relation name of the relation
     * @return Size of the relation
     */
    int getSize(String relation) throws NullPointerException;

    /**
     * Returns the selectivity of the given join
     * @param relationA First relation to check
     * @param relationB Second relation to check
     * @param predicate The join predicate
     * @return The selectivity of the join
     */
    double getSelectivity(String relationA, String relationB, String predicate) throws NullPointerException;

    /**
     * Returns the size of the given join.
     * @param relationA First relation to check
     * @param relationB Second relation to check
     * @param predicate The join predicate
     * @return Size of the join
     */
    int getJoinSize(String relationA, String relationB, String predicate) throws NullPointerException;
}

/**
 * The DummyFileDirectory uses a local file to read mock-metadata.
 * The file has to the following syntax:
 *
 *      Relation1   [Size]
 *              ...
 *      RelationN   [Size]
 *      Relation_i  Relation_j  [Selectivity]
 *              ...
 *
 */
class DummyFileDirectory implements IRelationDirectory {
    private HashMap<String,Integer> relations;
    private HashMap<Pair<String,String>,Double> selectivities;


    public DummyFileDirectory(File file) throws IOException{
        if (!file.exists()) throw new IOException("File does not exist");
        System.out.println("Reading file \""+file.getPath()+"\" ...");
        relations = new HashMap<>();
        selectivities = new HashMap<>();

        // Create a File scanner
        Scanner sc = new Scanner(file).useDelimiter("\n");

        int i = 0;
        while (sc.hasNextLine()){ //Read each line
            String line = sc.nextLine();
            String[] split = line.split("\\s+");
            if (split.length == 2){ //If the line consists of two parts: Read relations and size
                try{
                    relations.put(split[0],Integer.parseInt(split[1]));
                    System.out.println("Added relation \""+split[0]+"\" with size " + Integer.parseInt(split[1]));
                }catch (NumberFormatException e){
                    throw new IOException("Expected number but got string in line " + i);
                }
            }else if (split.length == 3){//If the line consists of three parts: Read relations and selectivity
                try{
                    selectivities.put(new Pair<>(split[0],split[1]),Double.parseDouble(split[2]));
                    selectivities.put(new Pair<>(split[1],split[0]),Double.parseDouble(split[2]));
                    System.out.println("Added selectivity of "+Double.parseDouble(split[2])+" between \"" + split[0] +
                            "\" and \"" + split[1] + "\"");
                }catch (NumberFormatException e){
                    throw new IOException("Expected number but got string in line " + i);
                }
            }else throw new IOException("Wrong syntax in line " + i);

            i++;
        }

    }

    @Override
    public int getSize(String relation) {
        if(!relations.containsKey(relation)) throw new NullPointerException("Relation \"" + relation +"\" not found.");
        return relations.get(relation);
    }

    @Override
    public double getSelectivity(String relationA, String relationB, String predicate) {
        Pair<String, String> pair = new Pair<>(relationA, relationB);
        if(!selectivities.containsKey(pair)) return 1; //Cross product
        return selectivities.get(pair);
    }

    @Override
    public int getJoinSize(String relationA, String relationB, String predicate) {
        return Math.toIntExact(Math.round(getSize(relationA) * getSize(relationB) * getSelectivity(relationA, relationB, "")));
    }

    @Override
    public List<Pair<String, Integer>> getRelations() {
        ArrayList<Pair<String,Integer>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : relations.entrySet()) {
            list.add(new Pair<>(entry.getKey(),entry.getValue()));
        }
        return list;
    }
}

class Pair<K,V> extends AbstractMap.SimpleEntry<K,V> {
    public Pair(K k, V v) {
        super(k,v);
    }
}