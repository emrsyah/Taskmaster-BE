-- Create regular_tasks table
CREATE TABLE regular_tasks (
    uuid VARCHAR(36) PRIMARY KEY,
    sequence_number BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    priority INTEGER NOT NULL,
    category_id BIGINT,
    user_id BIGINT,
    deadline TIMESTAMP,
    is_archived BOOLEAN DEFAULT FALSE
);

-- Create recurring_tasks table
CREATE TABLE recurring_tasks (
    uuid VARCHAR(36) PRIMARY KEY,
    sequence_number BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    priority INTEGER NOT NULL,
    category_id BIGINT,
    user_id BIGINT,
    is_archived BOOLEAN DEFAULT FALSE
);

-- Create table for recurring task days
CREATE TABLE recurring_task_recurrence_days (
    recurring_task_uuid VARCHAR(36) REFERENCES recurring_tasks(uuid),
    recurrence_days VARCHAR(20)
);

-- Create table for recurring task done dates
CREATE TABLE recurring_task_done_dates (
    recurring_task_uuid VARCHAR(36) REFERENCES recurring_tasks(uuid),
    done_dates TIMESTAMP
); 