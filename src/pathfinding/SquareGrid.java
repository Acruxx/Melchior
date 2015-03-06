package pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Holmgr 2015-03-02
 * Holds most data used by pathfinding, the location of inpassable walls etc.
 * Also has methods such as pulling all valid neighbors and finding the cost of a given Location.
 */
public class SquareGrid implements WeightedGraph {

    // All allowed directions, i.e 4-directional
    public static Location[] directions = new Location[]{
	    new Location(1, 0),
	    new Location(0, -1),
	    new Location(-1, 0),
	    new Location(0, 1)
    };

    private int width, height;

    private Set<Location> walls = new HashSet<Location>(); // Set of Locations which are unpassable
    private Set<Location> forrests = new HashSet<Location>(); // Set of Location which are harder to navigate


    public SquareGrid(int width, int height) {
	this.width = width;
	this.height = height;
    }

    public boolean inBounds(Location id){
	return 0<= id.x && id.x < width
	       && 0<= id.y && id.y < height;
    }

    public boolean isPassable(Location id){
	return !walls.contains(id);
    }

    @Override public int cost(final Location a, final Location b) {
	return forrests.contains(b) ? 5 : 1;
    }

    /**
     * Returns an ArrayList containing all
     * valid neighbors (passable and in bounds) for a given Location id.
     */
    @Override public Iterable<Location> neighbors(final Location id) {
	Collection<Location> neighbours = new ArrayList<Location>();
	for (Location direction : directions){

	    Location position = new Location(direction.x + id.x, direction.y + id.y);

	    if (inBounds(position) && isPassable(position))
		neighbours.add(position);
	}
	return neighbours;
    }

    public Set<Location> getWalls() {
	return walls;
    }

    public void setWalls(final Set<Location> walls) {
	this.walls = walls;
    }

    public Set<Location> getForrests() {
	return forrests;
    }

    public void setForrests(final Set<Location> forrests) {
	this.forrests = forrests;
    }
}