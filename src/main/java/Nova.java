import java.util.Scanner;
import java.util.ArrayList;

public class Nova {
    public static final String SEPARATOR = "____________________________________________________________";
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static int taskCount = 0;

    public static void main(String[] args) {
        printWelcomeMessage();
        processUserInput();
        printExitMessage();
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
        tasks.add(task);
        taskCount++; // Still useful for keeping track of the number of tasks
        System.out.println(SEPARATOR);
        System.out.println("Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(SEPARATOR);
    }

    private static void printTaskList() {
        System.out.println(SEPARATOR);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf(" %d. %s%n", i + 1, tasks.get(i));
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
