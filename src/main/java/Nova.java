import java.util.Scanner;

public class Nova {
    public static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Nova");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        try (Scanner scanner = new Scanner(System.in)) {
            String input;
            while (!(input = scanner.nextLine().trim()).equals("bye")) {
                processInput(input);
            }
        }


        System.out.println(SEPARATOR);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    private static void processInput(String input) {
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
            if (inputParts.length > 1) {
                addTask(new Todo(inputParts[1]));
            } else {
                System.out.println("Invalid format! Use: todo [task description]");
            }
            break;
        case "deadline":
            processDeadline(inputParts);
            break;
        case "event":
            processEvent(inputParts);
            break;
        default:
            System.out.println("Invalid command! Use: todo, deadline, event, mark, unmark, list, or bye.");
        }
    }

    private static void processDeadline(String[] inputParts) {
        if (inputParts.length > 1) {
            String[] details = inputParts[1].split(" /by ", 2);
            if (details.length == 2) {
                addTask(new Deadline(details[0], "by: " + details[1]));
            } else {
                System.out.println("Invalid format! Use: deadline [task] /by [dateDue]");
            }
        }
    }

    private static void processEvent(String[] inputParts) {
        if (inputParts.length > 1) {
            String[] details = inputParts[1].split(" /from ", 2);
            if (details.length == 2) {
                String[] timeParts = details[1].split(" /to ", 2);
                if (timeParts.length == 2) {
                    addTask(new Event(details[0], "from: " + timeParts[0], "to: " + timeParts[1]));
                } else {
                    System.out.println("Invalid format! Use: event [task] /from [start] /to [end]");
                }
            } else {
                System.out.println("Invalid format! Use: event [task] /from [start] /to [end]");
            }
        }
    }

    private static void addTask(Task task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount++] = task;
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
        for (int i = 0; i < taskCount; i++) {
            System.out.printf(" %d. %s%n", i + 1, tasks[i]);
        }

        System.out.println(SEPARATOR);
    }

    private static void markTask(String input, boolean isDone) {
        int taskIndex = getTaskIndex(input);
        if (taskIndex != -1) {
            tasks[taskIndex].markAsDone(isDone);
            System.out.println(SEPARATOR);
            System.out.println(" " + (isDone ? "Nice! I've marked this task as done:" : "OK, I've marked this task as not done yet:"));
            System.out.println("   " + tasks[taskIndex]);
            System.out.println(SEPARATOR);
        }
    }

    private static int getTaskIndex(String input) {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < taskCount) {
                return taskIndex;
            }
            System.out.println("Invalid task number.");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid input format. Use: mark [number] or unmark [number]");
        }
        return -1;
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
