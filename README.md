About
=====

CGMinerManager v0.2.2 by Sohcahtoa82

Binary at https://www.dropbox.com/s/mj9ggdt7f2pkf1p/CGMinerManager_v0.2.2.zip

CGMinerManager (Hereafter abbreviated "CM") is a program for automatically adjusting the intensity of cgminer's mining based on running programs and whether or not the computer is in use, designed for people who want to be able to use their computer and even play games while mining without manually having to remember to adjust cgminer's intensity and maximizing the use of the GPU while the computer is not in use.

https://github.com/Sohcahtoa82/CGMinerManager for source code and detailed instructions.

CGMinerManager is being written by a broke university student.  Please consider donating to any of these addresses:  
Bitcoin: 19Mz5onCDfvKwoHUBEeiVdbhuuQQh989yf  
Dogecoin: DHjpPYZBCr92T9kXQnwjqjw2Jfjeix4eHB  
Litecoin: LUPUMKvbWYgxcznJNh5mwB2svk8G3FpyyF  

Basic Usage
===========

Usage: java -jar CGMinerManager.jar [options]  
  --help  
      Displays this help.  
  --host | -h <host>  
      IP of the the CGMiner to interface with.  Default: "localhost"  
  --port | -p <port#>  
      Port of the CGMiner to interface with.  Default: 4028  
  --polltimer | -pt <ms>  
      Polling frequency in milliseconds.  Default: 5000  
  --idletimer | -it <sec>  
      How long the computer needs to be idle before switching to the idle  
      intensity in seconds.  Default: 1800 (30 minutes)  
  --idleinensity | -ii <intensity>  
      Intensity to use while the computer is idle.  Default: 20  
  --inuseintensity | -iui <intensity>  
      Intensity to use while the computer is in use.  Default: 17  
  --fullscreen | -fs <intensity> <x> <y>
      Enable the use of intensity adjustment when a full--screen program is
	  detected.  If this option is not specified, this feature will be
	  disabled.  See README for more information.
  --throttler | -t <process> <intensity>  
      Drops intensity to the specified value when the named process is running.  
      Note that the process name is NOT case-sensitive.  

Detailed Instructions
=====================

Without any command line options, CM will check every 5 seconds for mouse activity.  If after 30 minutes, the mouse has not moved, CM will set the mining intensity to 20, the maximum value for scrypt mining.  If activity is detected, the intensity is lowered to 17 to make the desktop more responsive while the computer is in use while still mining at high speed.

Using the --idletimer, --inuseintensity, and --idleintensity command line options (short versions -it, -ii, -iui, respectively), the time needed to consider the computer to not be in use can be changed, as well as the intensities to use when the computer is in use and when it is idle.

However, the real power comes from allowing CM to throttle intensity based on what applications are running, which is very useful for gaming while mining.  Multiple throttlers can be added by using the --throttler switch multiple times so that you can add multiple games to the list of applications which will cause throttling.  In addition, the intensity to throttle down to can be adjusted on a per-game basis, so if you have a game that requires very little GPU power, it can be run while cgminer keeps a higher intensity, while if you're playing a game that requires a high amount of GPU power, intensity can be adjusted to a minimum to free up as much GPU time as possible.

Example: java CGMinerManager -pt 1000 -it 15 -ii 19 -iui 16 -t game1.exe 13 -t game2.exe 9

This would make CM poll the running list of applications once per second.  If game2 was running, the intensity would be set to 9.  If game1 was running, the intensity would get set to 13.  If the computer was left idle (No mouse movement) for 15 minutes, the intensity would be set to 19.  Otherwise, the intensity would be set to 16.

Running programs take priority over the idleness of the computer.  Also, the intensity gets set to the LOWEST throttler.  For example, in the example above, if both games were being run at the same time, the intensity would get set to 9.

If the --fullscreen option is specified, then the pixel at location x, y is checked when the program starts.  Note that 0,0 is the top-left corner of the screen, and 1679, 1049 will be the bottom-right corner if you're running a 1680x1050 monitor.  This pixel will be checked on every poll cycle, and if it is different from the value it was when CGMinerManager was launched, then the intensity will be adjusted to the value specified on the command line.  Note that throttling by running application takes priority, so if the full-screen app intensity is set to 12, but you're running MyApp.exe with a "-t MyApp.exe 13" command line, then the intensity will be set to 13.

Known Bugs
==========

- CM has not yet been tested on Linux or Mac.
- Only GPU 0 in cgminer is changed.  If you are running multiple GPUs, the others will continue to run at whatever intensity they were originally set to, and CM will not adjust them.
- The idleness of the computer is measured only by mouse movement.  Keyboard input and mouse clicks without movement will not register as activity.

Change Log
==========

Version 0.2.2 - Released Jan 25, 2014
-------------------------------------
https://www.dropbox.com/s/mj9ggdt7f2pkf1p/CGMinerManager_v0.2.2.zip
- --idletimer/-it command option now properly interprets the value as seconds

Version 0.2.1 - Released Jan 1, 2014
------------------------------------
https://www.dropbox.com/s/e8vctacie79ray5/CGMinerManager_v0.2.1.zip
- Fixed so running applications actually DO take priority over full-screen throttling
- Prints an error and exits if getNewThrottleLevel() fails
- Properly handles when attempting to get the list of running tasks fails
- Intensity will always be set to the "in use" intensity upon starting
- Added a .bat file to load CGMinerManager with default settings

Version 0.2 - Released Dec 31, 2014
-----------------------------------
https://www.dropbox.com/s/wniq3ca5ny9sr7s/CGMinerManager_v0.2.zip
- Added --fullscreen feature to test enable throttling when a full-screen application is running.

Version 0.1 - Released Dec 30, 2014
-----------------------------------
https://www.dropbox.com/s/i39dc98kgwqrly5/CGMinerManager_v0.1.zip
- First public release.
