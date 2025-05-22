-- Drop existing primary key constraints if they exist
DO $$ 
BEGIN 
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints 
        WHERE table_name = 'regular_tasks' 
        AND constraint_type = 'PRIMARY KEY'
    ) THEN
        ALTER TABLE regular_tasks DROP CONSTRAINT regular_tasks_pkey;
    END IF;
END $$;

DO $$ 
BEGIN 
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints 
        WHERE table_name = 'recurring_tasks' 
        AND constraint_type = 'PRIMARY KEY'
    ) THEN
        ALTER TABLE recurring_tasks DROP CONSTRAINT recurring_tasks_pkey;
    END IF;
END $$;

-- Drop the old id columns if they exist
DO $$ 
BEGIN 
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'regular_tasks' 
        AND column_name = 'id'
    ) THEN
        ALTER TABLE regular_tasks DROP COLUMN id;
    END IF;
END $$;

DO $$ 
BEGIN 
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'recurring_tasks' 
        AND column_name = 'id'
    ) THEN
        ALTER TABLE recurring_tasks DROP COLUMN id;
    END IF;
END $$; 