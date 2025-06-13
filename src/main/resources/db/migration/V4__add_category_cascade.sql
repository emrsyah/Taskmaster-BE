-- Drop existing foreign key constraints if they exist
ALTER TABLE regular_tasks DROP CONSTRAINT IF EXISTS fk_regular_tasks_category;
ALTER TABLE recurring_tasks DROP CONSTRAINT IF EXISTS fk_recurring_tasks_category;

-- Add new foreign key constraints with cascade
ALTER TABLE regular_tasks
    ADD CONSTRAINT fk_regular_tasks_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE recurring_tasks
    ADD CONSTRAINT fk_recurring_tasks_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE; 