package towers;

import controllers.GameController;
import enemies.Enemy;
import pathfinding.Location;

/**
 * Created by Holmgr 2015-03-06
 * Abstract class for the Tower interface, implementing base functionality concerning upgrading, selling but also
 * the onTick method.
 */
public abstract class AbstractTower implements Tower{
    private Location location = null;
    private int upgradeLevel = 0;
    protected int upgradeCost = 50; // Default upgrade cost

    protected int cooldown = 100;
    protected int currentCooldown = 0;

    protected int range;
    protected GameController controller;

    protected Enemy nearestEnemy = null;

    protected AbstractTower(GameController controller, int cooldown, int range, int upgradeCost) {
        this.controller = controller; // Needed for requesting nearest enemy
        this.cooldown = cooldown;
        this.range = range;
        this.upgradeCost = upgradeCost;
    }

    @Override
    public void onTick(){
        if (currentCooldown <= 0) { // Ready to fire
            nearestEnemy = controller.getNearestEnemyInRange(this.location, range);
            if (nearestEnemy != null) {
                fire();
                currentCooldown = cooldown;
            }
        }
        else {
            currentCooldown--;
        }
    }
    @Override public int sell() {
        return (int)(upgradeCost * 0.75); // 25% loss on sell
    }

    @Override public void upgrade() {
        upgradeLevel++;
        upgradeCost += 200; // Arbitrary increase per level
    }

    public void setLocation(Location location){
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    @Override public int getUpgradeCost() {
        return upgradeCost;
    }
}
