package test;

import org.apache.commons.lang3.tuple.Pair;
import pipeLine.KGsMergeBasedOnContent;
import pipeLine.SchemeExtraction;
import preprocessing.ConstructGraph;
import publicDataStructure.*;

import java.io.IOException;
import java.util.List;

public class testEX {

    public void test() throws IOException {

        System.out.println("OK");



    }

    public static void main(String[] args) throws IOException {
        Pair<List<KG>, List<Item>> listListPair = ConstructGraph.prepareKGs();
        List<KG> kgs = listListPair.getLeft();
        List<Item> items = listListPair.getRight();

        KGsMergeBasedOnContent kGsMergeBasedOnContent = new KGsMergeBasedOnContent(kgs, items);
        KG mergedKG = kGsMergeBasedOnContent.runMerge();

        List<Triple> triples = mergedKG.getTriples();
        for (Triple triple: triples){
            System.out.print(triple.getHead().getEntityName() + " " + triple.getRela() + " " + triple.getTail().getEntityName());
            System.out.println();
        }

        new SchemeExtraction(mergedKG).runExtract();
        
    }
}
