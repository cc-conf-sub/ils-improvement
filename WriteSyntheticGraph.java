import java.util.*;
import java.io.*;

public class WriteSyntheticGraph {

    private static void writeGraph(ArrayList<ArrayList<Integer>> prob_matrix, String filename) {

        int num_nodes = prob_matrix.size();
        long num_edges = countEdges(prob_matrix, num_nodes);
        System.out.println("writing graph with " + num_nodes + " nodes and " + num_edges + " edges");


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(num_nodes + " " + num_edges); 
            writer.newLine();           

            for (int i = 0; i < num_nodes; i++) {
                ArrayList<Integer> cur_edges = prob_matrix.get(i);
                for (int j = 0; j < cur_edges.size(); j++) {
                    if (cur_edges.get(j) > i) {
                        writer.write(i + " " + cur_edges.get(j));
                        writer.newLine();
                    }
                }

            }            

            writer.close();
        } catch (Exception e) {
            System.out.println("Something went wrong. :(");
        }
    }

    private static ArrayList<ArrayList<Integer>> generateEdges(int num_nodes, int num_neighbors) {

        ArrayList<ArrayList<Integer>> prob_matrix = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < num_nodes; i++) { // initialize
            ArrayList<Integer> cur_edges = new ArrayList<Integer>();
            prob_matrix.add(cur_edges);
        }

        Random rand = new Random(); 
        for (int i = 0; i < num_nodes; i++) {
            HashSet<Integer> edge_set = new HashSet<Integer>();
            edge_set.addAll(prob_matrix.get(i));

	    int cur_limit = rand.nextInt(num_neighbors) + 1;
            
            while (edge_set.size() < cur_limit) {
                // get random between i+1 and num_nodes - 1
                int randomNum = rand.nextInt(num_nodes);
                if (!edge_set.contains(randomNum) && randomNum != i) {
                    prob_matrix.get(i).add(randomNum);
                    prob_matrix.get(randomNum).add(i);
                    edge_set.add(randomNum);
                }
            }
        }

        return prob_matrix;

    }

    private static long countEdges(ArrayList<ArrayList<Integer>> prob_matrix, int num_nodes) {

        long total = 0;
        for (int i = 0; i < num_nodes; i++) {
            ArrayList<Integer> cur_edges = prob_matrix.get(i);
            for (int j = 0; j < cur_edges.size(); j++) {
                if (cur_edges.get(j) > i) // count pairs i, f(i) where i < f(i)
                    total = total + 1;
            }

        }
        return total;
    }

    

    public static void main(String args[]){

        int num_nodes = 100000;
        int num_neighbors = 5000; // Integer.parseInt(args[0]);
	String file_name = "graph" + num_neighbors + ".txt";

        ArrayList<ArrayList<Integer>> prob_matrix = generateEdges(num_nodes, num_neighbors);
        writeGraph(prob_matrix, file_name);

    }
    
}
