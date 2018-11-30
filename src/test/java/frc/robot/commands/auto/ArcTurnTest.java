package frc.robot.commands.auto;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.NavX;

public class ArcTurnTest {
	@Test
	public void arcTurnDirectionTest() {
		double target = 90;
		for (double i = 0; i < target; i += 4) {
			arcTurnDirectionTestHelper(target, 100, false, 0.0, 0.0, i, 1.0, 1.0);
		}
		for (double i = target + 1; i < target * 2; i += 4) {
			arcTurnDirectionTestHelper(target, 100, false, 0.0, 0.0, i, -1.0, 1.0);
		}
		for (double i = 0; i < target; i += 4) {
			arcTurnDirectionTestHelper(target, 100, true, 0.0, 0.0, i, 1.0, -1.0);
		}
		for (double i = target + 1; i < target + 20; i += 4) {
			arcTurnDirectionTestHelper(target, 100, true, 0.0, 0.0, i, -1.0, -1.0);
		}
	}
	
	private static void arcTurnDirectionTestHelper(double target, double radius, boolean reversed, double currentL,
	        double currentR, double currentAngle, double outputSign, double leftSign) {
		// mock robot to return mocked drivetrain and navx
		Robot mockedRobot = mock(Robot.class);
		DriveTrain mockedDriveTrain = mock(DriveTrain.class);
		NavX mockedNavX = mock(NavX.class);
		when(mockedRobot.getDrivetrain()).thenReturn(mockedDriveTrain);
		when(mockedRobot.getNavx()).thenReturn(mockedNavX);
		when(mockedRobot.getROBOT_WIDTH()).thenReturn(20.0);
		
		// set sensor values
		when(mockedDriveTrain.leftPos()).thenReturn(0.0);
		when(mockedDriveTrain.rightPos()).thenReturn(0.0);
		when(mockedNavX.getAngle()).thenReturn(0.0);
		when(mockedNavX.getTotalAngle()).thenReturn(0.0);
		
		// init arcturn
		ArcTurn arcTurn = new ArcTurn(mockedRobot, radius, target, reversed);
		arcTurn.initialize();
		
		// capture left and right out
		ArgumentCaptor<Double> left = ArgumentCaptor.forClass(Double.class);
		ArgumentCaptor<Double> right = ArgumentCaptor.forClass(Double.class);
		
		// verify that the command has not tried to start the drivetrain
		verify(mockedDriveTrain, times(0)).set(left.capture(), right.capture());
		// avoid init and exec happening in the same millisecond -> robot always has
		// minimum delay of robot_period
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (Exception e) {
		}
		// set sensor values for test
		when(mockedDriveTrain.leftPos()).thenReturn(currentL);
		when(mockedDriveTrain.rightPos()).thenReturn(currentR);
		when(mockedNavX.getAngle()).thenReturn(currentAngle);
		when(mockedNavX.getTotalAngle()).thenReturn(currentAngle);
		// method under test
		arcTurn.execute();
		// verify that the drivetrain was set to some value
		verify(mockedDriveTrain).set(left.capture(), right.capture());
		// verify that the values are correct
		assertEquals("Drivetrain is moving backwards - left", outputSign, Math.signum(left.getValue()), 0.0);
		assertEquals("Drivetrain is moving backwards - right", outputSign, Math.signum(right.getValue()), 0.0);
		// verify that the right or left is greater (turn direction)
		assertEquals("Drivetrain is turning the wrong direction", leftSign,
		        Math.signum(Math.abs(right.getValue()) - Math.abs(left.getValue())), 0.0);
	}
}