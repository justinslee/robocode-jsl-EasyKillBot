package jsl;

import java.awt.geom.Point2D;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;

/**
 * <code>EasyKillBotV2</code> is a competitive Bot for Robocode
 * that is in the building phase.
 * Wins 80-100% against all included Robots, except two robots.
 * Loses to Walls Robot 75% of the time. Ties 50% with Corners Robot.
 *
 * @author Justin Lee
 * @version 1.1
 */
public class EasyKillBot extends AdvancedRobot {

//Used for weight calculations of circle of field
//private final double fieldRadius = .8;
/**
  * Maximum value for radar loss.
  */
private static final int MAXRADARLOSS = 10;
/**
 * Maximum value for tank turn rate.
 */
private static final int TURNANGLE = 5;
/**
 * Maximum value for radar turn rate.
 */
private static final int TURNRADAR = 5;
/**
 * Maximum value for moving forward.
 */
private static final int AHEADDISTANCE = 100;
/**
 * Angle of 360 degrees(Magic Number).
 */
private static final int CIRCLEANGLE = 360;
/**
 * Angle of 450 degrees(Magic Number).
 */
private static final int MIDANGLE = 450;
/**
 * Maximum range angle to fire.
 */
private static final int ACCURACYANGLE = 10;
/**
 * Used for firing long range.
 */
private static final int FIRELONG = 500;
/**
 * Used for firing medium range.
 */
private static final int FIREMEDIUM = 300;
/**
 * Used for firing short range.
 */
private static final int FIRESHORT = 200;
/**
 * Used to store and determine enemy information.
 */
private EnemyRobot enemyRobot;

/**
 * Used to determine the gun angle.
 */
private double gunAngle = 0;
/**
 * Used to determine the radar angle.
 */
private double radarAngle = 0;
//private double turnAngle = 0;
//private double aheadDistance = 0;
/**
 * Used to determine the number of turns since radar update.
 */
private double turnCounter = 0;
/**
 * Used to determine the enemy tracked.
 */
private boolean trackingEnemy = false;

  /**
   * Default Constructor used to initialize <code>EnemyRobot</code>
   * object class.
   */
  public EasyKillBot() {
    enemyRobot = new EnemyRobot();
  }

  /**
   * Default Constructor used to initialize <code>EnemyRobot</code>
   * object class.
   */
  @Override
  public final void run() {
    // Determines the radius the robot is allowed to travel.
//    double maxRadius = (getBattleFieldWidth()>= getBattleFieldHeight()) ?
//                         getBattleFieldHeight()/2*fieldRadius
//                           : getBattleFieldWidth()/2*fieldRadius;
    // Make radar turn independently from gun turret
    setAdjustRadarForGunTurn(true);

    moveToPoint(new Point2D.Double(getBattleFieldWidth() / 2,
                                     getBattleFieldHeight() / 2));

    do {
        // Sets robots actions before next execute cycle.
        setRobotRadar();
        setRobotGun();
        setRobotMove();
        setRobotFire();

        // Executes set actions
        execute();
      } while (true);
  }

  /**
   * Method <code>setRobotRadar</code> sets the radar determines
   * radar action based on current information.
   */
  public final void setRobotRadar() {

    //Checks to see if robot has been lost for a certain amount of turns
    if ((getTime() - turnCounter) >= MAXRADARLOSS) {
      setTurnRadarRight(TURNRADAR);
      trackingEnemy = false;
    } else {
      setTrackedRadar();
      trackingEnemy = true;
    }
  }

  /**
   * Method <code>setTrackedRadar</code> sets the radar turning for the robot.
   */
  public final void setTrackedRadar() {

    if (radarAngle <= CIRCLEANGLE / 2) {
      setTurnRadarRight(radarAngle);
    } else {
      setTurnRadarLeft(CIRCLEANGLE - radarAngle);
    }
  }


  /**
   * Method <code>onHitWall</code> has an action based on event hitting wall.
   * @param event has wall event
   */
  @Override
  public final void onHitWall(final HitWallEvent event) {
    moveToPoint(new Point2D.Double(getBattleFieldWidth() / 2,
                                    getBattleFieldHeight() / 2));
  }

  /**
   * Method <code>setRobotGun</code> sets the gun turning for the robot.
   */
  public final void setRobotGun() {
    gunAngle -= TURNANGLE / 2;
    if (gunAngle <= CIRCLEANGLE / 2) {
      setTurnGunRight(gunAngle);
    } else {
      setTurnGunLeft(gunAngle);
    }
  }

  /**
   * Method <code>setRobotMove</code> sets the turn and traveling
   * portion of the robot.
   * Currently travels in a circular motion.
   */
  public final void setRobotMove() {
    setAhead(AHEADDISTANCE);
    setTurnRight(TURNANGLE);
  }

  /**
   * Method <code>setRobotFire</code> sets action to fire at a
   * robot in range and power is proportional to distance.
   */
  public final void setRobotFire() {

    if ((enemyRobot.getLastEvent()) != null && trackingEnemy
           && (gunAngle <= ACCURACYANGLE
             || gunAngle >= CIRCLEANGLE - ACCURACYANGLE)) {

      double distance = enemyRobot.getLastEvent().getDistance();

      if (distance >= FIRELONG) {

        setFire(1);
      } else if (distance >= FIRESHORT) {

        setFire(1 + ((distance - FIRESHORT) / FIREMEDIUM)
                  * (Rules.MAX_BULLET_POWER - 1));
      } else {

        setFire(Rules.MAX_BULLET_POWER);
      }
    }
  }

  /**
   * Method <code>onScannedRobot</code> used to determine
   * actions based on scanning.
   * @param event object storing scanned information
   */
  @Override
  public final void onScannedRobot(final ScannedRobotEvent event) {
    // Store event for future reference
    enemyRobot.storeEvent(event);

    // Rotate gun and radar to the same target
    // Note: Add 720 then modulo to make result positive
    gunAngle = Math.floor((getHeading() - getGunHeading()
                + event.getBearing() + 2 * CIRCLEANGLE) % CIRCLEANGLE);
    radarAngle = Math.floor((getHeading() - getRadarHeading()
                  + event.getBearing() + 2 * CIRCLEANGLE) % CIRCLEANGLE);

    // Sets time to keep track of turns since last scanned event
    turnCounter = getTime();
  }

  /**
   * Method <code>moveTo</code> is supposed to move to a point for
   * turns in sequence. However changing the code for to set has an
   * interesting side-effect when turns and ahead are are
   * done at the same time.
   * @param nextPos position to move to on map
   */
  public final void moveToPoint(final Point2D nextPos) {

    double distance, robotAngle, turnAngle;

    Point2D.Double robot = new Point2D.Double(getX(), getY());
    distance = robot.distance(nextPos);
    robotAngle = angleConverter(getHeading());
    turnAngle = angleTan2((nextPos.getY() - robot.getY()),
                 (nextPos.getX() - robot.getX()));
    turnAngle = robotAngle - turnAngle;

    while (turnAngle > 0) {
      if (turnAngle > TURNANGLE) {
        setTurnRight(TURNANGLE);
        turnAngle -= TURNANGLE;
      } else {
        setTurnRight(turnAngle);
        turnAngle = 0;
      }
      execute();
    }
    while (distance > (AHEADDISTANCE)) {
      setAhead(AHEADDISTANCE);
      robot = new Point2D.Double(getX(), getY());
      distance = robot.distance(nextPos);
      execute();
    }

  }

  /**
   * Method <code>distanceFromCircle</code> determines if tank
   * is inside a circle based at the center of map of given radius.
   * @param myLocation - Point2D representation of the robot location
   * @param maxRadius - farthest range allowed.
   * @return returns distance between closest point of circle
   * to robot, if robot is in.
   * circle returns 0.
   */
  public final double distanceFromCircle(final Point2D myLocation,
                                          final double maxRadius) {

    final Point2D.Double center = getCenterPoint();
    final double distance = myLocation.distance(center);

    if (maxRadius > distance) {
      return 0;
    }

    return distance - maxRadius;
  }


  /**
   * Method <code>centerPoint</code> returns the center
   * coordinates as a Point2D.Double object.
   *
   * @return Point2D.Double representation of center of the battlefield.
   */
  public final Point2D.Double getCenterPoint() {
    return new Point2D.Double(getBattleFieldWidth() / 2,
                               getBattleFieldHeight() / 2);
  }

  /**
   * Method <code>angleTan2()</code> converts two points distances
   * to an angle.
   * @param deltaX - X=coordinate difference between two points
   * @param deltaY - Y-coordinate difference between two points
   * @return positive angle of Math.tan2
   */
  private double angleTan2(final double deltaY, final double deltaX) {
    return Math.atan2(deltaY, deltaX) * (CIRCLEANGLE / 2) / Math.PI;
  }

  /**
   * Method <code>angleTan2()</code> Converts between North
   * clockwise angle(getHeading()) to Cartesian angle format.
   * @param angle - angle for conversion
   * @return positive angle of the other format
   */
  private double angleConverter(final double angle) {
    return ((MIDANGLE - angle) % CIRCLEANGLE);
  }
}
