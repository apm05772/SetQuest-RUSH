package setquest;

import javax.swing.JFrame;

// administra las pantallas principales del juego
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("SetQuest Rush");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        showMenu();
        setVisible(true);
    }

    // abre una partida nueva
    public void startGame() {
        setScreen(new GamePanel(this));
    }

    // vuelve al menu principal
    public void showMenu() {
        setScreen(new MenuPanel(this));
    }

    // reemplaza el panel visible
    private void setScreen(javax.swing.JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }
}
