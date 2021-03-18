package test;
import Jama.Matrix;
import Jama.SingularValueDecomposition;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2019年3月29日
 *
 * TODO Java版LSA
 *
 * @author jiebaHZ
 */
public class testLSA {

    private List<String> stopwords;
    private List<String> docs;//注意这里输入的文档是分词之后的
    private Matrix matrix;
    private Map<String, List<Integer>> dictionary = new HashMap<String, List<Integer>>();
    private List<String> keywords = new ArrayList<String>();
    // 维数
    private static int LSD = 2;

    public testLSA(List<String> docs) {
        this.docs = docs;
    }

    public void lsa() {
        // 读取停用词
        stopwords = readStopwords();
        // 过滤停用词
        removeStopwords();
        // 生成单词字典
        createDictionary();
        // 得到关键词
        addKeywords();
        // 生成单词-文档矩阵
        createMatrix();
        // SVD分解，降维
        Matrix v = SVD();
        printMatrix(v);
        // 计算相似度
        //到这里就得到了矩阵v的转置。然后根据自己的需要就可以计算每两个向量的夹角余弦
        //……
    }

    /**
     * 读取停用词
     */
    public static List<String> readStopwords() {

        ArrayList<String> stopwords = new ArrayList<String>();
        // 读取停用词表
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("D:\\IntelliJ IDEA 2020.1.2\\Project\\KGFusion_edition2\\src\\preprocessing\\stopwords.txt"), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stopwords.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }

    /**
     * 过滤停用词
     */
    public void removeStopwords() {
        for (int i = 0; i < docs.size(); i++) {
            String[] doc = docs.get(i).split(" ");
            List<String> words = new ArrayList<String>();
            for (String string : doc) {
                words.add(string);
            }
            words.removeAll(stopwords);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < words.size(); j++) {
                sb.append(words.get(j));
                sb.append(" ");
                sb.toString();
            }
            docs.set(i, sb.toString().trim());
        }
    }

    /**
     * 记录每个单词出现在哪些文档中
     */
    public void createDictionary() {
        for (int i = 0; i < docs.size(); i++) {
            String[] words = docs.get(i).split(" ");
            for (String word : words) {
                if (dictionary.containsKey(word)) {
                    dictionary.get(word).add(i);
                } else {
                    List<Integer> idList = new ArrayList<Integer>();
                    idList.add(i);
                    dictionary.put(word, idList);
                }
            }
        }
    }

    /**
     * 得到关键词列表
     */
    public void addKeywords() {
        for (String word : dictionary.keySet()) {
            if (dictionary.get(word).size() >= 1) {
                keywords.add(word);
            }
        }
    }

    /**
     * 生成单词-文档矩阵
     */
    public void createMatrix() {
        double array[][] = new double[keywords.size()][docs.size()];
        matrix = new Matrix(array);
        for (int i = 0; i < keywords.size(); i++) {
            for (Integer j : dictionary.get(keywords.get(i))) {
                matrix.set(i, j, matrix.get(i, j) + 1);
            }
        }
    }

    /**
     * 打印矩阵
     *
     * @param matrix
     */
    public void printMatrix(Matrix matrix) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                System.out.printf("m(%d,%d) = %g\t", i, j, matrix.get(i, j));
            }
            System.out.printf("\n");
        }
    }

    /**
     * SVD分解，降维
     */
    public Matrix SVD() {
        SingularValueDecomposition svd = matrix.svd();
        // 注意，这里是v的转置
        Matrix v = svd.getV().transpose();
//        for (int i = LSD; i < v.getRowDimension(); i++) {
//            for (int j = 0; j < docs.size(); j++) {
//                v.set(i, j, 0.0);
//            }
//        }
        return v;
    }

    /**
     * 计算夹角余弦值
     *
     * @param v1
     * @param v2
     */
    double cos(List<Double> v1, List<Double> v2, int dim) {
        // Cos(theta) = A(dot)B / |A||B|
        double a_dot_b = 0;
        for (int i = 0; i < dim; i++) {
            a_dot_b += v1.get(i) * v2.get(i);
        }
        double A = 0;
        for (int j = 0; j < dim; j++) {
            A += v1.get(j) * v1.get(j);
        }
        A = Math.sqrt(A);
        double B = 0;
        for (int k = 0; k < dim; k++) {
            B += v2.get(k) * v2.get(k);
        }
        B = Math.sqrt(B);
        return a_dot_b / (A * B);
    }

    public static void main(String[] args) {

        List<String> items = new ArrayList<>();
        String item1 = "皇马 的 球员 C罗 今天 退役";
        String item2 = "梅西 是 一位 年轻 有 天赋 的 足球运动员 皇马";
        String item3 = "中国科学技术大学 在 今天 实现 了 用 机器学习 来 预测 地震";
        String item4 = "复旦大学 在 自然语言处理 和 知识图谱 领域 发表了 多篇 论文";
        String item5 = "中国 在 新冠病毒 疫情 中 做出了 重要 贡献 自然语言处理";

        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

        items.add(item5);


        testLSA LSA = new  testLSA(items);
        LSA.lsa();
    }
}

