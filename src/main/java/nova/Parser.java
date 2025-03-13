package nova;

import nova.command.*;
import nova.NovaException;

/**
 * The parser class for the Nova application.
 *
 * This class is responsible for parsing user input and creating the
 * corresponding Command objects.
 */
class Parser {

    /**
     * Parses a command from user input.
     *
     * @param input The user input to parse.
     * @return The parsed Command object.
     * @throws NovaException If the input is invalid or an unknown command is encountered.
     */
    public Command parse(String input) throws NovaException {
        String[] inputParts = input.split(" ", 2);
        String command = inputParts[0];
        switch (command) {
        case "list":
            return new ListCommand();
        case "mark":
            return new MarkCommand(input, true);
        case "unmark":
            return new MarkCommand(input, false);
        case "todo":
            return new TodoCommand(inputParts);
        case "deadline":
            return new DeadlineCommand(inputParts);
        case "event":
            return new EventCommand(inputParts);
        case "delete":
            return new DeleteCommand(input);
        case "bye":
            return new ExitCommand();
        case "find":
            return new FindCommand(inputParts);
        default:
            throw new NovaException("Unknown command! Available commands: list, mark, unmark, todo, deadline, event, delete, find, bye.");
        }
    }
}