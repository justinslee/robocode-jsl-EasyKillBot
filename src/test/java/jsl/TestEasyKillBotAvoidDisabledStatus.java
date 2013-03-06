package jsl;

import static org.junit.Assert.assertTrue;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.testing.RobotTestBed;

/**
 * Tests to determine if the robot EasyKillBot avoids shooting so it can't move with no energy.  
 * Crazy bot is used for its erratic movement behavior.
 *
 * @author Philip Johnson
 * @author Justin Lee
 */
public class TestEasyKillBotAvoidDisabledStatus extends RobotTestBed {

  boolean noDisabledStatus = true;
  int numberOfRounds = 5;
  
  /**
   * Specifies that Crazy and EasyKillBot are to be matched up in this test case.
   * 
   * @return The comma-delimited list of robots in this match.
   */
  @Override 
  public String getRobotNames() {
    return "sample.Crazy,jsl.EasyKillBot";
  }
  
  /**
   * This test runs for rounds specified in class.
   * 
   * @return The number of rounds. 
   */
  @Override 
  public int getNumRounds() {
    return numberOfRounds;
  }
  
  /**
   * At the end of each turn, checks for the specified robot to see
   * if it had become disabled. 
   * 
   * @param event Info about the current state of the battle.
   */
  @Override 
  public void onTurnEnded (TurnEndedEvent event) {
    
    // Checks each turn to determine if robot hit a wall.
    IRobotSnapshot robots[]= event.getTurnSnapshot().getRobots();
    for(IRobotSnapshot robot: robots) {
      if((robot.getName()).compareToIgnoreCase("jsl.EasyKillBot") == 0 
          || robot.getName().compareToIgnoreCase("jsl.EasyKillBot*") == 0) {
        if(robot.getState().isAlive() == true && robot.getEnergy() <= 0) {
          noDisabledStatus = false;
        }
      }
    }
  }
  
  /**
   * After running all matches, determine if EasyKillBot became disabled.
   * 
   * @param event Details about the completed battle.
   */
  @Override 
  public void onBattleCompleted(BattleCompletedEvent event) {
    assertTrue("Robot did not become disabled.", noDisabledStatus);
  }
}