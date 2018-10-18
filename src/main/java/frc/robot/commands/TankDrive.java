/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class TankDrive extends Command {
	public TankDrive() {
		requires(Robot.drivetrain);
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("Drive Control: TankDrive");
		System.out.println("\tTankDrive provides a control system using two joysticks, one for each side");
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		Robot.drivetrain.set(Robot.oi.getLeft(), Robot.oi.getRight());
	}
	
	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}
	
	// Called once after isFinished returns true
	@Override
	protected void end() {
		Robot.drivetrain.stop();
	}
	
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		Robot.drivetrain.stop();
	}
}
