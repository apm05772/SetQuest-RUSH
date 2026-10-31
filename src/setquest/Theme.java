package setquest;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

// contiene los colores, fuentes y estilos compartidos
public final class Theme {

    public static final Color BACKGROUND = new Color(11, 16, 32);
    public static final Color PANEL = new Color(21, 28, 47);
    public static final Color PANEL_LIGHT = new Color(31, 42, 68);
    public static final Color BUTTON = new Color(32, 42, 68);
    public static final Color BUTTON_HOVER = new Color(45, 60, 91);
    public static final Color CYAN = new Color(0, 217, 255);
    public static final Color MAGENTA = new Color(255, 61, 154);
    public static final Color GREEN = new Color(72, 229, 155);
    public static final Color RED = new Color(255, 82, 107);
    public static final Color YELLOW = new Color(255, 211, 92);
    public static final Color TEXT = new Color(242, 245, 255);
    public static final Color MUTED_TEXT = new Color(157, 168, 199);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 46);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 21);
    public static final Font STATUS_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 20);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private Theme() {
    }

    // aplica el estilo principal a un boton
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT);
        button.setBackground(BUTTON);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(createLineBorder(CYAN));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON);
                }
            }
        });
    }

    // crea un borde fino con espacio interno
    public static Border createLineBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );
    }

    // aplica el fondo principal a un componente
    public static void applyBackground(JComponent component) {
        component.setBackground(BACKGROUND);
        component.setForeground(TEXT);
    }
}
