@echo off 
REM Project name: Windows下的java启动脚本 
REM Author:       chenjw 
REM Date:         2013-8-19 
REM Version:      1.0 
 

SET CLSPATH=%CLASSPATH% 
 
FOR %%c IN (lib\*.jar) DO call :addcp %%c

java -cp "%CLSPATH%" com.chenjw.imagegrab.StartMain


:addcp
echo %1
set CLSPATH=%CLSPATH%;%1
goto :eof

