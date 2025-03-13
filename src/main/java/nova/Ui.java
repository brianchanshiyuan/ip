package nova;

import nova.task.Task;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The user interface class for the Nova application.
 *
 * This class is responsible for displaying messages to the user,
 * reading user input, and showing the task list.
 */
public class Ui {
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