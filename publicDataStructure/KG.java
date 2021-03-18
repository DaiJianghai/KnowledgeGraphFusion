package publicDataStructure;

import java.util.LinkedHashMap;
import java.util.List;

public class KG {

    private List<Node> nodes; //Node就是Entity
    private List<Triple> triples;
    private LinkedHashMap<Node, List<Node>> edges;//描述了每个节点及其相邻节点（对应与该节点相连的多条边）。可由triples生成//相当于图论中的List<Edge> edges;
    private LinkedHashMap<Node, List<Integer>> directions; //方向

    public KG(List<Node> nodes, List<Triple> triples, LinkedHashMap<Node, List<Node>> edges, LinkedHashMap<Node, List<Integer>> directions) {
        this.nodes = nodes;
        this.triples = triples;
        this.edges = edges;
        this.directions = directions;
    }

    public KG(List<Node> nodes, List<Triple> triples) {
        this.nodes = nodes;
        this.triples = triples;
    }

    public KG() {

    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Triple> getTriples() {
        return triples;
    }

    public LinkedHashMap<Node, List<Node>> getEdges() {
        return edges;
    }

    public LinkedHashMap<Node, List<Integer>> getDirections() {
        return directions;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void setTriples(List<Triple> triples) {
        this.triples = triples;
    }

    public void setEdges(LinkedHashMap<Node, List<Node>> edges) {
        this.edges = edges;
    }

    public void setDirections(LinkedHashMap<Node, List<Integer>> directions) {
        this.directions = directions;
    }
}
