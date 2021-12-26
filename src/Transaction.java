import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Transaction {
    private final DatabaseManager dbm;

    private long userId = -1;

    public Transaction() {
        dbm = new DatabaseManager();
        boolean result = dbm.init();

        if (!result) {
            System.out.println("Error in creating database.");
        }
    }

    public String register(String login, String password) {
        boolean result = dbm.addUser(login, Integer.toString(Objects.hashCode(password)));
        return result ? "Registered successfully." : "Error while registration.";
    }

    public String authorize(String login, String password) {
        long result = dbm.getUser(login, Integer.toString(Objects.hashCode(password)));
        this.userId = result;
        return result != -1 ? "Authorized successfully." : "Error while authorization.";
    }

    public String showUserProjects() {
        List<Project> userProjects = dbm.getProjectsNamesByUserId(userId);
        if (userProjects == null) {
            return "Error while processing query.";
        }

        StringBuilder res = new StringBuilder();
        for (Project project : userProjects) {
            res.append(project.id).append("\t").append(project.name).append("\r\n");
        }

        return res.toString();
    }

    public String showProject(long projectId) {
        List<Long> userProjects = dbm.getProjectsByUserId(userId);
        if (userProjects == null) {
            return "Error while processing query.";
        }

        if (!userProjects.contains(projectId)) {
            return "No permission to see this project.";
        }

        StringBuilder res = new StringBuilder();
        Project project = dbm.getProjectNameDescriptionStatusByProjectId(projectId);
        if (project == null) {
            return "Error while processing query.";
        }

        res.append(project.name).append("\t").append(project.description).append("\t").append(project.status);

        return res.toString();
    }

    public String createProject(String name, String description, String status) {
        boolean result = dbm.addProject(name, description, status, userId);
        if (result) {
            return "Project created successfully.";
        } else {
            return "Error while creating project.";
        }
    }

    public String createProjectInTeam(String name, String description, String status, long inTeam) {
        long creator = dbm.getTeamCreator(inTeam);
        if (creator != userId) {
            return "No permission to create project in this team.";
        }

        boolean result = dbm.addProjectInTeam(name, description, status, userId, inTeam);
        if (result) {
            return "Project created successfully.";
        } else {
            return "Error while creating project.";
        }
    }

    public String alterProject(long id, String name, String description, String status) {
        long teamId = dbm.getTeamOfProjectByProjectId(id);
        long creator = dbm.getTeamCreator(teamId);
        if (creator != userId) {
            return "No permission to alter project in this team.";
        }

        boolean result = dbm.updateProject(id, name, description, status);
        if (result) {
            return "Project altered successfully.";
        } else {
            return "Error while altering project.";
        }
    }

    public String deleteProject(long id) {
        long creator = dbm.getProjectCreatorByProjectId(id);
        if (userId != creator) {
            return "No permission to delete this project.";
        }

        boolean result = dbm.deleteProjectByProjectId(id);
        if (result) {
            return "Project deleted successfully.";
        } else {
            return "Error while deleting project.";
        }
    }

    public String addTaskToProject(long taskId, long projectId) {
        long creator = dbm.getProjectCreatorByProjectId(projectId);
        if (userId != creator) {
            return "No permission to add tasks to this project.";
        }

        long projectOfTask = dbm.getProjectOfTask(taskId);
        if (projectOfTask != 0) {
            return "This task is already in the project.";
        }

        boolean result = dbm.updateProjectOfTask(taskId, projectId);
        if (result) {
            return "Added task to the project successfully.";
        } else {
            return "Error while adding task to the project.";
        }
    }

    public String deleteTaskFromProject(long taskId, long projectId) {
        long creator = dbm.getProjectCreatorByProjectId(projectId);
        if (userId != creator) {
            return "No permission to delete tasks from this project.";
        }

        long projectOfTask = dbm.getProjectOfTask(taskId);
        if (projectOfTask != projectId) {
            return "This task is not in this project.";
        }

        boolean result = dbm.updateProjectOfTask(taskId, 0);
        if (result) {
            return "Deleted task from the project successfully.";
        } else {
            return "Error while deleting task from the project.";
        }
    }

    public String createTask(String name, String desc, LocalDateTime start, LocalDateTime end, String status, Byte importance) {
        boolean result = dbm.addTask(name, desc, this.userId, start, end, status, importance);
        if (result) {
            return "Task created successfully.";
        } else {
            return "Error while creating the task.";
        }
    }

    public String alterTask(long id, String name, String desc, LocalDateTime start, LocalDateTime end, String status, String importance) {
        long creator = dbm.getTaskCreatorByTaskId(id);
        if (creator != userId) {
            return "No permission to alter this task.";
        }
        boolean result = dbm.updateTaskById(id, name, desc, start, end, status, importance);
        if (result) {
            return "Task altered successfully.";
        } else {
            return "Error while altering the task.";
        }
    }

    public String deleteTask(long id) {
        long creator = dbm.getTaskCreatorByTaskId(id);
        if (creator != userId) {
            return "No permission to alter this task.";
        }
        boolean result = dbm.deleteTaskById(id);
        if (result) {
            return "Task deleted successfully.";
        } else {
            return "Error while deleting the task.";
        }
    }
    public String showTask(long id) {
        long creator = dbm.getTaskCreatorByTaskId(id);
        if (creator != userId) {
            return "No permission to see this task.";
        }

        Task task = dbm.getTaskById(id);
        if (task == null) {
            return "Error while accessing task info.";
        }
        StringBuilder res = new StringBuilder();
        return res.append(task.name).append("\t").append(task.description).append("\t").append(task.start).append("\t").append(task.end).append("\t").append(task.inProject).append("\t").append(task.status).append("\t").
                append(task.importance).toString();
    }
    public String showTasks() {
        List<Task> tasks = dbm.getTasksByUserId(userId);
        if (tasks == null) {
            return "Error while accessing task info.";
        }

        StringBuilder res = new StringBuilder();
        for (Task task : tasks) {
            res.append(task.id).append("\t").append(task.name).append("\r\n");
        }
        return res.toString();
    }

    public String showProjectTasks(long projectId) {
        List<Long> userProjects = dbm.getProjectsByUserId(userId);
        if (!userProjects.contains(projectId)) {
            return "No permission to create see tasks of this project.";
        }

        List<Task> tasks = dbm.getProjectTasksByProjectId(projectId);
        StringBuilder res = new StringBuilder().append("id\tname\r\n");
        for (Task task : tasks) {
            res.append(task.id).append("\t").append(task.name).append("\r\n");
        }
        return res.toString();
    }

    public String showTeams() {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (teamIds == null) {
            return "Error while processing query.";
        }
        List<Team> teams = new ArrayList<>();
        for (long teamId : teamIds) {
            String name = dbm.getTeamNameByTeamId(teamId);
            teams.add(new Team(teamId, name));
        }
        StringBuilder res = new StringBuilder();
        for (Team team : teams) {
            res.append(team.id).append("\t").append(team.name).append("\r\n");
        }
        return res.toString();
    }

    public String showUsersInTeam(long teamId) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (!teamIds.contains(teamId)) {
            return "No permission to see users in this team.";
        }

        StringBuilder res = new StringBuilder();
        List<Long> usersInTeam = dbm.getUsersInTeam(teamId);
        for (Long userId : usersInTeam) {
            User user = dbm.getLoginByUserId(userId);
            res.append(user.id).append("\t").append(user.login).append("\r\n");
        }

        return res.toString();
    }

    public String createTeam(String name) {
        long teamId = dbm.addTeam(name, userId);
        if (teamId == -1 || teamId == 0) {
            return "Error while creating the team.";
        }
        boolean result = dbm.addUserInTeam(userId, teamId);
        if (!result) {
            return "Error while creating the team.";
        }
        return "Team created successfully.";
    }

    public String getTeamCode(long teamId) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (!teamIds.contains(teamId)) {
            return "No permission to get this team's code.";
        }

        String code = dbm.getTeamCodeByTeamId(teamId);
        if (code == null) {
            return "Error while getting the team code.";
        } else {
            return "The team's code is " + code;
        }
    }

    public String changeTeamCode(long teamId) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (!teamIds.contains(teamId)) {
            return "No permission to get this team's code.";
        }

        boolean result = dbm.updateTeamCode(teamId);
        if (result) {
            return "Team's code changed successfully.";
        } else {
            return "Error while changing team's code.";
        }
    }

    public String joinTeam(long id, String code) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (teamIds.contains(id)) {
            return "You are already in this group.";
        }

        String actualCode = dbm.getTeamCodeByTeamId(id);
        if (!actualCode.equals(code)) {
            return "Wrong code.";
        }

        boolean result = dbm.addUserInTeam(userId, id);
        if (result) {
            return "You are added to team successfully.";
        } else {
            return "Error while adding to team.";
        }
    }

    public String leaveTeam(Long teamId) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (!teamIds.contains(teamId)) {
            return "You haven't joined the team.";
        }

        boolean result = dbm.deleteUserFromTeam(userId, teamId);
        if (result) {
            return "You are added to team successfully.";
        } else {
            return "Error while adding to team.";
        }
    }
    public String addUserToTeam(long newUserId, long teamId) {
        long creator = dbm.getTeamCreator(teamId);
        if (creator != this.userId) {
            return "No permission to add users to this team.";
        }

        boolean result = dbm.addUserInTeam(newUserId, teamId);
        if (result) {
            return "Added user to team successfully.";
        } else {
            return "Error while adding user to team.";
        }
    }
    public String deleteUserFromTeam(long userId, long teamId) {
        long creator = dbm.getTeamCreator(teamId);
        if (creator != userId) {
            return "No permission to delete users from this team.";
        }

        boolean result = dbm.deleteUserFromTeam(userId, teamId);
        if (result) {
            return "Deleted user to team successfully.";
        } else {
            return "Error while deleting user to team.";
        }
    }
    public String deleteTeam(long teamId) {
        long creator = dbm.getTeamCreator(teamId);
        if (creator != userId) {
            return "No permission to delete users from this team.";
        }

        boolean result = dbm.deleteTeam(teamId);
        if (result) {
            return "Deleted the team successfully.";
        } else {
            return "Error while deleting the team.";
        }
    }
    /********************* Data management *************************/
    public String getTasksByStatus() {
        List<Task> tasks = dbm.getTasksByStatus(userId);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Task task : tasks) {
            res.append(task.status).append("\t").append(task.id).append("\t")
                    .append(task.description).append("\t").append(task.start)
                    .append("\t").append(task.end).append("\t").append(task.inProject).append("\t")
                    .append(task.importance).append("\r\n");
        }
        return res.toString();
    }
    public String getTasksByStatusInProject(long projectId) {
        List<Long> userProjects = dbm.getProjectsByUserId(userId);
        if (userProjects == null) {
            return "Error while processing query.";
        }
        if (!userProjects.contains(projectId)) {
            return "No permission to see this project.";
        }

        List<Task> tasks = dbm.getTasksByStatusInProject(projectId);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Task task : tasks) {
            res.append(task.status).append("\t").append(task.id).append("\t")
                    .append(task.description).append("\t").append(task.createdBy).append("\t")
                    .append(task.start).append("\t").append(task.end).append("\t")
                    .append(task.importance).append("\r\n");
        }
        return res.toString();
    }
    public String getTasksByPriority() {
        List<Task> tasks = dbm.getTasksByPriority(userId);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Task task : tasks) {
            res.append(task.importance).append("\t").append(task.id).append("\t").append(task.name).append("\t")
                    .append(task.description).append("\t")
                    .append(task.start).append("\t").append(task.end).append("\t")
                    .append(task.inProject).append("\t")
                    .append(task.status).append("\r\n");
        }
        return res.toString();
    }
    public String getTasksByPriorityInProject(long projectId) {
        List<Long> userProjects = dbm.getProjectsByUserId(userId);
        if (userProjects == null) {
            return "Error while processing query.";
        }
        if (!userProjects.contains(projectId)) {
            return "No permission to see this project.";
        }

        List<Task> tasks = dbm.getTasksByPriorityInProject(projectId);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Task task : tasks) {
            res.append(task.importance).append("\t").append(task.id).append("\t").append(task.name).append("\t")
                    .append(task.description).append("\t").append(task.createdBy).append("\t")
                    .append(task.start).append("\t").append(task.end).append("\t")
                    .append(task.status).append("\r\n");
        }
        return res.toString();
    }
    public String getProjectsByStatus() {
        List<Project> projects = dbm.getProjectsByStatus(userId);
        if (projects == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Project project : projects) {
            res.append(project.status).append("\t").append(project.id).append("\t").append(project.name)
                    .append("\t").append(project.description).append("\t").append(project.inTeam).append("\r\n");
        }
        return res.toString();
    }
    public String getProjectsByStatusInTeam(long teamId) {
        List<Long> teamIds = dbm.getTeamsByUserId(userId);
        if (!teamIds.contains(teamId)) {
            return "You are not allowed to see projects of this team.";
        }
        List<Project> projects = dbm.getProjectsByStatusInTeam(teamId);
        if (projects == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        for (Project project : projects) {
            res.append(project.status).append("\t").append(project.id).append("\t").append(project.name)
                    .append("\t").append(project.description).append("\t").append(project.createdBy).append("\r\n");
        }
        return res.toString();
    }
    public String getTasksByDays(LocalDateTime start, LocalDateTime end) {
        List<Task> tasks = dbm.getTasksByDays(userId, start, end);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        LocalDate start1 = start.toLocalDate();
        LocalDate end1 = end.toLocalDate();
        LocalDate i = start1;
        res.append(i).append("\r\n");
        while (i.isBefore(end1) || i.isEqual(end1)) {
            for (Task task : tasks) {
                if (task.start.toLocalDate().isEqual(i)) {
                    res.append("starts\t").append(task.id).append("\t").append(task.name).append("\t")
                            .append(task.description).append("\t").append(task.start).append("\t")
                            .append(task.end).append("\t").append(task.inProject).append("\t")
                            .append(task.status).append("\t").append(task.importance).append("\r\n");
                } else if (task.end.toLocalDate().isEqual(i)) {
                    res.append(i).append("\r\n");
                    res.append("ends\t").append(task.id).append("\t").append(task.name).append("\t")
                            .append(task.description).append("\t").append(task.start).append("\t")
                            .append(task.end).append("\t").append(task.inProject).append("\t")
                            .append(task.status).append("\t").append(task.importance).append("\r\n");
                }
            }
            i = i.plusDays(1);
            res.append("\r\n");
        }
        return res.toString();
    }
    public String getTasksByDaysInProject(LocalDateTime start, LocalDateTime end, long projectId) {
        List<Long> userProjects = dbm.getProjectsByUserId(userId);
        if (userProjects == null) {
            return "Error while processing query.";
        }
        if (!userProjects.contains(projectId)) {
            return "No permission to see this project.";
        }

        List<Task> tasks = dbm.getTasksByDaysInProject(projectId, start, end);
        if (tasks == null) {
            return "Error while processing query.";
        }
        StringBuilder res = new StringBuilder();
        LocalDateTime i = start;
        res.append(i).append("\r\n");
        while (i.isBefore(end) || i.isEqual(end)) {
            for (Task task : tasks) {
                if (task.start.isEqual(i)) {
                    res.append("starts\t").append(task.id).append("\t").append(task.name).append("\t")
                            .append(task.description).append("\t")
                            .append(task.createdBy).append("\t")
                            .append(task.start).append("\t")
                            .append(task.end).append("\t")
                            .append(task.status).append("\t").append(task.importance).append("\r\n");
                } else if (task.end.isEqual(i)) {
                    res.append(i).append("\r\n");
                    res.append("ends\t").append(task.id).append("\t").append(task.name).append("\t")
                            .append(task.description).append("\t")
                            .append(task.createdBy).append("\t").append(task.start).append("\t")
                            .append(task.end).append("\t")
                            .append(task.status).append("\t").append(task.importance).append("\r\n");
                }
            }
            i = i.plusDays(1);
        }
        return res.toString();
    }
}
