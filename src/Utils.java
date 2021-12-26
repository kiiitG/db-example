import java.time.format.DateTimeFormatter;

public class Utils {
    public static String DDL_SCRIPT = "CREATE TABLE IF NOT EXISTS users (" +
            "user_id bigint PRIMARY KEY auto_increment," +
            "login varchar(50) UNIQUE NOT NULL," +
            "password varchar(50) NOT NULL)" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS teams (" +
            "team_id bigint PRIMARY KEY auto_increment," +
            "team_name varchar(50) NOT NULL," +
            "team_created_by bigint NOT NULL," +
            "code varchar(100) NOT NULL," +
            "FOREIGN KEY (team_created_by) REFERENCES users (user_id) ON DELETE RESTRICT)" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS status (" +
            "status_name varchar(50) PRIMARY KEY," +
            "status_desc varchar (100))" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS projects (" +
            "project_id bigint PRIMARY KEY auto_increment," +
            "project_name varchar(50) NOT NULL," +
            "project_description varchar(100)," +
            "project_status varchar(50) NOT NULL," +
            "project_created_by bigint NOT NULL," +
            "in_team bigint," +
            "FOREIGN KEY (in_team) REFERENCES teams (team_id) ON DELETE CASCADE," +
            "FOREIGN KEY (project_status) REFERENCES status (status_name) ON DELETE RESTRICT," +
            "FOREIGN KEY (project_created_by) REFERENCES users (user_id) ON DELETE CASCADE)" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS tasks (" +
            "task_id bigint PRIMARY KEY auto_increment," +
            "task_name varchar(50) UNIQUE NOT NULL," +
            "task_description varchar(100)," +
            "task_created_by bigint NOT NULL," +
            "start timestamp NOT NULL," +
            "end timestamp NOT NULL," +
            "in_project bigint," +
            "task_status varchar(50) NOT NULL," +
            "importance tinyint NOT NULL," +
            "FOREIGN KEY (task_created_by) REFERENCES users (user_id) ON DELETE CASCADE," +
            "FOREIGN KEY (in_project) REFERENCES projects (project_id) ON DELETE CASCADE," +
            "FOREIGN KEY (task_status) REFERENCES status (status_name) ON DELETE RESTRICT)" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS users_in_teams (" +
            "user_id bigint NOT NULL," +
            "team_id bigint NOT NULL," +
            "FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE," +
            "FOREIGN KEY (team_id) REFERENCES teams (team_id) ON DELETE CASCADE)";

    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
}
