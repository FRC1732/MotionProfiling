/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.config.Config;

/**
 * Add your docs here.
 */
public class DriveTrain extends Subsystem {
	
	public static final String PATH = "drivetrain";
	private final TalonSRX leftMaster;
	private final TalonSRX left2;
	private final TalonSRX left3;
	private final TalonSRX rightMaster;
	private final TalonSRX right2;
	private final TalonSRX right3;
	
	public DriveTrain(Config config) {
		leftMaster = config.createTalon(PATH + ".left1");
		left2 = config.createTalon(PATH + ".left2");
		left3 = config.createTalon(PATH + ".left3");
		rightMaster = config.createTalon(PATH + ".right1");
		right2 = config.createTalon(PATH + ".right2");
		right3 = config.createTalon(PATH + ".right3");

		left2.follow(leftMaster);
		left3.follow(leftMaster);

		right2.follow(rightMaster);
		right3.follow(rightMaster);
	}
	
	/**
	 * Sets the speed values as percentages 
	 * @param left Left demand, [-1, 1]
	 * @param right Right demand, [-1, 1]
	 */
	public void set(double left, double right) {
		leftMaster.set(ControlMode.PercentOutput, left);
		rightMaster.set(ControlMode.PercentOutput, right);
	}

	/**
	 * Sets the speed to 0
	 */
	public void stop() {
		leftMaster.set(ControlMode.PercentOutput, 0);
	}

	public double leftPos() {
		return leftMaster.getSelectedSensorPosition(0);
	}
	public double leftVel() {
		return leftMaster.getSelectedSensorVelocity(0);
	}
	public double rightPos() {
		return rightMaster.getSelectedSensorPosition(0);
	}
	public double rightVel() {
		return rightMaster.getSelectedSensorVelocity(0);
	}

	@Override
	public void initDefaultCommand() {
		// do nothing
	}
}
