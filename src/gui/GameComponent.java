package gui;

import enemies.Enemy;
import pathfinding.Location;
import pathfinding.Path;
import pathfinding.SquareGrid;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Holmgr 2015-03-04
 * The view in our MVC pattern, responsible for drawing
 * the gameboard, the enemies, towers and so on.
 */
public class GameComponent extends JComponent{

    private int cellSize;
    private int gridSize;
    private SquareGrid grid;
    private Path path;

    private ArrayList<Enemy> enemies;

    public GameComponent(SquareGrid grid, int gridSize, ArrayList<Enemy> enemies) {
        this.gridSize = gridSize;
        this.grid = grid;
        this.enemies = enemies;

        this.setOpaque(true); // Needed for background color to show
    }

    @Override
    // Needs to ignore superclass, want specific dimens
    public Dimension getPreferredSize() {
        return new Dimension(640, 640);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        cellSize = getHeight() / gridSize;

        g2d.clearRect(0, 0, getWidth(), getHeight()); // To prevent overlapping from last frame

        drawGrid(g2d);
        drawPath(g2d);
        drawWalls(g2d);
        drawEnemies(g2d);
    }

    private void drawEnemies(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);

        setAntialising(g2d, true);
        for(Enemy enemy : enemies){
            g2d.drawOval((int) (enemy.getPositionX() * cellSize), (int) (enemy.getPositionY() * cellSize),
                    cellSize / 2, cellSize / 2);
        }
        setAntialising(g2d, false);
    }

    private void drawWalls(final Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);

        for (Location wall : grid.getWalls()) {
            g2d.fillRect(wall.x * cellSize, wall.y * cellSize, cellSize, cellSize);
        }
    }

    private void drawPath(final Graphics2D g2d) {

        // No path to be drawn
        if (path == null)
            return;

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));

        Location current = path.getFirst();
        Location next = path.getNext(current);

        while (next != null){
            g2d.drawLine((int) ((current.x + 0.5) * cellSize), (int) ((current.y + 0.5) * cellSize),
                    (int) ((next.x + 0.5) * cellSize), (int) ((next.y + 0.5) * cellSize));

            current = path.getNext(current);
            next = path.getNext(current);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                g2d.drawLine(col * cellSize, row * cellSize, col * cellSize, getHeight());
            }
            g2d.drawLine(0, row * cellSize, getWidth(), row * cellSize);
        }
    }

    private void setAntialising(Graphics2D g2d, boolean mode) {
        if (mode){
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
