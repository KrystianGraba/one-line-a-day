CREATE TABLE analytics_events (
    id UUID PRIMARY KEY,
    event VARCHAR(255) NOT NULL,
    user_id UUID,
    metadata TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_analytics_user ON analytics_events(user_id);
CREATE INDEX idx_analytics_event ON analytics_events(event);
