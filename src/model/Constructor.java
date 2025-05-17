package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a constructor in a Java class
 */
public class Constructor extends ClassElement {
    private List<Parameter> parameters;
    private String body;
    
    public Constructor(String name, String visibility) {
        super(name, visibility);
        this.parameters = new ArrayList<>();
        this.body = "";
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
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
        
        // Visibility + name
        sb.append(getVisibility()).append(" ").append(getName()).append("(");
        
        // Parameters
        if (!parameters.isEmpty()) {
            String parameterList = parameters.stream()
                    .map(Parameter::toJavaCode)
                    .collect(Collectors.joining(", "));
            sb.append(parameterList);
        }
        
        sb.append(") {\n");
        
        // Body
        if (body != null && !body.isEmpty()) {
            sb.append("    ").append(body.replace("\n", "\n    ")).append("\n");
        }
        
        sb.append("}");
        
        return sb.toString();
    }
} 