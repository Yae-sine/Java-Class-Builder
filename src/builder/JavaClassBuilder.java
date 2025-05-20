package builder;

import java.util.ArrayList;
import java.util.List;
import model.Constructor;
import model.Field;
import model.Method;

/**
 * Core builder class responsible for creating Java class files
 */
public class JavaClassBuilder {
    private String className;
    private String packageName;
    private List<Field> fields;
    private List<Method> methods;
    private List<Constructor> constructors;
    private List<String> imports;
    private boolean isAbstract;
    private boolean isInterface;
    private String extendsClass;
    private List<String> implementsInterfaces;
    
    public JavaClassBuilder(String className) {
        this.className = className;
        this.packageName = ""; // Default empty package
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.imports = new ArrayList<>();
        this.isAbstract = false;
        this.isInterface = false;
        this.extendsClass = null;
        this.implementsInterfaces = new ArrayList<>();
    }
    
    // Getters and setters
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public void addField(Field field) {
        fields.add(field);
    }
    
    public void removeField(Field field) {
        fields.remove(field);
    }
    
    public Field getFieldByName(String name) {
        return fields.stream()
                .filter(field -> field.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public void addMethod(Method method) {
        methods.add(method);
    }
    
    public void removeMethod(Method method) {
        methods.remove(method);
    }
    
    public Method getMethodByName(String name) {
        return methods.stream()
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public List<Method> getMethods() {
        return methods;
    }
    
    public void addConstructor(Constructor constructor) {
        constructors.add(constructor);
    }
    
    public void removeConstructor(Constructor constructor) {
        constructors.remove(constructor);
    }
    
    public List<Constructor> getConstructors() {
        return constructors;
    }
    
    public void addImport(String importStatement) {
        imports.add(importStatement);
    }
    
    public void removeImport(String importStatement) {
        imports.remove(importStatement);
    }
    
    public List<String> getImports() {
        return imports;
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    
    public boolean isInterface() {
        return isInterface;
    }
    
    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
    
    public String getExtendsClass() {
        return extendsClass;
    }
    
    public void setExtendsClass(String extendsClass) {
        this.extendsClass = extendsClass;
    }
    
    public List<String> getImplementsInterfaces() {
        return implementsInterfaces;
    }
    
    public void addImplementsInterface(String interfaceName) {
        implementsInterfaces.add(interfaceName);
    }
    
    public void removeImplementsInterface(String interfaceName) {
        implementsInterfaces.remove(interfaceName);
    }

    public JavaClassBuilder clone() {
        JavaClassBuilder copy = new JavaClassBuilder(this.className);
        copy.packageName = this.packageName;
        // Deep copy fields
        for (Field f : this.fields) {
            Field fCopy = new Field(f.getName(), f.getType(), f.getVisibility());
            fCopy.setFinal(f.isFinal());
            fCopy.setStatic(f.isStatic());
            fCopy.setInitialValue(f.getInitialValue());
            copy.fields.add(fCopy);
        }
        // Deep copy methods
        for (Method m : this.methods) {
            Method mCopy = new Method(m.getName(), m.getReturnType(), m.getVisibility());
            mCopy.setStatic(m.isStatic());
            mCopy.setAbstract(m.isAbstract());
            mCopy.setBody(m.getBody());
            for (model.Parameter p : m.getParameters()) {
                model.Parameter pCopy = new model.Parameter(p.getName(), p.getType(), p.isFinal());
                mCopy.addParameter(pCopy);
            }
            copy.methods.add(mCopy);
        }
        // Deep copy constructors
        for (model.Constructor c : this.constructors) {
            model.Constructor cCopy = new model.Constructor(c.getName(), c.getVisibility());
            cCopy.setBody(c.getBody());
            for (model.Parameter p : c.getParameters()) {
                model.Parameter pCopy = new model.Parameter(p.getName(), p.getType(), p.isFinal());
                cCopy.addParameter(pCopy);
            }
            copy.constructors.add(cCopy);
        }
        copy.imports = new ArrayList<>(this.imports);
        copy.isAbstract = this.isAbstract;
        copy.isInterface = this.isInterface;
        copy.extendsClass = this.extendsClass;
        copy.implementsInterfaces = new ArrayList<>(this.implementsInterfaces);
        return copy;
    }

    public String buildClass() {
        StringBuilder sb = new StringBuilder();
        

        if (packageName != null && !packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Import
        if (!imports.isEmpty()) {
            for (String importStmt : imports) {
                sb.append("import ").append(importStmt).append(";\n");
            }
            sb.append("\n");
        }
        
        // Class
        sb.append("/**\n");
        sb.append(" * ").append(className).append("\n");
        sb.append(" */\n");
        
        sb.append("public ");
        
        if (isAbstract) {
            sb.append("abstract ");
        }
        
        if (isInterface) {
            sb.append("interface ");
        } else {
            sb.append("class ");
        }
        
        sb.append(className);
        
        // Extends
        if (extendsClass != null && !extendsClass.isEmpty()) {
            sb.append(" extends ").append(extendsClass);
        }
        
        // Implements
        if (!implementsInterfaces.isEmpty() && !isInterface) {
            sb.append(" implements ");
            sb.append(String.join(", ", implementsInterfaces));
        }
        
        sb.append(" {\n");
        
        // Fields
        if (!fields.isEmpty()) {
            for (Field field : fields) {
                sb.append("    ").append(field.toJavaCode()).append("\n");
            }
            sb.append("\n");
        }
        
        // Constructors
        if (!constructors.isEmpty()) {
            for (Constructor constructor : constructors) {
                sb.append("    ").append(constructor.toJavaCode().replace("\n", "\n    ")).append("\n\n");
            }
        } else if (!isInterface && constructors.isEmpty()) {
            // Default constructor if none provided
            sb.append("    public ").append(className).append("() {\n    }\n\n");
        }
        
        // Methods
        if (!methods.isEmpty()) {
            for (Method method : methods) {
                sb.append("    ").append(method.toJavaCode().replace("\n", "\n    ")).append("\n\n");
            }
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}