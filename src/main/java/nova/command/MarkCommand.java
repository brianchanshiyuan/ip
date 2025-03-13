package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;

/**
 * A command to mark a task as done or not done.
 */
public class MarkCommand extends Command {
    private final String input;
    private final boolean isDone;

    /**
     * Constructs a new MarkCommand.
     *
     * @param input  The user input containing the task number to mark.
     * @param isDone Whether to mark the task as done or not done.
     */
    public MarkCommand(String input, boolean isDone) {
        this.input = input;
        this.isDone = isDone;
    }

    /**
     * Executes the mark command.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = getTaskIndex(input, tasks);
        nova.task.Task task = tasks.getTask(taskIndex); // Specify ip.task.Task
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