package pl.diagnode.backend.domain.model.nodes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import pl.diagnode.backend.domain.model.NodeOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@DiscriminatorValue("SINGLE_ANSWER_QUESTION")
public class SingleAnswerQuestionNode extends Node {

    @OneToMany(mappedBy = "node", fetch = EAGER)
    private List<NodeOption> options = new ArrayList<>();

    public Optional<List<NodeOption>> getOptions() {
        return Optional.of(options);
    }
}
