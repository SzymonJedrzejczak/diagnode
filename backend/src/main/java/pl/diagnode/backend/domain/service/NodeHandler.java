package pl.diagnode.backend.domain.service;

import pl.diagnode.backend.domain.model.enums.NodeType;

public interface NodeHandler {

    /** The node type this handler is responsible for. */
    NodeType getSupportedType();

}
