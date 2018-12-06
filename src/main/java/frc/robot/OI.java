/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
 
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.Config;
import frc.robot.util.JoystickReader;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	public static final String PATH = "oi";
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);
	
	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.
	
	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:
	
	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());
	
	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());
	
	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	public final JoystickReader left;
	public final JoystickReader right;
	private Map<String, JoystickButton> buttonMap;
	public OI(Config config) {
		buttonMap = new HashMap<>();
		left = config.createJoystick(PATH+".left", buttonMap);
		right = config.createJoystick(PATH+".right", buttonMap);
	}

	public double getLeft() {
		return left.getValue();
	}
	public double getRight() {
		return right.getValue();
	}

	/**
	 * @param button - The button to bind to
	 * @param command to run while the button is pressed
	 * @return the command
	 */
	public Command bindCommandWhile(String button, Command command) {
		if(buttonMap.containsKey(button)) {
			buttonMap.get(button).whileHeld(command);
			return command;
		}else {
			throw new RuntimeException(button+" is not a defined button in config");
		}
	}
	
	/**
	 * @param button - The button to bind to
	 * @param command to run when the button is pressed
	 * @return the command
	 * Runs command until it is canceled, e.g. by bindCommandRelease(String, command), or the isFinished returns true
	 */
	public Command bindCommandPress(String button, Command command) {
		if(buttonMap.containsKey(button)) {
			buttonMap.get(button).whenPressed(command);
			return command;
		}else {
			throw new RuntimeException(button+" is not a defined button in config");
		}
	}

	/**
	 * @param button - The button to bind to
	 * @param command to cancel when the button is pressed
	 * @return the command
	 * Cancels running command, e.g. a command started by bindCommandPress(String, command)
	 */
	public Command bindCommandRelease(String button, Command command) {
		if(buttonMap.containsKey(button)) {
			buttonMap.get(button).cancelWhenPressed(command);
			return command;
		}else {
			throw new RuntimeException(button+" is not a defined button in config");
		}
	}

}
