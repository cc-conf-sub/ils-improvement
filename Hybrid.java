import java.util.*;

public class Hybrid {

    public static ArrayList<ArrayList<Integer>> large_graph_fix_clusters_local(ArrayList<ArrayList<Integer>> cur_clustering, ArrayList<ArrayList<Integer>> prob_matrix, boolean ECS) {
    /*
        Apply local search to clusters resulting from Pivot

    */

    ArrayList<ArrayList<Integer>> new_clustering = new ArrayList<ArrayList<Integer>>();
    int negative_examples = 0;
    int total_examples = 0;
    for (int c = 0; c < cur_clustering.size(); c++) {
        ArrayList<Integer> cluster = cur_clustering.get(c);
        int c_size = cluster.size();

        

        if (c_size <= 3) {
            new_clustering.add(cluster);
        } else { 

            HashMap<Integer, Integer> relabelling = new HashMap<Integer, Integer>();
            for (int i = 0; i < c_size; i++)
                relabelling.put(cluster.get(i), i);

            HashSet<Integer> cluster_set = new HashSet<Integer>();
            cluster_set.addAll(cluster);
            ArrayList<ArrayList<Integer>> cur_prob_matrix = new ArrayList<ArrayList<Integer>>();
	    ArrayList<Integer> cluster_relabel = new ArrayList<Integer>();

            //int half = 0;
            //int total = 0;
            for (int i = 0; i < c_size; i++) {
                ArrayList<Integer> new_edges = new ArrayList<Integer>();
                ArrayList<Integer> cur_edges = prob_matrix.get(cluster.get(i));
                int edge_count = 0;
                for (int j = 0; j < cur_edges.size(); j++){
                    int cur_edge = cur_edges.get(j);
                    if (cluster_set.contains(cur_edge)) {
                        new_edges.add(relabelling.get(cur_edge));
                        edge_count += 1;
                    }
                }
                //if (edge_count < c_size / 2)
                //    half += 1;
                //total += edge_count;
                cur_prob_matrix.add(new_edges);
		cluster_relabel.add(i);
            }
            /*
            double precision = ((double) total) / (c_size * (c_size -1));
            double half_ratio = ((double) half) / c_size;
            
            if (c_size >= 10 && precision > 0.33)
                total_examples += 1;               

            if (c_size >= 10 && precision > 0.33 && half_ratio < 1 - Math.sqrt(precision)) {
                System.out.println("Cluster size: " + c_size);
                System.out.println("Cluster precision: " + precision);
                System.out.println("Number of nodes with degree < n/2: " + half_ratio); 
                negative_examples += 1;
            }
            */

            ArrayList<ArrayList<Integer>> input_clustering = new ArrayList<ArrayList<Integer>>();
            input_clustering.add(cluster_relabel);
	    // System.out.println(input_clustering.size());
            ArrayList<ArrayList<Integer>> adjusted_clustering = DNode.local_search_network(input_clustering, cur_prob_matrix, ECS);

            // replace node labels
            for (int j = 0; j < adjusted_clustering.size(); j++) {
                ArrayList<Integer> adj_cluster = adjusted_clustering.get(j);
                for (int i = 0; i < adj_cluster.size(); i++)
                    adj_cluster.set(i, cluster.get(adj_cluster.get(i)));
                new_clustering.add(adj_cluster);
            }

        } 
    }
    // System.out.println("Probability of negative example: " + ((double) negative_examples) / total_examples);


    return new_clustering;

    }


// Parallel !

    public static ArrayList<ArrayList<Integer>> large_graph_fix_clusters_parallel(ArrayList<ArrayList<Integer>> cur_clustering, ArrayList<ArrayList<Integer>> prob_matrix, boolean ECS) {
        /*
            Apply local search to clusters resulting from Pivot
    
        */
    
        // Thread-safe? I hope!
        List<ArrayList<ArrayList<Integer>>> new_clusterings = Collections.synchronizedList(new ArrayList<ArrayList<ArrayList<Integer>>>()); 
    
        cur_clustering.parallelStream().forEach(cluster -> {
            new_clusterings.add(fix_cluster(cluster, prob_matrix, ECS));
        });

        ArrayList<ArrayList<Integer>> new_clustering = new ArrayList<ArrayList<Integer>>();        
        for (ArrayList<ArrayList<Integer>> clustering : new_clusterings)
            new_clustering.addAll(clustering);

        return new_clustering;
    
        }


        private static ArrayList<ArrayList<Integer>> fix_cluster(ArrayList<Integer> cluster, ArrayList<ArrayList<Integer>> prob_matrix, boolean ECS) {

            int c_size = cluster.size();
            ArrayList<ArrayList<Integer>> new_clustering = new ArrayList<ArrayList<Integer>>();
    
            if (c_size <= 3) {
                new_clustering.add(cluster);
            } else { 
    
                HashMap<Integer, Integer> relabelling = new HashMap<Integer, Integer>();
                for (int i = 0; i < c_size; i++)
                    relabelling.put(cluster.get(i), i);
    
                HashSet<Integer> cluster_set = new HashSet<Integer>();
                cluster_set.addAll(cluster);
                ArrayList<ArrayList<Integer>> cur_prob_matrix = new ArrayList<ArrayList<Integer>>();
                ArrayList<Integer> cluster_relabel = new ArrayList<Integer>();
    
                for (int i = 0; i < c_size; i++) {
                    ArrayList<Integer> new_edges = new ArrayList<Integer>();
                    ArrayList<Integer> cur_edges = prob_matrix.get(cluster.get(i));
                    int edge_count = 0;
                    for (int j = 0; j < cur_edges.size(); j++){
                        int cur_edge = cur_edges.get(j);
                        if (cluster_set.contains(cur_edge)) {
                            new_edges.add(relabelling.get(cur_edge));
                            edge_count += 1;
                        }
                    }

                    cur_prob_matrix.add(new_edges);
                    cluster_relabel.add(i);
                }

    
                ArrayList<ArrayList<Integer>> input_clustering = new ArrayList<ArrayList<Integer>>();
                input_clustering.add(cluster_relabel);
                ArrayList<ArrayList<Integer>> adjusted_clustering = DNode.local_search_network(input_clustering, cur_prob_matrix, ECS);
    
                // replace node labels
                for (int j = 0; j < adjusted_clustering.size(); j++) {
                    ArrayList<Integer> adj_cluster = adjusted_clustering.get(j);
                    for (int i = 0; i < adj_cluster.size(); i++)
                        adj_cluster.set(i, cluster.get(adj_cluster.get(i)));
                    new_clustering.add(adj_cluster);
                }
    
            } 

            return new_clustering;

        }    

    
}
