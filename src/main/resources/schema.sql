
CREATE TABLE category (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    processed_from TIMESTAMP,
    processed_to TIMESTAMP NOT NULL,
    PRIMARY KEY (id, processed_to)
);

CREATE TABLE task (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    dead_line TIMESTAMP,
    status VARCHAR(50),
    priority_status VARCHAR(50),
    assigned_to VARCHAR(255),
    reported_by VARCHAR(255),
    processed_from TIMESTAMP,
    processed_to TIMESTAMP NOT NULL,
    category_id UUID,
    category_processed_to TIMESTAMP,
    PRIMARY KEY (id, processed_to),
    FOREIGN KEY (category_id, category_processed_to) REFERENCES category(id, processed_to)
);
