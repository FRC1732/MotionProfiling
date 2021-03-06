/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.TankDrive;
import frc.robot.commands.auto.ArcTurn;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.NavX;
import frc.robot.util.Config;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	private final Config config = Config.createConfig();
	private final double ROBOT_WIDTH = config.getDouble("robot.width");
	private final double ROBOT_LENGTH = config.getDouble("robot.length");
	private final DriveTrain drivetrain = new DriveTrain(config);
	private final NavX navx = new NavX(config);
	private OI oi;
	
	public double getROBOT_WIDTH() {
		return this.ROBOT_WIDTH;
	}
	
	public double getROBOT_LENGTH() {
		return this.ROBOT_LENGTH;
	}
	
	public DriveTrain getDrivetrain() {
		return this.drivetrain;
	}
	
	public NavX getNavx() {
		return this.navx;
	}
	
	public OI getOi() {
		return this.oi;
	}
	
	private ArcTurn auton;
	
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI(config);
		reset();
		auton = new ArcTurn(this, 60, -90, false);
		SmartDashboard.putData(navx);
		SmartDashboard.putData(auton);
	}
	
	/**
	 * This function is called every robot packet, no matter the mode. Use this for
	 * items like diagnostics that you want ran during disabled, autonomous,
	 * teleoperated and test.
	 *
	 * <p>
	 * This runs after the mode specific periodic functions, but before LiveWindow
	 * and SmartDashboard integrated updating.
	 */
	@Override
	public void robotPeriodic() {
	}
	
	/**
	 * This function is called once each time the robot enters Disabled mode. You
	 * can use it to reset any subsystem information you want to clear when the
	 * robot is disabled.
	 */
	@Override
	public void disabledInit() {
		reset();
	}
	
	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString code to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons to
	 * the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		reset();
		// new DriveStraight(100).start();
		auton.start();
	}
	
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}
	
	@Override
	public void teleopInit() {
		reset();
		new TankDrive(this).start();
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
	
	private void reset() {
		Scheduler.getInstance().removeAll();
		navx.zero();
		drivetrain.zero();
	}
}
