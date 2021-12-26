public class ProjectBuilder {
    public long id;
    public String name;
    public String description;
    public String status;
    public long createdBy;
    public long inTeam;

    public ProjectBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public ProjectBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public ProjectBuilder setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ProjectBuilder setInTeam(long inTeam) {
        this.inTeam = inTeam;
        return this;
    }

    public Project build() {
        return new Project(id, name, description, status, createdBy, inTeam);
    }
}
