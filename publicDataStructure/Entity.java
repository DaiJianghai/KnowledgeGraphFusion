package publicDataStructure;

import com.sun.source.tree.Tree;

import java.util.*;

public class Entity {
    private String entityId;//实体id
    private String entityName;//实体名称
    private String parentId;//父实体（上一层实体）id
    private String itemId;//所属条目的id(从这个条目中抽取出来的)

    public Entity(String entityId, String entityName, String parentId, String itemId) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.parentId = parentId;
        this.itemId = itemId;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (!Objects.equals(entityName, entity.entityName)) return false;
        if (!entityId.equals(entity.entityId)) return false;
        if (!Objects.equals(parentId, entity.parentId)) return false;
        return Objects.equals(itemId, entity.itemId);
    }

    @Override
    public int hashCode() {
        int result = entityName != null ? entityName.hashCode() : 0;
        result = 31 * result + entityId.hashCode();
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        return result;
    }

    public static void main(String[] args) {
        Entity entity1 = new Entity("1", "hello", "-1", "1");
        Entity entity2 = new Entity("1", "hello", "-1", "1");
        Entity entity3 = new Entity("1","hello","-1", "2");
        Entity entity4 = new Entity("2", "hello", "-1","2");
        Entity entity5 = new Entity("2","world","-1","2");
        Entity entity6 = new Entity("3","thank","-1","1");

        System.out.println(entity1.equals(entity2));
        System.out.println(entity2.equals(entity3));
        System.out.println(entity3.equals(entity4));
        System.out.println(entity4.equals(entity5));
        System.out.println(entity2.equals(entity4));

        HashSet<Entity> entities = new HashSet<>();
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);
        entities.add(entity4);
        entities.add(entity5);
        entities.add(entity6);



        for (Entity entity: entities) {
            System.out.println(entity.toString());
        }
    }
}
