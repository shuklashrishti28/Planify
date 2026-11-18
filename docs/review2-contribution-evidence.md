# Review 2: Project and Contribution Evidence

## Project

**Planify** is a Java web project management app built with Jakarta Servlets, JSP, JDBC, MySQL, and Maven.

## Code and test evidence

- `src/main/java/com/planify/controller/` contains the servlet routes for login, users, projects, tasks, and the team-member dashboard.
- `src/main/java/com/planify/dao/` isolates database queries from the servlet controllers.
- `src/main/java/com/planify/model/` contains the `User`, `Project`, and `Task` models.
- `src/main/java/com/planify/util/` contains shared access checks, CSRF protection, password hashing, HTML escaping, JDBC connection settings, and dashboard routing.
- `src/test/java/com/planify/util/` covers role-based dashboard routes, HTML escaping, password hashing and verification, and CSRF token handling.
- `src/test/java/com/planify/controller/` calls the project and task servlet handlers to cover role rejection, CSRF rejection, invalid project dates/required fields, invalid task status, and malformed IDs.

The test command is `mvn test`. It was run on 2026-11-18: **20 tests, 0 failures, 0 errors, 0 skipped**.

## Team contribution evidence

The workspace copy used to prepare this document does not contain a `.git` directory. The public GitHub URL in the README was inspected for commit metadata. It currently shows two commits from one contributor identity; the two display names use the same underlying author email, so they should not be counted as separate teammates.

| Contributor shown in history | Commit evidence | What the commit title establishes |
|---|---|---|
| Shrishti Shukla (`shuklashrishti28`) | [Initial project commit](https://github.com/shuklashrishti28/Planify/commit/c7b67bb) | Project initialization |
| Same contributor identity | [README commit](https://github.com/shuklashrishti28/Planify/commit/5a2fe2d) | Project documentation added |

The public commit history does **not** show these teammates' work. The following entries are recorded as completed based on the team's confirmation; no evidence links were requested:

| Teammate | Contribution reported as completed |
|---|---|
| Vedant Baliyan | Completed servlet-level tests for project/task flows, including authorization and invalid form input. |
| Anika | Reviewed the responsive UI on desktop and mobile, fixed issues found, and prepared annotated screenshots for the presentation. |

The project now contains 20 passing tests, including servlet-level checks. 

