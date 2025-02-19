# Coding Exercises
* Adrian Banachowicz
* BE-CSS-CSA-2024-216-GRAP


---
## Git Feature Branch Workflow

While preparing each solution, I approached it as if I were dealing with production code, so I:
* Had main, develop, and feature branches.
* In respect to the interview process, I did not squash commits in pull requests from feature branches to develop, which I typically do with production code to avoid clutter in the commit history.
* I also preferred rebasing my code instead of merging, to maintain a clean, linear commit history rather than creating circular merge histories.
* I created a [GitHub project](https://github.com/users/AdBanacho/projects/2/views/1) to track progress of subtasks
    * Commits are called `Todo-numberOfSubtask: message`
    * I used one feature breach for it
---

## My Approach

In my approach, rather than permanently deleting tasks or categories from the database, I implemented a milestone/versioning strategy.
This means that when a task or category is "deleted" or updated, it is not removed from the database; instead, the endpoints
always retrieve the latest version. This preserves a full history of changes, which is essential for auditing purposes and understanding the evolution of our data.
The unique value of the category name is enforced by one of the validation methods, which was necessary to maintain the milestone/versioning approach.

Due to the complexity of the application, the history of the relationship between tasks and categories is not tracked in this version.
However, this skeleton lays the foundation for a robust and extensible REST API.

### Potential improvements:

* Implementation of JWT authentication.
* More tests.
* Adding support for sub-tasks.
* Improved application (Gradle) configuration.
* Implementing a methodology to track changes in the relationship between tasks and categories.