CREATE TABLE nodes (
    id UUID PRIMARY KEY,
    node_content TEXT NOT NULL,
    node_type VARCHAR(50) NOT NULL, -- Odpowiada @DiscriminatorColumn
    category VARCHAR(50),           -- Kategoria diagnostyczna
    node_key VARCHAR(100) UNIQUE,    -- Logiczna nazwa węzła
    mapping_key VARCHAR(100),       -- Klucz do mapowania w profilu
    next_node_id UUID REFERENCES nodes(id) -- Wskaźnik na kolejny krok
);