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

}
