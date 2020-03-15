call mvn clean package
java -jar target\benchmarks.jar
rem java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -jar target\benchmarks.jar > out.txt
rem to see generated assembly code, you need to put in your jre/bin/server a dll generated here: https://dropzone.nfshost.com/hsdis/

