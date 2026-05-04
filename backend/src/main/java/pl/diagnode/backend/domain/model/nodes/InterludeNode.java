package pl.diagnode.backend.domain.model.nodes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INTERLUDE")
public class InterludeNode extends Node {
}
