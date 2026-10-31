package setquest;

// representa una pregunta individual del juego
public class Puzzle {

    private final String question;
    private final String[] options;
    private final int correctIndex;
    private final String category;
    private final String difficulty;
    private final int timeLimit;
    private final boolean streakBreaker;

    public Puzzle(
            String question,
            String[] options,
            int correctIndex,
            String category,
            String difficulty,
            int timeLimit,
            boolean streakBreaker
    ) {
        this.question = question;
        if (options == null || options.length != 4 || correctIndex < 0 || correctIndex >= 4) {
            throw new IllegalArgumentException("Each puzzle needs four answers and a valid correct index");
        }
        if (timeLimit <= 0) throw new IllegalArgumentException("Time limit must be positive");
        this.options = options.clone();
        this.correctIndex = correctIndex;
        this.category = category;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.streakBreaker = streakBreaker;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options.clone();
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean isStreakBreaker() {
        return streakBreaker;
    }

    public boolean isCorrect(int index) {
        return index == correctIndex;
    }
}
