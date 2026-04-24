package pl.diagnode.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pl.diagnode.backend.domain.model.enums.Category;
import pl.diagnode.backend.domain.model.enums.NodeType;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Base entity representing a step (action) in the interview graph.
 * Uses single-table inheritance for fast reads — all node subtypes live in one table.
 */
@Entity
@Table(name = "nodes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "node_type")
public abstract class Node {

    @Id
    private UUID id;

    /** Question or message text sent to the user. */
    @Column(name = "node_content", nullable = false)
    private String nodeContent;

    /**
     * Read-only mirror of the discriminator column; Hibernate manages the value
     * based on the concrete subclass.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", insertable = false, updatable = false)
    private NodeType nodeType;

    /** Diagnostic category (e.g. DEPRESSION, ANXIETY) — used for aggregating points. */
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    /** Logical, human-readable key (e.g. {@code WELCOME_START}) for lookup without UUID. */
    @Column(name = "node_key", unique = true)
    private String nodeKey;

    /** Pointer to the next step (singly linked list of nodes). */
    @ManyToOne
    @JoinColumn(name = "next_node_id")
    private Node nextNode;

    /** Key under which the user's answer is stored in the profile data map. */
    @Column(name = "mapping_key")
    private String mappingKey;

    protected Node() {
        // required by JPA
    }

    public UUID getId() {
        return id;
    }

    public String getNodeContent() {
        return nodeContent;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Category getCategory() {
        return category;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public Node getNextNode() {
        return nextNode;
    }

    /** Null-safe accessor for the mapping key. */
    public Optional<String> getMappingKey() {
        return Optional.ofNullable(mappingKey);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Node node)) return false;
        return id != null && Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        // Constant hashCode is a recommended practice for JPA entities with generated IDs.
        return Objects.hashCode(id);
    }
}
