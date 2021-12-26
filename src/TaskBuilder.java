import java.time.LocalDateTime;

public class TaskBuilder {
    private long id;
    private String name;
    private String description;
    private long createdBy;
    private LocalDateTime start;
    private LocalDateTime end;
    private long inProject;
    private String status;
    private byte importance;

    public TaskBuilder() {
        this.id = -1;
        this.name = "";
        this.description = "";
        this.createdBy = -1;
        this.start = LocalDateTime.MAX;
        this.end = LocalDateTime.MAX;
        this.inProject = -1;
        this.status = "";
        this.importance = -1;
    }

    public TaskBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public TaskBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public TaskBuilder setStart(LocalDateTime start) {
        this.start = start;
        return this;
    }

    public TaskBuilder setEnd(LocalDateTime end) {
        this.end = end;
        return this;
    }

    public TaskBuilder setInProject(long inProject) {
        this.inProject = inProject;
        return this;
    }

    public TaskBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public TaskBuilder setImportance(byte importance) {
        this.importance = importance;
        return this;
    }

    public Task build() {
        return new Task(id, name, description, createdBy, start, end, inProject, status, importance);
    }
}
