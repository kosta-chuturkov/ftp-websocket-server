set FTP_CONFIG_HOME=%FTP_HOME%\conf
echo "FTP_CONFIG_HOME is set to %FTP_CONFIG_HOME%"

set TOMCAT_HOME=%FTP_HOME%\servers\tomcat
echo "TOMCAT_HOME is set to %TOMCAT_HOME%"

set CATALINA_HOME=%TOMCAT_HOME%

set JAVA_HOME=%JAVA_HOME%
echo "JAVA_HOME is set to %JAVA_HOME%"

set JAVA_OPTS=-Xmx2048m -Xms512m -XX:PermSize=1512m
set JAVA_OPTS=%JAVA_OPTS% -Dlog4j.debug
set JAVA_OPTS=%JAVA_OPTS% -Dftp.log.dir=%FTP_HOME%\logs
set JAVA_OPTS=%JAVA_OPTS% -Dftp.home=%FTP_HOME%

rem set JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configuration=file:%FTP_HOME%\conf\log4j.xml
