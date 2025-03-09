import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The main class for the Nova task management application.
 *
 * This class is responsible for initializing the application,
 * running the main command loop, and handling user input.
 */
public class Nova {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a new Nova instance.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Nova(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        try {
            tasks = new TaskList(storage.load());
        } catch (NovaException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main command loop.
     *
     * This method continuously reads user input, parses it,
     * and executes the corresponding command.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (NovaException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * The main entry point for the application.
     *
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        new Nova("./data/Nova.txt").run();
    }
}

/**
 * The user interface class for the Nova application.
 *
 * This class is responsible for displaying messages to the user,
 * reading user input, and showing the task list.
 */
class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Nova");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Displays a separator line.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Reads a command from the user.
     *
     * @return The command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays an error message.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /**
     * Displays an error message when loading tasks fails.
     *
     * @param message The error message to display.
     */
    public void showLoadingError(String message) {
        System.err.println("Error loading tasks: " + message);
    }

    /**
     * Displays the exit message.
     */
    public void showExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a message indicating that a task has been added.
     *
     * @param task The task that was added.
     * @param taskCount The new total number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a message indicating that a task has been removed.
     *
     * @param task The task that was removed.
     * @param taskCount The new total number of tasks.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the list of tasks.
     *
     * @param tasks The list of tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf(" %d. %s%n", i + 1, tasks.get(i));
        }
    }

    /**
     * Displays a message indicating that a task has been marked as done or not done.
     *
     * @param task The task that was marked.
     * @param isDone Whether the task was marked as done or not done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        System.out.println(" " + (isDone ? "Nice! I've marked this task as done:" : "OK, I've marked this task as not done yet:"));
        System.out.println("   " + task);
    }

    /**
     * Displays the list of tasks that match a search keyword.
     *
     * @param foundTasks The list of tasks that match the search keyword.
     */
    public void showFoundTasks(ArrayList<Task> foundTasks) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < foundTasks.size(); i++) {
            System.out.printf(" %d.%s%n", i + 1, foundTasks.get(i));
        }
    }
}


/**
 * The storage class for the Nova application.
 *
 * This class is responsible for loading tasks from and saving tasks to a file.
 */
class Storage {
    private final String filePath;
    /**
     * Constructs a new Storage instance.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }


    /**
     * Loads tasks from the storage file.
     *
     * @return An ArrayList of tasks loaded from the file.
     * @throws NovaException If there is an error loading the tasks.
     */
    public ArrayList<Task> load() throws NovaException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get("./data"));
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    tasks.add(parseTask(scanner.nextLine()));
                }
                scanner.close();
            }
        } catch (IOException e) {
            throw new NovaException("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses a task from a line of text.
     *
     * @param line The line of text to parse.
     * @return The parsed task.
     * @throws NovaException If there is an error parsing the task.
     */
    private Task parseTask(String line) throws NovaException {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            throw new NovaException("Invalid task format in file.");
        }
        String type = parts[0].trim();
        String description = parts[2].trim();
        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (parts.length == 4) {
                task = new Deadline(description, "by: " + parts[3].trim());
            } else {
                throw new NovaException("Invalid deadline format in file.");
            }
            break;
        case "E":
            if (parts.length == 5) {
                task = new Event(description, "from: " + parts[3].trim(), "to: " + parts[4].trim());
            } else {
                throw new NovaException("Invalid event format in file.");
            }
            break;
        default:
            throw new NovaException("Unknown task type in file.");
        }
        task.markAsDone(parts[1].trim().equals("1"));
        return task;
    }


    /**
     * Saves tasks to the storage file.
     *
     * @param tasks The list of tasks to save.
     * @throws NovaException If there is an error saving the tasks.
     */
    public void save(ArrayList<Task> tasks) throws NovaException {
        try {
            FileWriter writer = new FileWriter(filePath);
            for (Task task : tasks) {
                writer.write(formatTask(task) + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            throw new NovaException("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Formats a task as a string for saving to the file.
     *
     * @param task The task to format.
     * @return The formatted task string.
     */
    private String formatTask(Task task) {
        String type = "";
        if (task instanceof Todo) {
            type = "T";
        } else if (task instanceof Deadline) {
            type = "D";
        } else if (task instanceof Event) {
            type = "E";
        }
        int isDone = task.isDone ? 1 : 0;
        String formatted = type + " | " + isDone + " | " + task.description;
        if (task instanceof Deadline) {
            formatted += " | " + ((Deadline) task).by.substring(4).trim();
        } else if (task instanceof Event) {
            formatted += " | " + ((Event) task).from.substring(6).trim() + " | " + ((Event) task).to.substring(4).trim();
        }
        return formatted;
    }
}

/**
 * The parser class for the Nova application.
 *
 * This class is responsible for parsing user input and creating the
 * corresponding Command objects.
 */
class Parser {

    /**
     * Parses a command from user input.
     *
     * @param input The user input to parse.
     * @return The parsed Command object.
     * @throws NovaException If the input is invalid or an unknown command is encountered.
     */
    public Command parse(String input) throws NovaException {
        String[] inputParts = input.split(" ", 2);
        String command = inputParts[0];
        switch (command) {
        case "list":
            return new ListCommand();
        case "mark":
            return new MarkCommand(input, true);
        case "unmark":
            return new MarkCommand(input, false);
        case "todo":
            return new TodoCommand(inputParts);
        case "deadline":
            return new DeadlineCommand(inputParts);
        case "event":
            return new EventCommand(inputParts);
        case "delete":
            return new DeleteCommand(input);
        case "bye":
            return new ExitCommand();
        case "find":
            return new FindCommand(inputParts);
        default:
            throw new NovaException("Unknown command! Available commands: list, mark, unmark, todo, deadline, event, delete, find, bye.");
        }
    }
}

/**
 * A class representing a list of tasks.
 */
class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list with the given list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the list of tasks.
     *
     * @return The list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task from the list.
     *
     * @param index The index of the task to remove.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Gets a task from the list.
     *
     * @param index The index of the task to get.
     * @return The task at the given index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }
}

/**
 * An abstract class representing a command in the Nova application.
 */
abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    public boolean isExit() {
        return false;
    }
}

/**
 * A command to list all tasks.
 */
class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        ui.showTaskList(tasks.getTasks());
    }
}

/**
 * A command to mark a task as done or not done.
 */
class MarkCommand extends Command {
    private final String input;
    private final boolean isDone;

    /**
     * Constructs a new MarkCommand.
     *
     * @param input The user input containing the task number to mark.
     * @param isDone Whether to mark the task as done or not done.
     */
    public MarkCommand(String input, boolean isDone) {
        this.input = input;
        this.isDone = isDone;
    }

    /**
     * Executes the mark command.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = getTaskIndex(input, tasks);
        Task task = tasks.getTask(taskIndex);
        task.markAsDone(isDone);
        ui.showTaskMarked(task, isDone);
        storage.save(tasks.getTasks());
    }

    /**
     * Gets the index of the task to mark from the user input.
     *
     * @param input The user input containing the task number.
     * @param tasks The task list.
     * @return The index of the task to mark.
     * @throws NovaException If the input is invalid or the task number is out of range.
     */
    private int getTaskIndex(String input, TaskList tasks) throws NovaException {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                return taskIndex;
            }
            throw new NovaException("Invalid task number. Enter a number between 1 and " + tasks.size() + ".");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new NovaException("Invalid input format. Use: mark [number] or unmark [number]");
        }
    }
}

/**
 * A command to add a new todotask.
 */
class TodoCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new TodoCommand.
     *
     * @param inputParts The user input split into command and task description.
     */
    public TodoCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the todocommand.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty. Example: 'todo Buy groceries'.");
        }
        Task task = new Todo(inputParts[1]);
        tasks.addTask(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.getTasks());
    }
}

/**
 * A command to add a new deadline task.
 */
class DeadlineCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new DeadlineCommand.
     *
     * @param inputParts The user input split into command and task details.
     */
    public DeadlineCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the deadline command.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The description of a deadline cannot be empty. Example: 'deadline Buy groceries /by 2024-12-24'.");
        }
        String[] details = parseTaskDetails(inputParts[1], "/by");
        Task task = new Deadline(details[0], "by: " + details[1]);
        tasks.addTask(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Parses the task details from the user input.
     *
     * @param input The user input containing the task details.
     * @param delimiter The delimiter to split the input by.
     * @return The parsed task details.
     * @throws NovaException If the input is invalid.
     */
    private string[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'deadline [task] /by [date]'.");
        }
        return details;
    }
}

/**
 * A command to add a new event task.
 */
class EventCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new EventCommand.
     *
     * @param inputParts The user input split into command and task details.
     */
    public EventCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the event command.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The description of an event cannot be empty. Example: 'event Project meeting /from 2024-12-24 14:00 /to 2024-12-24 16:00'.");
        }
        String[] details = parseTaskDetails(inputParts[1], "/from");
        String[] timeParts = parseTaskDetails(details[1], "/to");
        Task task = new Event(details[0], "from: " + timeParts[0], "to: " + timeParts[1]);
        tasks.addTask(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Parses the task details from the user input.
     *
     * @param input The user input containing the task details.
     * @param delimiter The delimiter to split the input by.
     * @return The parsed task details.
     * @throws NovaException If the input is invalid.
     */
    private String[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'event [task] /from [start] /to [end]'.");
        }
        return details;
    }
}

/**
 * A command to delete a task.
 */
class DeleteCommand extends Command {
    private final String input;

    /**
     * Constructs a new DeleteCommand.
     *
     * @param input The user input containing the task number to delete.
     */
    public DeleteCommand(String input) {
        this.input = input;
    }

    /**
     * Executes the delete command.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = getTaskIndex(input, tasks);
        Task removedTask = tasks.getTask(taskIndex);
        tasks.removeTask(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Gets the index of the task to delete from the user input.
     *
     * @param input The user input containing the task number.
     * @param tasks The task list.
     * @return The index of the task to delete.
     * @throws NovaException If the input is invalid or the task number is out of range.
     */
    private int getTaskIndex(String input, TaskList tasks) throws NovaException {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                return taskIndex;
            }
            throw new NovaException("Invalid task number. Enter a number between 1 and " + tasks.size() + ".");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new NovaException("Invalid input format. Use: delete [number]");
        }
    }
}

/**
 * A command to exit the application.
 */
class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        ui.showExit();
    }

    /**
     * Returns true, as this command is an exit command.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}

/**
 * A command to find tasks containing a specific keyword.
 */
class FindCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new FindCommand.
     *
     * @param inputParts The user input split into command and keyword.
     */
    public FindCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the find command.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface to use for display.
     * @param storage The storage (not used in this command).
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The keyword to find cannot be empty. Example: 'find book'.");
        }
        String keyword = inputParts[1].trim().toLowerCase();
        ArrayList<Task> foundTasks = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            if (task.description.toLowerCase().contains(keyword)) {
                foundTasks.add(task);
            }
        }
        ui.showFoundTasks(foundTasks);
    }
}

/**
 * An abstract class representing a task in the Nova application.
 */
class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks the task as done or not done.
     *
     * @param isDone Whether the task is done or not done.
     */
    public void markAsDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns a string representation of the task.
     *
     * @return The string representation of the task.
     */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }
}

/**
 * A class representing a todotask.
 */
class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (" + by + ")";
    }
}

class Event extends Task {
    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (" + from + " " + to + ")";
    }
}

class NovaException extends Exception {
    public NovaException(String message) {
        super(message);
    }
}