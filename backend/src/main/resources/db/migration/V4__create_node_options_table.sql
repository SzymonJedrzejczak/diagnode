CREATE TABLE node_options
(
    id           VARCHAR(255) PRIMARY KEY,
    label        VARCHAR(255) NOT NULL,
    score        INTEGER      NOT NULL,
    node_id      UUID         NOT NULL REFERENCES nodes (id),
    next_node_id UUID         NOT NULL REFERENCES nodes (id)
);