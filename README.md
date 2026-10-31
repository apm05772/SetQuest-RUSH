# SetQuest Rush

A desktop **Java Swing game in Spanish** for practicing propositional logic, quantified statements and elementary set theory through timed multiple-choice questions.

## Game overview

- **30 rounds** per game, from a bank of **90 distinct questions** (76 regular + 14 special streak-breakers).
- Four shuffled answers per question, with a timer, points, multiplier and **three starting lives**.
- A streak-breaker challenge is queued after every five consecutive correct regular answers.
- Categories cover truth tables, implication, quantifiers, sets and logic puzzles.
- Use a mouse or keys **1, 2, 3, 4** (including number-pad keys) to answer.
- The interface text is Spanish; the code is Java 17+ and requires no external libraries.

## Run with a JDK

Install **JDK 17 or newer**. From the repository root:

```sh
mkdir -p out
javac -encoding UTF-8 -d out src/setquest/*.java
java -cp out setquest.Main
```

A desktop graphical environment is required to play.

## Run in NetBeans

Open the repository root as a project in Apache NetBeans, choose JDK 17+ and run `setquest.Main`. Legacy Ant/NetBeans project metadata (`build.xml`, `nbproject/`) is retained for convenience. The generated `dist/` JAR is excluded from version control; build from the source instead.

## Tests

Run the portable, **headless** tests:

```sh
./scripts/test.sh
```

Tests compile the Java sources and check bank size, non-repetition, answer keys, time limits, scoring, streak-breaker lifecycle and lives. They test the **programmatic game rules**, not Swing rendering or the mathematical correctness of every question.

## Repository layout

| Path | Purpose |
| --- | --- |
| `src/setquest/Main.java` | Swing entry point |
| `src/setquest/GameFrame.java`, `MenuPanel.java`, `GamePanel.java` | UI, flow, timers and keyboard controls |
| `src/setquest/GameState.java` | Per-game scores, lives and streaks |
| `src/setquest/Puzzle.java`, `PuzzleGenerator.java` | Question data and shuffled question bank |
| `src/setquest/Theme.java` | Shared visual styling |
| `tests/setquest/SmokeTest.java` | Dependency-free smoke tests |
| `scripts/test.sh` | Compile and execute checks |

## Known limitations

- Desktop Swing UI only; no mobile or web edition.
- Questions are static, and progress is not persisted across sessions.
- Keyboard/gameplay is not covered by automated UI tests.

## Licensing

No open-source license has been specified.