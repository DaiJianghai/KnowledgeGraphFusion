package publicDataStructure;

public class Item {

    private String itemTitle;  //条目标题
    private String itemText;//条目内容
    private String itemId;//条目id
    private String chapterId;//章节号
    private String docId;//文档id
    private Float score = 0.0f; //匹配分数（计算相似度时）
    private String entityIds;//从这个条目中抽取出来的实体的id, id之间用‘,’隔开
    private String tripleIds;//从这个条目中抽取出来的三元组的id, id之间用‘,’隔开


    public Item(String itemTitle, String itemText, String itemId, String chapterId, String docId, Float score, String entityIds, String tripleIds) {
        this.itemTitle = itemTitle;
        this.itemText = itemText;
        this.itemId = itemId;
        this.chapterId = chapterId;
        this.docId = docId;
        this.score = score;
        this.entityIds = entityIds;
        this.tripleIds = tripleIds;
    }

    public Item(String itemText, String itemId) {
        this.itemText = itemText;
        this.itemId = itemId;
    }

    public Item(String s, String s1, String s2) {
        this.itemText = s;
        this.itemId = s1;
        this.docId = s2;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemText() {
        return itemText;
    }

    public String getItemId() {
        return itemId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public String getDocId() {
        return docId;
    }

    public Float getScore() {
        return score;
    }

    public String getEntityIds() {
        return entityIds;
    }

    public String getTripleIds() {
        return tripleIds;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public void setEntityIds(String entityIds) {
        this.entityIds = entityIds;
    }

    public void setTripleIds(String tripleIds) {
        this.tripleIds = tripleIds;
    }
}
