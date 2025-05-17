package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a method in a Java class
 */
public class Method extends ClassElement {
    private String returnType;
    private List<Parameter> parameters;
    private boolean isStatic;
    private boolean isAbstract;
    private String body;
    
    public Method(String name, String returnType, String visibility) {
        super(name, visibility);
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.isStatic = false;
        this.isAbstract = false;
        this.body = "";
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }
    
    public boolean isStatic() {
        return isStatic;
    }
    
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    @Override
    public String toJavaCode() {
        StringBuilder sb = new StringBuilder();
        
        // Visibility + modifiers
        sb.append(getVisibility()).append(" ");
        
        if (isStatic) {
            sb.append("static ");
        }
        
        if (isAbstract) {
            sb.append("abstract ");
        }
        
        // Return type + name
        sb.append(returnType).append(" ").append(getName()).append("(");
        
        // Parameters
        if (!parameters.isEmpty()) {
            String parameterList = parameters.stream()
                    .map(Parameter::toJavaCode)
                    .collect(Collectors.joining(", "));
            sb.append(parameterList);
        }
        
        sb.append(")");
        
        // Body or semicolon for abstract methods
        if (isAbstract) {
            sb.append(";");
        } else {
            sb.append(" {\n");
            if (body != null && !body.isEmpty()) {
                sb.append("    ").append(body.replace("\n", "\n    ")).append("\n");
            } else if (!returnType.equals("void")) {
                // Default return for non-void methods
                if (returnType.equals("boolean")) {
                    sb.append("    return false;\n");
                } else if (returnType.equals("int") || returnType.equals("long") || 
                        returnType.equals("byte") || returnType.equals("short") || 
                        returnType.equals("float") || returnType.equals("double")) {
                    sb.append("    return 0;\n");
                } else if (returnType.equals("char")) {
                    sb.append("    return '\\0';\n");
                } else {
                    sb.append("    return null;\n");
                }
            }
            sb.append("}");
        }
        
        return sb.toString();
    }
} 