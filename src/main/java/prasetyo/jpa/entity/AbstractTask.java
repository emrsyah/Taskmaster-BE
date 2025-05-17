package prasetyo.jpa.entity;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractTask implements Archivable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean isCompleted;
    private Date createdAt;
    private Date updatedAt;
    private int priority;

    private Category category;

    

    // Getters and setters (or use Lombok if you prefer)
}