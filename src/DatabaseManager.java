import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseManager {
    private static final String cs = "jdbc:mysql://localhost:3306/TimeManager?createDatabaseIfNotExist=true&useUnicode=true&serverTimezone=UTC";

    public boolean init() {
        String[] statements = Utils.DDL_SCRIPT.split("\n");

        for (String query : statements) {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        int stCount = getStatusCount();
        boolean def = true;
        if (stCount == 0) {
            def = addDefaultStatuses();
        }
        return def && stCount != -1;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(cs, "root", "root");
    }

    public int getStatusCount() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT COUNT(*) FROM status")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean addDefaultStatuses() {
        Map<String, String> data = Map.of("CRT", "The task is just created.", "STR", "The task is started.", "PRG", "The task is in progress.", "FNS", "The task is finished.", "DLD", "The task is delayed.");
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO status VALUES (?, ?)")) {
            for (var entry : data.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setString(2, entry.getValue());
                int result = statement.executeUpdate();
                if (result != 1) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean addUser(String login, String password) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO users VALUES (DEFAULT, ?, ?)")) {

            statement.setString(1, login);
            statement.setString(2, password);

            int result = statement.executeUpdate();

            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getUser(String login, String password) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT user_id FROM users WHERE login = ? AND password = ?")) {
            statement.setString(1, login);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("user_id");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public List<Long> getTeamsByUserId(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT team_id FROM users_in_teams WHERE user_id = ?")) {
            statement.setLong(1, userId);
            List<Long> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long teamId = rs.getLong("team_id");
                    res.add(teamId);
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Long> getProjectsByUserId(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_id FROM projects WHERE project_created_by = ?")) {
            statement.setLong(1, userId);
            List<Long> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long teamId = rs.getLong("project_id");
                    res.add(teamId);
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Project> getProjectsNamesByUserId(long userId) {
        List<Project> res = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_id, project_name FROM projects WHERE project_created_by = ?")) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("project_id");
                    String name = rs.getString("project_name");
                    res.add(new ProjectBuilder().setId(id).setName(name).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Project getProjectNameDescriptionStatusByProjectId(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_name, project_description, project_status FROM projects WHERE project_id = ?")) {

            statement.setLong(1, projectId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("project_name");
                    String desc = rs.getString("project_description");
                    String status = rs.getString("project_status");
                    return new ProjectBuilder().setName(name).setDescription(desc).setStatus(status).build();
                }
            }

            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean addProject(String name, String description, String status, long createdBy) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO projects VALUES (DEFAULT, ?, ?, ?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, status);
            statement.setLong(4, createdBy);
            statement.setNull(5, Types.NULL);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean addProjectInTeam(String name, String description, String status, long createdBy, long inTeam) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO projects VALUES (DEFAULT, ?, ?, ?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, status);
            statement.setLong(4, createdBy);
            statement.setLong(5, inTeam);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getTeamOfProjectByProjectId(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT in_team FROM projects WHERE project_id = ?")) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("in_team");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean updateProject(long id, String name, String description, String status) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("UPDATE projects SET project_name = ?, project_description = ?, project_status = ? WHERE project_id = ?")) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, status);
            statement.setLong(4, id);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getProjectCreatorByProjectId(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_created_by FROM projects WHERE project_id = ?")) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("project_created_by");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean deleteProjectByProjectId(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("DELETE FROM projects WHERE project_id = ?")) {
            statement.setLong(1, projectId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getProjectOfTask(long taskId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT in_project FROM tasks WHERE task_id = ?")) {
            statement.setLong(1, taskId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("in_project");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean updateProjectOfTask(long taskId, long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("UPDATE tasks SET in_project = ? WHERE task_id = ?")) {
            if (projectId == 0) {
                statement.setNull(1, Types.NULL);
            } else {
                statement.setLong(1, projectId);
            }
            statement.setLong(2, taskId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean addTask(String name, String desc, long createdBy, LocalDateTime start, LocalDateTime end, String status, byte importance) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO tasks VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, desc);
            statement.setLong(3, createdBy);
            statement.setTimestamp(4, Timestamp.valueOf(start));
            statement.setTimestamp(5, Timestamp.valueOf(end));
            statement.setNull(6, Types.NULL);
            statement.setString(7, status);
            statement.setByte(8, importance);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getTaskCreatorByTaskId(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT task_created_by FROM tasks WHERE task_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("task_created_by");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean updateTaskById(long id, String name, String desc, LocalDateTime start, LocalDateTime end, String status, String importance) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("UPDATE tasks SET task_name = ?, task_description = ?, start = ?, end = ?, task_status = ?, importance = ? WHERE task_id = ?")) {
            statement.setString(1, name);
            statement.setString(2, desc);
            statement.setTimestamp(3, Timestamp.valueOf(start));
            statement.setTimestamp(4, Timestamp.valueOf(end));
            statement.setString(5, status);
            statement.setString(6, importance);
            statement.setLong(7, id);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteTaskById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("DELETE FROM tasks WHERE task_id = ?")) {
            statement.setLong(1, id);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Task getTaskById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM tasks WHERE task_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    long userId = rs.getLong("task_created_by");
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                    long projectId = rs.getLong("in_project");
                    String status = rs.getString("task_status");
                    byte importance = rs.getByte("importance");
                    return new Task(id, name, desc, userId, start, end, projectId, status, importance);
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Task> getTasksByUserId(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT task_id, task_name FROM tasks WHERE task_created_by = ?")) {
            statement.setLong(1, id);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long taskId = rs.getLong("task_id");
                    String taskName = rs.getString("task_name");
                    res.add(new TaskBuilder().setId(taskId).setName((taskName)).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Task> getProjectTasksByProjectId(long projectId) {
        List<Task> res = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT task_id, task_name FROM tasks WHERE in_project = ?")) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long taskId = rs.getLong("task_id");
                    String taskName = rs.getString("task_name");
                    res.add(new TaskBuilder().setId(taskId).setName((taskName)).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Long> getUsersInTeam(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT user_id FROM users_in_teams WHERE team_id = ?")) {
            statement.setLong(1, teamId);
            List<Long> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long userId = rs.getLong("user_id");
                    res.add(userId);
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public User getLoginByUserId(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT user_id, login FROM users WHERE user_id = ?")) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("user_id");
                    String login = rs.getString("login");
                    return new User(id, login);
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String getTeamNameByTeamId(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT team_name FROM teams WHERE team_id = ?")) {
            statement.setLong(1, teamId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("team_name");
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public long addTeam(String name, long createdBy) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO teams VALUES (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setLong(2, createdBy);
            statement.setString(3, UUID.randomUUID().toString());
            int result = statement.executeUpdate();
            if (result != 1) {
                return -1;
            }
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public String getTeamCodeByTeamId(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT code FROM teams WHERE team_id = ?")) {
            statement.setLong(1, teamId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("code");
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean addUserInTeam(long userId, long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO users_in_teams VALUES (?, ?)")) {
            statement.setLong(1, userId);
            statement.setLong(2, teamId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteUserFromTeam(long userId, long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("DELETE FROM users_in_teams WHERE user_id = ? AND team_id = ?")) {
            statement.setLong(1, userId);
            statement.setLong(2, teamId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteTeam(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("DELETE FROM teams WHERE team_id = ?")) {
            statement.setLong(1, teamId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long getTeamCreator(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT team_created_by FROM teams WHERE team_id = ?")) {
            statement.setLong(1, teamId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("team_created_by");
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean updateTeamCode(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("UPDATE teams SET code = ? WHERE teamId = ?")) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setLong(2, teamId);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Task> getTasksByStatus(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT task_status, task_id, task_name, task_description, start, end, in_project, importance FROM tasks WHERE task_created_by = ? ORDER BY task_status DESC")) {
            statement.setLong(1, userId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("task_status");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                    long inProject = rs.getLong("in_project");
                    byte importance = rs.getByte("importance");
                    res.add(new TaskBuilder().setStatus(status)
                            .setId(id).setName(name).setDescription(desc)
                            .setStart(start).setEnd(end).setInProject(inProject)
                            .setImportance(importance).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Task> getTasksByStatusInProject(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT task_status, task_id, task_name, task_description, task_created_by, start, end, importance FROM tasks WHERE in_project = ? ORDER BY task_status DESC")) {
            statement.setLong(1, projectId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("task_status");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    long createdBy = rs.getLong("task_created_by");
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                    byte importance = rs.getByte("importance");
                    res.add(new TaskBuilder().setStatus(status)
                            .setId(id).setName(name).setDescription(desc)
                            .setCreatedBy(createdBy).setStart(start).setEnd(end)
                            .setImportance(importance).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Task> getTasksByPriority(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT importance, task_id, task_name, task_description, start, end, in_project, task_status FROM tasks WHERE task_created_by = ? ORDER BY importance DESC")) {
            statement.setLong(1, userId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    byte importance = rs.getByte("importance");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                    long projectId = rs.getLong("in_project");
                    String status = rs.getString("task_status");
                    res.add(new TaskBuilder().setImportance(importance)
                    .setId(id).setName(name).setDescription(desc).setStart(start).setEnd(end).setInProject(projectId)
                    .setStatus(status).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Task> getTasksByPriorityInProject(long projectId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT importance, task_id, task_name, task_description, task_created_by, start, end, task_status FROM tasks WHERE in_project = ? ORDER BY importance DESC")) {
            statement.setLong(1, projectId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("task_status");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    long createdBy = rs.getLong("task_created_by");
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                    byte importance = rs.getByte("importance");
                    res.add(new TaskBuilder().setStatus(status)
                            .setId(id).setName(name).setDescription(desc)
                            .setCreatedBy(createdBy).setStart(start).setEnd(end)
                            .setImportance(importance).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public List<Project> getProjectsByStatus(long userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_status, project_id, project_name, project_description, in_team FROM projects WHERE project_created_by = ? ORDER BY project_status DESC")) {
            statement.setLong(1, userId);
            List<Project> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("project_status");
                    long id = rs.getLong("project_id");
                    String name = rs.getString("project_name");
                    String desc = rs.getString("project_description");
                    long inTeam = rs.getLong("in_team");
                    res.add(new ProjectBuilder().setStatus(status).setId(id)
                            .setName(name).setDescription(desc).setInTeam(inTeam).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Project> getProjectsByStatusInTeam(long teamId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT project_status, project_id, project_name, project_description, project_created_by FROM projects WHERE in_team = ? ORDER BY " +
                             "project_status  DESC")) {
            statement.setLong(1, teamId);
            List<Project> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("project_status");
                    long id = rs.getLong("project_id");
                    String name = rs.getString("project_name");
                    String desc = rs.getString("project_description");
                    long createdBy = rs.getLong("project_created_by");
                    res.add(new ProjectBuilder().setStatus(status).setId(id)
                            .setName(name).setDescription(desc).setCreatedBy(createdBy).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Task> getTasksByDays(long userId, LocalDateTime start, LocalDateTime end) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM tasks WHERE start >= ? AND end <= ? AND task_created_by = ?")) {
            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));
            statement.setLong(3, userId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("task_status");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    LocalDateTime start1 = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end1 = rs.getTimestamp("end").toLocalDateTime();
                    byte importance = rs.getByte("importance");
                    long projectId = rs.getLong("in_project");
                    res.add(new TaskBuilder().setId(id).setName(name).setDescription(desc)
                            .setStart(start1).setEnd(end1).setInProject(projectId)
                    .setStatus(status).setImportance(importance).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Task> getTasksByDaysInProject(long projectId, LocalDateTime start, LocalDateTime end) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM tasks WHERE start >= ? AND end <= ? AND in_project = ?")) {
            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));
            statement.setLong(3, projectId);
            List<Task> res = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("task_status");
                    long id = rs.getLong("task_id");
                    String name = rs.getString("task_name");
                    String desc = rs.getString("task_description");
                    long createdBy = rs.getLong("task_created_by");
                    LocalDateTime start1 = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime end1 = rs.getTimestamp("end").toLocalDateTime();
                    byte importance = rs.getByte("importance");
                    res.add(new TaskBuilder().setId(id).setName(name).setDescription(desc)
                            .setCreatedBy(createdBy).setStart(start1).setEnd(end1)
                            .setStatus(status).setImportance(importance).build());
                }
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
