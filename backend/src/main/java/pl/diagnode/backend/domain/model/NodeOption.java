package pl.diagnode.backend.domain.model;

import jakarta.persistence.*;
import pl.diagnode.backend.domain.model.nodes.Node;

@Entity
@Table(name = "node_options")
public class NodeOption {

    @Id
    private String id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private int score;

    @ManyToOne(optional = false)
    @JoinColumn(name = "node_id")
    private Node node;

    @ManyToOne(optional = false)
    @JoinColumn(name = "next_node_id")
    private Node nextNode;

    public String id() {
        return id;
    }

    public NodeOption setId(String id) {
        this.id = id;
        return this;
    }

    public String label() {
        return label;
    }

    public NodeOption setLabel(String label) {
        this.label = label;
        return this;
    }

    public int score() {
        return score;
    }

    public NodeOption setScore(int score) {
        this.score = score;
        return this;
    }

    public Node node() {
        return node;
    }

    public NodeOption setNode(Node node) {
        this.node = node;
        return this;
    }

    public Node nextNode() {
        return nextNode;
    }

    public NodeOption setNextNode(Node nextNode) {
        this.nextNode = nextNode;
        return this;
    }
}
