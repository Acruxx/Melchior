package enemies;

/**
 * Created by acrux on 2015-03-04.
 */
public interface Enemy {
    public int getHealth();

    public int getMaximumHealth();

    public void moveStep();

    public void takeDamage(int damage);

    public double getX();

    public double getY();

    public Direction getCurrentDirection();

    public double getSize();

    public int getKillReward();

    public void addEnemyListener(EnemyListener el);

}