call mvn clean package
rem java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder "-XX:StartFlightRecording=name=\"LeaksApp_8080_2021_04_27_122857\",settings=\"default\",dumponexit=true,filename=\"C:\Users\victo\javaFlightRecorder.jfr\"" -XX:FlightRecorderOptions=stackdepth=2048 -XX:TieredStopAtLevel=1  -jar target\benchmarks.jar
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder "-XX:StartFlightRecording=name=\"LeaksApp_8080_2021_04_27_122857\",settings=\"default\",dumponexit=true,filename=\"C:\Users\victo\javaFlightRecorder.jfr\"" -XX:FlightRecorderOptions=stackdepth=2048 -XX:TieredStopAtLevel=1  -jar target\benchmarks.jar

rem java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -jar target\benchmarks.jar > out.txt
rem to see generated assembly code, you need to put in your jre/bin/server a dll generated here: https://dropzone.nfshost.com/hsdis/

