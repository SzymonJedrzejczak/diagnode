package pl.diagnode.backend.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INTERLUDE")
public class InterludeNode extends Node {
}
