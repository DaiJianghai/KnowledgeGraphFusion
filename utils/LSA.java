package utils;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.*;


public class LSA {

    private Matrix matrix;

    private int truncatedDimension = Integer.MAX_VALUE;

    public LSA(double[][] matrix) {
        this.matrix = Matrix.constructWithCopy(matrix);
    }

    public void setTruncatedDimension(int dimension){
        this.truncatedDimension = dimension;
    }

    public Matrix lsa() {

        Matrix u = geU();
        Matrix s = geS();
        Matrix v = geV();

        return transform(u, s , v);
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
    public Matrix geV() {
        SingularValueDecomposition svd = matrix.svd();
        // 注意，这里是v的转置
        Matrix v = svd.getV().transpose();
        return v;
    }

    public Matrix geU() {
        SingularValueDecomposition svd = matrix.svd();
        Matrix u = svd.getU();
        return u;
    }

    public Matrix geS() {
        SingularValueDecomposition svd = matrix.svd();
        Matrix s = svd.getS();
        return s;
    }


    /**
     * 计算相似度
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

    public Matrix transform(Matrix u, Matrix s, Matrix v){
        int notZeroLength = 0;
        for (int i = 0; i < s.getRowDimension(); i++){
            if (s.get(i,i) >= 0){
                notZeroLength++; // 统计>=0的特征值个数
            }
        }

        Matrix newUMatrix = new Matrix(u.getRowDimension(), notZeroLength);
        Matrix newSMatrix = new Matrix(notZeroLength, notZeroLength);
        Matrix newVMatrix = new Matrix(notZeroLength, v.getColumnDimension());

        if (truncatedDimension < Math.min(matrix.getRowDimension(), matrix.getColumnDimension())){

            for (int i = 0; i < u.getRowDimension(); i++){
                for (int j = 0; j < truncatedDimension; j++){
                    newUMatrix.set(i, j, u.get(i,j));
                }
            }
            for (int i = 0; i < truncatedDimension; i++){
                for (int j = 0; j < truncatedDimension; j++){
                    newSMatrix.set(i, j, s.get(i,j));
                }
            }
            for (int i = 0; i < truncatedDimension; i++){
                for (int j = 0; j < v.getColumnDimension(); j++){
                    newVMatrix.set(i, j, v.get(i, j));
                }
            }

            return newUMatrix.times(newSMatrix).times(newVMatrix);
        }

        for (int i = 0; i < u.getRowDimension(); i++){
            for (int j = 0; j < notZeroLength; j++){
                newUMatrix.set(i, j, u.get(i,j));
            }
        }
        for (int i = 0; i < notZeroLength; i++){
            for (int j = 0; j < notZeroLength; j++){
                newSMatrix.set(i, j, s.get(i,j));
            }
        }
        for (int i = 0; i < notZeroLength; i++){
            for (int j = 0; j < v.getColumnDimension(); j++){
                newVMatrix.set(i, j, v.get(i, j));
            }
        }

        return newUMatrix.times(newSMatrix).times(newVMatrix);
    }

    /**
     * 计算cos距离
     *
     * @param matrix
     */
    public Matrix cosDistance(Matrix matrix){
        int length = matrix.getColumnDimension();
        Matrix res = new Matrix(matrix.getRowDimension(), matrix.getRowDimension());
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getRowDimension(); j++) {
                ArrayList<Double> vectorX = new ArrayList<>();
                ArrayList<Double> vectorY = new ArrayList<>();
                double[] x = matrix.getMatrix(i, i, 0, matrix.getColumnDimension() - 1).getArray()[0];
                double[] y = matrix.getMatrix(j, j, 0, matrix.getColumnDimension() - 1).getArray()[0];

                for (double d : x) {
                    vectorX.add(d);
                }

                for (double d : y) {
                    vectorY.add(d);
                }

                res.set(i, j, 1 - cos(vectorX, vectorY, length));
            }
        }

        return res;
    }

    public static void main(String[] args) {
//        List<String> items = new ArrayList<>();
//        String item1 = "皇马 的 球员 C罗 今天 退役";
//        String item2 = "梅西 是 一位 年轻 有 天赋 的 足球运动员 皇马";
//        String item3 = "中国 中国科学技术大学 在 今天 实现 了 用 机器学习 来 预测 地震";
//        String item4 = "复旦大学 在 自然语言处理 和 知识图谱 领域 发表了 多篇 论文";
//        String item5 = "中国 在 新冠病毒 复旦大学 疫情 中 做出了 重要 贡献 自然语言处理";
//
//        items.add(item1);
//        items.add(item2);
//        items.add(item3);
//        items.add(item4);
//        items.add(item5);

        int M = 100;
        int N = 20;

        double [][] m = new double[M][N];
        for(int i = 0 ; i < M ; i ++ ) {
            m[i] = new double[N];
            for(int j = 0 ; j < N ; j++) {
                m[i][j] = Math.random();
            }
        }


//        LSA lsa = new LSA(m);
//        double[][] res = lsa.lsa();
//        System.out.println(res.length);
//        System.out.println(res[0].length);
//        for (int i = 0; i < res.length; i++){
//            for (int j = 0; j < res[0].length; j++){
//                System.out.print(res[i][j]+" ");
//            }
//            System.out.println();
//        }
    }
}
