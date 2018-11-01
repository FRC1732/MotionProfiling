/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.Integrator;
import frc.robot.util.MathUtil;

public class ArcTurn extends Command {
	
	public static final double TOLERANCE = 0;
	public static final double P = 0.2;
	public static final double I = 0;
	public static final double D = 0;
	private final double radius;
	private final double angle;
	
	public ArcTurn(double radius, double angle) {
		this.radius = radius;
		this.angle = angle;
		this.iError = new Integrator();
		this.t = new Timer();
		requires(Robot.drivetrain);
	}
	
	private double end;
	private Integrator iError;
	private Timer t;
	private double lastT;
	
	private double getError() {
		return Robot.navx.getTotalAngle() - end;
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		end = Robot.navx.getTotalAngle() - angle;
		iError.reset();
		t.reset();
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		double error = getError();// * Math.signum(angle);
		double p = P * error;
		double i = I * iError.addSection(error, t.get() - lastT);
		double d = D * (error - iError.getLast()) / (t.get() - lastT);
		double pid = MathUtil.clamp(p + i + d);
		double innerPid = pid * MathUtil.innerSpeed(radius, Robot.ROBOT_WIDTH);
		if (angle < 0) {
			Robot.drivetrain.set(innerPid, pid);
		} else {
			Robot.drivetrain.set(pid, innerPid);
		}
		
		System.out.println("e: " + getError() + ", i:" + MathUtil.innerSpeed(radius, Robot.ROBOT_WIDTH));
		lastT = t.get();
	}
	
	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return Math.abs(getError()) < TOLERANCE;
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
