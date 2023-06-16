Data Format:
* Data files are stored in Data/[name of data set]/graph.txt
  * All data sets are either publicly available, provided here, or the code used to generate them is provided
  * We provide data set samples to show the format for the various experiments 
* Delimiter is hard-coded in driver files
* For Correlation Clustering: 
  * The first line of the file must contain the total number of nodes (anything that follows it on the line will be ignored)
  * Rest of file lists positive edges as [node1] [node2]

To Compile: javac *.java
To Run: java [DriverName] [data set folder name]
  * additional heap space may be needed for some experiments; increase the max heap size with the -Xmx flag

Drivers
-------

Hard coded parameters:
* delimiter for data set
* number of Pivot rounds, number of attributes used, etc. 

RunPivotLS.java
* Input: positive edge list
* Methods: "neighborhood oracle" for Pivot algorithm; also
  * ILS,
  * "timed" LS (same amount of time as ILS),
  * match LS (matches improvement level of ILS),
  * full LS
* Sample data sets: cor_amazon, cor_dblp

RunVote.java
* Input: positive edge list
* Method: Vote algorithm
* Sample data sets: cor_amazon, cor_dblp


Code Files
----------

DNode.java
* Implementations of Vote, LocalSearch

Helper.java
* Helper functions for reading data sets

Hybrid.java
* ILS algorithm implementation (calls LocalSearch on input clusters)

PKwik.java
* Pivot algorithm implementations


Data Generation
---------------

WriteSyntheticGraph.java
* generates synthetic graph for correlation clustering
* creates specified number of nodes
* nodes are assigned up to a specified number of neighbors at random

