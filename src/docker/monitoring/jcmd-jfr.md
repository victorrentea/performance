
### Start and auto-stop time-based
jcmd 5368 JFR.start duration=60s filename=myrecording.jfr

### Start and later manual stop 
jcmd PID JFR.start  filename=myrecording2.jfr

jcmd PID JFR.stop name=1
(name is printed by the JFR.start command. Typical 1, 2, ...)

### Creating a Recording On Exit
-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true,dumponexitpath=path
as start-up parameters

More: 
- https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/run.htm#JFRUH179
- https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/comline.htm#JFRUH191