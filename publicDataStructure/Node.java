package publicDataStructure;

public class Node {

    private String nodeId;//实体id
    private String nodeName;//实体名称
    private String parentId;//父实体（上一层实体）id
    private String itemId;//所属条目的id(从这个条目中抽取出来的)

    public Node(String nodeId, String nodeName, String parentId, String itemId) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.parentId = parentId;
        this.itemId = itemId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getParentId() {
        return parentId;
    }

    public String getItemId() {
        return itemId;
    }

    public static void main(String[] args) {

    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
