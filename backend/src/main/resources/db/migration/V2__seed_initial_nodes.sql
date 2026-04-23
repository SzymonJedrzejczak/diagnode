INSERT INTO nodes (id, node_type, node_content, node_key, next_node_id)
VALUES ('4d76cf99-dd10-4ce5-88ff-835091f8c518',
    'INTERLUDE',
    'Hej, fajnie że jesteś! Poznajmy się trochę lepiej',
    'WELCOME_MESSAGE',
    '910afbbf-cf62-4fb2-a410-444c7a26a203');

INSERT INTO nodes (id, node_type, node_content, mapping_key, next_node_id)
VALUES ('910afbbf-cf62-4fb2-a410-444c7a26a203',
    'OPEN_QUESTION',
    'Jak Ci na imię?',
    'userName',
    NULL);