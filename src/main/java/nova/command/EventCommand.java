package nova.command;

import nova.TaskList;
import nova.Ui;
import nova.Storage;
import nova.NovaException;
import nova.task.Task;
import nova.task.Event;

/**
 * A command to add a new event task.
 */
public class EventCommand extends Command {
    private final String[] inputParts;

    /**
     * Constructs a new EventCommand.
     *
     * @param inputParts The user input split into command and task details.
     */
    public EventCommand(String[] inputParts) {
        this.inputParts = inputParts;
    }

    /**
     * Executes the event command.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface to use for display.
     * @param storage The storage to use for saving tasks.
     * @throws NovaException If there is an error executing the command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            throw new NovaException("The description of an event cannot be empty. Example: 'event Project meeting /from 2024-12-24 14:00 /to 2024-12-24 16:00'.");
        }
        String[] details = parseTaskDetails(inputParts[1], "/from");
        String[] timeParts = parseTaskDetails(details[1], "/to");
        Task task = new Event(details[0], "from: " + timeParts[0], "to: " + timeParts[1]);
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
            throw new NovaException("Invalid format! Use 'event [task] /from [start] /to [end]'.");
        }
        return details;
    }
}