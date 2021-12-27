import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Dialog {
    private final Transaction transaction;
    private final InputStream is;
    private final PrintStream os;
    
    public Dialog(InputStream is, PrintStream os) {
        this.is = is;
        this.os = os;
        this.transaction = new Transaction();
    }
    
    public void start() {
        Scanner scanner = new Scanner(is);

        os.print(">>> ");
        String nextLine = scanner.nextLine();

        while (!nextLine.equals("exit")) {
            String[] args = parseLine(nextLine);
            try {
                processCommand(args);
            } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
                os.println(e.getMessage());
            }

            os.print(">>> ");
            nextLine = scanner.nextLine();
        }
    }

    private String[] parseLine(String inputLine) {
        String[] args = inputLine.split(" ");
        os.println(Assertions.assertArgsCountBigger(args, 0));
        String command = args[0];

        if (args.length > 1) {
            String line = String.join(" ", args);
            line = line.substring(command.length(), line.length() - 1);
            line = command + "'" + line;
            return line.split("' '");
        }
        return args;
    }
    
    private void processCommand(String[] args) {
        switch (args[0]) {
            case "register":
                os.println(Assertions.assertArgsCount(args, 3));
                os.println(transaction.register(args[1], args[2]));
                break;
            case "authorize":
                os.println(Assertions.assertArgsCount(args, 3));
                os.println(transaction.authorize(args[1], args[2]));
                break;
            case "show_projects":
                os.println(transaction.showUserProjects());
                break;
            case "show_projects_in_team":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.showProjectsInTeam(Long.parseLong(args[1])));
            case "show_project":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.showProject(Long.parseLong(args[1])));
                break;
            case "create_project":
                os.println(Assertions.assertArgsCount(args, 4));
                os.println(transaction.createProject(args[1], args[2], args[3]));
                break;
            case "create_project_in_team":
                os.println(Assertions.assertArgsCount(args, 5) + " "
                        + Assertions.assertLong(args, List.of(4)));
                os.println(transaction.createProjectInTeam(args[1], args[2], args[3], Long.parseLong(args[4])));
                break;
            case "alter_project":
                os.println(Assertions.assertArgsCount(args, 5));
                os.println(transaction.alterProject(Long.parseLong(args[1]), args[2], args[3], args[4]));
                break;
            case "delete_project":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.deleteProject(Long.parseLong(args[1])));
                break;
            case "add_task_to_project":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertLong(args, List.of(1, 2)));
                os.println(transaction.addTaskToProject(Long.parseLong(args[1]), Long.parseLong(args[2])));
                break;
            case "delete_task_from_project":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertLong(args, List.of(1, 2)));
                os.println(transaction.deleteTaskFromProject(Long.parseLong(args[1]), Long.parseLong(args[2])));
                break;
            case "show_tasks_in_project":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.showProjectTasks(Long.parseLong(args[1])));
                break;
            case "create_task": {
                os.println(Assertions.assertArgsCount(args, 7)
                        + " " + Assertions.assertByte(args, List.of(6))
                        + " " + Assertions.assertDateTime(args, List.of(3, 4))
                        + " " + Assertions.assertValidTimeGap(args[3], args[4]));
                os.println(transaction.createTask(args[1], args[2],
                        LocalDateTime.parse(args[3], Utils.FORMATTER),
                        LocalDateTime.parse(args[4], Utils.FORMATTER),
                        args[5], Byte.parseByte(args[6])));
                break;
            }
            case "alter_task": {
                os.println(Assertions.assertArgsCount(args, 8)
                        + " " + Assertions.assertByte(args, List.of(6))
                        + " " + Assertions.assertDateTime(args, List.of(4, 5))
                        + " " + Assertions.assertValidTimeGap(args[4], args[5]));
                os.println(transaction.alterTask(Long.parseLong(args[1]), args[2], args[3],
                        LocalDateTime.parse(args[4], Utils.FORMATTER),
                        LocalDateTime.parse(args[5], Utils.FORMATTER),
                        args[6], args[7]));
                break;
            }
            case "delete_task":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.deleteTask(Long.parseLong(args[1])));
                break;
            case "show_tasks":
                os.println(transaction.showTasks());
                break;
            case "show_task":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.showTask(Long.parseLong(args[1])));
                break;
            case "show_teams":
                os.println(transaction.showTeams());
                break;
            case "show_users_in_team":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.showUsersInTeam(Long.parseLong(args[1])));
                break;
            case "create_team":
                os.println(Assertions.assertArgsCount(args, 2));
                os.println(transaction.createTeam(args[1]));
                break;
            case "get_team_code":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.getTeamCode(Long.parseLong(args[1])));
                break;
            case "change_team_code":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.changeTeamCode(Long.parseLong(args[1])));
                break;
            case "join_team":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertLong(args, List.of(1, 2)));
                os.println(transaction.joinTeam(Long.parseLong(args[1]), args[2]));
                break;
            case "leave_team":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.leaveTeam(Long.parseLong(args[1])));
                break;
            case "add_user_to_team":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertLong(args, List.of(1, 2)));
                os.println(transaction.addUserToTeam(Long.parseLong(args[1]), Long.parseLong(args[2])));
                break;
            case "delete_user_from_team":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertLong(args, List.of(1, 2)));
                os.println(transaction.deleteUserFromTeam(Long.parseLong(args[1]), Long.parseLong(args[2])));
                break;
            case "delete_team":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.deleteTeam(Long.parseLong(args[1])));
                break;
            case "tasks_by_status":
                os.println(transaction.getTasksByStatus());
                break;
            case "tasks_by_status_in_project":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.getTasksByStatusInProject(Long.parseLong(args[1])));
                break;
            case "tasks_by_priority":
                os.println(transaction.getTasksByPriority());
                break;
            case "tasks_by_priority_in_project":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.getTasksByPriorityInProject(Long.parseLong(args[1])));
                break;
            case "tasks_by_days":
                os.println(Assertions.assertArgsCount(args, 3)
                        + " " + Assertions.assertDateTime(args, List.of(1, 2))
                        + " " + Assertions.assertValidTimeGap(args[1], args[2]));
                os.println(transaction.getTasksByDays(LocalDateTime.parse(args[1], Utils.FORMATTER),
                        LocalDateTime.parse(args[2], Utils.FORMATTER)));
                break;
            case "tasks_by_days_in_project":
                os.println(Assertions.assertArgsCount(args, 4)
                        + " " + Assertions.assertDateTime(args, List.of(1, 2))
                        + " " + Assertions.assertLong(args, List.of(3))
                        + " " + Assertions.assertValidTimeGap(args[1], args[2]));
                os.println(transaction.getTasksByDaysInProject(LocalDateTime.parse(args[1], Utils.FORMATTER),
                        LocalDateTime.parse(args[2], Utils.FORMATTER),
                        Long.parseLong(args[3])));
                break;
            case "projects_by_status":
                os.println(transaction.getProjectsByStatus());
                break;
            case "projects_by_status_in_team":
                os.println(Assertions.assertArgsCount(args, 2)
                        + " " + Assertions.assertLong(args, List.of(1)));
                os.println(transaction.getProjectsByStatusInTeam(Long.parseLong(args[1])));
                break;
            default:
                os.println("Wrong command.");
        }
    }
}
