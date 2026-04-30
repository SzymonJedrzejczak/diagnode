package pl.diagnode.backend.api;

import pl.diagnode.backend.domain.model.enums.NodeType;

import java.util.List;

public record NodeResponse(NodeType type, String content, String mappingKey, List<String> options) {
}