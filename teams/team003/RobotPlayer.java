package team003;

import battlecode.common.*;

public class RobotPlayer {
	
	// Parameters
	private static int enemySenseDistance = 100;
	private static int attackInterval = 300;
	private static int[] attackGroups = {0, 2};
	private static int groupDistance = 5;

	// Globals
	private static RobotController robot;
	private static int robotID = -1;
	private static int robotGroupID = -1;
	private static MapLocation robotRallyPoint = null;
	private static boolean isAttacker;

	private static MapLocation[] rallyPoints = {null, null, null, null, null};
	private static MapLocation enemyHQLocation = null;
	private static Direction enemyHQDirection = null;
	
	public static void run(RobotController rc) {
		while (true) {
			robot = rc;

			// Compute fixed concepts
			if (enemyHQLocation == null) enemyHQLocation = robot.senseEnemyHQLocation();
			if (enemyHQDirection == null) enemyHQDirection = robot.getLocation().directionTo(enemyHQLocation);

			// Compute the rally points
			if (rallyPoints[0] == null) {
				MapLocation hqLoc = robot.getLocation();
				int[] dirOffsets = {0, 1, -1, 2, -2};
				Direction currentDir = enemyHQDirection;
				for (int dirOffset:dirOffsets) {
					currentDir = Direction.values()[(enemyHQDirection.ordinal() + dirOffset + 8) % 8];
					rallyPoints[dirOffset + 2] = hqLoc.add(currentDir, groupDistance);
				}
			}
			
			// Compute robot-specific characteristics
			if (robotID == -1) robotID = robot.getRobot().getID();
			if (robotGroupID == -1) {
				robotGroupID = robotID % 5;
				for (int g:attackGroups) {
					if (g == robotGroupID) isAttacker = true;
				}
			}
			if (robotRallyPoint == null) robotRallyPoint = rallyPoints[robotGroupID];

			RobotType robotType = rc.getType();
			
			try {
				if (robotType == RobotType.SOLDIER) {
					runSoldier();
				} else if (robotType == RobotType.HQ) {
					runHQ();
                }
                rc.yield();
			} catch (Exception e) {
				System.out.println("Exception occurred:");
				e.printStackTrace();
			}
		}
	}
	
	// Robot runners
	
	private static void runSoldier() throws GameActionException {
		boolean shouldAttack = Clock.getRoundNum() > attackInterval;
		if (shouldAttack && isAttacker) {
            goToLocation(enemyHQLocation);
		} else {
			MapLocation nearestEnemyLocation = getNearestEnemyRobotLocation();
			if (nearestEnemyLocation != null) {
				goToLocation(nearestEnemyLocation);
			} else {
				layMine();
				goToLocation(robotRallyPoint);
			}
		}
	}
	
	private static void runHQ() throws GameActionException {
        if (robot.isActive() && robot.canMove(enemyHQDirection)) {
                robot.spawn(enemyHQDirection);
        }
	}
	
	// Helper methods

	private static MapLocation getNearestEnemyRobotLocation() throws GameActionException {
		Robot[] enemyRobots = robot.senseNearbyGameObjects(Robot.class, enemySenseDistance, robot.getTeam().opponent());
		if (enemyRobots.length == 0) {
			return null;
		}
		int minDist = enemySenseDistance + 1;
		MapLocation nearestRobotLocation = null;
		for (Robot enemyRobot:enemyRobots) {
			RobotInfo enemyRobotInfo = robot.senseRobotInfo(enemyRobot);
			int enemyRobotDist = enemyRobotInfo.location.distanceSquaredTo(robot.getLocation());
			if (enemyRobotDist < minDist) {
				minDist = enemyRobotDist;
				nearestRobotLocation = enemyRobotInfo.location;
			}
		}
		return nearestRobotLocation;
	}

	private static void goToLocation(MapLocation whereToGo) throws GameActionException {
		int dist = robot.getLocation().distanceSquaredTo(whereToGo);
		if (dist > 0 && robot.isActive()) {
			Direction dir = robot.getLocation().directionTo(whereToGo);
			int[] directionOffsets = {0, 1, -1, 2, -2};
			Direction lookingAtCurrently = dir;
			lookAround: for (int d:directionOffsets) {
				lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
				if (robot.canMove(lookingAtCurrently)) {
					break lookAround;
				}
			}
			// Look for enemy mines!
			MapLocation nextLocation = robot.getLocation().add(lookingAtCurrently);
			Team mineTeam = robot.senseMine(nextLocation);
			if (mineTeam != null && mineTeam != robot.getTeam()) {
				robot.defuseMine(nextLocation);
			}
			robot.move(lookingAtCurrently);
		}
	}
	
	private static void layMine() throws GameActionException {
		// Look for friendly mines!
		Team mineTeam = robot.senseMine(robot.getLocation());
		if (mineTeam == null) {
			robot.layMine();
		}
	}
}
