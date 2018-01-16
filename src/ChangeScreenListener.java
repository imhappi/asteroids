import java.util.*;
import java.awt.*;

interface ChangeScreenListener {
    void changeScreenToLevelEditor(int heightBlocks, int widthBlocks);
    void changeScreenToGame();
    void changeScreenToMainMenu();
    void changeScreenToCustomGame(ArrayList<Column> columns, int customHeight, int customWidth);
    void changeScreenToDimenPicker();
}

