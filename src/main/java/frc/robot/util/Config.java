package frc.robot.util;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import org.json.simple.parser.JSONParser;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("unchecked")
public class Config {
	public static final JSONParser parser = new JSONParser();
	private final String name;
	private final Map<String, Object> configOBJ;
	private int TIMEOUT;

	public Config(String name) {
		this.name = name;
		try {
			configOBJ = (Map<String, Object>) parser
					.parse(new InputStreamReader(Config.class.getResourceAsStream(name + ".json")));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Config failed to parse");
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

	private int next(String s, char one, char two) {
		int o = s.indexOf(one);
		int t = s.indexOf(two);
		if (o == -1 && t == -1) {
			return s.length();
		} else if (t == -1) {
			return o;
		} else if (o == -1) {
			return t;
		} else {
			return Math.min(o, t);
		}
	}

	private int next1(String s, char one, char two) {
		int o = s.indexOf(one);
		int t = s.indexOf(two);
		if (o == -1 && t == -1) {
			return s.length() - 1;
		} else if (t == -1) {
			return o;
		} else if (o == -1) {
			return t;
		} else {
			return Math.min(o, t);
		}
	}

	private Object get(String path, Object config) {
		if (path.length() == 0) {
			return config;
		} else if (config instanceof Map) {
			String part = path.substring(0, next(path, '.', '['));
			return get(path.substring(next1(path, '.', '[') + 1), ((Map<String, Object>) config).get(part));
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
		motor.setNeutralMode(NeutralMode.Brake);
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
				motor.setSensorPhase(getBool(path + ".sensor.reversed"));
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
		return motor;
	}

	public VictorSPX createVictor(String path) {
		VictorSPX motor = new VictorSPX(getInt(path + ".id"));
		try {
			motor.setInverted(getBool(path + ".reversed"));
		} catch (Exception e) {
		}
		motor.setNeutralMode(NeutralMode.Brake);

		return motor;
	}

	public AHRS createNavX(String path) {
		AHRS out;
		switch (getString(path + ".port")) {
		case "kMXP":
			out = new AHRS(SPI.Port.kMXP);
			break;
		default:
			throw new RuntimeException(getString(path + ".port") + " is not a valid port type for navx");
		}
		return out;
	}

	public JoystickReader createJoystick(String path, Map<String, JoystickButton> buttonMap) {
		JoystickReader stick = new JoystickReader(getInt(path + ".port"));
		Map<String, Object> map = getMap(path + ".buttons");
		for (Entry<String, Object> e : map.entrySet()) {
			buttonMap.put(e.getKey(), new JoystickButton(stick, ((Number) e.getValue()).intValue()));
		}
		try {
			stick.setAxis(getString(path+".axis"));
		}catch(Exception e) {
		}
		try {
			stick.setInverted(getBool(path+".reversed"));
		}catch(Exception e) {
		}
		return stick;
	}

	public static Config createConfig() {
		try {
			Map<String, Object> configs = (Map<String, Object>) parser
					.parse(new InputStreamReader(Config.class.getResourceAsStream("configs.json")));
			String current = getPreference("config", (String) configs.get("default"));
			SmartDashboard
					.putData("Config", new ConfigChooser(current, (List<String>) configs.get("options")));
			return new Config(current);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("configs.json failed to parse");
		}
	}

	private static final Preferences prefs = Preferences.userNodeForPackage(Config.class);

	private static String getPreference(String pref, String defualt) {
		return prefs.get(pref, defualt);
	}

	private static void setPreference(String pref, String value) {
		prefs.put(pref, value);
	}

	private static class ConfigChooser extends SendableBase implements Sendable {
		private static final String DEFAULT = "default";
		private static final String SELECTED = "selected";
		private static final String OPTIONS = "options";

		private String defaultChoice;
		private List<String> map;

		public ConfigChooser(String defaultChoice, List<String> map) {
			this.defaultChoice = defaultChoice;
			this.map = map;
		}
		private String getDefault() {
			return defaultChoice;
		}
		private String[] getChoices() {
			return map.toArray(new String[0]);
		}
		private void onEntry(EntryNotification e) {
			try {
				Map<String, Object> configs = (Map<String, Object>) parser
						.parse(new InputStreamReader(Config.class.getResourceAsStream("configs.json")));
				setPreference("config",
						e.getEntry().getString(getPreference("config", (String) configs.get("default"))));
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("configs.json failed to parse");
			}
			System.exit(0);
		}

		@Override
		public void initSendable(SendableBuilder builder) {
			builder.setSmartDashboardType("String Chooser");
			builder.addStringProperty(DEFAULT, this::getDefault, null);
			builder.addStringArrayProperty(OPTIONS, this::getChoices, null);
			builder.getEntry(SELECTED).addListener(this::onEntry, EntryListenerFlags.kUpdate);
		}
	}

}
