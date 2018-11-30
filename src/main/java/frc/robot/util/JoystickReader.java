package frc.robot.util;

import edu.wpi.first.wpilibj.Joystick;

public class JoystickReader extends Joystick {
	private String axis = "y";
	private double inverted = 1;
	
	public JoystickReader(int port) {
		super(port);
	}
	
	public double getValue() {
		switch (axis) {
			case "x":
				return getX() * inverted;
			case "y":
				return getY() * inverted;
			case "z":
				return getZ() * inverted;
			case "r":
				return getTwist() * inverted;
			case "t":
				return getThrottle() * inverted;
			default:
				throw new RuntimeException(axis + " is not a valid axis");
		}
	}
	
	public void setAxis(String a) {
		this.axis = a;
		getValue();
	}
	
	public void setInverted(boolean in) {
		if (in) {
			inverted = -1;
		} else {
			inverted = 1;
		}
	}
	
}