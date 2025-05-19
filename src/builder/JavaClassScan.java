package builder;

import model.Field;
import model.Method;
import exception.InvalidNameException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaClassScan {

    public static JavaClassBuilder scan(String source) throws InvalidNameException {
        String[] lines = source.split("\\r?\\n");

        String className = null;
        boolean isAbstract = false;
        boolean isInterface = false;
        String extendsClass = null;
        List<String> implementsInterfaces = new ArrayList<>();

        List<Field> fields = new ArrayList<>();
        List<Method> methods = new ArrayList<>();

        // Regex patterns
        Pattern classPattern = Pattern.compile(
                "\\s*(public\\s+)?(abstract\\s+)?(interface|class)\\s+(\\w+)(\\s+extends\\s+(\\w+))?(\\s+implements\\s+([\\w,\\s]+))?\\s*\\{?");

        Pattern fieldPattern = Pattern.compile(
                "\\s*(public|protected|private)?\\s*(static\\s+)?(final\\s+)?([\\w<>\\[\\]]+)\\s+(\\w+)\\s*(=\\s*[^;]+)?;");

        Pattern methodPattern = Pattern.compile(
                "\\s*(public|protected|private)?\\s*(static\\s+)?(abstract\\s+)?([\\w<>\\[\\]]+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{|;).*");

        for (String line : lines) {
            line = line.trim();

            // Parse class declaration
            if (className == null) {
                Matcher cm = classPattern.matcher(line);
                if (cm.find()) {
                    isAbstract = cm.group(2) != null && cm.group(2).contains("abstract");
                    isInterface = "interface".equals(cm.group(3));
                    className = cm.group(4);
                    extendsClass = cm.group(6);
                    if (cm.group(8) != null) {
                        String[] impls = cm.group(8).split(",");
                        for (String impl : impls) {
                            implementsInterfaces.add(impl.trim());
                        }
                    }
                    continue;
                }
            }

            // Parse fields
            Matcher fm = fieldPattern.matcher(line);
            if (fm.find()) {
                String visibility = fm.group(1) == null ? "" : fm.group(1);
                String type = fm.group(4);
                String name = fm.group(5);

                Field field = new Field(name, type, visibility);
                fields.add(field);
                continue;
            }

            // Parse methods (signature only)
            Matcher mm = methodPattern.matcher(line);
            if (mm.find()) {
                String visibility = mm.group(1) == null ? "" : mm.group(1);
                boolean isStatic = mm.group(2) != null && mm.group(2).contains("static");
                boolean isAbstractMethod = mm.group(3) != null && mm.group(3).contains("abstract");
                String returnType = mm.group(4);
                String methodName = mm.group(5);
                // Parameters parsing omitted for brevity; can be added later

                Method method = new Method(methodName, returnType, visibility);
                method.setStatic(isStatic);
                method.setAbstract(isAbstractMethod);
                methods.add(method);
            }
        }

        if (className == null) {
            throw new InvalidNameException("Could not find class or interface declaration");
        }

        JavaClassBuilder builder = new JavaClassBuilder(className);
        builder.setAbstract(isAbstract);
        builder.setInterface(isInterface);
        builder.setExtendsClass(extendsClass);
        for (String impl : implementsInterfaces) {
            builder.addImplementsInterface(impl);
        }

        // Add parsed fields and methods
        for (Field f : fields) builder.addField(f);
        for (Method m : methods) builder.addMethod(m);

        return builder;
    }
}
