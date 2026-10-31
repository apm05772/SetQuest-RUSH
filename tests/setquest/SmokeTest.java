package setquest;

import java.util.HashSet;
import java.util.Set;

/** Headless smoke tests of the 90-question bank and game state. */
public final class SmokeTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        PuzzleGenerator bank = new PuzzleGenerator();
        check(bank.getTotalBankSize() == 90, "Bank should have 90 questions");
        check(bank.getRegularCount() == 76, "Expected 76 regular questions");
        check(bank.getBreakerCount() == 14, "Expected 14 streak breakers");
        Set<String> seenQuestions = new HashSet<>();
        for (int i = 0; i < 90; i++) {
            Puzzle puzzle = bank.nextPuzzle(i < 14);
            check(seenQuestions.add(puzzle.getQuestion()), "Duplicate question in one match");
            check(puzzle.getOptions().length == 4, "Each question has four choices");
            check(puzzle.isCorrect(puzzle.getCorrectIndex()), "Incorrect answer key");
            check(puzzle.getTimeLimit() > 0, "Time limit must be positive");
        }
        check(bank.getTotalBankSize() == 0, "Questions should be consumed");
        try {
            bank.nextPuzzle(false);
            throw new AssertionError("Empty bank should throw");
        } catch (IllegalStateException expected) { /* expected */ }

        GameState state = new GameState(30);
        check(state.getLives() == 3, "Three initial lives");
        for (int i = 0; i < 5; i++) {
            state.recordCorrect(false);
            state.completeRound();
        }
        check(state.isBreakerPending(), "Streak breaker after five correct answers");
        state.consumeBreaker();
        check(!state.isBreakerPending(), "Consumed streak breaker should clear");
        state.recordWrong();
        state.completeRound();
        check(state.getStreak() == 0 && state.getLives() == 2, "Incorrect-answer behavior");
        check(Math.abs(state.getAccuracy() - 500.0 / 6.0) < 1e-6, "Accuracy should be correct answers / completed rounds");
        state.recordWrong(); state.recordWrong(); state.recordWrong();
        check(state.getLives() == 0 && state.isFinished(), "Lives should not fall below zero");
        System.out.println("SetQuest Rush smoke tests passed");
    }
}
