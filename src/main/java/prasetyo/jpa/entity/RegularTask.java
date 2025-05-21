package prasetyo.jpa.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regular_tasks")
@Getter
@Setter
public class RegularTask extends AbstractTask {
    private Date deadline;
    private boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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
}
