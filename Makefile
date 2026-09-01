SHELL := C:/PROGRA~1/Git/usr/bin/bash.exe
.SHELLFLAGS := -ec

JDK8_PATH := /c/Program Files/Git/usr/bin:/c/Program Files/Git/mingw64/bin:/c/java/java-se-8u44-ri/bin:$$PATH
# The java-se-8u44-ri install ships with no lib/security/cacerts at all, so any
# non-offline mvn call (anything without -o) fails HTTPS to Central with
# "InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty".
# Borrow a real truststore from the JDK 21 install for such calls.
JDK8_TRUSTSTORE := C:/java/jdk-21.0.1/lib/security/cacerts
JDK8_ENV := PATH="$(JDK8_PATH)" JAVA_HOME=/c/java/java-se-8u44-ri MAVEN_OPTS="-Djavax.net.ssl.trustStore=$(JDK8_TRUSTSTORE) -Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStorePassword=changeit"

.PHONY: build solution_editor run

build:
	$(JDK8_ENV) mvn gwt:compile -Dgwt.module=hotmath.gwt.cm.CatchupMath -Dgwt.compiler.force=true -o

solution_editor:
	$(JDK8_ENV) mvn gwt:compile -Dgwt.module=hotmath.gwt.solution_editor.SolutionEditor -Dgwt.compiler.force=true -o
	cp -r src/main/webapp/solution_editor target/cm-1.0-SNAPSHOT

run:
	$(JDK8_ENV) CM_PROPERTIES=/c/dev/projects/catchupmath/cm/local/cm.properties mvn compile org.mortbay.jetty:maven-jetty-plugin:6.1.10:run
