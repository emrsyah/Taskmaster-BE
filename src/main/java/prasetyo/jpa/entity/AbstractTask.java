package prasetyo.jpa.entity;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractTask implements Archivable {
    public enum TaskStatus {
        PENDING,    // Initial state
        IN_PROGRESS,// Task is being worked on
        COMPLETED,  // Task is done
        ARCHIVED    // Task is archived
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean isCompleted;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
    
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    private int priority;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        status = TaskStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public void markInProgress() {
        if (status == TaskStatus.PENDING || status == TaskStatus.ARCHIVED) {
            status = TaskStatus.IN_PROGRESS;
            isCompleted = false;
        }
    }

    public void markCompleted() {
        status = TaskStatus.COMPLETED;
        isCompleted = true;
    }

    @Override
    public void archive() {
        status = TaskStatus.ARCHIVED;
    }

    @Override
    public void unarchive() {
        status = TaskStatus.PENDING;
    }

    @Override
    public boolean isArchived() {
        return status == TaskStatus.ARCHIVED;
    }
}