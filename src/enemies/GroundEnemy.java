package enemies;

import pathfinding.Path;

/**
 * Created by acrux on 2015-03-04.
 */
public class GroundEnemy extends AbstractEnemy{

    protected GroundEnemy(Path path) {
        super(path);
        health = 100;

    }
}
