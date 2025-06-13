package prasetyo.jpa.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recurring_tasks")
@Getter
@Setter
public class RecurringTask extends AbstractTask {
    @ElementCollection
    private List<String> recurrenceDays;

    @ElementCollection
    private List<Date> doneDates;

    @JsonBackReference
    @ManyToOne
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
        this.doneDates.add(new Date());
    }
}
