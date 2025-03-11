# Nova User Guide

Nova is a command-line task management application that helps you organize your tasks efficiently. It allows you to create, manage, and track various types of tasks, including todos, deadlines, and events.

## Adding Deadlines

To add a task with a deadline, use the `deadline` command followed by the task description and the deadline date/time.

Example: `deadline Submit report /by 2024-03-15`

This command adds a deadline task with the description "Submit report" and a deadline of March 15, 2024.

Added: [D][ ] Submit report (by: 2024-03-15)
Now you have 1 tasks in the list.

## Adding Todos

To add a simple todo task, use the `todo` command followed by the task description.

Example: `todo Buy groceries`

This command adds a todo task with the description "Buy groceries".

Added: [T][ ] Buy groceries
Now you have 1 tasks in the list.

## Adding Events

To add an event task, use the `event` command followed by the task description, start date/time, and end date/time.

Example: `event Meeting with John /from 2024-03-10 10:00 /to 2024-03-10 11:00`

This command adds an event task with the description "Meeting with John", a start time of March 10, 2024 at 10:00 AM, and an end time of March 10, 2024 at 11:00 AM.

Added: [E][ ] Meeting with John (from: 2024-03-10 10:00 to: 2024-03-10 11:00)
Now you have 1 tasks in the list.

## Listing Tasks

To list all tasks, use the `list` command.

Example: `list`

This command displays all the tasks currently in the list.

Here are the tasks in your list:

[T][ ] Buy groceries
[D][ ] Submit report (by: 2024-03-15)
[E][ ] Meeting with John (from: 2024-03-10 10:00 to: 2024-03-10 11:00)

## Marking Tasks

To mark a task as done, use the `mark` command followed by the task number.

Example: `mark 1`

This command marks the first task in the list as done.

Nice! I've marked this task as done:
[T][X] Buy groceries

## Unmarking Tasks

To unmark a task as done, use the `unmark` command followed by the task number.

Example: `unmark 1`

This command unmarks the first task in the list.

OK, I've marked this task as not done yet:
[T][ ] Buy groceries


## Deleting Tasks

To delete a task, use the `delete` command followed by the task number.

Example: `delete 1`

This command deletes the first task in the list.

Noted. I've removed this task:
[T][ ] Buy groceries
Now you have 2 tasks in the list.

## Finding Tasks

To find tasks containing a specific keyword, use the `find` command followed by the keyword.

Example: `find report`

This command searches for tasks containing the keyword "report" in their descriptions.

Here are the matching tasks in your list:
1.[D][ ] Submit report (by: 2024-03-15)


## Exiting Nova

To exit Nova, use the `bye` command.

Example: `bye`

This command exits the application.

Bye. Hope to see you again soon!
