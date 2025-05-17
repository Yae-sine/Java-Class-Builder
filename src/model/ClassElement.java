package model;

/**
 * Abstract base class for all Java class elements
 */
public abstract class ClassElement {
    protected String name;
    protected String visibility;
    
    public ClassElement(String name, String visibility) {
        this.name = name;
        this.visibility = visibility;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVisibility() {
        return visibility;
    }
    
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    
    /**
     * Returns the Java code representation of this element
     */
    public abstract String toJavaCode();
} 