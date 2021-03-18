package utils;

import javax.swing.*;
import java.math.*;
import java.util.ArrayList;

public class Distance {

    public static void main(String[] args) {

        double editDistance = computeEditDistance("人事经理", "人资专员");
        System.out.println("editSimilarity is:" + editDistance);
    }


    public static double computeEditDistance(String str1, String str2) {
        // 计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        // 建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        // 赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        // 计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {

//                System.out.println("i = " + i + " j = " + j + " str1 = "
//                        + str1.charAt(i - 1) + " str2 = " + str2.charAt(j - 1));
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);

//                System.out.println("i = " + i + ", j = " + j + ", dif[i][j] = "
//                        + dif[i][j]);
            }
        }
//        System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        // 取数组右下角的值，同样不同位置代表不同字符串的比较
//        System.out.println("差异步骤：" + dif[len1][len2]);
        // 计算相似度
        double similarity = 1 - ((double) dif[len1][len2]
                / Math.max(str1.length(), str2.length()));
//        System.out.println("相似度：" + similarity);

        return similarity;
    }


    public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static double[][] geRelationSimilarityMatrix(String[] relationName) {
        int length = relationName.length;
        double[][] resMatrix = new double[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                resMatrix[i][j] = computeEditDistance(relationName[i], relationName[j]);
            }
        }

        return resMatrix;
    }


}

