package tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import main.Note;

/**
 * View for Tutorial Model
 *
 * @author Emma and Jelena.
 */
public class TutorialModeView extends JPanel implements PropertyChangeListener {

    private TutorialModel model;

    private Instructions ins = new Instructions();
    private JLabel tutorialImg = new JLabel(new ImageIcon("Assets/tutorial.png"));
    private JLabel tutorialInfo = new JLabel((String) ins.getString(0));

    private JLabel streakLbl = new JLabel("<html> <center> Note\n streak </center> </html>");
    private JLabel streakCount;
    private JLabel score;

    private JLabel fret0 = new JLabel();
    private JLabel fret1 = new JLabel();
    private JLabel fret2 = new JLabel();
    private JLabel fret3 = new JLabel();
    private JLabel fret4 = new JLabel();
    private JLabel fret5 = new JLabel();

    /**
     * Construct the tutorial mode view in the MVC pattern, instantiating all the
     * components.
     * 
     * @param model The tutorial mode model that controls the logic.
     */
    public TutorialModeView(TutorialModel model) {
        this.setName("TUTORIAL");
        this.setLayout(null);
        this.model = model;

        for (Note n : model.notesOnScreen) {
            this.add(n);
        }

        score = new JLabel(Integer.toString(model.getScore()));
        score.setLocation(60, 650);
        score.setSize(200, 60);
        score.setFont(new Font("Sans Serif", Font.BOLD, 30));
        this.add(score);

        streakCount = new JLabel(Integer.toString(model.getStreakCount()));
        streakCount.setLocation(420, 60);
        streakCount.setSize(200, 70);
        streakCount.setFont(new Font("Sans Serif", Font.BOLD, 50));
        this.add(streakCount);

        streakLbl.setLocation(490, 60);
        streakLbl.setSize(100, 70);
        streakLbl.setFont(new Font("Sans Serif", Font.BOLD, 21));
        this.add(streakLbl);

        /* fret labels appear when fret pressed to indicate btn press */
        fret0.setLocation(270, 600);
        fret0.setSize(30, 30);
        fret0.setOpaque(true);
        fret0.setBackground(Color.BLACK);
        fret0.setBorder(new LineBorder(Color.CYAN, 3));
        fret0.setVisible(false);
        this.add(fret0);

        fret1.setLocation(460, 600);
        fret1.setSize(30, 30);
        fret1.setOpaque(true);
        fret1.setBackground(Color.BLACK);
        fret1.setBorder(new LineBorder(Color.CYAN, 3));
        fret1.setVisible(false);
        this.add(fret1);

        fret2.setLocation(640, 600);
        fret2.setSize(30, 30);
        fret2.setOpaque(true);
        fret2.setBackground(Color.BLACK);
        fret2.setBorder(new LineBorder(Color.CYAN, 3));
        fret2.setVisible(false);
        this.add(fret2);

        fret3.setLocation(310, 600);
        fret3.setSize(30, 30);
        fret3.setOpaque(true);
        fret3.setBackground(Color.WHITE);
        fret3.setBorder(new LineBorder(Color.CYAN, 3));
        fret3.setVisible(false);
        this.add(fret3);

        fret4.setLocation(500, 600);
        fret4.setSize(30, 30);
        fret4.setOpaque(true);
        fret4.setBackground(Color.WHITE);
        fret4.setBorder(new LineBorder(Color.CYAN, 3));
        fret4.setVisible(false);
        this.add(fret4);

        fret5.setLocation(680, 600);
        fret5.setSize(30, 30);
        fret5.setOpaque(true);
        fret5.setBackground(Color.WHITE);
        fret5.setBorder(new LineBorder(Color.CYAN, 3));
        fret5.setVisible(false);
        this.add(fret5);

        tutorialImg.setLocation(680, 200);
        tutorialImg.setSize(150, 150);
        tutorialImg.setVisible(true);
        this.add(tutorialImg);

        tutorialInfo.setLocation(400, 120);
        tutorialInfo.setSize(210, 120);
        tutorialInfo.setFont(new Font("Sans Serif", Font.BOLD, 16));
        this.add(tutorialInfo);

        ArrowBubble arrowBubble = new ArrowBubble();
        arrowBubble.setLocation(390, 130);
        arrowBubble.setSize(220, 130);
        arrowBubble.setVisible(true);
        this.add(arrowBubble);

        this.setOpaque(false);
        this.setFocusable(true);
        model.addPropertyChangeListener(this);
        validate();

    }

    /**
     * Start playing the song at the given path.
     * 
     * @param path
     */
    public void startPlaying(String path) {
        model.startSong();
    }

    /**
     * Show a light indicating the fret has been pressed.
     * 
     * @param fret the fret that was pressed.
     */
    public void fretPressed(int fret) {
        switch (fret) {
        case 0:
            fret0.setVisible(true);
            break;
        case 1:
            fret1.setVisible(true);
            break;
        case 2:
            fret2.setVisible(true);
            break;
        case 3:
            fret3.setVisible(true);
            break;
        case 4:
            fret4.setVisible(true);
            break;
        case 5:
            fret5.setVisible(true);
            break;
        }
        redraw();
    }

    /**
     * Hide the associated light when a fret is released.
     * 
     * @param fret The fret being released.
     */
    public void fretReleased(int fret) {
        switch (fret) {
        case 0:
            fret0.setVisible(false);
            break;
        case 1:
            fret1.setVisible(false);
            break;
        case 2:
            fret2.setVisible(false);
            break;
        case 3:
            fret3.setVisible(false);
            break;
        case 4:
            fret4.setVisible(false);
            break;
        case 5:
            fret5.setVisible(false);
            break;
        }
        redraw();
    }

    /**
     * Collect a note when it is successfully played.
     * 
     * @param n The note being collected.
     */
    public void collectNote(Note n) {
        streakCount.setText(Integer.toString(model.getStreakCount()));
        score.setText(Integer.toString(model.getScore()));
        remove(n);
        redraw();
    }

    /**
     * Remove a note if it reaches the end of the highway.
     */
    public void dropNote() {
        streakCount.setText(Integer.toString(model.getStreakCount()));
        // TODO proper note removal
    }

    public void redraw() {
        revalidate();
        repaint();
    }

    /**
     * Updates triggered by PlayModel for the MVC pattern
     */
    public void propertyChange(PropertyChangeEvent evt) throws IndexOutOfBoundsException {
        if (evt.getPropertyName() == "notes") {
            collectNote((Note) evt.getNewValue());
        }
        if (evt.getPropertyName() == "note dropped") {
            dropNote();
        }
        if (evt.getPropertyName() == "fretPressed") {
            fretPressed((int) evt.getNewValue());
        }
        if (evt.getPropertyName() == "fretReleased") {
            fretReleased((int) evt.getNewValue());
        }
        if (evt.getPropertyName() == "addNote") {
            this.add((Note) evt.getNewValue());
        }
        if (evt.getPropertyName() == "instruction") {
            tutorialInfo.setText(ins.getString((int) evt.getNewValue()));
        }

        redraw();
    }

    /**
     * Create an arrow bubble used for displaying the instructions.
     * 
     * @author Jelena
     */
    private static class ArrowBubble extends JPanel {

        private int strokeThickness = 5;
        private int padding = strokeThickness / 2;
        private int radius = 20;
        private int arrowSize = 25;
        private int arrowWidth = 10;

        protected void paintComponent(final Graphics g) {
            final Graphics2D graphics2D = (Graphics2D) g;
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            graphics2D.setRenderingHints(qualityHints);
            graphics2D.setColor(new Color(190, 186, 182));
            graphics2D.setStroke(new BasicStroke(strokeThickness));

            int width = getWidth() - (strokeThickness * 2);
            int height = getHeight() - strokeThickness - arrowSize - 5;

            graphics2D.fillRect(padding, padding, width, height);
            RoundRectangle2D.Double rect = new RoundRectangle2D.Double(padding, padding, width, height, radius, radius);
            Area area = new Area(rect);

            Polygon arrow = new Polygon();
            arrow.addPoint(width / 2 + arrowWidth * 2, height);
            arrow.addPoint(width / 2 + arrowWidth * 8, height);
            arrow.addPoint(width - 5, height + arrowSize);

            graphics2D.draw(area);
            graphics2D.fill(arrow);
            graphics2D.dispose();
        }
    }

}