-- Create the task sequence
CREATE SEQUENCE IF NOT EXISTS task_sequence START WITH 1 INCREMENT BY 1;

-- Add UUID column to regular_tasks if it doesn't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'regular_tasks' 
        AND column_name = 'uuid'
    ) THEN
        ALTER TABLE regular_tasks ADD COLUMN uuid VARCHAR(36) PRIMARY KEY;
    END IF;
END $$;

-- Add sequence_number column to regular_tasks if it doesn't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'regular_tasks' 
        AND column_name = 'sequence_number'
    ) THEN
        ALTER TABLE regular_tasks ADD COLUMN sequence_number BIGINT;
    END IF;
END $$;

-- Add UUID column to recurring_tasks if it doesn't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'recurring_tasks' 
        AND column_name = 'uuid'
    ) THEN
        ALTER TABLE recurring_tasks ADD COLUMN uuid VARCHAR(36) PRIMARY KEY;
    END IF;
END $$;

-- Add sequence_number column to recurring_tasks if it doesn't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'recurring_tasks' 
        AND column_name = 'sequence_number'
    ) THEN
        ALTER TABLE recurring_tasks ADD COLUMN sequence_number BIGINT;
    END IF;
END $$; 