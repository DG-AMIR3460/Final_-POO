package packagee.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class PanelRound extends JPanel {

    private int radius = 30;

    public PanelRound() {
        setOpaque(false);
    }

    public int getRadius()        { return radius; }
    public void setRadius(int r)  { this.radius = r; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
}
