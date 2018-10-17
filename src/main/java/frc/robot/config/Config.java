package frc.robot.config;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.List;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
public class Config {
	private final String name;
	private final Map<String, Object> configOBJ;
	private int TIMEOUT;
	
	public Config(String name) {
		this.name = name;
		try {
			configOBJ = (Map<String, Object>) new JSONParser()
			        .parse(new InputStreamReader(Config.class.getResourceAsStream(name + ".json")));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Config Failed to parse");
		}
		try {
			TIMEOUT = getInt("timeout");
		} catch (Exception e) {
			TIMEOUT = 20;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Object get(String path) {
		Object o = get(path, configOBJ);
		if (o == null) {
			throw new RuntimeException("Path '" + path + "' does not exist in config " + name);
		}
		return o;
	}
	
	private Object get(String path, Object config) {
		if (path.length() == 0) {
			return config;
		} else if (config instanceof Map) {
			String part = path.substring(0, Math.min(path.indexOf('.'), path.indexOf('[')));
			return get(path.substring(Math.min(path.indexOf('.'), path.indexOf('[')) + 1),
			        ((Map<String, Object>) config).get(part));
		} else if (config instanceof List) {
			String part = path.substring(0, path.indexOf(']'));
			return get(path.substring(path.indexOf(']') + 2), ((List<Object>) config).get(Integer.parseInt(part)));
		} else {
			return null;
		}
	}
	
	public int getInt(String path) {
		Object n = get(path);
		if (n instanceof Number) {
			return ((Number) n).intValue();
		} else {
			throw new RuntimeException("Path '" + path + "' is not an int in config " + name);
		}
	}
	
	public double getDouble(String path) {
		Object n = get(path);
		if (n instanceof Number) {
			return ((Number) n).doubleValue();
		} else {
			throw new RuntimeException("Path '" + path + "' is not a double in config " + name);
		}
	}
	
	public boolean getBool(String path) {
		Object n = get(path);
		if (n instanceof Boolean) {
			return ((Boolean) n).booleanValue();
		} else {
			throw new RuntimeException("Path '" + path + "' is not a boolean in config " + name);
		}
	}
	
	public String getString(String path) {
		Object n = get(path);
		if (n instanceof String) {
			return (String) n;
		} else {
			throw new RuntimeException("Path '" + path + "' is not a string in config " + name);
		}
	}
	
	public List<Object> getList(String path) {
		Object n = get(path);
		if (n instanceof List) {
			return (List<Object>) n;
		} else {
			throw new RuntimeException("Path '" + path + "' is not a list in config " + name);
		}
	}
	
	public Map<String, Object> getMap(String path) {
		Object n = get(path);
		if (n instanceof Map) {
			return (Map<String, Object>) n;
		} else {
			throw new RuntimeException("Path '" + path + "' is not a map in config " + name);
		}
	}
	
	public TalonSRX createTalon(String path) {
		TalonSRX motor = new TalonSRX(getInt(path + ".id"));
		try {
			motor.setInverted(getBool(path + ".reversed"));
		} catch (Exception e) {
		}
		
		try {
			get(path + ".sensor");
			int pidIdx;
			try {
				pidIdx = getInt(path + ".sensor.pid");
			} catch (Exception e) {
				pidIdx = 0;
			}
			switch (getString(path + ".sensor.type")) {
				case "quad":
					motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidIdx, TIMEOUT);
					break;
				case "analog":
					motor.configSelectedFeedbackSensor(FeedbackDevice.Analog, pidIdx, TIMEOUT);
					break;
				default: 
					throw new RuntimeException(getString(path + ".sensor.type") + " is not a valid encoder type");
			}
			try {
				motor.setSensorPhase(getBool(path+".sensor.reversed"));
			} catch (Exception e) {
			}
		}catch(Exception e) {
		}
		return motor;
	}
	
	public VictorSPX createVictor(String path) {
		VictorSPX motor = new VictorSPX(getInt(path + ".id"));
		try {
			motor.setInverted(getBool(path + ".reversed"));
		} catch (Exception e) {
		}
		
		return motor;
	}
}