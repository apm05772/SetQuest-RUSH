package setquest;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;

// controla la partida, el tiempo, la racha y las animaciones
public class GamePanel extends JPanel {

    private static final int TOTAL_ROUNDS = 30;
    private static final int TRANSITION_DELAY = 850;

    private final GameFrame frame;
    private final GameState state;
    private final PuzzleGenerator generator;

    private Puzzle currentPuzzle;
    private Timer questionTimer;
    private Timer transitionTimer;
    private long timeRemaining;
    private long questionDeadlineNanos;
    private long totalTime;
    private boolean inputLocked;

    private final AnimatedPanel animatedPanel;
    private final JLabel roundLabel;
    private final JLabel scoreLabel;
    private final JLabel livesLabel;
    private final JLabel streakLabel;
    private final JLabel categoryLabel;
    private final JTextArea questionArea;
    private final JProgressBar timeBar;
    private final JButton[] optionButtons;

    public GamePanel(GameFrame frame) {
        this.frame = frame;
        state = new GameState(TOTAL_ROUNDS);
        generator = new PuzzleGenerator();

        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 26, 24, 26));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        animatedPanel = new AnimatedPanel();
        animatedPanel.setLayout(new BorderLayout(0, 16));
        animatedPanel.setBackground(Theme.PANEL);
        animatedPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_LIGHT, 2),
                BorderFactory.createEmptyBorder(18, 22, 20, 22)
        ));

        categoryLabel = new JLabel("", SwingConstants.CENTER);
        categoryLabel.setFont(Theme.STATUS_FONT);
        categoryLabel.setForeground(Theme.MAGENTA);
        animatedPanel.add(categoryLabel, BorderLayout.NORTH);

        questionArea = new JTextArea();
        questionArea.setFont(Theme.BODY_FONT);
        questionArea.setForeground(Theme.TEXT);
        questionArea.setBackground(Theme.PANEL);
        questionArea.setEditable(false);
        questionArea.setFocusable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        animatedPanel.add(questionArea, BorderLayout.CENTER);

        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 14, 14));
        answerPanel.setOpaque(false);
        answerPanel.setPreferredSize(new Dimension(0, 190));
        optionButtons = new JButton[4];

        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i] = new JButton();
            Theme.styleButton(optionButtons[i]);
            optionButtons[i].setFont(new Font("SansSerif", Font.BOLD, 17));
            optionButtons[i].addActionListener(event -> checkAnswer(index));
            answerPanel.add(optionButtons[i]);
        }

        animatedPanel.add(answerPanel, BorderLayout.SOUTH);
        add(animatedPanel, BorderLayout.CENTER);

        timeBar = new JProgressBar(0, 1000);
        timeBar.setValue(1000);
        timeBar.setStringPainted(true);
        timeBar.setFont(Theme.STATUS_FONT);
        timeBar.setForeground(Theme.CYAN);
        timeBar.setBackground(Theme.PANEL_LIGHT);
        timeBar.setBorderPainted(false);
        timeBar.setPreferredSize(new Dimension(0, 24));
        add(timeBar, BorderLayout.SOUTH);

        roundLabel = findStatusLabel(headerPanel, 0);
        scoreLabel = findStatusLabel(headerPanel, 1);
        livesLabel = findStatusLabel(headerPanel, 2);
        streakLabel = findStatusLabel(headerPanel, 3);

        configureKeyboardControls();
        loadNextPuzzle();
    }

    // crea la zona superior con las estadisticas
    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("setquest rush", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(Theme.CYAN);
        wrapper.add(titleLabel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statusPanel.setOpaque(false);

        statusPanel.add(createStatusLabel());
        statusPanel.add(createStatusLabel());
        statusPanel.add(createStatusLabel());
        statusPanel.add(createStatusLabel());

        wrapper.add(statusPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // crea una etiqueta uniforme para las estadisticas
    private JLabel createStatusLabel() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(Theme.STATUS_FONT);
        label.setForeground(Theme.TEXT);
        label.setOpaque(true);
        label.setBackground(Theme.PANEL);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_LIGHT, 1),
                BorderFactory.createEmptyBorder(9, 8, 9, 8)
        ));
        return label;
    }

    // obtiene una etiqueta de la fila de estadisticas
    private JLabel findStatusLabel(JPanel headerPanel, int index) {
        JPanel statusPanel = (JPanel) headerPanel.getComponent(1);
        return (JLabel) statusPanel.getComponent(index);
    }

    // carga una pregunta normal o un rompe racha
    private void loadNextPuzzle() {
        if (state.isFinished()) {
            endGame();
            return;
        }

        inputLocked = true;
        boolean breaker = state.isBreakerPending();

        if (breaker) {
            state.consumeBreaker();
        }

        currentPuzzle = generator.nextPuzzle(breaker);
        updateInterface();
        resetButtons();

        if (currentPuzzle.isStreakBreaker()) {
            setButtonsEnabled(false);
            animatedPanel.showEffect("rompe racha", Theme.MAGENTA, 900);

            transitionTimer = new Timer(900, event -> {
                setButtonsEnabled(true);
                inputLocked = false;
                startQuestionTimer();
            });
            transitionTimer.setRepeats(false);
            transitionTimer.start();
        } else {
            setButtonsEnabled(true);
            inputLocked = false;
            startQuestionTimer();
        }
    }

    // actualiza el texto visible de la pregunta y el estado
    private void updateInterface() {
        int shownRound = Math.min(state.getCurrentRound(), state.getTotalRounds());

        roundLabel.setText("ronda: " + shownRound + "/" + state.getTotalRounds());
        scoreLabel.setText("puntaje: " + state.getScore());
        livesLabel.setText("vidas: " + state.getLives());
        streakLabel.setText("racha: " + state.getStreak() + "  x" + state.getMultiplier());

        if (currentPuzzle != null) {
            String prefix = currentPuzzle.isStreakBreaker() ? "rompe racha  |  " : "";
            categoryLabel.setText(prefix + currentPuzzle.getCategory() + "  |  " + currentPuzzle.getDifficulty());
            categoryLabel.setForeground(currentPuzzle.isStreakBreaker() ? Theme.MAGENTA : Theme.MUTED_TEXT);
            questionArea.setText(currentPuzzle.getQuestion().strip());
            questionArea.setCaretPosition(0);

            String[] options = currentPuzzle.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText((i + 1) + "   " + options[i]);
            }
        }
    }

    // inicia la cuenta regresiva de la pregunta actual
    private void startQuestionTimer() {
        stopQuestionTimer();

        totalTime = currentPuzzle.getTimeLimit() * 1000L;
        timeRemaining = totalTime;
        questionDeadlineNanos = System.nanoTime() + totalTime * 1_000_000L;
        updateTimeBar();

        questionTimer = new Timer(50, event -> {
            // Monotonic clock avoids the timer drift caused by a busy Swing event thread.
            timeRemaining = Math.max(0, (questionDeadlineNanos - System.nanoTime()) / 1_000_000L);
            updateTimeBar();

            if (timeRemaining <= 0) {
                stopQuestionTimer();
                finishQuestion(false, -1, true);
            }
        });
        questionTimer.start();
    }

    // actualiza la barra y su color segun el tiempo restante
    private void updateTimeBar() {
        double ratio = totalTime == 0 ? 0.0 : Math.max(0.0, timeRemaining / (double) totalTime);
        timeBar.setValue((int) Math.round(ratio * 1000));
        timeBar.setString(String.format("%.1f s", Math.max(0, timeRemaining) / 1000.0));

        if (ratio <= 0.25) {
            timeBar.setForeground(Theme.RED);
        } else if (ratio <= 0.50) {
            timeBar.setForeground(Theme.YELLOW);
        } else {
            timeBar.setForeground(Theme.CYAN);
        }
    }

    // verifica una respuesta elegida por mouse o teclado
    private void checkAnswer(int index) {
        if (inputLocked || index < 0 || index >= optionButtons.length) {
            return;
        }

        finishQuestion(currentPuzzle.isCorrect(index), index, false);
    }

    // registra el resultado, muestra la animacion y prepara la siguiente ronda
    private void finishQuestion(boolean correct, int selectedIndex, boolean timeout) {
        if (inputLocked) {
            return;
        }

        inputLocked = true;
        stopQuestionTimer();
        setButtonsEnabled(false);

        String effectText;
        Color effectColor;

        if (correct) {
            int points = state.recordCorrect(currentPuzzle.isStreakBreaker());
            optionButtons[selectedIndex].setBackground(Theme.GREEN);
            optionButtons[selectedIndex].setForeground(Theme.BACKGROUND);
            effectText = createCorrectEffect(points);
            effectColor = currentPuzzle.isStreakBreaker() ? Theme.MAGENTA : Theme.GREEN;
        } else {
            state.recordWrong();

            if (selectedIndex >= 0) {
                optionButtons[selectedIndex].setBackground(Theme.RED);
                optionButtons[selectedIndex].setForeground(Theme.TEXT);
            }

            optionButtons[currentPuzzle.getCorrectIndex()].setBackground(Theme.GREEN);
            optionButtons[currentPuzzle.getCorrectIndex()].setForeground(Theme.BACKGROUND);

            effectText = timeout ? "tiempo agotado" : "racha perdida";
            effectColor = Theme.RED;
        }

        state.completeRound();
        updateInterface();
        animatedPanel.showEffect(effectText, effectColor, TRANSITION_DELAY);

        transitionTimer = new Timer(TRANSITION_DELAY, event -> {
            if (state.isFinished()) {
                endGame();
            } else {
                loadNextPuzzle();
            }
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }

    // elige el mensaje que aparece al responder correctamente
    private String createCorrectEffect(int points) {
        if (currentPuzzle.isStreakBreaker()) {
            return "rompe racha superado  +" + points;
        }

        int streak = state.getStreak();

        if (streak >= 10 && streak % 5 == 0) {
            return "imparable x" + streak + "  +" + points;
        }

        if (streak >= 5) {
            return "racha x" + streak + "  +" + points;
        }

        if (streak >= 3) {
            return "precision x" + streak + "  +" + points;
        }

        return "correcto  +" + points;
    }

    // restaura la apariencia normal de los cuatro botones
    private void resetButtons() {
        for (JButton button : optionButtons) {
            button.setBackground(Theme.BUTTON);
            button.setForeground(Theme.TEXT);
        }
    }

    // activa o desactiva todas las respuestas
    private void setButtonsEnabled(boolean enabled) {
        for (JButton button : optionButtons) {
            button.setEnabled(enabled);
        }
    }

    // agrega controles rapidos con las teclas 1, 2, 3 y 4
    private void configureKeyboardControls() {
        for (int i = 0; i < 4; i++) {
            final int index = i;
            String actionName = "answer" + i;

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, 0),
                    actionName
            );
            getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    checkAnswer(index);
                }
            });

            String numpadActionName = "numpadAnswer" + i;
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1 + i, 0),
                    numpadActionName
            );
            getActionMap().put(numpadActionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    checkAnswer(index);
                }
            });
        }
    }

    // detiene el reloj activo de la pregunta
    private void stopQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.stop();
        }
    }

    // muestra el resumen final y permite reiniciar o volver al menu
    private void endGame() {
        inputLocked = true;
        stopQuestionTimer();

        if (transitionTimer != null) {
            transitionTimer.stop();
        }

        String result = state.getLives() > 0 ? "partida completada" : "sin vidas";
        String message = """
                %s

                puntaje final: %d
                respuestas correctas: %d/%d
                precision: %.1f%%
                mejor racha: %d
                """.formatted(
                result,
                state.getScore(),
                state.getCorrectAnswers(),
                state.getCompletedRounds(),
                state.getAccuracy(),
                state.getHighestStreak()
        );

        Object[] options = {"jugar de nuevo", "volver al menu", "salir"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "resultado",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            frame.startGame();
        } else if (choice == 1) {
            frame.showMenu();
        } else {
            System.exit(0);
        }
    }

    // panel que dibuja destellos y texto sobre la interfaz
    private static class AnimatedPanel extends JPanel {

        private String overlayText = "";
        private Color overlayColor = Theme.CYAN;
        private float overlayAlpha;
        private float flashAlpha;
        private int overlaySize = 38;
        private Timer animationTimer;

        public void showEffect(String text, Color color, int duration) {
            if (animationTimer != null) {
                animationTimer.stop();
            }

            overlayText = text;
            overlayColor = color;
            long startTime = System.currentTimeMillis();

            animationTimer = new Timer(16, event -> {
                double progress = Math.min(1.0, (System.currentTimeMillis() - startTime) / (double) duration);
                overlayAlpha = (float) Math.sin(Math.PI * progress);
                flashAlpha = (float) ((1.0 - progress) * 0.22);
                overlaySize = 38 + (int) Math.round(progress * 24);
                repaint();

                if (progress >= 1.0) {
                    animationTimer.stop();
                    overlayText = "";
                    overlayAlpha = 0.0f;
                    flashAlpha = 0.0f;
                    repaint();
                }
            });
            animationTimer.start();
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);

            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (flashAlpha > 0.0f) {
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flashAlpha));
                graphics2D.setColor(overlayColor);
                graphics2D.fillRect(0, 0, getWidth(), getHeight());
            }

            if (!overlayText.isEmpty() && overlayAlpha > 0.0f) {
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
                graphics2D.setColor(overlayColor);
                graphics2D.setFont(new Font("SansSerif", Font.BOLD, overlaySize));

                FontMetrics metrics = graphics2D.getFontMetrics();
                int x = Math.max(18, (getWidth() - metrics.stringWidth(overlayText)) / 2);
                int y = getHeight() / 2 + metrics.getAscent() / 2;

                graphics2D.drawString(overlayText, x, y);
            }

            graphics2D.dispose();
        }
    }
}
