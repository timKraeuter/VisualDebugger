# AGENTS.md

Guidance for AI coding agents working in the `VisualDebugger` repository.

## Project Overview

IntelliJ IDEA plugin (Java 25 / Gradle 9.4) that visualizes debugging variables as UML
object diagrams. Supports browser-based WebSocket UI (object-diagram-js) and embedded
PlantUML visualization. Listens to IntelliJ debugger events, analyzes JDI stack frames,
builds object diagram domain models, and sends them to clients via WebSocket.

## Build / Test Commands

Use `gradlew.bat` on Windows, `./gradlew` on Linux/macOS.

```bash
./gradlew check          # Compile + Spotless check + Error Prone + tests (CI command)
./gradlew test           # Run tests only
./gradlew spotlessApply  # Auto-format all Java files (Google Java Format)
./gradlew spotlessCheck  # Check formatting without modifying
./gradlew build          # Full build (compile + test + assemble plugin)
./gradlew runIde         # Launch IntelliJ sandbox with plugin installed
```

### Running a Single Test

```bash
./gradlew test --tests "no.hvl.tk.visual.debugger.SharedStateTest"
./gradlew test --tests "no.hvl.tk.visual.debugger.SharedStateTest.addAndRemoveWebsocketClient"
./gradlew test --tests "no.hvl.tk.visual.debugger.debugging.stackframe.*"
```

### Test Locations

- Unit tests: `src/test/java/no/hvl/tk/visual/debugger/**/*Test.java`
- Manual test scenarios: `src/test/java/.../manueltests/` (excluded from Error Prone)
- JDI mock objects: `src/test/java/.../debugging/stackframe/mocks/`

### Pre-commit Checklist

Always run `./gradlew spotlessApply` then `./gradlew check` before committing. The CI
runs `./gradlew check` which will fail on formatting violations or Error Prone warnings.

## Code Style

Formatting enforced by **Spotless** (Google Java Format). Static analysis via **Error Prone**.

### Formatting Rules

- **Google Java Format**: 2-space indentation, 100-char line width
- **Java 25** source compatibility
- Prefer modern Java: records, pattern matching for `instanceof`, switch expressions,
  text blocks, `var` declarations where type is obvious
- Use `final` on method parameters and local variables where practical
  (e.g., `final Session session`, `final var reader = ...`)
- Annotations on parameters are formatted inline by `formatAnnotations()`

### Import Conventions

Imports are ordered automatically by Google Java Format: `static` imports first,
then non-static alphabetically (`com.*`, `jakarta.*`, `java.*`, `no.*`, `org.*`).
Avoid wildcard imports except where already established (e.g., `com.sun.jdi.*` in
`StackFrameAnalyzer`). In tests, `static` wildcard imports are acceptable for
assertion/mock libraries (`org.junit.jupiter.api.Assertions.*`, `org.mockito.Mockito.*`).

### Naming Conventions

| Category       | Convention                              | Examples                                                      |
| -------------- | --------------------------------------- | ------------------------------------------------------------- |
| Classes        | `PascalCase`                            | `StackFrameAnalyzer`, `WebSocketDebuggingVisualizer`          |
| Interfaces     | `PascalCase`                            | `DebuggingInfoVisualizer`, `IStackFrame`                      |
| Enums          | `PascalCase`, values `UPPER_SNAKE_CASE` | `PrimitiveTypes.BYTE`, `DebuggingMessageType.NEXT_DEBUG_STEP` |
| Records        | `PascalCase`                            | `ODPrimitiveRootValue`, `UIConfig`                            |
| Constants      | `UPPER_SNAKE_CASE`                      | `HOST_NAME`, `DEFAULT_UI_SERVER_PORT`                         |
| Methods        | `camelCase`                             | `doVisualization`, `getOrCreateDebuggingInfoVisualizer`       |
| Private fields | `camelCase` (no prefix)                 | `this.debugSession`, `this.browser`                           |
| Loggers        | `private static final Logger LOGGER`    | `Logger.getInstance(ClassName.class)`                         |
| Test classes   | `ClassNameTest`                         | `SharedStateTest`, `StackFrameAnalyzerTest`                   |

### Visibility and Class Structure

- Test classes and test methods are **package-private** (no `public` modifier)
- Production classes use appropriate visibility; avoid unnecessary `public`
- Field order: `private static final` (logger, constants) → instance fields → constructor → methods
- Private constructor for utility/singleton classes (e.g., `SharedState`)

### Error Handling

- Custom `RuntimeException`: `StackFrameAnalyzerException` wraps checked exceptions
- Logging via IntelliJ `Logger`: `LOGGER.error(e)`, `LOGGER.warn("message", e)`
- Try-catch with logging in server/IO code; do not swallow exceptions silently
- Null checks / guard clauses: `if (server == null) { return; }`
- Use `volatile` for shared mutable state accessed across threads

### Annotations

- `@NotNull`, `@Nullable` from `org.jetbrains.annotations`
- Jackson: `@JsonProperty`, `@JsonIdentityInfo`, `@JsonIdentityReference`, `@JsonInclude`
- Jakarta WebSocket: `@ServerEndpoint`, `@OnOpen`, `@OnClose`, `@OnMessage`
- `@Override` consistently on all overriding methods

## Testing Conventions

- **JUnit 5** (Jupiter) with `useJUnitPlatform()`
- **Hamcrest** assertions preferred: `assertThat(..., is(...))`, `assertThat(..., equalTo(...))`
- **JUnit 5** assertions also used: `assertTrue`, `assertFalse`, `assertThrows`, `assertDoesNotThrow`
- **Mockito** for mocking: `mock(Session.class)`, `when(...).thenReturn(...)`, `verify(...)`
- **Given/When/Then** comments to structure test bodies
- `@AfterEach` cleanup to reset `SharedState` between tests
- Hand-written JDI mock objects in `src/test/.../debugging/stackframe/mocks/` — prefer
  extending these mocks over Mockito for JDI interfaces

## Architecture

IntelliJ Platform Plugin architecture:

1. **Entry point**: `DebugProcessListener` (XDebuggerManagerListener, registered in plugin.xml)
2. **Session**: `StackFrameSessionListener` manages debugging lifecycle and UI updates
3. **Analysis**: `StackFrameAnalyzer` traverses JDI values, builds `ObjectDiagram` via builder
4. **Visualization**: `DebuggingInfoVisualizer` interface with WebSocket and PlantUML impls
5. **Server**: Tyrus WebSocket + Grizzly HTTP serving static UI from `src/main/resources/ui/`
6. **State**: `SharedState` static singleton for cross-component state (thread-safe via
   `ConcurrentHashMap` and `volatile` fields)

### Key Directories

```
src/main/java/.../debugging/stackframe/     # Stack frame analysis (JDI traversal)
src/main/java/.../debugging/visualization/  # Visualizer implementations
src/main/java/.../domain/                   # Object diagram domain model (records, builders)
src/main/java/.../server/                   # WebSocket + HTTP servers
src/main/java/.../server/endpoint/          # WebSocket endpoint + message types
src/main/java/.../settings/                 # Plugin settings (persistent state)
src/main/java/.../ui/                       # IntelliJ actions, icons, dialogs
src/main/java/.../util/                     # JSON conversion, classloader helpers
src/main/resources/META-INF/plugin.xml      # IntelliJ plugin descriptor
src/main/resources/ui/                      # Bundled browser UI assets (generated, do not edit)
```

### Key Dependencies

- **IntelliJ Platform SDK** (2026.1) — provides Logger, JDI, CEF browser, UI framework
- **PlantUML** — embedded UML diagram rendering (Smetana layout, no Graphviz needed)
- **Tyrus / Grizzly** — Jakarta WebSocket server implementation
- **Jackson** (via IntelliJ) — JSON serialization with annotations
- **Apache Commons Lang3** — utility methods

## CI

GitHub Actions on push/PR to any branch. Ubuntu runner, JDK 25 (Temurin), Gradle caching.
Runs `./gradlew check` (compile + spotless + error prone + tests). SonarCloud analysis
runs conditionally when `SONAR_TOKEN` is available.
