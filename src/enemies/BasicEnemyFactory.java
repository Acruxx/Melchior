package enemies;

import pathfinding.Path;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by acrux on 2015-03-18.
 */
public class BasicEnemyFactory implements Iterable<Enemy>{

    private int level;
    private Path path;
    private int enemyCount;
    private int points;

    public BasicEnemyFactory(int level, Path path) {
        this.level = level;
        this.path = path;

        enemyCount = (int) (Math.pow(level, 0.5) * 10);
        points = (int) (( 1 + ((float)level * ((float)level/10)/2)) * 1000);
        System.out.println("Level: " + level + " Enemy count: " + enemyCount);
        System.out.println("Level: " + level + " Points: " + points);

    }

    @Override
    public Iterator<Enemy> iterator() {
        Iterator<Enemy> it = new Iterator<Enemy>() {
            @Override
            public boolean hasNext() {
                return 0 < enemyCount;
            }

            @Override
            public Enemy next() {
                Random rnd = new Random();
                GroundEnemyType type = GroundEnemyType.values()[rnd.nextInt(GroundEnemyType.values().length)];
                Enemy enemy = new GroundEnemy(path, ((points / enemyCount) - points / (enemyCount * 2) + rnd.nextInt(points/(enemyCount + 1))));
                points -= enemy.getHealth();
                System.out.println("Enemy created: " + enemy.getHealth());
                enemyCount --;

                return enemy;
            }
        };
        return it;
    }
}
