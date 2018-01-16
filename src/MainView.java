import java.util.*;
import java.awt.*;
import javax.swing.*;


public class MainView extends JFrame implements ChangeScreenListener {

    private EditorModel editorModel;
    private MainMenuView mainMenuView;
    private GameView gameView;
    private EditorView editorView;
    private CustomGameDimenPickerView dimenPicker;

    int WIDTH = 800;
    int HEIGHT = 600;

    /**
     * Create a new View.
     */
    public MainView() {
        mainMenuView = new MainMenuView(this);
        // Set up the window.
        this.setTitle("CS349 - A1");
        this.setMinimumSize(new Dimension(350, 350));
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setContentPane(mainMenuView);

        setVisible(true);
    }

    public void changeScreenToLevelEditor(int heightBlocks, int widthBlocks) {
        editorView = new EditorView(heightBlocks, widthBlocks, this, this);
        this.setContentPane(editorView);
        this.invalidate();
        this.validate();
    }

    public void changeScreenToDimenPicker() {
        dimenPicker = new CustomGameDimenPickerView(this);
        this.setContentPane(dimenPicker);
        this.invalidate();
        this.validate();
    }

    public void changeScreenToGame() {
        gameView = new GameView(this, null, 0, 0);

        this.setContentPane(gameView);
        this.invalidate();
        this.validate();
    }

    public void changeScreenToCustomGame(ArrayList<Column> columns, int customHeight, int customWidth) {
        gameView = new GameView(this, columns, customHeight, customWidth);

        this.setContentPane(gameView);
        this.invalidate();
        this.validate();
    }

    public void changeScreenToMainMenu() {
        gameView = new GameView(this, null, 0, 0);

        this.setContentPane(mainMenuView);
        this.invalidate();
        this.validate();
    }
}
