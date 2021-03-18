package pipeLine;

import publicDataStructure.KG;

import java.util.ArrayList;
import java.util.List;

public class KGsCleaning {

    private List<KG> KGList;

    public List<KG> runClean() {
        return process();
    }

    public List<KG> process() {
        List<KG> KGList = new ArrayList<>();
        for (KG kg : KGList) {
            KG cleanedKG = processPerKG(kg);
            KGList.add(cleanedKG);
        }


        return KGList;
    }

    static class cleanTool {


        public KG grammarRegularize(KG kg) {
            return new KG();
        }

        public KG spellingCorrect(KG kg) {
            return new KG();
        }

        public KG fullSimpleConvert(KG kg) {
            return new KG();
        }

        public KG stopWordsDelete(KG kg) {
            return new KG();
        }

        //...
    }

    public KG processPerKG(KG KGList) {
        return new KG();
    }

    public KGsCleaning(List<KG> KGList) {
        this.KGList = KGList;
    }

    public List<KG> getKGList() {
        return KGList;
    }

    public void setKGList(List<KG> KGList) {
        this.KGList = KGList;
    }


}
