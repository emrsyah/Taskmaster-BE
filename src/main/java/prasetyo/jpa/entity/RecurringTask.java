package prasetyo.jpa.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.CollectionTable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recurring_tasks")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RecurringTask extends AbstractTask {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recurring_task_recurrence_days", 
                    joinColumns = @JoinColumn(name = "recurring_task_uuid"))
    private Set<String> recurrenceDays = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recurring_task_done_dates", 
                    joinColumns = @JoinColumn(name = "recurring_task_uuid"))
    private Set<Date> doneDates = new HashSet<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    @Override
    public void markCompleted() {
        super.markCompleted();
        if (doneDates == null) {
            doneDates = new HashSet<>();
        }
        doneDates.add(new Date());
    }
}
