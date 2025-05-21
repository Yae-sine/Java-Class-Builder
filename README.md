# Java Class Builder

A powerful, interactive console-based application for designing, visualizing, and generating Java classes, interfaces, and inheritance structures. Ideal for students, educators, and developers who want to quickly prototype or refactor Java code with live feedback and robust validation.

## Features

- **Interactive Console UI**
  - Modern, clear menus with box drawing, line numbers, and sectioning
  - Welcome and exit banners for a polished user experience

- **Class & Interface Creation**
  - Create Java classes and interfaces with fields, methods, and constructors
  - Add, edit, or remove fields, methods, and parameters
  - Supports inheritance (extends/implements) with live validation
  - Prevents interfaces from extending classes (enforces Java rules)

- **Undo/Redo Functionality**
  - Instantly revert or reapply changes using stack-based snapshots
  - Unlimited undo/redo during a session

- **Live Preview & Syntax Highlighting**
  - See a real-time, syntax-highlighted preview of your Java class as you build
  - Boxed, line-numbered, and color-formatted output for easy reading

- **Input Validation & Error Feedback**
  - Detailed feedback for invalid names, types, or Java rule violations
  - Duplicate detection for class, field, and method names (at input and before saving)

- **Inheritance Visualization**
  - Vertical, node-based tree view of class/interface inheritance
  - Handles multiple interfaces and complex hierarchies

- **Refactoring Tools**
  - Rename fields, methods, or classes
  - Change types or method signatures on the fly

- **Project & File Management**
  - Choose or create a working folder for your Java files
  - Save generated classes/interfaces directly to disk

- **Robust Utility Layer**
  - Modular codebase with clear separation (model, builder, menu, util, exception)
  - Utility classes for file management, validation, and formatting

- **Error Handling**
  - Custom exceptions for invalid names and duplicate elements
  - Clear, actionable error messages throughout the UI

- **Ready for Extension**
  - Easily add new features or extend existing ones thanks to clean architecture

## Getting Started

1. **Clone the repository:**
   ```sh
   git clone https://github.com/yourusername/JavaClassBuilder.git
   ```
2. **Compile the project:**
   ```sh
   javac -d bin src/**/*.java
   ```
3. **Run the application:**
   ```sh
   java -cp bin Main
   ```

## Who is it for?
- Java learners and students
- Educators teaching OOP and Java basics
- Developers prototyping or refactoring Java code

## Contributing
Pull requests and suggestions are welcome! Please open an issue to discuss your ideas or report bugs.

## License
MIT License

---

### Short GitHub Description

