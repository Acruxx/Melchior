package pathfinding;

/**
 * Created by Holmgr 2015-03-02
 */
public interface WeightedGraph {
    public int cost(Location location); // The weighted cost at that position
    public Iterable<Location> neighbors(Location id);
}