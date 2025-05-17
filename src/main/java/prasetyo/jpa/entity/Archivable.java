package prasetyo.jpa.entity;

public interface Archivable {
    public void archive();
    public void unarchive();
    public boolean isArchived();
}