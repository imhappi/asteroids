/**
 * Created by naomikoo on 2017-07-02.
 */
public interface GameStatusListener {
    public void scoreChanged(int score);
    public void gameStatusChanged(boolean won, int score);
    public void addLabels();
}
