import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CGMinerManager {
	
	private class Throttler {
		String process;
		int intensity;
	}
	
	int TIMER_LEN = 5000;
	int INTENSITY_IDLE = 20;
	int INTENSITY_INUSE = 17;
	long IDLE_TIMEOUT = 1000 * 60 * 30;
	Socket socket;
	PrintWriter socketOut;
	BufferedReader socketIn;
	ArrayList<Throttler> throttlers;
	long idleStart = System.currentTimeMillis();
	Point lastPoint = MouseInfo.getPointerInfo().getLocation();
	String HOST = "localhost";
	int PORT = 4028;
	String throttleReason = "Computer is in use.";

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("CGMiner Intensity Manager v0.1 by Sohcahtoa82");
		System.out.println("https://github.com/Sohcahtoa82/CGMinerManager for source code and detailed instructions.");
		System.out.println("Donation addresses:");
		System.out.println("Bitcoin: 19Mz5onCDfvKwoHUBEeiVdbhuuQQh989yf");
		System.out.println("Dogecoin: DHjpPYZBCr92T9kXQnwjqjw2Jfjeix4eHB");
		System.out.println("Litecoin: LUPUMKvbWYgxcznJNh5mwB2svk8G3FpyyF");
		System.out.println();
		new CGMinerManager(args);
	}
	
	CGMinerManager(String[] args) throws IOException, InterruptedException {
		throttlers = new ArrayList<Throttler>();
		int throttleLevel = INTENSITY_INUSE;
		
		try {
			if (!parseArgs(args)){
				printUsage();
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("A parameter expecting an integer did not contain an integer.");
			printUsage();
			return;
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Unexpected end of parameters.");
			printUsage();
			return;
		}
		
		System.out.println("Press CTRL-C to stop.");
		
		while(true) {
			//while (socketIn != null && socketIn.ready()) {
			//	System.out.println(socketIn.readLine());
			//}
			int newThrottleLevel = getNewThrottleLevel();
			if (newThrottleLevel == -1){
				return;
			}
			
			if (newThrottleLevel != throttleLevel) {
				throttleLevel = newThrottleLevel;
				System.out.print((new java.util.Date()).toString() + " ");
				System.out.printf("Setting intensity to %d because %s\n", newThrottleLevel, throttleReason);
				setIntensity(newThrottleLevel);
			}
			
			Thread.sleep(TIMER_LEN);
		}
	}
	
	private boolean parseArgs(String[] args) throws IndexOutOfBoundsException, NumberFormatException {
		if (args.length == 0) {
			return false;
		}
		
		for (int i = 0; i < args.length; i++){
			if (args[i].equals("--help") || args[i].equals("-h")){
				return false;
			} else if (args[i].equals("--host") || args[i].equals("-h")){
				HOST = args[i + 1];
				i++;
			} else if (args[i].equals("--port") || args[i].equals("-p")){
				PORT = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("--polltimer") || args[i].equals("-pt")){
				TIMER_LEN = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("--idletimer") || args[i].equals("-it")){
				IDLE_TIMEOUT = Long.parseLong(args[i + 1]) * 1000 * 60;
				i++;
			} else if (args[i].equals("--throttler") || args[i].equals("-t")){
				Throttler throttler = new Throttler();
				throttler.process = args[i + 1].toUpperCase();
				throttler.intensity = Integer.parseInt(args[i + 2]);
				throttlers.add(throttler);
				i += 2;
			} else if (args[i].equals("--idleintensity") || args[i].equals("-ii")){
				INTENSITY_IDLE = Integer.parseInt(args[i+1]);
				i++;
			} else if (args[i].equals("--inuseintensity") || args[i].equals("-iui")){
				INTENSITY_INUSE = Integer.parseInt(args[i+1]);
				i++;
			} else {
				System.out.println("Unknown option: " + args[i]);
				return false;
			}
		}
		
		return true;
	}
	
	private static void printUsage(){
		System.out.println("Usage: java CGMinerManager [options]");
		System.out.println("  --help");
		System.out.println("      Displays this help.");
		System.out.println("  --host | -h <host>");
		System.out.println("      IP of the the CGMiner to interface with.  Default: \"localhost\"");
		System.out.println("  --port | -p <port#>");
		System.out.println("      Port of the CGMiner to interface with.  Default: 4028");
		System.out.println("  --polltimer | -pt <ms>");
		System.out.println("      Polling frequency in milliseconds.  Default: 5000");
		System.out.println("  --idletimer | -it <sec>");
		System.out.println("      How long the computer needs to be idle before switching to the idle");
		System.out.println("      intensity in seconds.  Default: 1800 (30 minutes)");
		System.out.println("  --idleintensity | -ii <intensity>");
		System.out.println("      Intensity to use while the computer is idle.  Default: 20");
		System.out.println("  --inuseintensity | -iui <intensity>");
		System.out.println("      Intensity to use while the computer is in use.  Default: 17");
		System.out.println("  --throttler | -t <process> <intensity>");
		System.out.println("      Drops intensity to the specified value when the named process is running.");
		System.out.println("      Note that the process name is NOT case-sensitive.");
		System.out.println();
		System.out.println("Example: java CGMinerManager -pt 1000 -it 15 -ii 19 -iui 16 -t game1.exe 13 -t game2.exe 9");		
	}
	
	private int getNewThrottleLevel() {
		int newThrottleLevel = INTENSITY_INUSE;
		throttleReason = "computer is in use.";
		
		Point currPoint = MouseInfo.getPointerInfo().getLocation();
		if (currPoint.x != lastPoint.x || currPoint.y != lastPoint.y){
			idleStart = System.currentTimeMillis();
			newThrottleLevel = INTENSITY_INUSE;
			throttleReason = "computer is in use.";
		} else if (System.currentTimeMillis() - idleStart > IDLE_TIMEOUT) {
			newThrottleLevel = INTENSITY_IDLE;
			throttleReason = "computer is idle.";
		}
		lastPoint = currPoint;
		
		ArrayList<String> processes = getRunningProcesses();
		if (processes == null) {
			return -1;
		}
		for(String process : processes){
			for (Throttler throttler : throttlers){
				if (throttler.process.equals(process) && throttler.intensity < newThrottleLevel) {
					newThrottleLevel = throttler.intensity;
					throttleReason = throttler.process + " is running.";
				}
			}
		}
		
		return newThrottleLevel;
	}
	
	private void setIntensity(int intensity) {
		try {
			socket = new Socket(HOST, PORT);
			socketOut = new PrintWriter(socket.getOutputStream(), true);
			socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String cmd = "{\"command\":\"gpuintensity\",\"parameter\":\"0," + Integer.toString(intensity) + "\"}";
			//System.out.println(cmd);
			socketOut.println(cmd);
		} catch (Exception e) {
			System.out.printf("Error setting intensity: %s", e.toString());
		}
	}

	private ArrayList<String> getRunningProcesses() {
		ArrayList<String> processes = new ArrayList<String>();
		try {
	        String line;
	        if (System.getProperty("os.name").contains("Windows")) {
	        	Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /fo csv /nh");
	        	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        while ((line = input.readLine()) != null) {
		            line = line.split("\"")[1].toUpperCase();
		            processes.add(line);
		        }
		        input.close();
	        } else {
	        	Process p = Runtime.getRuntime().exec("ps -e | awk '{print $4}'");
	        	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        while ((line = input.readLine()) != null) {
		            processes.add(line);
		        }
		        input.close();
	        }
	        
	    } catch (Exception err) {
	    	System.out.println("Error retrieving running processes:");
	        err.printStackTrace();
	        return null;
	    }

		return processes;
	}
}
