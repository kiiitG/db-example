public class Project {
    public long id;
    public String name;
    public String description;
    public String status;
    public long createdBy;
    public long inTeam;

    public Project(long id, String name, String description, String status, long createdBy, long inTeam) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.inTeam = inTeam;
    }
}
