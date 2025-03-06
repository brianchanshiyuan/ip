import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Nova {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

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

    public static void main(String[] args) {
        new Nova("./data/Nova.txt").run();
    }
}

class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Nova");
        System.out.println("What can I do for you?");
        showLine();
    }

    public void showLine() {
        System.out.println(SEPARATOR);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    public void showLoadingError(String message) {
        System.err.println("Error loading tasks: " + message);
    }

    public void showExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf(" %d. %s%n", i + 1, tasks.get(i));
        }
    }

    public void showTaskMarked(Task task, boolean isDone) {
        System.out.println(" " + (isDone ? "Nice! I've marked this task as done:" : "OK, I've marked this task as not done yet:"));
        System.out.println("   " + task);
    }

    public void showFoundTasks(ArrayList<Task> foundTasks) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < foundTasks.size(); i++) {
            System.out.printf(" %d.%s%n", i + 1, foundTasks.get(i));
        }
    }
}

class Storage {
    private final String filePath;
    public Storage(String filePath) {
        this.filePath = filePath;
    }

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

class Parser {
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

class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(int index) {
        tasks.remove(index);
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }
}

abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    public boolean isExit() {
        return false;
    }
}

class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        ui.showTaskList(tasks.getTasks());
    }
}

class MarkCommand extends Command {
    private final String input;
    private final boolean isDone;

    public MarkCommand(String input, boolean isDone) {
        this.input = input;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = getTaskIndex(input, tasks);
        Task task = tasks.getTask(taskIndex);
        task.markAsDone(isDone);
        ui.showTaskMarked(task, isDone);
        storage.save(tasks.getTasks());
    }

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

class TodoCommand extends Command {
    private final String[] inputParts;

    public TodoCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

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

class DeadlineCommand extends Command {
    private final String[] inputParts;

    public DeadlineCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

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

    private String[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'deadline [task] /by [date]'.");
        }
        return details;
    }
}

class EventCommand extends Command {
    private final String[] inputParts;

    public EventCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

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

    private String[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'event [task] /from [start] /to [end]'.");
        }
        return details;
    }
}

class DeleteCommand extends Command {
    private final String input;

    public DeleteCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = getTaskIndex(input, tasks);
        Task removedTask = tasks.getTask(taskIndex);
        tasks.removeTask(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.getTasks());
    }

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

class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        ui.showExit();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

class FindCommand extends Command {
    private final String[] inputParts;

    public FindCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

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

class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }
}

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