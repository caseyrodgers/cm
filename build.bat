set "JAVA_HOME=C:\java\java-se-8u44-ri"
set "PATH=%JAVA_HOME%\bin;%PATH%"

mvn gwt:compile -Dgwt.module=hotmath.gwt.cm.CatchupMath -Dgwt.compiler.force=true -o
