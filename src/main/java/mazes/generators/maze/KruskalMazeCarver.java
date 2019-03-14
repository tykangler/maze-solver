package mazes.generators.maze;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.Graph;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        ISet<Wall> allWalls = maze.getWalls();
        Random rand = new Random();
        for (Wall wall : allWalls) {
            wall.setDistance(rand.nextDouble());
        }
        Graph<Room, Wall> fullMaze = new Graph<Room, Wall>(maze.getRooms(), allWalls);
        ISet<Wall> pathToFollow = fullMaze.findMinimumSpanningTree();
        ISet<Wall> wallsToRemove = new ChainedHashSet<Wall>();
        for (Wall wall : pathToFollow) {
            wallsToRemove.add(wall);
        }
        for (Wall wall : allWalls) {
            wall.resetDistanceToOriginal();
        }
        return wallsToRemove;
    }
}
