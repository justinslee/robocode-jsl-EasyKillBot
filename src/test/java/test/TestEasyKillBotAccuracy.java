package test;

import static org.junit.Assert.assertTrue;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.testing.RobotTestBed;

/**
 * Tests accuracy for EasyKillBot.  This test will be used for
 * future tests for predictive targeting.
 * @author Philip Johnson
 * @author Justin Lee
 */
public class TestEasyKillBotAccuracy extends RobotTestBed {

  /**
   *  Used for accuracy calculation.
   */
  private int lastElement = 0;
  /**
   *  Bullets that hit.
   */
  //CHECKSTYLEOFF MagicNumber
  private boolean [] bulletHits = new boolean[1000];
  //CHECKSTYLEON MagicNubmer
  /**
   * Set for 30% accuracy.  Bullet damage is 4 * firepower. If firepower > 1,
   * it does an additional damage = 2 * (power - 1).
   * This means accuracy above 25% is good for all bullets.
   */
  //CHECKSTYLEOFF MagicNumber
  private double firingAccuracy = .3;
  //CHECKSTYLEON MagicNubmer
  /**
   * Specifies that SittingDuck and EasyKillBot are to
   * be matched up in this test case.
   * Will either build a random movement bot or
   * Walls/corner bot that does not fire to
   * test accuracy on a moving target.
   *
   * @return The comma-delimited list of robots in this match.
   */
  @Override
  public String getRobotNames() {
    return "sample.SittingDuck,jsl.EasyKillBot";
  }

  /**
   * This test runs for 20 rounds.
   *
   * @return The number of rounds.
   */
  @Override
  public final int getNumRounds() {
    return 20;
  }

  /**
   * Checks each turn to see if a new bullet has finished.
   *
   * @param event Info about the current state of the battle.
   */
  @Override
  public final void onTurnEnded (TurnEndedEvent event) {
    
    // All active bullets belong to EasyKillBot since SittingDuck does not fire.
    IBulletSnapshot bullets[] = (event.getTurnSnapshot()).getBullets(); //NOPMD
    
    for (int i = 0; i < bullets.length; i++) {
      if (bullets[i].getState() == BulletState.HIT_VICTIM) { //NOPMD
        if (bullets[i].getBulletId() >= 0) { //NOPMD
          bulletHits[bullets[i].getBulletId()] = true; //NOPMD
        }
      }
      if (bullets[i].getBulletId() > lastElement) { //NOPMD
        lastElement = bullets[i].getBulletId(); //NOPMD
      }
    }
  }
  
  /**
   * After running all matches, determine if EasyKillBot 
   * has hit the sitting duck accurately while
   * moving around.
   * 
   * @param event Details about the completed battle.
   */
  @Override 
  public final void onBattleCompleted(BattleCompletedEvent event) {
    
    double hits = 0;
    for (int i = 0; i <= lastElement; i++) {
      if (bulletHits[i]) {
        hits ++;
      }
    }
    assertTrue("Bullets Hit Accurately", (hits / lastElement) >= firingAccuracy );
    //assertTrue("foo", false);
  }
}
