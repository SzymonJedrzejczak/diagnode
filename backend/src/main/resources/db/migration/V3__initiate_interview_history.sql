CREATE TABLE interview_history (
    user_id        VARCHAR(255) NOT NULL,
    current_node_id UUID,
    collected_points JSONB        NOT NULL DEFAULT '{}',
    answers          JSONB        NOT NULL DEFAULT '{}',
    ai_consent_given BOOLEAN      NOT NULL DEFAULT FALSE,
    profile_data     JSONB        NOT NULL DEFAULT '{}',
    updated_at       TIMESTAMP,
    CONSTRAINT pk_interview_history PRIMARY KEY (user_id)
);