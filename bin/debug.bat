cd /d %~dp0
cd..
set FTP_HOME=%cd%
echo "FTP_HOME is set to %FTP_HOME%"

call %FTP_HOME%\bin\common.bat

set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8077

cd "%TOMCAT_HOME%"
rem call %TOMCAT_HOME%\bin\catalina.bat jpda run
call %TOMCAT_HOME%\bin\catalina.bat jpda start
