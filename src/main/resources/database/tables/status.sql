CREATE TABLE status
(
    status              VARCHAR(256),
    description         VARCHAR(256),
    created_on          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT,
    updated_on          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by          BIGINT,

    CONSTRAINT pk_status PRIMARY KEY (status)
);
