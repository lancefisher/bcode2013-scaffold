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

					Direction direction = me.directionTo(them);
					MapLocation dest = me.add(direction);

					if (rc.isActive() && rc.senseMine(dest) != null) {
						Direction altDir = alternateDirection(me, them, direction);
						MapLocation altLoc = me.add(altDir);
						
						if (rc.isActive() && rc.senseMine(altLoc) != null) {							
							rc.defuseMine(altLoc);
							rc.yield();
						} else {
							if (rc.isActive()) {
								rc.move(altDir);
								rc.yield();
							}
						}						
					} else if (rc.isActive() && rc.canMove(direction)) {
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

	private static Direction alternateDirection(MapLocation me, MapLocation them, Direction direction) {
		Direction dir = Direction.values()[(direction.ordinal() + 1 + 8) % 8];
		
		return dir;
	}



}
