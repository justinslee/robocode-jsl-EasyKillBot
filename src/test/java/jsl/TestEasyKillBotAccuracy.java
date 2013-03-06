package jsl;

import static org.junit.Assert.assertTrue;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.testing.RobotTestBed;

/**
 * Tests accuracy for EasyKillBot.  This test will be used for future tests for predictive targeting.
 * This test is not working yet.  
 * Currently the bot only hits with around 40% accuracy against a non-moving target and fails this test
 * @author Philip Johnson
 * @author Justin Lee
 */
public class TestEasyKillBotAccuracy extends RobotTestBed {

  // Used for accuracy calculation
  int lastElement = 0;
  boolean [] bulletHits = new boolean[1000];
  // Set for 70%
  double firingAccuracy = .7;
  /**
   * Specifies that SittingDuck and EasyKillBot are to be matched up in this test case.
   * Will either build a random movement bot or Walls/corner bot that does not fire to
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
  public int getNumRounds() {
    return 20;
  }
  
  /**
   * Checks each turn to see if a new bullet has finished.
   * 
   * @param event Info about the current state of the battle.
   */
  @Override 
  public void onTurnEnded (TurnEndedEvent event) {
    
    // All active bullets belong to EasyKillBot since SittingDuck does not fire.
    IBulletSnapshot bullets[] = event.getTurnSnapshot().getBullets();
    
    for (int i = 0; i < bullets.length; i++) {
      if(bullets[i].getState() == BulletState.HIT_VICTIM) {
        if(bullets[i].getBulletId()>=0) {
          bulletHits[bullets[i].getBulletId()] = true;
        }
      }
      if(bullets[i].getBulletId()>lastElement) {
        lastElement = bullets[i].getBulletId();
      }
    }
  }
  
  /**
   * After running all matches, determine if EasyKillBot has hit the sitting duck accurately while
   * moving around.
   * 
   * @param event Details about the completed battle.
   */
  @Override 
  public void onBattleCompleted(BattleCompletedEvent event) {
    
    double hits = 0;
    for(int i = 0; i <= lastElement; i++) {
      if(bulletHits[i] == true) {
        hits ++;
      }
    }

    assertTrue("Bullets Hit Accurately", (hits/lastElement) >= firingAccuracy );
    //assertTrue("foo", false);
  }
}