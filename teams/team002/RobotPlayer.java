package team002;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously.
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					MapLocation me = rc.getLocation();

					MapLocation them = rc.senseEnemyHQLocation();

					Direction direction = Direction.OMNI;
					MapLocation dest = new MapLocation(me.x, me.y);
					if (me.x < them.x) {
						if (me.y < them.y) {
							dest = new MapLocation(me.x + 1, me.y + 1);
							direction = Direction.SOUTH_EAST;
						} else if (me.y > them.y) {
							dest = new MapLocation(me.x + 1, me.y - 1);
							direction = Direction.NORTH_EAST;
						} else {
							dest = new MapLocation(me.x + 1, me.y);
							direction = Direction.EAST;
						}
					} else if (me.x > them.x) {
						if (me.y < them.y) {
							dest = new MapLocation(me.x - 1, me.y + 1);
							direction = Direction.SOUTH_WEST;
						} else if (me.y > them.y) {
							dest = new MapLocation(me.x - 1, me.y - 1);
							direction = Direction.NORTH_WEST;
						} else {
							dest = new MapLocation(me.x - 1, me.y);
							direction = Direction.WEST;
						}
					} else { // me.x == them.x
						if (me.y < them.y) {
							dest = new MapLocation(me.x, me.y + 1);
							direction = Direction.SOUTH;
						} else if (me.y > them.y) {
							dest = new MapLocation(me.x, me.y - 1);
							direction = Direction.NORTH;
						} else {
							//do nothing
						}						
					}

					if (rc.isActive() && rc.senseMine(dest) != null) {
						if (rc.isActive()) {
							rc.defuseMine(dest);
							rc.yield();
						}
					}

					if (rc.isActive() && rc.canMove(direction)) {
						rc.move(direction);
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
