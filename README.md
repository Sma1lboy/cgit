# cgitlet: A Java-Based Version Control System

## Introduction

cgitlet is a lightweight, Java-based version control system inspired by Git. It offers essential functionalities for tracking and managing changes in your codebase, making it suitable for personal projects and learning purposes. 

## Features

cgitlet supports a variety of commands that are similar to Git, including but not limited to:

- `test`: Runs tests for the tool.
- `add <filename>`: Adds a file to the staging area.
- `commit <message>`: Commits the staged changes with a given message.
- `rm <filename>`: Removes a file from the current tracking and staging area.
- `log`: Displays the commit history for the current branch.
- `global-log`: Shows the commit history across all branches.
- `find <message>`: Finds and displays commits with a specific message.
- `status`: Displays the status of the working directory and staging area.
- `checkout <commit/branch>`: Checks out a specific commit or branch.
- `branch <branchname>`: Creates a new branch.
- `rm-branch <branchname>`: Deletes a specified branch.
- `reset <commit>`: Resets the current branch's head to the specified commit.
- `merge <branch>`: Merges a specified branch into the current branch.

Advanced features such as handling remote repositories (`add-remote`, `rm-remote`, `push`, `fetch`, `pull`) are also available.

## Installation


### Building from Source

To build cgitlet from the source code, you need a Java Development Kit (JDK) installed on your system. Follow these steps:

1. Clone the repository: `git clone [URL to cgitlet repository]`.
2. Navigate to the project directory: `cd cgitlet`.
3. Compile the project: `javac [path to java files]`.

### Running the Pre-built Jar

If you prefer using the pre-built jar, `cgitlet.jar` can be downloaded and run directly. Ensure you have Java Runtime Environment (JRE) installed, then execute:

```shell
java -jar cgitlet.jar [command]
```

### Setting Up Alias for Easy Usage

For convenience, you can set up an alias in your command-line interface. For example, in a Unix-like environment, add this line to your `.bashrc` or `.bash_profile`:

```
shellCopy code
alias cgit='java -jar /path/to/cgitlet.jar'
```

Replace `/path/to/cgitlet.jar` with the actual path to the `cgitlet.jar` file. After setting up the alias, you can use `cgit` followed by the commands listed above.

## Contributing

We welcome contributions to cgitlet! Please read our contributing guidelines [MIT](https://opensource.org/license/mit/) for details on how to submit changes.
