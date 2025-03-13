package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;
import nova.task.Task;

import java.util.ArrayList;

/**
 * A command to find tasks containing a specific keyword.
 */
public class FindCommand extends Command {
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
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
     * @param storage The storage (not used in this command).
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The keyword to find cannot be empty. Example: 'find book'.");
        }
        String keyword = inputParts[1].trim().toLowerCase();
        ArrayList<Task> foundTasks = new ArrayList<>(); // Specify ip.task.Task
        for (nova.task.Task task : tasks.getTasks()) { // Specify ip.task.Task
            if (task.description.toLowerCase().contains(keyword)) {
                foundTasks.add(task);
            }
        }
        ui.showFoundTasks(foundTasks);
    }
}