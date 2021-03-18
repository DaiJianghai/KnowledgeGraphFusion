package pipeLine;

import publicDataStructure.KG;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KGFusion {

    private double[][] similarityMatrix;
    private Integer nPairs;
    private HashMap<Integer, String> idxToEntityDict;
    private HashMap<Integer, String> idxToRelationDict;


    public KG runFusion() {
        return new KG();
    }

    public List<Pair<Integer, Integer>> findNSimilarPairs() {
        return new ArrayList<>();
    }

    public KG process(List<Pair<Integer, Integer>> nSimilarPairs) {
        return new KG();
    }


    public KGFusion(double[][] similarityMatrix, Integer nPairs, HashMap<Integer, String> idxToEntityDict, HashMap<Integer, String> idxToRelationDict) {
        this.similarityMatrix = similarityMatrix;
        this.nPairs = nPairs;
        this.idxToEntityDict = idxToEntityDict;
        this.idxToRelationDict = idxToRelationDict;
    }

    public double[][] getSimilarityMatrix() {
        return similarityMatrix;
    }

    public Integer getnPairs() {
        return nPairs;
    }

    public HashMap<Integer, String> getIdxToEntityDict() {
        return idxToEntityDict;
    }

    public HashMap<Integer, String> getIdxToRelationDict() {
        return idxToRelationDict;
    }

    public void setnPairs(Integer nPairs) {
        this.nPairs = nPairs;
    }
}
