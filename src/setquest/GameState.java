package setquest;

// guarda el progreso y las estadisticas de una partida
public class GameState {

    private final int totalRounds;
    private int completedRounds;
    private int score;
    private int lives;
    private int streak;
    private int highestStreak;
    private int correctAnswers;
    private boolean breakerPending;

    public GameState(int totalRounds) {
        if (totalRounds <= 0) throw new IllegalArgumentException("Total rounds must be positive");
        this.totalRounds = totalRounds;
        lives = 3;
    }

    // registra una respuesta correcta y devuelve los puntos obtenidos
    public int recordCorrect(boolean breaker) {
        streak++;
        correctAnswers++;
        highestStreak = Math.max(highestStreak, streak);

        int basePoints = breaker ? 300 : 100;
        int points = basePoints * getMultiplier();
        score += points;

        if (!breaker && streak % 5 == 0) {
            breakerPending = true;
        }

        return points;
    }

    // registra una respuesta incorrecta o una respuesta fuera de tiempo
    public void recordWrong() {
        lives = Math.max(0, lives - 1);
        streak = 0;
        breakerPending = false;
    }

    // registra que una ronda ya fue completada
    public void completeRound() {
        if (completedRounds < totalRounds) completedRounds++;
    }

    // marca el desafio pendiente como utilizado
    public void consumeBreaker() {
        breakerPending = false;
    }

    public int getMultiplier() {
        return Math.min(5, 1 + streak / 3);
    }

    public int getCurrentRound() {
        return completedRounds + 1;
    }

    public int getCompletedRounds() {
        return completedRounds;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getStreak() {
        return streak;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public boolean isBreakerPending() {
        return breakerPending;
    }

    public boolean isFinished() {
        return completedRounds >= totalRounds || lives <= 0;
    }

    public double getAccuracy() {
        if (completedRounds == 0) {
            return 0.0;
        }

        return correctAnswers * 100.0 / completedRounds;
    }
}
