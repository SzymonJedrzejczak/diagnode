package pl.diagnode.backend.domain.model.nodes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPEN_QUESTION")
public class OpenQuestionNode extends Node {
}
