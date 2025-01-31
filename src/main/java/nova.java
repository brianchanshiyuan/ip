import java.util.Scanner;

public class nova {
    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        String input;
        while (!(input = scanner.nextLine().trim()).equals("bye")) {
            processInput(input);
        }
        scanner.close();

        System.out.println("____________________________________________________________");
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }

    private static void processInput(String input) {
        if (input.equals("list")) {
            printTaskList();
        } else if (input.startsWith("mark ")) {
            markTask(input, true);
        } else if (input.startsWith("unmark ")) {
            markTask(input, false);
        } else if (input.startsWith("todo ")) {
            addTask(new Todo(input.substring(5)));
        } else if (input.startsWith("deadline ")) {
            String[] parts = input.substring(9).split(" /by ", 2);
            if (parts.length == 2) {
                addTask(new Deadline(parts[0], "by: " + parts[1]));
            } else {
                System.out.println(" Invalid format! Use: deadline [task] /by [dateDue]");
            }
        } else if (input.startsWith("event ")) {
            String[] parts = input.substring(6).split(" /from ", 2);
            if (parts.length == 2) {
                String[] timeParts = parts[1].split(" /to ", 2);
                if (timeParts.length == 2) {
                    addTask(new Event(parts[0], "from: " + timeParts[0], "to: " + timeParts[1]));
                } else {
                    System.out.println(" Invalid format! Use: event [task] /from [start] /to [end]");
                }
            } else {
                System.out.println(" Invalid format! Use: event [task] /from [start] /to [end]");
            }
        } else {
            System.out.println(" Invalid command! Use: todo, deadline, event, mark, unmark, list, or bye.");
        }
    }

    private static void addTask(Task task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount++] = task;
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + taskCount + " tasks in the list.");
            System.out.println("____________________________________________________________");
        } else {
            System.out.println(" Task list is full!");
        }
    }

    private static void printTaskList() {
        System.out.println("____________________________________________________________");
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
        System.out.println("____________________________________________________________");
    }

    private static void markTask(String input, boolean isDone) {
        int taskIndex = getTaskIndex(input);
        if (taskIndex != -1) {
            tasks[taskIndex].setDone(isDone);
            System.out.println("____________________________________________________________");
            System.out.println(" " + (isDone ? "Nice! I've marked this task as done:" : "OK, I've marked this task as not done yet:"));
            System.out.println("   " + tasks[taskIndex]);
            System.out.println("____________________________________________________________");
        }
    }

    private static int getTaskIndex(String input) {
        try {
            int taskIndex = Integer.parseInt(input.split(" ")[1]) - 1;
            if (taskIndex >= 0 && taskIndex < taskCount) {
                return taskIndex;
            }
            System.out.println(" Invalid task number.");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println(" Invalid input format. Use: mark [number] or unmark [number]");
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

    public void setDone(boolean isDone) {
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
