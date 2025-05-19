# ClassBuilder

A Java console-based application that allows users to dynamically create Java class files by inputting the class name, attributes, constructors, methods, and to manage them programmatically.

## Project Structure

```
/src
  ├── menu              → main class and menu system
  ├── builder          → business logic (core class builder components)
  ├── model            → models representing class elements (Field, Method, etc.)
  ├── util             → file handling, validations, and string helpers
  └── exception        → custom exceptions
```

## Features

- Create new Java classes with attributes, methods, and constructors
- Define visibility, types, and modifiers for all elements
- Generate properly formatted Java source code
- Save class files to disk

## How to Run

### Using PowerShell Script (Windows)

```
.\build.ps1
```

### Manually

1. Compile the project:
```
javac -d out src/model/*.java src/builder/*.java src/util/*.java src/exception/*.java src/menu/*.java
```

2. Run the application:
```
java -cp out Main
```

## Usage

The application presents a menu-driven interface:

1. Create new class
2. Add attribute
3. Add method
4. Add constructor
5. Display class preview
6. Save class to file
7. Exit

Follow the prompts to build your Java class. When finished, you can preview the code and save it to a file. 