package setquest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// muestra el menu principal
public class MenuPanel extends JPanel {

    public MenuPanel(GameFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(70, 180, 80, 180));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SETQUEST", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.CYAN);

        JLabel subtitleLabel = new JLabel("rush logico", SwingConstants.CENTER);
        subtitleLabel.setFont(Theme.SUBTITLE_FONT);
        subtitleLabel.setForeground(Theme.MAGENTA);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton startButton = createMenuButton("iniciar");
        JButton guideButton = createMenuButton("guia");
        JButton exitButton = createMenuButton("salir");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(10, 0, 10, 0);

        constraints.gridy = 0;
        buttonPanel.add(startButton, constraints);
        constraints.gridy = 1;
        buttonPanel.add(guideButton, constraints);
        constraints.gridy = 2;
        buttonPanel.add(exitButton, constraints);

        add(buttonPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel(
                "30 rondas  |  90 preguntas  |  controles: 1, 2, 3, 4",
                SwingConstants.CENTER
        );
        footerLabel.setFont(Theme.SMALL_FONT);
        footerLabel.setForeground(Theme.MUTED_TEXT);
        add(footerLabel, BorderLayout.SOUTH);

        startButton.addActionListener(event -> frame.startGame());
        guideButton.addActionListener(event -> showGuide());
        exitButton.addActionListener(event -> System.exit(0));
    }

    // crea un boton uniforme para el menu
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(420, 62));
        Theme.styleButton(button);
        return button;
    }

    // muestra las reglas y controles
    private void showGuide() {
        JOptionPane.showMessageDialog(
                this,
                """
                responde 30 preguntas de logica y teoria de conjuntos.

                cada pregunta tiene un limite de tiempo.
                una respuesta correcta aumenta la racha y el multiplicador.
                una respuesta incorrecta o fuera de tiempo quita una vida.
                cada cinco respuestas seguidas aparece un rompe racha.

                usa el mouse o las teclas 1, 2, 3 y 4.
                """,
                "guia",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
