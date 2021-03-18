package pipeLine;

import publicDataStructure.KG;

import java.util.List;

public class HeterogeneousRelationRegularize {

    private List<KG> mergedKGs;
    private List<String> rawDocuments;

    public double[][] geUnifiedRelationMatrix() {
        return new double[1][1];
    }

    public HeterogeneousRelationRegularize(List<KG> mergedKGs, List<String> rawDocuments) {
        this.mergedKGs = mergedKGs;
        this.rawDocuments = rawDocuments;
    }

    public List<KG> getMergedKGs() {
        return mergedKGs;
    }

    public List<String> getRawDocuments() {
        return rawDocuments;
    }

    public void setMergedKGs(List<KG> mergedKGs) {
        this.mergedKGs = mergedKGs;
    }

    public void setRawDocuments(List<String> rawDocuments) {
        this.rawDocuments = rawDocuments;
    }
}
