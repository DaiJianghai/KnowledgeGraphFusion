package pipeLine;

import java.io.IOException;
import java.util.*;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.lang3.tuple.Pair;
import publicDataStructure.Entity;
import publicDataStructure.Item;
import publicDataStructure.KG;
import publicDataStructure.Node;
import publicDataStructure.Triple;
import preprocessing.ConstructGraph;
import static preprocessing.ConstructGraph.prepareKGs;


public class KGsMergeBasedOnContent {
    private List<KG> cleanedKGs;
    private List<Item> rawDocuments;
    public KG runMerge() throws IOException {
        return process();
    }
    public KG process() throws IOException {

        List<KG> kgs=getCleanedKGs();
        List<Item> items=getRawDocuments();
        List<String> simNodeIds=MergeTool.ComputeSim(MergeTool.ComputeTF(kgs,items));

        List<Entity> tempentitys = new ArrayList<Entity>();
        List<Entity> entitys = new ArrayList<Entity>();//entitylist集合用于修改
        List<Entity> entitys1 = new ArrayList<Entity>();//entitylist集合用于查询元素（不修改）
        List<Triple> temptriples = new ArrayList<Triple>();
        List<Triple> triples = new ArrayList<Triple>();
        List<Triple> newtriples = new ArrayList<Triple>();
        for (KG kg : kgs) {
            temptriples = kg.getTriples();
            for (int j = 0; j < temptriples.size(); j++) {
                triples.add(temptriples.get(j));
            }
        }
        for (Triple triple : triples) {
            tempentitys.add(triple.getHead());
        }
        for (Triple triple : triples) {
            tempentitys.add(triple.getTail());
        }
        HashMap<String, Integer> temp1 = new HashMap<String, Integer>();
        for (Entity entity : tempentitys) {
            if (!temp1.containsKey(entity.getEntityId())) {
                temp1.put(entity.getEntityId(), 1);
                entitys.add(entity);
                entitys1.add(entity);
            }
        }
        //合并过程
        for(Triple triple:triples){//triple中的entity集合去重
            for(Entity entity:entitys) {
                if (triple.getHead().getEntityId().equals(entity.getEntityId())) {
                    triple.setHead(entity);
                } else if (triple.getTail().getEntityId().equals(entity.getEntityId())) {
                    triple.setTail(entity);
                }
            }
        }
        int simsize=simNodeIds.size()-1;//寻找被合并ID最小的结点
        for (int i = simsize; i >= 0; i--) {
            String[] pair1 = simNodeIds.get(i).split(",");
            String s1=pair1[0];
            String s2=pair1[1];
            for(int j = i-1; j >= 0; j--){
                String[] pair2 = simNodeIds.get(j).split(",");
                if(pair2[1].equals(s1)){
                    s1=pair2[0];
                }
            }
            simNodeIds.set(i,s1+","+s2);
        }
        HashSet set1 = new HashSet();//simnodeids去重
        List newList = new ArrayList();
        for (Iterator iter = simNodeIds.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set1.add(element))
                newList.add(element);
        }
        simNodeIds.clear();
        simNodeIds.addAll(newList);//去重结束
        List<String> removeid = new ArrayList<String>();//用于存储要删除的结点ID集
        for (int k = 0; k < simNodeIds.size(); k++) {
            for (Entity entity : entitys) {
                String[] simnodepair = simNodeIds.get(k).split(",");//相似结点对存储在一维数组
                if (entity.getEntityId().equals(simnodepair[1])) {//相似对中后一个相似的结点 更改为前一个结点
                    for (Triple triple:triples) {
                        Entity entity1 = triple.getHead();
                        Entity entity2 = triple.getTail();
                        if (entity2.getEntityId().equals(entity.getEntityId())) {//修改三元组
                            int b = Integer.parseInt(simnodepair[0]) - 1;
                            triple.setTail(entitys1.get(b));
                        } else if (entity1.getEntityId().equals(entity.getEntityId())) {
                            int b = Integer.parseInt(simnodepair[0]) - 1;
                            triple.setHead(entitys1.get(b));
                        }
                    }
                    String i = entity.getEntityId();//修改完成三元组，记录需要删去的已合并的结点ID
                    if (!removeid.contains(i)) {
                        removeid.add(i);
                    }
                }
            }
        }
        for (int iu = 0; iu < removeid.size(); iu++) {//通过记录的结点ID删除结点
            for (Entity entity : entitys) {
                if (entity.getEntityId().equals(removeid.get(iu))) {
                    entitys.remove(entity);
                    break;
                }
            }
        }
        removeid.clear();//清空以记录头尾实体相同的三元组的实体ID并删除。

        HashMap<String,Integer> map1 = new HashMap<>();//将新生成的triple放入newtriples
        for (Entity entity : entitys) {
            map1.put(entity.getEntityId(), 1);
        }
        List<Integer> removelist=new ArrayList<>();//需删除的三元组ID list
        List<String> headtailids= new ArrayList<>(); //头尾实体ID LIST
        for(Triple triple:triples){
            String headid=triple.getHead().getEntityId();
            String tailid=triple.getTail().getEntityId();
            if(headid.equals(tailid)){//头尾实体相同，舍去三元组
                if(!removeid.contains(headid)){
                    removeid.add(headid);//同时去掉该实体
                }
                removelist.add(Integer.parseInt(triple.getTripleId()));//三元组删除ID集合
            }
            headtailids.add(headid+","+tailid);
        }
        for(int i=0;i<headtailids.size();i++){//三元组去重
            String first = headtailids.get(i);
            List<Integer> relist=new ArrayList<>();
            for(int j=i+1;j<headtailids.size();j++){
                if(first.equals(headtailids.get(j))){
                    relist.add(j+1);
                }
            }
            if(relist.size()!=0){
                removelist.addAll(relist);
            }
        }
        for (int iu = 0; iu < removelist.size(); iu++) {//通过记录的三元组ID删除三元组
            for (Triple triple : triples) {
                if (Integer.parseInt(triple.getTripleId())==(removelist.get(iu))) {
                    triples.remove(triple);
                    break;
                }
            }
        }
        List<String> removeids=new ArrayList<>();//如果头尾实体相同的三元组中的实体在别的三元组出现过，则不能删除
        for(int iu = 0; iu < removeid.size(); iu++){
            int count=0;
            for(Triple triple:triples){
                if(triple.getHead().getEntityId().equals(removeid.get(iu))||triple.getTail().getEntityId().equals(removeid.get(iu))) {
                    count++;
                }
            }
            if(count>=1){
                String id = removeid.get(iu);
                removeids.add(id);
            }
        }
        for(int iu = 0; iu < removeids.size(); iu++) {//在待删结点idList中去掉在别的三元组出现过的实体ID
            for(String id :removeid){
                if(removeids.get(iu).equals(id)){
                    removeid.remove(id);
                    break;
                }
            }
        }
        for (int iu = 0; iu < removeid.size(); iu++) {//通过记录的结点ID删除结点
            for (Entity entity : entitys) {
                if (entity.getEntityId().equals(removeid.get(iu))) {
                    entitys.remove(entity);
                    break;
                }
            }
        }

        List<Node> newNodes = new ArrayList<Node>();//修改完成，将新的ENtity集合转成newnodelist
        for (Entity entity : entitys) {
            String nodeid = entity.getEntityId();
            String nodename = entity.getEntityName();
            String parentid = entity.getParentId();
            String itemid = entity.getItemId();
            Node node = new Node(nodeid, nodename, parentid, itemid);
            newNodes.add(node);
        }
        List<String> headtailids1= new ArrayList<>();//修改完成，将新的triple集合转为newtriples集合
        for(Triple triple:triples){
            String headid=triple.getHead().getEntityId();
            String tailid=triple.getTail().getEntityId();
            headtailids1.add(headid+","+tailid);
        }
        List<Integer> tripleids=new ArrayList<>();
        for(int c=0;c<headtailids1.size();c++){
            String[] tempid=headtailids1.get(c).split(",");
            if(map1.containsKey(tempid[0])&& map1.containsKey(tempid[1])){
                tripleids.add(c);
            }
        }
        for(int id:tripleids){
            newtriples.add(triples.get(id));
        }
        return new KG(newNodes,newtriples);
    }
    static class MergeTool {
        static double[][] tfidfMatrix = new double[332][136];//矩阵存放TFIDF
        static double[][] simMartix = new double[332][332];//矩阵存放单词相似度
        static List<String> simNodeIds = new ArrayList<>();//List存放相似度大于0.9的实体ID对
        static public double[][] ComputeTF(List<KG> kgs, List<Item> items) {
            List<Node> tempentitys = new ArrayList<Node>();
            List<Node> entitys = new ArrayList<Node>();
            int j = 1;
            for (KG kg : kgs) {
                tempentitys = kg.getNodes();
                for (int i = 0; i < tempentitys.size(); i++) {
                    entitys.add(tempentitys.get(i));
                }
            }
            List<String> cutWords = new ArrayList<String>();
            //分词模块
            JiebaSegmenter segmenter = new JiebaSegmenter();
            for (Item item : items) {
                String text = item.getItemText();
                String result = segmenter.sentenceProcess(text).toString();
                cutWords.add(result);
            }
            //存放（单词，单词数量）
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            //存放（单词，单词词频）
            HashMap<String, Double> tf = new HashMap<String, Double>();
            //存放（单词，tfidf）
            HashMap<String, Double> tfidf = new HashMap<String, Double>();
            //计算TFIDF矩阵
            for (String word : cutWords) {
                String[] k = word.split(",");
                int length = k.length + 1;
                int count = 0;
                for (Node node : entitys) {
                    for (int i = 0; i < k.length; i++) {
                        String te = node.getNodeName();
                        if (k[i].contains(te)) {
                            count++;
                        }
                    }
                    dict.put(node.getNodeId(), count);
                    count = 0;
                    for (Map.Entry<String, Integer> entry : dict.entrySet()) {
                        double wordTf = (double) entry.getValue() / length;
                        tf.put(entry.getKey(), wordTf);
                    }
                }
                int D = 135; //总条目数目
                for (Node node : entitys) {
                    int Dt = 0;// Dt为出现该实体的条目数目
                    for (String word1 : cutWords) {
                        if (word1.contains(node.getNodeName())) {
                            Dt++;
                        }
                    }
                    double idfvalue = (double) Math.log(Float.valueOf(D) / 1 + Dt);
                    tfidf.put(node.getNodeId(), idfvalue * tf.get(node.getNodeId()));
                }
                for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
                    String idd = entry.getKey();
                    int i = Integer.parseInt(idd);
                    double vv = entry.getValue();
                    tfidfMatrix[i][j] = vv;
                }
                j++;
            }
            return tfidfMatrix;
        }// 方法：计算TF-IDF矩阵
        static public List<String> ComputeSim(double[][] martix) {
            for (int i = 1; i <= martix.length - 1; i++) {
                ArrayList va = new ArrayList();
                for (int j = 1; j <= martix[i].length - 1; j++) { // va=martix[1][]
                    va.add(martix[i][j]);
                }
                for (int a = 1; a <= martix.length - 1; a++) {
                    ArrayList vb = new ArrayList();
                    for (int b = 1; b <= martix[a].length - 1; b++) { //vb=martix[1][] [2][] [3][]...[331][]
                        vb.add(martix[a][b]);
                    }
                    int size = va.size();
                    double simVal = 0;
                    double num = 0;
                    double den = 1;
                    double powa_sum = 0;
                    double powb_sum = 0;
                    for (int k = 0; k < size; k++) {
                        String ssa = String.valueOf(va.get(k));
                        String ssb = String.valueOf(vb.get(k));
                        double sa = Double.parseDouble(ssa);
                        double sb = Double.parseDouble(ssb);
                        num = num + sa * sb;
                        powa_sum = powa_sum + (double) Math.pow(sa, 2);
                        powb_sum = powb_sum + (double) Math.pow(sb, 2);
                    }
                    double sqrta = (double) Math.sqrt(powa_sum);
                    double sqrtb = (double) Math.sqrt(powb_sum);
                    den = sqrta * sqrtb;
                    simVal = num / den;
                    simMartix[i][a] = simVal;
                }
            }
            for (int a = 1; a < simMartix.length; a++) {
                for (int b = 1; b < simMartix[a].length; b++) {
                    if (a < b && simMartix[a][b] >= 0.9) {
                        simNodeIds.add(a + "," + b);
                    }
                }
            }
            return simNodeIds;

        } //方法：计算单词相似度矩阵

        // function2
        // function3
        // ...

        public static List<Pair<Node, Node>> geBagsOfSimilarWords(KG kg) {
            return new ArrayList<>();
        }
    }

    public static class Encoder {


        public static HashMap<String, Integer> geEntityToIdxMapping(KG kg) {
            return new HashMap<>();
        }

        public static HashMap<String, Integer> geRelationToIdxMapping(KG kg) {
            return new HashMap<>();
        }

        public static HashMap<Integer, String> geIdxToEntityMapping(HashMap<String, Integer> entityToIdxDict) {
            return new HashMap<>();
        }

        public static HashMap<Integer, String> geIdxToRelationMapping(HashMap<String, Integer> relationToIdxDict) {
            return new HashMap<>();
        }


    }


    public KGsMergeBasedOnContent(List<KG> cleanedKGs, List<Item> rawDocuments) {
        this.cleanedKGs = cleanedKGs;
        this.rawDocuments = rawDocuments;
    }

    public List<KG> getCleanedKGs() {
        return cleanedKGs;
    }

    public void setCleanedKGs(List<KG> cleanedKGs) {
        this.cleanedKGs = cleanedKGs;
    }

    public void setRawDocuments(List<Item> rawDocuments) {
        this.rawDocuments = rawDocuments;
    }

    public List<Item> getRawDocuments() {
        return rawDocuments;
    }

    public static void main(String[] args) {

    }
}
