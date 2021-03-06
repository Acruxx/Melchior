package controllers;

import enemies.EnemyWave;
import enemies.Enemy;
import enemies.EnemyListener;
import gui.GameComponent;
import gui.GameFrame;
import handlers.SoundHandler;
import pathfinding.*;
import towers.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static controllers.GameConstants.*;


/**
 * Created by Holmgr 2015-03-09
 * Controller acting as the main "hub" that oversees and manages all the major game mechanics such as the clock and
 * collision handling. Is also an observer to both enemies and projectiles currently in play.
 */
public class GameController implements EnemyListener, ProjectileListener{

    private GameState currentState = GameState.BUILD;
    private Timer loopTimer;

    private GameFrame frame;
    private GameComponent gameComponent;

    private SquareGrid grid;

    private Location defaultStart = new Location(0, 0);
    private Location defaultEnd = new Location(GRID_SIZE - 1, GRID_SIZE - 1); // Right bottom most corner
    private Path path = null;

    private ArrayList<Enemy> enemies;
    private List<Enemy> enemiesToBeRemoved;
    private ArrayList<Tower> towers;
    private ArrayList<Projectile> projectiles;
    private List<Projectile> projectilesToBeRemoved;

    private EnemyWave enemyWave = null;
    private int round = 1; // Current round (increments when changing to state RUNNING)

    private int score = 0;
    private int cash = STARTING_CASH; //Used to buy/upgrade towers
    private int health = STARTING_HEALTH;

    private Location selectedLocation = new Location(0, 0);

    private int spawnDelayCounter = ENEMY_SPAWN_MAX_DELAY;
    private int stateDelayCounter = BUILD_STATE_TIME;

    public GameController() {

        grid = new SquareGrid(GRID_SIZE, GRID_SIZE);


        path = calcualtePath();
        assert path != null; // Path should be possible given an empty grid

        towers = new ArrayList<>();
        enemies = new ArrayList<>();
        enemiesToBeRemoved = new ArrayList<>();
        projectiles = new ArrayList<>();
        projectilesToBeRemoved = new ArrayList<>();

        gameComponent = new GameComponent(grid, enemies, towers, projectiles, this);
        gameComponent.setPath(path);
        frame = new GameFrame(gameComponent, health, cash, this);

        // Game loop timer, approx 30hz
        loopTimer = new Timer(GAME_TICK_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doTick();
            }
        });
        loopTimer.setCoalesce(true);
        loopTimer.start();


        if (PLAY_MUSIC){
            SoundHandler.getInstance().playMusic();
        }
    }

    private Path calcualtePath() {
        AStarSearch search = new AStarSearch(grid, defaultStart, defaultEnd);
        Path newPath = null;
        try {
            newPath = search.createPath();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        }
        return newPath;
    }

    /**
     * Tick method run by main timer
     */
    private void doTick() {

        switch (currentState){
            case RUNNING:
                runningTick();
                gameComponent.repaint();
                break;

            case BUILD:
                buildTick();
                gameComponent.repaint();
                break;

            case PAUSE:
                break;

            case GAME_OVER:
                loopTimer.stop();
                frame.launchHighScore(score);
                currentState = GameState.BUILD;
                loopTimer.start();
                break;

            default:
                break;
        }
    }

    /**
     * Switches to RUNNING state if timer runs out otherwise counts down.
     */
    private void buildTick() {
        if (stateDelayCounter == 0){
            stateDelayCounter = BUILD_STATE_TIME;
            enemyWave = new EnemyWave(round, path);
            currentState = GameState.RUNNING;
            round++;
            frame.setRoundLabel(round);
        }
        else {
            stateDelayCounter--;
            frame.setCounterLabel(stateDelayCounter / GAME_TICK_DELAY);
        }
    }

    /**
     * Tick method in main state i.e RUNNING. Executes all movement methods, collision checking also spawns
     * new enemies if ready by using the EnemyFactory and switches to BUILD if factory is empty and no enemies
     * are in play
     */
    private void runningTick() {
        // Remove all enemies which are dead or have reached the goal
        enemies.removeAll(enemiesToBeRemoved);
        enemiesToBeRemoved.clear();

        // Remove all projectiles that hit
        projectiles.removeAll(projectilesToBeRemoved);
        projectilesToBeRemoved.clear();

        for (Projectile projectile : projectiles) {
            projectile.moveStep();
        }

        for(Enemy enemy : enemies) {
            enemy.moveStep();
            doCollisions(enemy);
        }
        for(Tower tower : towers) {
            tower.onTick();
        }

        if(spawnDelayCounter == 0) {
            Random rnd = new Random();

            spawnDelayCounter = rnd.nextInt(ENEMY_SPAWN_MAX_DELAY);
            if (enemyWave.iterator().hasNext()) {
                Enemy enemy = enemyWave.iterator().next();
                enemy.addEnemyListener(this);
                enemies.add(enemy);
            }
            else if (enemies.isEmpty()) {
                currentState = GameState.BUILD;
                projectiles.clear(); // Remove any lingering projectiles in the air
                System.out.println("State: BUILD");
            }
        }
        else {
            spawnDelayCounter--;
        }
    }

    public void resetBoard(){
        enemies.clear();
        towers.clear();
        projectiles.clear();
        grid = new SquareGrid(GRID_SIZE, GRID_SIZE);
        score = 0;
        round = 1;
        health = STARTING_HEALTH;
        cash = STARTING_CASH; //Used to buy/upgrade towers

        path = calcualtePath();
        assert path != null;
        gameComponent.setPath(path);
    }

    /**
     * Creates a new tower of given type (class), inserts into grid only if location is empty, the cash >= the cost
     * of the given tower and that a complete path can be constructed.
     */
    public void buyTower(TowerFactory factory) {
        if (currentState != GameState.BUILD){
            return;
        }
        Tower tower = factory.createTower(this);
        if (selectedLocation != null && grid.inBounds(selectedLocation) && grid.isPassable(selectedLocation) && cash >= tower.getUpgradeCost()){
            tower.setLocation(selectedLocation);
            grid.addTower(tower.getLocation());

            Path testPath = calcualtePath();
            if (testPath != null) {
                cash -= tower.getUpgradeCost();
                towers.add(tower);
                path = testPath;
                gameComponent.setPath(path);
                frame.setCashLabel(cash);
                gameComponent.repaint();
            }
            else {
                grid.removeTower(tower.getLocation());
            }
        }
    }

    public void upgradeTower() {

        for (Tower tower : towers){
            if (tower.getLocation().equals(selectedLocation)) {
                int upgradeCost = tower.getUpgradeCost();

                if (cash >= upgradeCost){
                    cash -= upgradeCost;
                    tower.upgrade();
                    frame.setCashLabel(cash);
                }
                break;
            }
        }
    }

    public void sellTower() {

        for (Iterator<Tower> iterator = towers.iterator();  iterator.hasNext();) {
            Tower tower = iterator.next();
            if (tower.getLocation().equals(selectedLocation)) {
                iterator.remove();
                cash += tower.sell();
                grid.removeTower(tower.getLocation());
                path = calcualtePath();

                assert path != null; // Removing a tower should never break pathfinding
                gameComponent.setPath(path);
                break;
            }
        }

    }

    public List<Enemy> allEnemiesInRange(Location id, int range) {

        List<Enemy> enemiesInRange = new ArrayList<>();
        for (Enemy enemy : enemies){
            if (range >= getDistance(enemy.getX(), enemy.getY(), id.x, id.y)){
                enemiesInRange.add(enemy);
            }
        }
        return enemiesInRange;
    }

    public Enemy getNearestEnemyInRange(Location id, int range) {

        Enemy nearest = null;
        double distance = -1; // Placeholder distance if there are no enemies

        for (Enemy enemy : enemies) {
            double distanceToEnemy = getDistance(enemy.getX(), enemy.getY(), id.x, id.y);
            if (nearest == null || distanceToEnemy < getDistance(nearest.getX(), nearest.getY(), id.x, id.y)){
                distance = distanceToEnemy;
                nearest = enemy;
            }
        }

        if (distance == -1 || range < distance) { // If no enemies matching or range to short
            return null;
        }
        else {
            return nearest;
        }
    }

    /**
     * Searches through all projectiles to see if any collide, if so then the enemy will take the damage
     * specified by the projectile and the projectile will then be added to the remove list.
     */
    private void doCollisions(Enemy enemy) {

        for (Projectile proj : projectiles) {
            double distanceToProjectile = getDistance(proj.getX(), proj.getY(), enemy.getX(), enemy.getY());
            if (!projectilesToBeRemoved.contains(proj) && proj.getRadius() + enemy.getSize() >= distanceToProjectile) {
                projectilesToBeRemoved.add(proj);
                enemy.takeDamage(proj.getDamage());
            }
        }
    }
    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) +
                Math.pow(y1 - y2, 2));
    }

    public void spawnProjectile(Location id, Enemy enemy, double speed, int damage, int range) {
        Projectile proj = new Projectile(speed, damage, id.x, id.y, enemy.getX(), enemy.getY(), range);
        proj.addProjectileListener(this);
        projectiles.add(proj);
    }

    public void switchPause() {
        if (currentState == GameState.RUNNING) {
            currentState = GameState.PAUSE;
        }
        else if (currentState == GameState.PAUSE){
            currentState = GameState.RUNNING;
        }
    }

    public void moveSelected(int offsetX, int offsetY) {
        Location loc = new Location(selectedLocation.x + offsetX, selectedLocation.y + offsetY);

        // To not move selected outside the grid
        if (grid.inBounds(loc)){
            selectedLocation = loc;
        }
    }

    private void removeEnemy(Enemy enemy) {
        assert enemies.contains(enemy);
        enemiesToBeRemoved.add(enemy);
    }

    @Override
    public void onEnemyKilled(Enemy enemy) {
        cash += enemy.getKillReward();
        score += enemy.getKillReward();
        frame.setCashLabel(cash);
        removeEnemy(enemy);
    }

    @Override
    public void onReachedGoal(Enemy enemy) {
        if (health == 1){
            currentState = GameState.GAME_OVER;
        }
        else {
            health--;
            frame.setHealthLabel(health);
            System.out.println("Enemy reached goal, health:" + health);
            removeEnemy(enemy);
        }
    }

    @Override
    public void onReachedRangeLimit(Projectile projectile) {
        assert projectiles.contains(projectile);
        projectilesToBeRemoved.add(projectile);
    }

    public void setSelectedLocation(Location selectedLocation) {
        if (grid.inBounds(selectedLocation)){
            this.selectedLocation = selectedLocation;
        }
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public static void main(String[] args) {
        new GameController();
    }
}
