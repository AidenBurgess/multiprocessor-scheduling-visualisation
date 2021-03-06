# Visualisation Module

JavaFX was used to create the visualisation module. It has five components as shown in the picture below. They are current/best schedules, CPU/RAM graphs, and algorithm statistics.

![Visualisation-in-process](images/visualisation.png)

## Interactivity

The tasks on the current and the best schedules can be hovered over and a tooltip with the name, length, start time and the end time of the task.

## Statistics

The visualisation displays several useful statistics about the search to the user.

- **Active Branches**: number of edges that are actively being explored in the traversal of the search tree
- **Visited States**: total number of schedules that have been explored during the search.
- **Complete Schedules**: number of times the search has found a complete schedule to update the bound.

## Styling

CSS was used for all the styling in the module. CSS variables were used to switch between the light/dark themes. The colours of the current and the best schedule tasks were chosen such that there was a good contrast between them. The bright yellow of the best schedule tasks draws the user's attention to it and makes it seem more prominent whereas the grey colour of the current schedule tasks complements their fast-changing nature.
