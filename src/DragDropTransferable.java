import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Created by naomikoo on 2017-07-05.
 */
public class DragDropTransferable implements Transferable {

    public int blockx;
    public int blocky;
    public int blockwidth;
    public int blockheight;

    public DragDropTransferable(int blockx, int blocky, int blockwidth, int blockheight) {
        this.blockx = blockx;
        this.blocky = blocky;
        this.blockwidth = blockwidth;
        this.blockheight = blockheight;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[0];
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        // not matter what flavour is i am returning a dragdroptransferable
        return new Rectangle(blockx, blocky, blockwidth, blockheight);
    }
}
