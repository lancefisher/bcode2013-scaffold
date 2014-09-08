package team002;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;



/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously.
 */
public class RobotPlayer {
	
	//private static int teamNumber = 0;
	
	public static void run(RobotController rc) {
		while (true) {
			//teamNumber = rc.getRobot().getID() % 2;
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
						Direction altDir1 = direction.rotateLeft();
						MapLocation altLoc1 = me.add(altDir1);						
						if (rc.isActive() && rc.senseMine(altLoc1) != null) {							
							Direction altDir2 = direction.rotateRight();
							MapLocation altLoc2 = me.add(altDir2);
							if (rc.isActive() && rc.senseMine(altLoc2) != null) {
								rc.defuseMine(altLoc2);
								rc.yield();
							} else {
								moveIfPossible(rc, altDir2);
							}
						} else {							
							moveIfPossible(rc, altDir1);
						}						
					} else
						moveIfPossible(rc, direction);
					}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void moveIfPossible(RobotController rc, Direction altDir)
			throws GameActionException {
		if (rc.isActive() && rc.canMove(altDir)) {
			rc.move(altDir);
			rc.yield();
		}
	}

}
