package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;
import nova.task.Task; // Import Task
import nova.task.Todo; // Import Todo

/**
 * A command to add a new todotask.
 */
public class TodoCommand extends Command {
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
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
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