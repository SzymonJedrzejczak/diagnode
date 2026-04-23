package pl.diagnode.backend.domain.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.diagnode.backend.domain.model.Node;

import java.util.Optional;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<@NonNull Node, @NonNull UUID> {

    @Cacheable
    Optional<Node> findByNodeKey(String nodeKey);

}
