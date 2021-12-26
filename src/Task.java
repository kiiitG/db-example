import java.time.LocalDateTime;

public class Task {
    public long id;
    public String name;
    public String description;
    public long createdBy;
    public LocalDateTime start;
    public LocalDateTime end;
    public long inProject;
    public String status;
    public byte importance;


    public Task(long id, String name, String description, long createdBy, LocalDateTime start, LocalDateTime end, long inProject, String status, byte importance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.start = start;
        this.end = end;
        this.inProject = inProject;
        this.status = status;
        this.importance = importance;
    }
}
