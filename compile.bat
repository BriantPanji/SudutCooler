@echo off
REM Compile script for Windows
javac -d bin -cp "lib/*" src/database/*.java src/model/*.java src/dao/*.java src/gui/*.java src/Main.java
