import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionClass {
    public List<String> getMethodNames(Object object){
        Class<?> objectClass = object.getClass();
        List<String> methods = new ArrayList<>();
        for (Method declaredMethod : objectClass.getDeclaredMethods()) {
            methods.add(declaredMethod.getName());
        }
        return methods;
    }
    public Object getFieldContent(Object object, String fieldName) throws Exception{
        Class<?> objectClass = object.getClass();
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
    public void setFieldContent(Object object, String fieldName, Object content) throws Exception{
        Class<?> objectClass = object.getClass();
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        try {
            field.set(object, content);
        } catch (IllegalArgumentException e){

        }
    }
    public Object call(Object object, String methodName, Object[] parameters) throws Exception{
        Class<?> objectClass = object.getClass();
        Method method = null;
        for (Method declaredMethod : objectClass.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(methodName))
                method = declaredMethod;
        }
        if (method == null)
            throw new NoSuchMethodException();
        method.setAccessible(true);
        Object obj;
        try {
            obj = method.invoke(object, parameters);
        } catch (IllegalArgumentException e){
            throw new NoSuchMethodException();
        }
        return obj;
    }
    public Object createANewObject(String fullClassName, Object[] initials) throws Exception{
        Class<?> objectClass = Class.forName(fullClassName);
        Constructor<?> constructor = null;
        for (Constructor<?> declaredConstructor : objectClass.getDeclaredConstructors()) {
            if (declaredConstructor.getParameterCount() == initials.length)
                constructor = declaredConstructor;
        }
        if (constructor == null)
            throw new NoSuchMethodException();
        constructor.setAccessible(true);
        Object obj;
        try {
            obj = constructor.newInstance(initials);
        } catch (IllegalArgumentException e){
            throw new NoSuchMethodException();
        }
        return obj;
    }
    public String debrief(Object object){
        StringBuilder stringBuilder = new StringBuilder();
        Class<?> objectClass = object.getClass();
        stringBuilder.append("Name: " + objectClass.getSimpleName() + "\n");
        stringBuilder.append("Package: " + objectClass.getPackage().getName() + "\n");
        stringBuilder.append("No. of Constructors: " + objectClass.getDeclaredConstructors().length);
        stringBuilder.append("\n===\nFields:\n");
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));
        Comparator<Field> fieldComparator = Comparator.comparing(Field::getName);
        List<Field> sortedFields = fields.stream().sorted(fieldComparator).collect(Collectors.toList());
        for (Field declaredField : sortedFields) {
            int mod = declaredField.getModifiers();
            String fieldString = ((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + declaredField.getType().getSimpleName() + " "
                    + declaredField.getName() + "\n";
            stringBuilder.append(fieldString);
        }
        stringBuilder.append("(" + objectClass.getDeclaredFields().length + " fields)\n===\nMethods:\n");
        ArrayList<Method> methods = new ArrayList<>(Arrays.asList(objectClass.getDeclaredMethods()));
        Comparator<Method> methodComparator = Comparator.comparing(Method::getName);
        List<Method> sortedMethods = methods.stream().sorted(methodComparator).collect(Collectors.toList());
        for (Method declaredMethod : sortedMethods) {
            StringJoiner sj = new StringJoiner(", ", declaredMethod.getReturnType().getSimpleName() + " " + declaredMethod.getName() + "(", ")");
            for (Class<?> parameterType : declaredMethod.getParameterTypes()) {
                sj.add(parameterType.getSimpleName());
            }
            stringBuilder.append(sj + "\n");
        }
        stringBuilder.append("(" + objectClass.getDeclaredMethods().length + " methods)");
        return stringBuilder.toString();
    }

    private void copyFields(Object toClone, Object copied, Class<?> objectClass){
        for (Field declaredField : objectClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                if (Modifier.isFinal(declaredField.getModifiers())) continue;
                if (declaredField.getType().isPrimitive())
                    declaredField.set(copied, declaredField.get(toClone));
                else
                    declaredField.set(copied, clone(declaredField.get(toClone)));
            } catch (Exception ignored) {
            }
        }
    }
    public Object clone(Object toClone){
        Class<?> objectClass = toClone.getClass();
        Object copied = null;
        for (Constructor<?> constructor : objectClass.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            try {
                copied = constructor.newInstance();
            } catch (Exception ignored){
            }
        }
        while (objectClass != null){
            copyFields(toClone, copied, objectClass);
            objectClass = objectClass.getSuperclass();
        }
        return copied;
    }

}

