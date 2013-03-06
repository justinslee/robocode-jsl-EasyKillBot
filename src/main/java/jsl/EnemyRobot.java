package jsl;

import robocode.ScannedRobotEvent;

/**
 * <code>EnemyRobot</code> is stores event data and other enemy information.
 * It will probably be an ArrayList containing many events
 * and robot tracking calculations.
 * @author Justin Lee
 * @version 1.0
 */
public class EnemyRobot {

  /**
   * Used for enemyEvent storage.
   */
  private final EnemyEvent enemyEvent;

  /**
   * Default constructor.
   */
  public EnemyRobot() {
    enemyEvent = new EnemyEvent();
  }

  /**
   * <code>EnemyEvent</code> nested class structure that will contain more
   * enemy robot information besides events.
   */
  private static class EnemyEvent {

    /**
     * Used to store events.
     */
    private ScannedRobotEvent event = null;

    /**
     * Method <code>getEvent</code> returns the stored event.
     * @return event of type ScannedR
     */
    public ScannedRobotEvent getEvent() {
      return event;
    }

    /**
     * Method <code>setEvent</code> sets the event to a value.
     * @param eve - object of type ScannedRobotEvent
     */
    public final void setEvent(final ScannedRobotEvent eve) {
      this.event = eve;
    }

  }


  /**
   * Method <code>storeEvent</code> stores an event to be used later.
   * @param event event to be stored
   * @return true
   */
  public final boolean storeEvent(final ScannedRobotEvent event) {

    enemyEvent.setEvent(event);

    return true;
  }

  /**
   * Method <code>getLastEvent</code> gets the last event stored.
   * @return last event.
   */
  public final ScannedRobotEvent getLastEvent() {

    return enemyEvent.getEvent();
  }
}
