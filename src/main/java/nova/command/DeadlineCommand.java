package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;
import nova.task.Task;
import nova.task.Deadline;

/**
 * A command to add a new deadline task.
 */
public class DeadlineCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new DeadlineCommand.
     *
     * @param inputParts The user input split into command and task details.
     */
    public DeadlineCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the deadline command.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
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

    /**
     * Parses the task details from the user input.
     *
     * @param input     The user input containing the task details.
     * @param delimiter The delimiter to split the input by.
     * @return The parsed task details.
     * @throws NovaException If the input is invalid.
     */
    private String[] parseTaskDetails(String input, String delimiter) throws NovaException {
        String[] details = input.split(" " + delimiter + " ", 2);
        if (details.length < 2) {
            throw new NovaException("Invalid format! Use 'deadline [task] /by [date]'.");
        }
        return details;
    }
}