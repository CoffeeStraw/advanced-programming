package the8puzzle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;

/**
 * A status label for the EightPuzzle game.
 */
public class EightStatus extends JLabel implements PropertyChangeListener {

    /**
     * Creates new form EightStatus
     */
    public EightStatus() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        setText("STATUS");
        setPreferredSize(new java.awt.Dimension(50, 50));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case EightController.EVENT_NEWGAME:
                this.setText("START");
                break;
            case EightController.EVENT_MOVEVETOED:
                this.setText("KO");
                break;
            case EightController.EVENT_MOVEALLOWED:
                this.setText("OK");
                break;
            default:
                break;
        }
    }
}
