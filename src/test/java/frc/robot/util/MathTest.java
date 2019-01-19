package frc.robot.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathTest {
	@Test
	public void testClamp1() {
		assertEquals("Clamp failed: 2 -> 1", 1, MathUtil.clamp(2), 0.001);
	}
	@Test
	public void testClamp2() {
		assertEquals("Clamp failed: -2 -> -1", -1, MathUtil.clamp(-2), 0.001);
	}
	@Test
	public void testClamp3() {
		assertEquals("Clamp failed: 200 -> 1", 1, MathUtil.clamp(200), 0.001);
	}
	@Test
	public void testClamp4() {
		assertEquals("Clamp failed: -200 -> -1", -1, MathUtil.clamp(-200), 0.001);
	}
	@Test
	public void testClamp5() {
		assertEquals("Clamp failed: 0 -> 0", 0, MathUtil.clamp(0), 0.001);
	}
	@Test
	public void testClamp6() {
		assertEquals("Clamp failed: 0.5 -> 0.5", 0.5, MathUtil.clamp(0.5), 0.001);
	}
	@Test
	public void testClamp7() {
		assertEquals("Clamp failed: -0.5 -> -0.5", -0.5, MathUtil.clamp(-0.5), 0.001);
	}
	@Test
	public void testClamp8() {
		assertEquals("Clamp failed: 1 -> 1", 1, MathUtil.clamp(1), 0.001);
	}
	@Test
	public void testClamp9() {
		assertEquals("Clamp failed: -1 -> -1", -1, MathUtil.clamp(-1), 0.001);
	}
}