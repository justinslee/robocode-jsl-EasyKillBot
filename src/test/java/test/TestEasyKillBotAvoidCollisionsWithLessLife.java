package test;

import static org.junit.Assert.assertTrue;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.testing.RobotTestBed;

/**
 * Tests to determine if the robot EasyKillBot avoids collisions
 * with other robots while it has less life.  If it has greater 
 * life then ramming/collisions are fine. Updated so collisions
 * are tested to see if a majority this case is true.
 * @author Philip Johnson
 * @author Justin Lee
 */
public class TestEasyKillBotAvoidCollisionsWithLessLife extends RobotTestBed {

  int numberOfRounds = 10;
  int collisionLess = 0;
  int collisionGreater = 0;
  
  
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
   * if it had hit a robot with greater life during the course of the match. 
   * 
   * @param event Info about the current state of the battle.
   */
  @Override 
  public void onTurnEnded (TurnEndedEvent event) {
    
    // Checks each turn to determine if robot hit another robot.
    IRobotSnapshot robots[]= event.getTurnSnapshot().getRobots();
    if((robots[0].getName()).compareToIgnoreCase("jsl.EasyKillBot") == 0 
          || robots[0].getName().compareToIgnoreCase("jsl.EasyKillBot*") == 0) {
      if(robots[0].getState().isHitRobot() == true) {
        if(robots[0].getEnergy() < robots[1].getEnergy()) {
          collisionLess ++;
        } else {
          collisionGreater ++;
        }
      }
    } else {
      if(robots[1].getState().isHitRobot() == true) {
        if(robots[1].getEnergy() < robots[0].getEnergy()) {      
          collisionLess ++;
        } else {
          collisionGreater ++;
        }
      }      
    }
  }
  
  /**
   * After running all matches, determine if EasyKillBot has not hit a robot with
   * more life than it had.
   * 
   * @param event Details about the completed battle.
   */
  @Override 
  public void onBattleCompleted(BattleCompletedEvent event) {
    assertTrue("Robot hit robot with less life a majority of the time",
                 collisionGreater >= collisionLess);
  }
}