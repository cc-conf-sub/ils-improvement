import java.util.*;

public class RunPivotLS {

    private static ArrayList<ArrayList<Integer>> makeCopy (ArrayList<ArrayList<Integer>> cur_clustering) {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < cur_clustering.size(); i++) {
            ArrayList<Integer> cluster_copy = new ArrayList<Integer>();
            cluster_copy.addAll(cur_clustering.get(i));
            copy.add(cluster_copy);
        }
        return copy;
    }

    public static void main(String args[]){

        // ----- PARAMETERS -----
        String data_set = args[0];
        String delimiter = "\\s"; 
        int ROUNDS = 10;
        // ----------------------

        ArrayList<ArrayList<Integer>> prob_matrix = Helper.read_large_network_relabel("Data/"+ data_set + "/graph.txt", delimiter);

        double pKwikTimeTotal = 0;
        double hybridTimeTotal = 0;
	    double hybrid2TimeTotal = 0;
        double localTimeTotal = 0;
        double randTimeTotal = 0;
        double parallelTimeTotal = 0; 

        double[] pKwikTimes = new double[ROUNDS];
        double[] hybridTimes = new double[ROUNDS];
        double[] hybrid2Times = new double[ROUNDS];
        double[] localTimes = new double[ROUNDS];
        double[] randTimes = new double[ROUNDS];
        double[] parallelTimes = new double[ROUNDS];
  
        long pKwikNumClusters = 0;
        long hybridNumClusters = 0;
	    long hybrid2NumClusters = 0;
        long localNumClusters = 0;
        long randNumClusters = 0;

        long largestPKwikCluster = 0;
        long largestHybridCluster = 0;
	    long largestHybrid2Cluster = 0;
        long largestLocalCluster = 0;
        long largestRandCluster = 0;

	long pKwikScores = 0;
    long outerScores = 0;
	long hybridScores = 0;
	long hybrid2Scores = 0;
	long localScores = 0;
	long randScores = 0;

        long[] pKwikScoresList = new long[ROUNDS];
        long[] outerScoresList = new long[ROUNDS];
        long[] hybridScoresList = new long[ROUNDS];
        long[] hybrid2ScoresList = new long[ROUNDS];
        long[] localScoresList = new long[ROUNDS];
        long[] randScoresList = new long[ROUNDS];

        double pPrecision = 0;
        double hPrecision = 0;
        double lPrecision = 0;

        double[] pPrecisionList = new double[ROUNDS];
        double[] hPrecisionList = new double[ROUNDS];
        double[] lPrecisionList = new double[ROUNDS];

        double pRecall = 0;
        double hRecall = 0;
        double lRecall = 0;

        double[] pRecallList = new double[ROUNDS];
        double[] hRecallList = new double[ROUNDS];
        double[] lRecallList = new double[ROUNDS];

        System.out.println("Num Nodes: " + prob_matrix.size());
        System.out.println("Start");
        for (int j = 0; j < ROUNDS; j++) {

        long pKwikStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> result = PKwik.pKwikClustering(prob_matrix);
        long pKwikTime = System.currentTimeMillis() - pKwikStart;
        pKwikTimeTotal += (pKwikTime / 1000.0);
        pKwikTimes[j] = pKwikTime / 1000.0;
        // System.out.println("Pivot finished in: " + pKwikTime / 1000.0 + " s");

        ArrayList<ArrayList<Integer>> result_copy = makeCopy(result);

        long hybridStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> fix = Hybrid.large_graph_fix_clusters_local(result_copy, prob_matrix, false);
        long hybridTime = System.currentTimeMillis() - hybridStart;
        hybridTimeTotal += (hybridTime / 1000.0);
        hybridTimes[j] = hybridTime / 1000.0;
        // System.out.println("Hybrid finished in: " + hybridTime / 1000.0 + " s");

        result_copy = makeCopy(result);

        long hybrid2Start = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> fix2 = DNode.local_search_network_timed(result_copy, prob_matrix, false, hybrid2Start, hybridTime);
        long hybrid2Time = System.currentTimeMillis() - hybrid2Start;
        hybrid2TimeTotal += (hybrid2Time / 1000.0);
        hybrid2Times[j] = hybrid2Time / 1000.0;
        // System.out.println("Hybrid2 finished in: " + hybrid2Time / 1000.0 + " s");

        result_copy = makeCopy(result);
        
        long randStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> rand_result = DNode.local_search_network_score(result_copy, prob_matrix, false, Helper.quick_edit_dist(fix, prob_matrix));
        long randTime = System.currentTimeMillis() - randStart;
        randTimeTotal += (randTime / 1000.0);
        randTimes[j] = randTime / 1000.0;

        result_copy = makeCopy(result);

        long localStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> fix3 = DNode.local_search_network(result_copy, prob_matrix, false);
        long localTime = System.currentTimeMillis() - localStart;
        localTimeTotal += (localTime / 1000.0);
        localTimes[j] = localTime / 1000.0;
        // System.out.println("LS finished in: " + localTime / 1000.0 + " s");

        result_copy = makeCopy(result);
        long parallelStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> parallel_result = Hybrid.large_graph_fix_clusters_parallel(result_copy, prob_matrix, false);
        long parallelTime = System.currentTimeMillis() - parallelStart;
        parallelTimeTotal += (parallelTime / 1000.0);
        parallelTimes[j] = parallelTime / 1000.0;

        // System.out.println();
        pKwikNumClusters += result.size();
        // System.out.println("Num pKwik Clusters: " + result.size());
        hybridNumClusters += fix.size();
	hybrid2NumClusters += fix2.size();
        // System.out.println("Num Hybrid Clusters: " + fix.size());
        // System.out.println();
        localNumClusters += fix3.size();
        randNumClusters += rand_result.size();

        int max_pkwik = 0;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).size() > max_pkwik)
                max_pkwik = result.get(i).size();
        }
        largestPKwikCluster += max_pkwik;
        // System.out.println("Largest pKwik cluster: " + max_pkwik);
        
        int max_hybrid = 0;
        for (int i = 0; i < fix.size(); i++) {
            if (fix.get(i).size() > max_hybrid)
                max_hybrid = fix.get(i).size();
        }
        largestHybridCluster += max_hybrid;
        // System.out.println("Largest Hybrid cluster: " + max_hybrid);
        
        int max_hybrid2 = 0;
        for (int i = 0; i < fix2.size(); i++) {
            if (fix2.get(i).size() > max_hybrid2)
                max_hybrid2 = fix2.get(i).size();
        }
        largestHybrid2Cluster += max_hybrid2;


        int max_local = 0;
        for (int i = 0; i < fix3.size(); i++) {
            if (fix3.get(i).size() > max_local)
                max_local = fix3.get(i).size();
        }
        largestLocalCluster += max_local;

        
        int max_rand = 0;
        for (int i = 0; i < rand_result.size(); i++) {
            if (rand_result.get(i).size() > max_rand)
                max_rand = rand_result.get(i).size();
        }
        largestRandCluster += max_rand;
        

        pKwikStart = System.currentTimeMillis();
        int p_score = Helper.quick_edit_dist(result, prob_matrix);
        pKwikTime = System.currentTimeMillis() - pKwikStart;
        // System.out.println("pKwik Score finished in: " + pKwikTime / 1000.0 + " s");        
        // int score = Helper.large_graph_prob_edit_dist(result, prob_matrix);
        pKwikScores += p_score;
        pKwikScoresList[j] = p_score;
	// System.out.println("PKwik Score: " + p_score);

        long outer_score = Helper.pivot_outer_cost(result, prob_matrix);
        outerScores += outer_score;
        outerScoresList[j] = outer_score;	
        
        hybridStart = System.currentTimeMillis();
        // int h_score = Helper.large_graph_prob_edit_dist_hash(fix, prob_matrix);
        int h_score = Helper.quick_edit_dist(fix, prob_matrix);
        hybridTime = System.currentTimeMillis() - hybridStart;
        // System.out.println("Hybrid Score finished in: " + hybridTime / 1000.0 + " s");
        hybridScores += h_score;
        hybridScoresList[j] = h_score;
	// System.out.println("Hybrid Score: " + h_score);

        hybrid2Start = System.currentTimeMillis();
        // int h_score = Helper.large_graph_prob_edit_dist_hash(fix, prob_matrix);
        int h2_score = Helper.quick_edit_dist(fix2, prob_matrix);
        hybrid2Time = System.currentTimeMillis() - hybrid2Start;
        // System.out.println("Hybrid Score finished in: " + hybridTime / 1000.0 + " s");
        hybrid2Scores += h2_score;
        hybrid2ScoresList[j] = h2_score;

        int l_score = Helper.quick_edit_dist(fix3, prob_matrix);
        localScores += l_score;
        localScoresList[j] = l_score;

        int r_score = Helper.quick_edit_dist(rand_result, prob_matrix);
        randScores += r_score;
        randScoresList[j] = l_score;

        double[] pPrecisionRecall = Helper.get_precision_recall(result, prob_matrix);
        pPrecision += pPrecisionRecall[0];
        pPrecisionList[j] = pPrecisionRecall[0];
        pRecall += pPrecisionRecall[1];
        pRecallList[j] = pPrecisionRecall[1];
        double[] hPrecisionRecall = Helper.get_precision_recall(fix, prob_matrix);
        hPrecision += hPrecisionRecall[0];
        hPrecisionList[j] = hPrecisionRecall[0];
        hRecall += hPrecisionRecall[1];
        hRecallList[j] = hPrecisionRecall[1];
        double[] lPrecisionRecall = Helper.get_precision_recall(fix3, prob_matrix);
        lPrecision += lPrecisionRecall[0];
        lPrecisionList[j] = lPrecisionRecall[0];
        lRecall += lPrecisionRecall[1];
        lRecallList[j] = lPrecisionRecall[1];

        }
        System.out.println("Finish");
        System.out.println();
        System.out.println("pivot times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pKwikTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("pivot scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pKwikScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("outer scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(outerScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("ils times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybridTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("parallel times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(parallelTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("ils scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybridScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("timed ls times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybrid2Times[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("timed ls scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybrid2ScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("local times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(localTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("local scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(localScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("match times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(randTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("match scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(randScoresList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Average pivot time: " + pKwikTimeTotal / ((double) ROUNDS));
        System.out.println("Average ils time: " + hybridTimeTotal / ((double) ROUNDS));
        System.out.println("Average parallel time: " + parallelTimeTotal / ((double) ROUNDS));
	    System.out.println("Average timed ls time: " + hybrid2TimeTotal / ((double) ROUNDS));
        System.out.println("Average local time: " + localTimeTotal / ((double) ROUNDS));
        System.out.println("Average match time: " + randTimeTotal / ((double) ROUNDS));
        System.out.println();


	    System.out.println("Average pivot score: " + pKwikScores / ((double) ROUNDS));
        System.out.println("Average outer score: " + outerScores / ((double) ROUNDS));

	    System.out.println("Average ils score: " + hybridScores / ((double) ROUNDS));
        System.out.println("Percent Improvement: " + (pKwikScores - hybridScores) / ((double) pKwikScores) * 100.0);
	System.out.println("Average timed ls score: " + hybrid2Scores / ((double) ROUNDS));
        System.out.println("Percent Improvement: " + (pKwikScores - hybrid2Scores) / ((double) pKwikScores) * 100.0);
	    System.out.println("Average local score: " + localScores / ((double) ROUNDS));
        System.out.println("Percent Improvement: " + (pKwikScores - localScores) / ((double) pKwikScores) * 100.0); 
	    System.out.println("Average match score: " + randScores / ((double) ROUNDS));
        System.out.println("Percent Improvement: " + (pKwikScores - randScores) / ((double) pKwikScores) * 100.0); 
        System.out.println();


        System.out.println("Average pivot num clusters: " + pKwikNumClusters / ((double) ROUNDS));
        System.out.println("Average ils num clusters: " + hybridNumClusters / ((double) ROUNDS));
	System.out.println("Average timed ls num clusters: " + hybrid2NumClusters / ((double) ROUNDS));
        System.out.println("Average local num clusters: " + localNumClusters / ((double) ROUNDS));
        System.out.println("Average match num clusters: " + randNumClusters / ((double) ROUNDS));
        System.out.println();
        System.out.println("Average pivot max cluster size: " + largestPKwikCluster / ((double) ROUNDS));
        System.out.println("Average ils max cluster size: " + largestHybridCluster / ((double) ROUNDS));
	System.out.println("Average timed ls max cluster size: " + largestHybrid2Cluster / ((double) ROUNDS));
        System.out.println("Average local max cluster size: " + largestLocalCluster / ((double) ROUNDS));
        System.out.println("Average match max cluster size: " + largestRandCluster / ((double) ROUNDS));
        System.out.println();

        
        System.out.println("pivot precision: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pPrecisionList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("pivot recall: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pRecallList[i] + " ");
        System.out.println();
        System.out.println();
        
        System.out.println("ils precision: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hPrecisionList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("ils recall: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hRecallList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("local precision: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(lPrecisionList[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("local recall: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(lRecallList[i] + " ");
        System.out.println();
        System.out.println();
    

	System.out.println("Average pivot precision: " + pPrecision / ((double) ROUNDS));
	   System.out.println("Average ils precision: " + hPrecision / ((double) ROUNDS));
	    System.out.println("Average local precision: " + lPrecision / ((double) ROUNDS));
        System.out.println();

	    System.out.println("Average pivot recall: " + pRecall / ((double) ROUNDS));
	    System.out.println("Average ils recall: " + hRecall / ((double) ROUNDS));
	    System.out.println("Average local recall: " + lRecall / ((double) ROUNDS));
         System.out.println();
        
	
       
    }

    
}
