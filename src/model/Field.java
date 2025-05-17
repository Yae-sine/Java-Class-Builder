package model;

/**
 * Represents a field/attribute in a Java class
 */
public class Field extends ClassElement {
    private String type;
    private boolean isFinal;
    private boolean isStatic;
    private String initialValue;
    
    public Field(String name, String type, String visibility) {
        super(name, visibility);
        this.type = type;
        this.isFinal = false;
        this.isStatic = false;
        this.initialValue = null;
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
    
    public boolean isStatic() {
        return isStatic;
    }
    
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    public String getInitialValue() {
        return initialValue;
    }
    
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
    
    @Override
    public String toJavaCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVisibility()).append(" ");
        
        if (isStatic) {
            sb.append("static ");
        }
        
        if (isFinal) {
            sb.append("final ");
        }
        
        sb.append(type).append(" ").append(getName());
        
        if (initialValue != null && !initialValue.isEmpty()) {
            sb.append(" = ").append(initialValue);
        }
        
        sb.append(";");
        return sb.toString();
    }
} 