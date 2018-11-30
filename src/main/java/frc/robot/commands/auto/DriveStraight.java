/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class DriveStraight extends Command {

	public static final double TOLERANCE = 0.5;
	public static final double P = 0.05;
	public static final double I = 0;
	public static final double D = 0;
	public static final double K = 0.01;
	private final double distance;
	private final Robot robot;

	public DriveStraight(Robot robot, double distance) {
		this.distance = distance;
		this.robot = robot;
		requires(robot.getDrivetrain());
	}
	
	private double end;
	private double getError() {
		return end - (robot.getDrivetrain().leftPos()+robot.getDrivetrain().rightPos())/2;
	}
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		end = (robot.getDrivetrain().leftPos()+robot.getDrivetrain().rightPos())/2 + distance;
		System.out.println("Init Drivestraight");
		System.out.printf("%f -> %f\n", getError(), 0.0);
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		double error = getError();
		double pid = (Math.abs(P*error) + K) * Math.signum(error);
		System.out.printf("%f -> %f\n", error, pid);
		robot.getDrivetrain().set(clamp(pid), clamp(pid));
	}

	private double clamp(double d) {
		if(d > 1) {
			return 1;
		}else if(d < -1) {
			return -1;
		}else {
			return d;
		}
	}
	
	private int time = 0;
	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		if(Math.abs(getError()) < TOLERANCE) {
			time++;
		}else {
			time = 0;
		}
		return time >= 2;
	}
	
	// Called once after isFinished returns true
	@Override
	protected void end() {
		robot.getDrivetrain().stop();
	}
	
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		robot.getDrivetrain().stop();
	}
}
