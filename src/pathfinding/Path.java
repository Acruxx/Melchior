package pathfinding;

import java.util.Map;

/**
 * Created by Holmgr 2015-03-04
 * Holds a sequence of Locations demanded as an opimal path given my the A*
 * algoritm. Each Location is used as a key for getting the next Location
 * in the sequence. The value for the last Location as a key is null.
 */
public class Path {

    private Map<Location, Location> path;
    private Location startLocation;
    private Location goalLocation = null;

    public Path(final Map<Location, Location> path, Location start) {
        this.path = path;
        this.startLocation = start;

        for (Location key : path.keySet()){
            if (path.get(key) == null){
                goalLocation = key;
            }
        }
    }

    public Location getStartLocation(){
        return startLocation;
    }

    public Location getNext(Location currentLocation){
        return path.get(currentLocation);
    }

    public Location getGoalLocation(){
        return goalLocation;
    }
}
