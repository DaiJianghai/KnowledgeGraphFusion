package preprocessing;

import org.apache.commons.lang3.tuple.Pair;
import publicDataStructure.*;

import java.io.*;
import java.util.*;

public class ConstructGraph {

    public static Pair<List<KG>, List<Item>> prepareKGs() throws IOException {
        String fileName = "D:\\IntelliJ IDEA 2020.1.2\\Project\\KGFusion_edition2\\src\\input\\51.txt";
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        BufferedReader read = new BufferedReader(isr);
        String line = "";
        int beforeDocId = 1;
        int tempDocId = 1;
        List<Item> items = new ArrayList<>();
        List<KG> kgs = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<Triple> triples = new ArrayList<>();

        int count = 0;
        while ((line = read.readLine()) != null) {
            List<String> lineItems = Arrays.asList(line.strip().split("\t"));
            tempDocId = Integer.parseInt(lineItems.get(lineItems.size() - 1));

            if (count == 0){
                beforeDocId = tempDocId;
            }
            if (beforeDocId == tempDocId) {
                Node node1 = new Node(lineItems.get(0), lineItems.get(1), "-1", lineItems.get(6));
                Node node2 = new Node(lineItems.get(3), lineItems.get(4), "-1", lineItems.get(6));
                Entity headEntity = new Entity(lineItems.get(0), lineItems.get(1), "-1", lineItems.get(6));
                Entity tailEntity = new Entity(lineItems.get(3), lineItems.get(4), "-1", lineItems.get(6));
                nodes.add(node1);
                nodes.add(node2);
                String relation = lineItems.get(2);
                Triple triple = new Triple(lineItems.get(5), headEntity, tailEntity, relation, lineItems.get(6));
                triples.add(triple);
                Item item = new Item(lineItems.get(7), lineItems.get(6),lineItems.get(8));
                items.add(item);
            }
            else{
                KG kg = new KG(nodes, triples);
                kgs.add(kg);
                nodes = new ArrayList<>();
                triples = new ArrayList<>();
                beforeDocId = tempDocId;
                if(tempDocId == 0)
                    continue;
                Node node1 = new Node(lineItems.get(0), lineItems.get(1), "-1", lineItems.get(6));
                Node node2 = new Node(lineItems.get(3), lineItems.get(4), "-1", lineItems.get(6));
                Entity headEntity = new Entity(lineItems.get(0), lineItems.get(1), "-1", lineItems.get(6));
                Entity tailEntity = new Entity(lineItems.get(3), lineItems.get(4), "-1", lineItems.get(6));

                nodes.add(node1);
                nodes.add(node2);
                String relation = lineItems.get(2);

                Triple triple = new Triple(lineItems.get(5), headEntity, tailEntity, relation, lineItems.get(6));
                triples.add(triple);

                Item item = new Item(lineItems.get(7), lineItems.get(6),lineItems.get(8));
                items.add(item);
            }

            count++;

        }




        for(KG kg : kgs){

            List<Node> newNodes = new ArrayList<>();
            HashMap<String, Integer> map = new HashMap<>();
            for (Node node: kg.getNodes()){

                if(!map.containsKey(node.getNodeId())){
                    map.put(node.getNodeId(), 1);
                    newNodes.add(node);

                }
            }

            kg.setNodes(newNodes);

        }
        List<Item> newItems = new ArrayList<>();
        HashMap<String,Integer> map1 = new HashMap<String, Integer>();
        for(Item item: items){

            if(!map1.containsKey(item.getItemId())){
                map1.put(item.getItemId(),1);
                newItems.add(item);
            }
        }

        return new Pair<List<KG>, List<Item>>() {
            @Override
            public List<Item> setValue(List<Item> value) {
                return value;
            }

            @Override
            public List<KG> getLeft() {
                return kgs;
            }

            @Override
            public List<Item> getRight() {
                return newItems;
            }


        };
    }

    public static void main(String[] args) throws IOException {




    }
}
