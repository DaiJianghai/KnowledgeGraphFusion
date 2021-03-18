package pipeLine;

import Jama.Matrix;
import org.apache.commons.lang3.tuple.Pair;
import preprocessing.ConstructGraph;
import publicDataStructure.Item;
import publicDataStructure.KG;
import publicDataStructure.Triple;

import java.io.IOException;
import java.util.*;

public class SimilarityJoin {

    static int numNodes;
    private KG mergedKG;
    private double[][] unifiedRelationMatrix;



    public double[][] geSimilarityMatrix(int numIter, double cons) {

        Pair<Integer, Map<Integer, Set<Integer>>> graph = Graph();
        Matrix matrix = computeSimRank(graph.getLeft(), numIter, graph.getRight(), cons);
        return matrix.getArray();
    }

    public Pair<Integer, Map<Integer, Set<Integer>>> Graph() {

        List<Triple> triples = getMergedKG().getTriples();


        Map<Integer, Set<Integer>> InLinksMap = new HashMap<Integer, Set<Integer>>();

        Set<Integer> nodelinks = new HashSet<>();
        Set<Integer> nodes = new HashSet<>();

        //kg中对应的实体与边的集合
        int triplesNum = triples.size();
        Integer headEntityID = 0;
        Integer tailEntityID = 0;

        try {
            for (int i = 0; i < triplesNum; i++) {

                headEntityID = Integer.valueOf(triples.get(i).getHead().getEntityId()); //获取第i个元组的头实
                tailEntityID = Integer.valueOf(triples.get(i).getTail().getEntityId());
                nodes.add(headEntityID);
                nodes.add(tailEntityID);


                if (InLinksMap.containsKey(tailEntityID)) {
                    nodelinks = InLinksMap.get(tailEntityID);
                    nodelinks.add(headEntityID);
                    InLinksMap.put(tailEntityID, nodelinks);

                } else {
                    nodelinks = new HashSet<>();
                    nodelinks.add(headEntityID);
                    InLinksMap.put(tailEntityID, nodelinks);
                }
            }
            numNodes = nodes.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return Pair.of(numNodes, InLinksMap);
    }

    public Matrix computeSimRank(int numLinks, int iter, Map<Integer, Set<Integer>> linkmap, double c) {//计算相似度 参数为迭代次数
        double score[][] = new double[numLinks][numLinks];
        System.out.println(numLinks);
        Matrix simMatrix = new Matrix(score);
        Matrix tempScores;
        for (int i = 0; i < numLinks; i++) {
            for (int j = 0; j < numLinks; j++) {
                if (i == j) {
                    simMatrix.set(i, j, 1.0);
                } else {
                    simMatrix.set(i, j, 0.0);
                }

            }
        }
        tempScores = simMatrix;

        double simScore = 0.0;
        int numInLinks1 = 0, numInLinks2 = 0;
        Map<Integer, Set<Integer>> Links = linkmap;


        while ((iter--) > 0) {

            for (int id1 = 0; id1 < numLinks; id1++) {
                for (int id2 = 0; id2 < id1; id2++) {

                    if (id1 == id2) {
                        simScore = new Double(1.0);
                        continue;
                    }
                    Set<Integer> inlinks1 = Links.get(new Integer(id1));//id1的所有邻居集合 endnode的所有startnode集合
                    Set<Integer> inlinks2 = Links.get(new Integer(id2));//id2所有邻居集合
                    simScore = 0.0;
                    if (inlinks1 == null || inlinks2 == null) {
                        simScore = new Double(0.0);
                        continue;
                    } else if (inlinks1 != null && inlinks2 != null) {
                        numInLinks1 = inlinks1.size();//邻居的数量
                        numInLinks2 = inlinks2.size();

                        for (int i : inlinks1) {        //计算所有邻居的相似度
                            for (int j : inlinks2) {
                                if (i < numNodes && j < numNodes) {
                                    if (i == j) {
                                        simScore += new Double(1.0);
                                        continue;
                                    }
                                    if (j > i) {
                                        Integer k = j;
                                        j = i;
                                        i = k;
                                    }
                                    simScore += simMatrix.get(i, j);
                                }
                            }
                        }
                        simScore = new Double((c / (numInLinks1 * numInLinks2)) * simScore);
                        if (simScore != 0.0) tempScores.set(id1, id2, simScore);        //i行j列的值
                    }
                }
            }//for循环结束
            simMatrix = new Matrix(score);
            for (int i = 0; i < numLinks; i++) {
                for (int j = 0; j < i; j++) {
                    simScore = tempScores.get(i, j);
                    if (simScore != 0) simMatrix.set(i, j, simScore);
                }
            }
            tempScores = new Matrix(score);//更新
        }
        return simMatrix;
    }

    public SimilarityJoin(double[][] unifiedRelationMatrix) {
        this.unifiedRelationMatrix = unifiedRelationMatrix;
    }

    public SimilarityJoin(KG mergedKG){
        this.mergedKG = mergedKG;
    }

    public double[][] getUnifiedRelationMatrix() {
        return unifiedRelationMatrix;
    }

    public KG getMergedKG() {
        return mergedKG;
    }

    public void setUnifiedRelationMatrix(double[][] unifiedRelationMatrix) {
        this.unifiedRelationMatrix = unifiedRelationMatrix;
    }

    public static void main(String[] args) throws IOException {
        Pair<List<KG>, List<Item>> listListPair = ConstructGraph.prepareKGs();
        List<KG> kgs = listListPair.getLeft();
        List<Item> items = listListPair.getRight();

        KGsMergeBasedOnContent kGsMergeBasedOnContent = new KGsMergeBasedOnContent(kgs, items);
        KG mergedKG = kGsMergeBasedOnContent.runMerge();

        SimilarityJoin similarityJoin = new SimilarityJoin(mergedKG);
        double[][] res = similarityJoin.geSimilarityMatrix(5, 0.85);
        for (int i = 0; i < res.length; i++){
            for (int j = 0; j < res[0].length; j++){
                System.out.print(res[i][j] + " ");
            }
            System.out.println();
        }
    }
}
