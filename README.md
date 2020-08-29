# SOFTENG 306 Project 1

This is the repository for **Team 5 - Anubhav's Cheerleaders**. This includes the project code, the wiki and the executable file for the project, "scheduler.jar"

## Instructions

The file "scheduler.jar" contains the full project, including all dependencies. It can be executed independently from the command line:
`java -jar scheduler.jar INPUT.dot P [OPTION]`

| Argument (optional args in _italics_) | Meaning                                                                      |
| ------------------------------------- | ---------------------------------------------------------------------------- |
| INPUT.dot                             | File path to a task graph with integer weights in .dot format                |
| P                                     | The number of processors to schedule the input graph on (must be an integer) |
| _-o OUTPUT_                           | Path to write output to                                                      |
| _-v_                                  | Flag to use visualisation                                                    |
| _-p N_                                | Parallelise the scheduler with _N_ extra threads                             |
| _--help_                              | Use as first argument to get assistance with usage                           |

If you want to compile the code, the .jar files in `/project1/resources` will have to be added as libraries for the project.

## Wiki

The wiki can be found in `/wiki`. The markdown file for the home page is found [here](wiki/README.md)

## References

External dependencies can be found [here](wiki/References.md)

### Summary

JavaFX and JFoenix were used for the visualisation.  
JUnit3 was used for testing.  
ANTLR was used for parsing input graphs.  
StackOverflow for general code snippets.  
Material for the color theme.
