package prasetyo.jpa.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "recurring_tasks")
public class RecurringTask extends AbstractTask {
    private List<String> recurrenceDays;

    private boolean isArchived;


    @Override
    public void archive() {
        this.isArchived = true;
    }

    @Override
    public void unarchive() {
        this.isArchived = false;
    }
    
    @Override
    public boolean isArchived() {
        return isArchived;
    }

    public List<String> getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(List<String> recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }
    
    


}
