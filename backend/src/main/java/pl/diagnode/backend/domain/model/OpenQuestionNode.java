package pl.diagnode.backend.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Node that asks the user an open-ended question. The raw textual answer
 * is stored verbatim and (optionally) mapped into the profile under {@code mappingKey}.
 */
@Entity
@DiscriminatorValue("OPEN_QUESTION")
public class OpenQuestionNode extends Node {
}
