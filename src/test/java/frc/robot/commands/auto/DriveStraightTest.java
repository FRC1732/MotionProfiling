package frc.robot.commands.auto;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;

public class DriveStraightTest {
	@Test
	public void driveStraightDirectionTest() {
		double target = 100;
		for (double i = 0; i < target; i += 4) {
			driveStraightDirectionTestHelper(target, i, 1.0, "Drivetrain is moving backwards");
		}
		// Drivetrain is moving at zero (inside deadband), but Math.signum(error)
		// removes this specifically at zero. This means that the following test is
		// technically passing, but alternate *working* implementations would not pass;
		// therefore the test has been exluded
		// driveStraightDirectionTestHelper(target, target, 0.0, "Drivetrain is moving
		// when at target");
		for (double i = target + 1; i < target + 20; i += 4) {
			driveStraightDirectionTestHelper(target, i, -1.0, "Drivetrain is moving backwards");
		}
	}
	
	private static void driveStraightDirectionTestHelper(double target, double current, double outputSign,
	        String message) {
		// mock robot to return mocked drivetrain
		Robot mockedRobot = mock(Robot.class);
		DriveTrain mockedDriveTrain = mock(DriveTrain.class);
		when(mockedRobot.getDrivetrain()).thenReturn(mockedDriveTrain);
		
		// set initial sensor values
		when(mockedDriveTrain.leftPos()).thenReturn(0.0);
		when(mockedDriveTrain.rightPos()).thenReturn(0.0);
		
		// init drivestraight
		DriveStraight driveStraight = new DriveStraight(mockedRobot, target);
		driveStraight.initialize();
		
		// capture the output of the drivestraight to the drivetrain
		ArgumentCaptor<Double> left = ArgumentCaptor.forClass(Double.class);
		ArgumentCaptor<Double> right = ArgumentCaptor.forClass(Double.class);
		// verify that drivestraight has not tried to move the drivetrain yet
		verify(mockedDriveTrain, times(0)).set(left.capture(), right.capture());
		
		// set sensor values for test
		when(mockedDriveTrain.leftPos()).thenReturn(current);
		when(mockedDriveTrain.rightPos()).thenReturn(current);
		// method under test
		driveStraight.execute();
		// verify that the drivetrain was set to some speed
		verify(mockedDriveTrain).set(left.capture(), right.capture());
		// assert that the drivetrain is moving in the correct direction
		assertEquals(message, outputSign, Math.signum(left.getValue()), 0.0);
		assertEquals(message, outputSign, Math.signum(right.getValue()), 0.0);
		// verify that the right or left is greater (turn direction) == 0.0 for going straight
		assertEquals(message, 0.0, Math.signum(right.getValue() - left.getValue()), 0.0);
	}
}