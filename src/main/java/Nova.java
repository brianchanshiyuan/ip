import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;

public class Nova {
    public static final String SEPARATOR = "____________________________________________________________";
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static int taskCount = 0;
    private static final String DATA_FILE_PATH = "./data/Nova.txt";

    public static void main(String[] args) {
        loadTasks();
        printWelcomeMessage();
        processUserInput();
        saveTasks();
        printExitMessage();
    }

    private static void loadTasks() {
        try {
            createDataDirectory();
            loadFile();
        } catch (IOException e) {
            handleLoadingError(e);
        }
    }

    private static void createDataDirectory() throws IOException {
        Files.createDirectories(Paths.get("./data"));
    }

    private static void loadFile() throws FileNotFoundException {
        File file = new File(DATA_FILE_PATH);
        if (file.exists()) {
            readTasksFromFile(file);
        }
    }

    private static void readTasksFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            processLine(scanner.nextLine());
        }
        scanner.close();
    }

    private static void processLine(String line) {
        try {
            Task task = parseTask(line);
            addTaskToList(task);
        } catch (NovaException e) {
            handleParsingError(line, e);
        }
    }

    private static Task parseTask(String line) throws NovaException {
        String[] parts = line.split("\\|");
        validateTaskFormat(parts);
        return createTaskFromParts(parts);
    }

    private static void validateTaskFormat(String[] parts) throws NovaException {
        if (parts.length < 3) {
            throw new NovaException("Invalid task format in file.");
        }
    }

    private static Task createTaskFromParts(String[] parts) throws NovaException {
        String type = parts[0].trim();
        String description = parts[2].trim();
        Task task = determineTaskType(type, description, parts);
        setTaskDoneStatus(task, parts[1].trim().equals("1"));
        return task;
    }

    private static Task determineTaskType(String type, String description, String[] parts) throws NovaException {
        switch (type) {
        case "T":
            return new Todo(description);
        case "D":
            return createDeadline(description, parts);
        case "E":
            return createEvent(description, parts);
        default:
            throw new NovaException("Unknown task type in file.");
        }
    }

    private static Task createDeadline(String description, String[] parts) throws NovaException {
        if (parts.length == 4) {
            return new Deadline(description, "by: " + parts[3].trim());
        } else {
            throw new NovaException("Invalid deadline format in file.");
        }
    }

    private static Task createEvent(String description, String[] parts) throws NovaException {
        if (parts.length == 5) {
            return new Event(description, "from: " + parts[3].trim(), "to: " + parts[4].trim());
        } else {
            throw new NovaException("Invalid event format in file.");
        }
    }

    private static void setTaskDoneStatus(Task task, boolean isDone) {
        if (task != null) {
            task.markAsDone(isDone);
        }
    }

    private static void addTaskToList(Task task) {
        if (task != null && taskCount < MAX_TASKS) { // Check for null AND array bounds
            tasks[taskCount++] = task; // Correct way to add to an array
        }
    }

    private static void handleParsingError(String line, NovaException e) {
        System.err.println("Error loading task: " + line + " - " + e.getMessage());
    }

    private static void handleLoadingError(IOException e) {
        System.err.println("Error loading tasks: " + e.getMessage());
    }
    private static void saveTasks() {
        try {
            writeTasksToFile();
        } catch (IOException e) {
            handleSavingError(e);
        }
    }

    private static void writeTasksToFile() throws IOException {
        File file = new File(DATA_FILE_PATH);
        FileWriter writer = new FileWriter(file);
        for (int i = 0; i < taskCount; i++) {  // Iterate up to taskCount!
            if (tasks[i] != null) { // Check for null before formatting
                writer.write(formatTask(tasks[i]) + System.lineSeparator());
            }
        }
        writer.close();
    }

    private static String formatTask(Task task) {
        String type = determineTaskTypeString(task);
        int isDone = task.isDone ? 1 : 0;
        String formatted = type + " | " + isDone + " | " + task.description;

        if (task instanceof Deadline) {
            formatted += " | " + ((Deadline) task).by.substring(4).trim(); // Remove "by: "
        } else if (task instanceof Event) {
            formatted += " | " + ((Event) task).from.substring(6).trim() + " | " + ((Event) task).to.substring(4).trim(); // Remove "from: " and "to: "
        }
        return formatted;
    }

    private static String determineTaskTypeString(Task task) {
        if (task instanceof Todo) {
            return "T";
        } else if (task instanceof Deadline) {
            return "D";
        } else if (task instanceof Event) {
            return "E";
        }
        return ""; // Or throw an exception for unknown task types
    }

    private static void handleSavingError(IOException e) {
        System.err.println("Error saving tasks: " + e.getMessage());
    }

    private static void printWelcomeMessage() {
        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Nova");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    private static void processUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            String input;
            while (!(input = scanner.nextLine().trim()).equals("bye")) {
                handleInput(input);
            }
        }
    }

    private static void handleInput(String input) {
        try {
            processInput(input);
        } catch (NovaException e) {
            printErrorMessage(e.getMessage());
        }
    }

    private static void printErrorMessage(String message) {
        System.out.println(SEPARATOR);
        System.out.println("OOPS!!! " + message);
        System.out.println(SEPARATOR);
    }

    private static void printExitMessage() {
        System.out.println(SEPARATOR);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    private static void processInput(String input) throws NovaException {
        String[] inputParts = input.split(" ", 2);
        String command = inputParts[0];

        switch (command) {
        case "list":
            printTaskList();
            break;
        case "mark":
            markTask(input, true);
            break;
        case "unmark":
            markTask(input, false);
            break;
        case "todo":
            validateTaskDescription(inputParts, "todo");
            addTask(new Todo(inputParts[1]));
            break;
        case "deadline":
            //processDeadline(inputParts);
            addDeadline(inputParts);
            break;
        case "event":
            //processEvent(inputParts);
            addEvent(inputParts);
            break;
        case "delete":
            deleteTask(input);
            break;
        default:
            throw new NovaException("Unknown command! Available commands: list, mark, unmark, todo, deadline, event, delete.");
            //System.out.println("Invalid command! Use: todo, deadline, event, mark, unmark, list, or bye.");
        }
    }

    private static void deleteTask(String input) throws NovaException {
        int taskIndex = getTaskIndex(input);
        if (taskIndex != -1) {
            Task removedTask = tasks.remove(taskIndex);
            taskCount--; // Decrement task count
            System.out.println(SEPARATOR);
            System.out.println("Noted. I've removed this task:");
            System.out.println("   " + removedTask);
            System.out.println("Now you have " + taskCount + " tasks in the list.");
            System.out.println(SEPARATOR);
        }
    }

    private static void addDeadline(String[] inputParts) throws NovaException {
        validateTaskDescription(inputParts, "deadline");
        String[] details = parseTaskDetails(inputParts[1], "/by");
        addTask(new Deadline(details[0], "by: " + details[1]));
    }

    private static void addEvent(String[] inputParts) throws NovaException {
        validateTaskDescription(inputParts, "event");
        String[] details = parseTaskDetails(inputParts[1], "/from");
        String[] timeParts = parseTaskDetails(details[1], "/to");
        addTask(new Event(details[0], "from: " + timeParts[0], "to: " + timeParts[1]));
    }

    private static String[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'deadline [task] /by [date]' or 'event [task] /from [start] /to [end]'.");
        }
        return details;
    }

    private static void validateTaskDescription(String[] inputParts, String taskType) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The description of a " + taskType + " cannot be empty. Example: '" + taskType + " Buy groceries'.");
        }
    }

    private static void addTask(Task task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount] = task;  // Add the task at the current index
            taskCount++;             // Increment the task count AFTER adding
            System.out.println(SEPARATOR);
            System.out.println("Got it. I've added this task:");
            System.out.println("   " + task);
            System.out.println("Now you have " + taskCount + " tasks in the list.");
            System.out.println(SEPARATOR);
        } else {
            System.out.println("Task list is full!");
        }
    }

    private static void printTaskList() {
        System.out.println(SEPARATOR);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) { // Iterate up to taskCount
            System.out.printf(" %d. %s%n", i + 1, tasks[i]);
        }

        System.out.println(SEPARATOR);
    }

    private static void markTask(String input, boolean isDone) throws NovaException {
        int taskIndex = getTaskIndex(input);
        if (taskIndex != -1) {
            tasks.get(taskIndex).markAsDone(isDone); // Use ArrayList's get()
            System.out.println(SEPARATOR);
            System.out.println(" " + (isDone ? "Nice! I've marked this task as done:" : "OK, I've marked this task as not done yet:"));
            System.out.println("   " + tasks.get(taskIndex)); // Use ArrayList's get()
            System.out.println(SEPARATOR);
        }
    }

    private static int getTaskIndex(String input) throws NovaException {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) { // Use ArrayList's size()
                return taskIndex;
            }
            throw new NovaException("Invalid task number. Enter a number between 1 and " + tasks.size() + "."); // Use ArrayList's size()
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new NovaException("Invalid input format. Use: mark [number] or unmark [number]");
        }
    }
    private static void printError(String message) {
        System.out.println(SEPARATOR);
        System.out.println("OOPS!!! " + message);
        System.out.println(SEPARATOR);
    }
    static class NovaException extends Exception {
        public NovaException(String message) {
            super(message);
        }
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
