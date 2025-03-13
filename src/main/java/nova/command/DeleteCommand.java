package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;
import nova.task.Task;

/**
 * A command to delete a task.
 */
public class DeleteCommand extends Command {
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
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
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