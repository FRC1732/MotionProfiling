package frc.robot.util;

public class MathUtil {
	public static double clamp(double d) {
		if(d > 1) {
			return 1;
		}else if(d < -1) {
			return -1;
		}else {
			return d;
		}
	}

	public static double innerSpeed(double radius, double robotWidth) {
		double outerCir = 2 * Math.PI * radius;
		double innerCir = 2 * Math.PI * (radius - robotWidth);
		return innerCir / outerCir;
	}
}