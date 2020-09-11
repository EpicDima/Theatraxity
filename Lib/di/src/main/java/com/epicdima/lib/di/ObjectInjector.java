package com.epicdima.lib.di;

import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.exceptions.CreateObjectInjectorException;
import com.epicdima.lib.di.exceptions.InitializeInjectorException;
import com.epicdima.lib.di.exceptions.InjectObjectInjectorException;
import com.epicdima.lib.di.exceptions.InjectorException;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.epicdima.lib.di.AnnotationsHelper.*;
import static com.epicdima.lib.di.ReflectionUtils.*;

/**
 * @author EpicDima
 */
public final class ObjectInjector {
    private static Class<?> componentClass;
    private final Context context = new Context();

    private ObjectInjector() {
        try {
            initialization();
        } catch (ReflectiveOperationException e) {
            throw new InitializeInjectorException(e);
        }
    }

    public static void setComponentClass(Class<?> componentClass) {
        if (ObjectInjector.componentClass == null) {
            ObjectInjector.componentClass = componentClass;
        }
    }

    public static <T> T create(Class<T> implementationClass) {
        try {
            T object = implementationClass.getConstructor().newInstance();
            Holder.INJECTOR.injectWithExceptions(object);
            invokePostConstruct(implementationClass, object);
            return object;
        } catch (ReflectiveOperationException e) {
            throw new CreateObjectInjectorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createOrReturn(Class<T> implementationClass) {
        try {
            return (T) Holder.INJECTOR.createObjectForInjection(implementationClass, Named.DEFAULT_NAME);
        } catch (ReflectiveOperationException e) {
            throw new CreateObjectInjectorException(e);
        }
    }

    public static void inject(Object object) {
        try {
            Holder.INJECTOR.injectWithExceptions(object);
        } catch (ReflectiveOperationException e) {
            throw new InjectObjectInjectorException(e);
        }
    }

    private void initialization() throws ReflectiveOperationException {
        for (Class<?> module : getAllModulesFromComponent(componentClass)) {
            context.modules.put(module, module.getConstructor().newInstance());
            findClassImplementations(module);
            findProvidableMethods(module);
        }
    }

    private void findClassImplementations(Class<?> module) {
        for (Class<?> implemenation : getImplementationsFromModule(module)) {
            context.availableImplementations
                    .computeIfAbsent(getNameFromAnnotationElement(implemenation), s -> new HashSet<>())
                    .add(implemenation);
        }
    }

    private void findProvidableMethods(Class<?> module) {
        for (Method method : getProvidesMethodsFromModule(module)) {
            context.providableImplementations
                    .computeIfAbsent(getNameFromAnnotationElement(method), k -> new HashMap<>())
                    .put(method.getReturnType(), method);
        }
    }

    private void injectWithExceptions(Object object) throws ReflectiveOperationException {
        for (Field field : getAllFieldsForInject(object.getClass())) {
            setValueToField(object, field,
                    createObjectForInjection(field.getType(),
                            getNameFromAnnotationElement(field)));
        }
        for (Method method : getPublicMethodsForInject(object.getClass())) {
            method.invoke(object, createObjectsForParameters(method));
        }
    }

    private Object[] createObjectsForParameters(Executable executable) throws ReflectiveOperationException {
        int length = executable.getParameterCount();
        Parameter[] parameters = executable.getParameters();
        Object[] parametersObjects = new Object[length];
        for (int i = 0; i < length; i++) {
            parametersObjects[i] = createObjectForInjection(parameters[i].getType(),
                    getNameFromAnnotationElement(parameters[i]));
        }
        return parametersObjects;
    }

    private Object createObjectForInjection(Class<?> type, String name) throws ReflectiveOperationException {
        Map<Class<?>, Object> namedSingletons = context.singletons.get(name);
        Object object = null;
        if (namedSingletons != null) {
            object = namedSingletons.get(type);
        }
        if (object == null) {
            object = getImplementation(type, name);
            if (object == null) {
                throw new InjectorException(String.format("Correct implementation class for type '%s' %sis not found",
                        type, Named.DEFAULT_NAME.equals(name) ? "" : String.format("with @Named annotation with value '%s' ", name)));
            }
            addSingleton(object.getClass(), type, name, object);
            inject(object);
            invokePostConstruct(object.getClass(), object);
        }
        return object;
    }

    private Object getImplementation(Class<?> type, String name) throws ReflectiveOperationException {
        Object object = null;
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            object = createAvailableImplementationObject(type, name);
        }
        if (object == null) {
            Map<Class<?>, Method> classMethodMap = context.providableImplementations.get(name);
            if (classMethodMap != null) {
                Method method = classMethodMap.get(type);
                if (method != null) {
                    object = invokeProvidesMethod(type, name, method);
                }
            }
        }
        if (object == null) {
            object = createInstance(type);
        }
        return object;
    }

    private Object createAvailableImplementationObject(Class<?> type, String name) throws ReflectiveOperationException {
        for (Class<?> availableImplementation : context.availableImplementations
                .getOrDefault(name, Collections.emptySet())) {
            for (Class<?> availableClass : getAllInterfacesAndSuperClasses(availableImplementation)) {
                if (type.equals(availableClass)) {
                    return createInstance(availableImplementation);
                }
            }
        }
        return null;
    }

    private Object invokeProvidesMethod(Class<?> type, String name, Method method)
            throws ReflectiveOperationException {
        Object module = context.modules.get(method.getDeclaringClass());
        Object object = method.invoke(module, createObjectsForParameters(method));
        addSingleton(method, type, name, object);
        return object;
    }

    private void addSingleton(AnnotatedElement annotatedElement, Class<?> type, String name, Object object) {
        if (isSingleton(annotatedElement)) {
            context.singletons
                    .computeIfAbsent(name, cls -> new HashMap<>())
                    .put(type, object);
        }
    }

    private Object createInstance(Class<?> implementationClass) throws ReflectiveOperationException {
        Constructor<?> constructor = getPublicConstructorForInject(implementationClass);
        if (constructor != null) {
            return constructor.newInstance(createObjectsForParameters(constructor));
        }
        try {
            return implementationClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException e) {
            return null;
        }
    }


    private static final class Holder {
        public static final ObjectInjector INJECTOR = new ObjectInjector();
    }


    private static final class Context {
        public final Map<Class<?>, Object> modules = new HashMap<>();
        public final Map<String, Set<Class<?>>> availableImplementations = new HashMap<>();
        public final Map<String, Map<Class<?>, Method>> providableImplementations = new HashMap<>();

        public final Map<String, Map<Class<?>, Object>> singletons = new ConcurrentHashMap<>();
    }
}
