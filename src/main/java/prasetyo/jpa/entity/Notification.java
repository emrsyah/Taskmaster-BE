package prasetyo.jpa.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @JsonIgnore
    @ManyToOne
    private RecurringTask recurringTask;
    
    @JsonIgnore
    @ManyToOne
    private RegularTask regularTask;
    
    private Date date;
    private boolean isRead;

    @JsonIgnore
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getTaskId() {
        if (regularTask != null) {
            return regularTask.getUuid();
        } else if (recurringTask != null) {
            return recurringTask.getUuid();
        }
        return null;
    }

    public String getTaskTitle() {
        if (regularTask != null) {
            return regularTask.getTitle();
        } else if (recurringTask != null) {
            return recurringTask.getTitle();
        }
        return null;
    }

    public String getTaskType() {
        if (regularTask != null) {
            return "regular";
        } else if (recurringTask != null) {
            return "recurring";
        }
        return null;
    }
}
