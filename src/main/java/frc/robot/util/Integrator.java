package frc.robot.util;

public class Integrator {
	private double total = 0;
	private double time = 0;
	private double last = 0;
	
	public double addSection(double next, double deltaTime) {
		time+= deltaTime;
		total+= time*(last+next)/2;
		last = next;
		return total;
	}

	public void reset() {
		total = 0;
		time = 0;
		last = 0;
	}

	public double get() {
		return total;
	}

	public double getLast() {
		return last;
	}
}