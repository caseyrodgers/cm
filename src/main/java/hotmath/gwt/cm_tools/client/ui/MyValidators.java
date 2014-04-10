package hotmath.gwt.cm_tools.client.ui;

public enum MyValidators {
    ALPHABET("^[a-zA-Z_]+$", "alphabet"), 
    ALPHANUMERIC("^[a-zA-Z0-9_]+$", "alphanumeric"), 
    INTEGER("^[+0-9\\-]+$", "integer"),
    EMAIL("^(\\w+)([-+.][\\w]+)*@(\\w[-\\w]*\\.){1,5}([A-Za-z]){2,4}$", "email"), 
    DECIMAL("^[\\.0-9-]\\d*(\\.\\d+)?$", "decimal");
    String regex;
    String name;

    MyValidators(String regex, String name) {
      this.regex = regex;
      this.name = name;
    }
  }