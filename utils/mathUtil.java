package utils;

public class mathUtil {
    public static double computeCosineSimilarity(double[] vector1, double[] vector2){

        double innerDot = 0;
        double vector1mod = 0;
        double vector2mod = 0;
        for (int i = 0; i < vector1.length; i++){
            double tempVal = vector1[i] * vector2[i];
            innerDot += tempVal;
        }

        for (int i = 0; i < vector1.length; i++){
            vector1mod += Math.pow(vector1[i], 2);
            vector2mod += Math.pow(vector2[i], 2);
        }
        return innerDot / (Math.sqrt(vector1mod) * Math.sqrt(vector2mod));
    }
}
