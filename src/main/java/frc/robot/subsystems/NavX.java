/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.util.Config;

/**
 * Add your docs here.
 */
public class NavX extends Subsystem implements Sendable {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	
	private AHRS ahrs;
	
	public NavX(Config config) {
		ahrs = config.createNavX("navx");
	}
	
	public double getAngle() {
		return ahrs.getYaw();
	}
	
	public double getTotalAngle() {
		return ahrs.getAngle();
	}
	
	public void zero() {
		ahrs.zeroYaw();
	}
	
	@Override
	public void initDefaultCommand() {
	}
	
	public boolean isConnected() {
		return ahrs.isConnected();
	}
	
	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Gyro");
		builder.addDoubleProperty("Value", this::getAngle, null);
	}
}
