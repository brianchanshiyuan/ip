package nova.task;

/**
 * A class representing an event task.
 */
public class Event extends Task {
    public String from;
    public String to;

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