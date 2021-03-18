package pipeLine;


import Jama.Matrix;
import org.apache.commons.lang3.tuple.Pair;
import publicDataStructure.Entity;
import publicDataStructure.KG;
import publicDataStructure.Node;
import publicDataStructure.Triple;
import utils.LSA;
import utils.mathUtil;

import java.util.*;

public class SchemeExtraction {
    private KG fusionKg;

    public KG runExtract() {

        HashMap<String, Pair<List<Entity>, List<Entity>>> relationNexus = geRelationNexus();
        Pair<HashMap<String, Integer>, HashMap<String, Pair<List<Entity>, List<Entity>>>> reMapAndNexus = geAbstractRelationType(relationNexus);
        HashMap<Entity, Integer> entityIntegerMap = geAbstractEntityType(reMapAndNexus.getRight());
        return process(entityIntegerMap, reMapAndNexus.getRight());
    }


    public KG process(HashMap<Entity, Integer> abstractEntityType, HashMap<String, Pair<List<Entity>, List<Entity>>> mergedRelationNexus) {

        List<Node> schemaNodes = new ArrayList<>();
        List<Triple> schemaTriples = new ArrayList<>();

        int tripleCounter = 0;
        for (String relation: mergedRelationNexus.keySet()){
            List<Entity> left = mergedRelationNexus.get(relation).getLeft();
            List<Entity> right = mergedRelationNexus.get(relation).getRight();
            Entity head = new Entity(abstractEntityType.get(left.get(0)).toString(), "", "", "");
            Entity tail = new Entity(abstractEntityType.get(right.get(0)).toString(), "", "", "");
            Node node1 = new Node(abstractEntityType.get(left.get(0)).toString(), "", "", "");
            Node node2 = new Node(abstractEntityType.get(right.get(0)).toString(), "", "", "");
            schemaNodes.add(node1);
            schemaNodes.add(node2);
            Triple triple = new Triple(String.valueOf(tripleCounter), head, tail, relation, "");
            schemaTriples.add(triple);
            tripleCounter++;
        }

        KG schemaKG = new KG(schemaNodes, schemaTriples, null, null);
        return schemaKG;
    }


    /**
     * 按照相同关系的节点具有相同类型的规则进行节点分类
     */
    public HashMap<String, Pair<List<Entity>, List<Entity>>> geRelationNexus (){
        HashMap<String, Pair<List<Entity>, List<Entity>>>  relationNexus = new HashMap<>();
        KG kg = getFusionKg();

        List<Triple> triples = kg.getTriples();
        for (Triple triple: triples){
            String relation = triple.getRela();
            Entity head = triple.getHead();
            Entity tail = triple.getTail();
            if (relationNexus.containsKey(relation)){
                List<Entity> left = relationNexus.get(relation).getLeft();
                List<Entity> right = relationNexus.get(relation).getRight();
                boolean hasHead = false;
                boolean hasTail = false;

                for (Entity entity: left){
                    if (entity.getEntityId().equals(head.getEntityId()))
                        hasHead = true;
                }

                if (!hasHead)
                    left.add(head);

                // 避免形成环
                for (Entity entity: right){
                    if (entity.getEntityId().equals(tail.getEntityId()))
                        hasTail = true;
                }

                for (Entity entity: left){
                    if (entity.getEntityId().equals(tail.getEntityId()))
                        hasTail = true;
                }


                if (!hasTail)
                    right.add(tail);

                relationNexus.put(relation, Pair.of(left, right));
            }
            else {
                List<Entity> left = new ArrayList<>();
                List<Entity> right = new ArrayList<>();
                left.add(head);
                right.add(tail);
                relationNexus.put(relation, Pair.of(left, right));
            }

        }

        return relationNexus;
    }

    /**
     * 计算关系相似度,合并关系
     * 返回关系分类map, 返回新的relationNexus，作为节点分类依据
     */
    public Pair<HashMap<String, Integer>, HashMap<String, Pair<List<Entity>, List<Entity>>>> geAbstractRelationType(HashMap<String, Pair<List<Entity>, List<Entity>>> relationNexus) {

        // 从relationNexus构建relationList
        List<String> relations =  new ArrayList<>();
        for (String relation: relationNexus.keySet()){
            relations.add(relation);
        }
        List<Entity> entities = copyFromNode();
        // 构建 实体-关系矩阵 用于计算关系相似度
        int lenEntity = entities.size();
        int lenRelation = relations.size();
        double[][] frequencyMatrix = new double[lenEntity][lenRelation];
        for (String relation: relations){
            List<Entity> left = relationNexus.get(relation).getLeft();
            List<Entity> right = relationNexus.get(relation).getRight();
            int relationIndex = relations.indexOf(relation);
            for (Entity leftEntity: left){
                int entityIndex = entities.indexOf(leftEntity);
                frequencyMatrix[entityIndex][relationIndex] += 1;
            }

            for (Entity rightEntity: right){
                int entityIndex = entities.indexOf(rightEntity);
                frequencyMatrix[entityIndex][relationIndex] += 1;
            }
        }

        // 利用LSA得到 降维后的Matrix类型矩阵
        LSA lsa = new LSA(frequencyMatrix);
        Matrix decomposedMatrix = lsa.lsa();
        double[][] relationDistanceMatrix = new double[lenRelation][lenRelation];
        for (int i = 0; i < lenRelation; i++){
            for (int j = 0; j < lenRelation; j++){
                relationDistanceMatrix[i][j] = 1 - mathUtil.computeCosineSimilarity(decomposedMatrix.getColumnVector(i), decomposedMatrix.getColumnVector(j));
            }
        }

        // 筛选阈值， 获取比较相似的关系对 index
        List<Pair<Integer, Integer>> similarPair = new ArrayList<>();
        for (int i = 0; i < lenRelation; i++){
            for (int j = i + 1; j < lenRelation; j++){
                if (relationDistanceMatrix[i][j] < 0.6)
                    similarPair.add(Pair.of(i, j));
            }
        }

        // 在合并之前，为了满足功能需求，还需要对关系进行分类
        int relationClassCount = 0;
        HashMap<String, Integer> relationClassMap = new HashMap<>();
        List<String> simRelation = new ArrayList<>();
        for (Pair<Integer, Integer> pair: similarPair){
            int leftIndex = pair.getLeft();
            int rightIndex = pair.getRight();
            String leftRelation = relations.get(leftIndex);
            String rightRelation = relations.get(rightIndex);
            simRelation.add(leftRelation);
            simRelation.add(rightRelation);
            if (!relationClassMap.containsKey(leftRelation))
                relationClassMap.put(leftRelation, relationClassCount);
            if (!relationClassMap.containsKey(rightRelation))
                relationClassMap.put(rightRelation, relationClassCount);
            relationClassCount++;
        }

        for (String relation: relations){
            if (simRelation.contains(relation))
                continue;
            relationClassMap.put(relation, relationClassCount);
            relationClassCount++;
        }


        // 获取到了相似的关系对，合并relationNexus的 left and right
        for (Pair<Integer, Integer> pair: similarPair) {
            int leftIndex = pair.getLeft();
            int rightIndex = pair.getRight();
            List<Entity> ll = relationNexus.get(relations.get(leftIndex)).getLeft();
            List<Entity> lr = relationNexus.get(relations.get(leftIndex)).getRight();
            List<Entity> rl = relationNexus.get(relations.get(rightIndex)).getLeft();
            List<Entity> rr = relationNexus.get(relations.get(rightIndex)).getRight();
            // 合并 ll,rl and lr,rr
            for (Entity entity : rl) {
                boolean hasEntity = false;
                for (Entity l : ll) {
                    if (entity.equals(l)) {
                        hasEntity = true;
                        break;
                    }
                }
                if (!hasEntity)
                    ll.add(entity);
            }

            for (Entity entity : rr) {
                boolean hasEntity = false;
                for (Entity l : lr) {
                    if (entity.equals(l)) {
                        hasEntity = true;
                        break;
                    }
                }
                if (!hasEntity)
                    lr.add(entity);
            }

            // 左右set融合后，删除多余的relationNexus, 统一删除右边的
            relationNexus.remove(relations.get(rightIndex));

        }
        return Pair.of(relationClassMap, relationNexus);
    }

    /**
     * 节点分类
     */
    public HashMap<Entity, Integer> geAbstractEntityType(HashMap<String, Pair<List<Entity>, List<Entity>>> mergedRelationNexus) {

        List<Entity> entities = copyFromNode();
        // 是否标注备忘录
        HashMap<Entity, Boolean> markMem = new HashMap<>();
        for (Entity entity: entities){
            markMem.put(entity, false);
        }
        HashMap<Entity, Integer> entityClassMap = new HashMap<>();
        int relationCount = 0;
        for (String retainRelation: mergedRelationNexus.keySet()){
            List<Entity> left = mergedRelationNexus.get(retainRelation).getLeft();
            List<Entity> right = mergedRelationNexus.get(retainRelation).getRight();
            for (Entity le: left){
                if (!markMem.get(le)){
                    entityClassMap.put(le, relationCount);
                    markMem.put(le, true);
                }
            }
            relationCount++;

            for (Entity re: right){
                if (!markMem.get(re)){
                    entityClassMap.put(re, relationCount);
                    markMem.put(re, true);
                }
            }
            relationCount++;
        }


        return entityClassMap;
    }



    public List<Entity> copyFromNode(){
        // 构建svd使用的单词文档矩阵 单词就是实体id，文档是关系
        List<Entity> entities = new ArrayList<>();

        List<Node> nodes = fusionKg.getNodes();
        // 构建nodes对应的entities, 便于查表
        for (Node node : nodes){
            String nodeId = node.getNodeId();
            String nodeName = node.getNodeName();
            String itemId = node.getItemId();

            Entity entity = new Entity(nodeId, nodeName, "-1", itemId);
            entities.add(entity);
        }
        return entities;
    }

    public SchemeExtraction(KG fusionKg) {
        this.fusionKg = fusionKg;
    }

    public void setFusionKg(KG fusionKg) {
        this.fusionKg = fusionKg;
    }

    public KG getFusionKg() {
        return fusionKg;
    }
}
