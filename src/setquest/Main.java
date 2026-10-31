package setquest;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// inicia el programa en el hilo grafico de swing
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.background", Theme.PANEL);
            UIManager.put("Panel.background", Theme.PANEL);
            UIManager.put("OptionPane.messageForeground", Theme.TEXT);
            UIManager.put("Button.font", Theme.BUTTON_FONT);
            UIManager.put("Label.font", Theme.BODY_FONT);

            new GameFrame();
        });
    }
}
