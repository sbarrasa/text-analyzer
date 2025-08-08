# Running Tests in text-regressor Project

This is a single-project Gradle setup. To run tests correctly, use one of the following commands:

## Correct Commands:
- `./gradlew test` - Run all tests
- `./gradlew :test` - Run all tests (alternative syntax)
- `./gradlew check` - Run tests and other verification tasks

## Incorrect Command (causes the error):
- `./gradlew text-regressor:test` - This fails because it tries to find a subproject named 'text-regressor'

## Explanation:
The error "Cannot locate tasks that match 'text-regressor:test' as project 'text-regressor' not found in root project 'text-regressor'" occurs because:

1. This project has no subprojects (confirmed by running `./gradlew projects`)
2. The syntax `project-name:task` is used for multi-project builds to specify which subproject's task to run
3. Since this is a single-project build, you should use just the task name (`test`) or the root project syntax (`:test`)

## Project Structure:
- Root project: 'text-regressor'
- Subprojects: None

Use `./gradlew tasks` to see all available tasks for this project.