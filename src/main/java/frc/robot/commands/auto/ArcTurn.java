/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Sendable;
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
	private final Robot robot;
	private double angle;
	private boolean reversed;
	
	public ArcTurn(Robot robot, double radius, double angle, boolean reversed) {
		this.robot = robot;
		this.radius = radius;
		this.angle = angle;
		this.reversed = reversed;
		this.iError = new Integrator();
		requires(robot.getDrivetrain());
	}
	
	private double end;
	private Integrator iError;
	private double lastT;
	
	private double getError() {
		return end - robot.getNavx().getTotalAngle();
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		end = robot.getNavx().getTotalAngle() + angle;
		iError.reset();
		lastT = System.currentTimeMillis() / 1000.0;
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		double error = getError();// * Math.signum(angle);
		double d = D * (error - iError.getLast()) / (System.currentTimeMillis() / 1000.0 - lastT);
		double i = I * iError.addSection(error, System.currentTimeMillis() / 1000.0 - lastT);
		double p = P * error;
		double pid = Math.abs(MathUtil.clamp(p + i + d + F)) * Math.signum(error);
		double innerPid = pid * MathUtil.innerSpeed(radius, robot.getROBOT_WIDTH());
		if (!reversed) {
			robot.getDrivetrain().set(innerPid, pid);
		} else {
			robot.getDrivetrain().set(pid, innerPid);
		}
		
		System.out.println(
		        "e: " + getError() + ", pid: " + pid + ", i:" + (pid*MathUtil.innerSpeed(radius, robot.getROBOT_WIDTH())));
		lastT = System.currentTimeMillis() / 1000.0;
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
		robot.getDrivetrain().stop();
	}
	
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		robot.getDrivetrain().stop();
	}
}
