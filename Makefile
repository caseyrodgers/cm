.PHONY: build

build:
	JAVA_HOME=/c/java/java-se-8u44-ri PATH="/c/java/java-se-8u44-ri/bin:$$PATH" mvn gwt:compile -Dgwt.module=hotmath.gwt.cm.CatchupMath -Dgwt.compiler.force=true -o
