import java.util.*;

public class RunVote {

    public static void main(String args[]){

        // ----- PARAMETERS -----
        String data_set = args[0];
        String delimiter = "\\s"; 
        int ROUNDS = 10;
        // ----------------------

        ArrayList<ArrayList<Integer>> prob_matrix = Helper.read_large_network_relabel("Data/"+ data_set + "/graph.txt", delimiter);

        double localTimeTotal = 0;
        double[] localTimes = new double[ROUNDS];
        long localNumClusters = 0;
        long largestLocalCluster = 0;
	    long localScores = 0;
        long[] localScoresList = new long[ROUNDS];
        double lPrecision = 0;
        double[] lPrecisionList = new double[ROUNDS];
        double lRecall = 0;
        double[] lRecallList = new double[ROUNDS];

        System.out.println("Num Nodes: " + prob_matrix.size());
        System.out.println("Start");
        for (int j = 0; j < ROUNDS; j++) {

            long localStart = System.currentTimeMillis();
            ArrayList<ArrayList<Integer>> fix3 = DNode.random_node_network(prob_matrix);
            long localTime = System.currentTimeMillis() - localStart;
            localTimeTotal += (localTime / 1000.0);
            localTimes[j] = localTime / 1000.0;
            // System.out.println("LS finished in: " + localTime / 1000.0 + " s");
        
            localNumClusters += fix3.size();

            int max_local = 0;
            for (int i = 0; i < fix3.size(); i++) {
                if (fix3.get(i).size() > max_local)
                    max_local = fix3.get(i).size();
            }
            largestLocalCluster += max_local;

            int l_score = Helper.quick_edit_dist(fix3, prob_matrix);
            localScores += l_score;
            localScoresList[j] = l_score;

            double[] lPrecisionRecall = Helper.get_precision_recall(fix3, prob_matrix);
            lPrecision += lPrecisionRecall[0];
            lPrecisionList[j] = lPrecisionRecall[0];
            lRecall += lPrecisionRecall[1];
            lRecallList[j] = lPrecisionRecall[1];

        }

        System.out.println("Finish");
        System.out.println();
        System.out.println("vote times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(localTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("vote scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(localScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Average vote time: " + localTimeTotal / ((double) ROUNDS));
        System.out.println();
	    System.out.println("Average vote score: " + localScores / ((double) ROUNDS));
        System.out.println();

        System.out.println("Average vote num clusters: " + localNumClusters / ((double) ROUNDS));
        System.out.println();
        System.out.println("Average vote max cluster size: " + largestLocalCluster / ((double) ROUNDS));
        System.out.println();

        
        System.out.println("vote precision: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(lPrecisionList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("vote recall: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(lRecallList[i] + " ");
        System.out.println();
        System.out.println();
    
	    System.out.println("Average vote precision: " + lPrecision / ((double) ROUNDS));
        System.out.println();

	    System.out.println("Average vote recall: " + lRecall / ((double) ROUNDS));
        // System.out.println();
    
	
       
    }

    
}
