package publicDataStructure;

public class Triple {
    private String tripleId;//三元组id
    private Entity head;//头实体
    private Entity tail;//尾实体
    private String rela;//关系名称
    private String itemId;//所属条目的id(从这个条目中抽取出来的)


    public Triple(String tripleId, Entity head, Entity tail, String rela, String itemId) {
        this.tripleId = tripleId;
        this.head = head;
        this.tail = tail;
        this.rela = rela;
        this.itemId = itemId;
    }

    public String getTripleId() {
        return tripleId;
    }

    public Entity getHead() {
        return head;
    }

    public Entity getTail() {
        return tail;
    }

    public String getRela() {
        return rela;
    }

    public String getItemId() {
        return itemId;
    }

    public void setTripleId(String tripleId) {
        this.tripleId = tripleId;
    }

    public void setHead(Entity head) {
        this.head = head;
    }

    public void setTail(Entity tail) {
        this.tail = tail;
    }

    public void setRela(String rela) {
        this.rela = rela;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
