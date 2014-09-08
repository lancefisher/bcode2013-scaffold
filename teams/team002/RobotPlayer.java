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

					Direction direction = directionToThem(me, them);
					MapLocation dest = mapLocationFromDirection(me, direction);

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

	private static MapLocation mapLocationFromDirection(MapLocation me, Direction direction) {
		int x = me.x;
		int y = me.y;

		switch (direction) {
		case NORTH:
			y -= 1;
			break;
		case NORTH_EAST:
			x += 1;
			y -= 1;
		case NORTH_WEST:
			x -= 1;
			y -= 1;
		case SOUTH:
			y += 1;
		case SOUTH_EAST:
			x += 1;
			y += 1;
		case SOUTH_WEST:
			x += 1;
			y += 1;
		case WEST:
			x -= 1;
		case EAST:
			x += 1;		
		default:
			break;
		}
		
		return new MapLocation(x, y);
	}

	private static Direction directionToThem(MapLocation me, MapLocation them) {
		Direction direction;
		
		if (me.x < them.x) {
			if (me.y < them.y) {
				direction = Direction.SOUTH_EAST;
			} else if (me.y > them.y) {
				direction = Direction.NORTH_EAST;
			} else {
				direction = Direction.EAST;
			}
		} else if (me.x > them.x) {
			if (me.y < them.y) {
				direction = Direction.SOUTH_WEST;
			} else if (me.y > them.y) {
				direction = Direction.NORTH_WEST;
			} else {
				direction = Direction.WEST;
			}
		} else { // me.x == them.x
			if (me.y < them.y) {
				direction = Direction.SOUTH;
			} else if (me.y > them.y) {
				direction = Direction.NORTH;
			} else {
				direction = Direction.OMNI;
			}						
		}
		return direction;
	}
}
