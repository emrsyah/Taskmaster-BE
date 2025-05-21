package prasetyo.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category implements Archivable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String color;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
