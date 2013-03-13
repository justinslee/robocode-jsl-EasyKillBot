package jsl;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

/**
 * <code>EasyKillBotV2</code> is a competitive Bot for Robocode.
 * @author Justin Lee
 * @version 1.2
 */
public class EasyKillBot extends AdvancedRobot { //NOPMD CC-13

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
 * Angle of 90 degrees(Magic Number).
 */
private static final int A90 = 90;
/**
 * Angle of 180 degrees(Magic Number).
 */
private static final int A180 = 180;
/**
 * Angle of 270 degrees(Magic Number).
 */
private static final int A270 = 270;
/**
 * Angle of 360 degrees(Magic Number).
 */
private static final int A360 = 360;
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
 * Used to reference max to wait.
 */
private static final int WAITTURNS = 400;
/**
 * Maximum range for firing.
 */
private static final int MAXDISTANCE = 175;
/**
 * Minimum range for firing.
 */
private static final int MINDISTANCE = 0;
/**
 * Minimum wall buffer.
 */
private static final int WALL = 50;

/**
 * Used to store and determine enemy information.
 */
private ScannedRobotEvent enemyRobot;  //NOPMD

/**
 * Used to determine the gun angle.
 */
private double gunAngle = 0; //NOPMD
/**
 * Used to determine the radar angle.
 */
private double radarAngle = 0; //NOPMD

/**
 * Used to determine the number of turns since radar update.
 */
private double turnCounter = 0; //NOPMD
/**
 * Used to determine the enemy tracked.
 */
private boolean trackingEnemy = false; //NOPMD

  /**
   * Default Constructor used to initialize <code>EnemyRobot</code>
   * object class.
   */
  public EasyKillBot() { //NOPMD
  }

  /**
   * Default Constructor used to initialize <code>EnemyRobot</code>
   * object class.
   */
  @Override
  public final void run() {

    // Make radar turn independently from gun turret
    setAdjustRadarForGunTurn(true);

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
    } 
    else {
      setTrackedRadar();
      trackingEnemy = true;
    }
  }

  /**
   * Method <code>setTrackedRadar</code> sets the radar turning for the robot.
   */
  public final void setTrackedRadar() {

    if (radarAngle <= A360 / 2) {
      setTurnRadarRight(radarAngle);
    } 
    else {
      setTurnRadarLeft(A360 - radarAngle);
    }
  }

  /**
   * Method <code>setRobotGun</code> sets the gun turning for the robot.
   */
  public final void setRobotGun() {
    gunAngle -= TURNANGLE / 2;
    if (gunAngle <= A360 / 2) {
      setTurnGunRight(gunAngle);
    }
    else {
      setTurnGunLeft(gunAngle);
    }
  }

  /**
   * Method <code>setRobotMove</code> sets the turn and traveling
   * portion of the robot.
   * Currently travels in a circular motion.
   */
  public final void setRobotMove() { //NOPMD Cyclomatic complexity - 13

    setAhead(AHEADDISTANCE);
    // Avoiding walls
    if (getX() < WALL
        && getHeading() >= A180 && getHeading() <= A360
        || getX() > getBattleFieldWidth() - WALL
            && getHeading() >= 0 && getHeading() <= A180
        || getY() < WALL
            && getHeading() >= A90 && getHeading() <= A270
        || getY() > getBattleFieldHeight() - WALL
            && (getHeading() >= A270 || getHeading() <= A90)) {
      stop();
    }
    setTurnRight(TURNANGLE);
  }

  /**
   * Method <code>setRobotFire</code> sets action to fire at a
   * robot in range and power is proportional to distance.
   */
  public final void setRobotFire() { //NOPMD

    double distance;

    if (gunAngle <= ACCURACYANGLE || gunAngle >= A360 - ACCURACYANGLE
         && trackingEnemy) {

      if (!(enemyRobot == null)) {  //NOPMD
        distance = enemyRobot.getDistance();
        if (distance >= MINDISTANCE && distance <= MAXDISTANCE
            || getTime() >= WAITTURNS) {

          if (distance >= FIRELONG) {

            setFire(1);
          } 
          else if (distance >= FIRESHORT) {

            setFire(1 + ((distance - FIRESHORT) / FIREMEDIUM)
                   * (Rules.MAX_BULLET_POWER - 1));
          } 
          else {

            setFire(Rules.MAX_BULLET_POWER);
          }
        }
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
    enemyRobot = event; //NOPMD

    // Rotate gun and radar to the same target
    // Note: Add 720 then modulo to make result positive
    gunAngle = Math.floor((getHeading() - getGunHeading()
                + event.getBearing() + 2 * A360) % A360);
    radarAngle = Math.floor((getHeading() - getRadarHeading()
                  + event.getBearing() + 2 * A360) % A360);

    // Sets time to keep track of turns since last scanned event
    turnCounter = getTime();
  }

}
