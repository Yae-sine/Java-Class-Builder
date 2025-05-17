package model;

/**
 * Represents a parameter for methods and constructors
 */
public class Parameter {
    private String name;
    private String type;
    private boolean isFinal;
    
    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
        this.isFinal = false;
    }
    
    public Parameter(String name, String type, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    
    public String toJavaCode() {
        StringBuilder sb = new StringBuilder();
        if (isFinal) {
            sb.append("final ");
        }
        sb.append(type).append(" ").append(name);
        return sb.toString();
    }
} 