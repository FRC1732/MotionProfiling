/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.Robot;
import frc.robot.util.Integrator;
import frc.robot.util.MathUtil;

public class ArcTurn extends Command implements Sendable {
	
	public double TOLERANCE = 0;
	public double P = 0.2;
	public double I = 0;
	public double D = 0;
	public double F = 0;
	private final double radius;
	private double angle;
	private boolean reversed = false;
	
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
		t.start();
		lastT = 0;
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		double error = getError();// * Math.signum(angle);
		double d = D * (error - iError.getLast()) / (t.get() - lastT);
		double i = I * iError.addSection(error, t.get() - lastT);
		double p = P * error;
		double pid = MathUtil.clamp(p + i + d) * (reversed ? -1 : 1);
		double innerPid = pid * MathUtil.innerSpeed(radius, Robot.ROBOT_WIDTH);
		if (angle < 0) {
			Robot.drivetrain.set(innerPid, pid);
		} else {
			Robot.drivetrain.set(pid, innerPid);
		}
		
		System.out.println(
		        "e: " + getError() + ", pid: " + pid + ", i:" + MathUtil.innerSpeed(radius, Robot.ROBOT_WIDTH));
		lastT = t.get();
	}
	
	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("PIDController");
		builder.setSafeState(this::reset);
		builder.addDoubleProperty("p", this::getP, this::setP);
		builder.addDoubleProperty("i", this::getI, this::setI);
		builder.addDoubleProperty("d", this::getD, this::setD);
		builder.addDoubleProperty("f", this::getF, this::setF);
		builder.addDoubleProperty("setpoint", this::getSetpoint, this::setSetpoint);
		builder.addBooleanProperty("enabled", this::isEnabled, this::setEnabled);
	}
	
	private void reset() {
	}
	
	private double getP() {
		return P;
	}
	
	private double getI() {
		return I;
	}
	
	private double getD() {
		return D;
	}
	
	private double getF() {
		return F;
	}
	
	private void setP(double p) {
		P = p;
	}
	
	private void setI(double i) {
		I = i;
	}
	
	private void setD(double d) {
		D = d;
	}
	
	private void setF(double f) {
		F = f;
	}
	
	private double getSetpoint() {
		return angle;
	}
	
	private void setSetpoint(double a) {
		angle = a;
	}
	
	private boolean isEnabled() {
		return reversed;
	}
	
	private void setEnabled(boolean a) {
		reversed = a;
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
