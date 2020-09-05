package com.epicdima.lib.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.*;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author EpicDima
 */
final class AnnotationsHelper {

    private AnnotationsHelper() {
        throw new AssertionError();
    }

    static Set<Class<?>> getAllModulesFromComponent(Class<?> componentClass) {
        verifyComponent(componentClass);
        Class<?>[] modules = componentClass.getAnnotation(Component.class).modules();
        Set<Class<?>> modulesSet = new HashSet<>(Arrays.asList(modules));
        for (Class<?> module : modules) {
            verifyModule(module);
            getSubmodules(module, modulesSet);
        }
        return modulesSet;
    }

    private static void verifyComponent(Class<?> componentClass) {
        if (!componentClass.isAnnotationPresent(Component.class)) {
            throw new IllegalArgumentException(String.format("Type '%s' doesn't have a @Component annotation. Need type with @Component annotation", componentClass));
        }
    }

    private static void verifyModule(Class<?> moduleClass) {
        if (!moduleClass.isAnnotationPresent(Module.class)) {
            throw new IllegalArgumentException(String.format("Type '%s' doesn't have a @Module annotation. Need type with @Module annotation", moduleClass));
        }
        if (moduleClass.isInterface() || Modifier.isAbstract(moduleClass.getModifiers()) || moduleClass.isEnum()) {
            throw new IllegalArgumentException(String.format("Type '%s' with @Module annotation must be instantiated class (not interface or abstract)", moduleClass));
        }
        try {
            Constructor<?> constructor = moduleClass.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) {
                throw new IllegalArgumentException(String.format("Constructor '%s' of class '%s' with @Module annotatio must be public", constructor, moduleClass));
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Class '%s' with @Module annotation must have public default constructor without parameters", moduleClass), e);
        }
    }

    private static void getSubmodules(Class<?> moduleClass, Set<Class<?>> submodules) {
        Set<Class<?>> currentSubmodules = new HashSet<>();
        for (Class<?> submodule : moduleClass.getAnnotation(Module.class).submodules()) {
            verifyModule(submodule);
            if (!currentSubmodules.add(submodule)) {
                throw new IllegalArgumentException(String.format("Type '%s' with @Module annotation has duplicate submodule '%s'", moduleClass, submodule));
            }
        }
        for (Class<?> submodule : currentSubmodules) {
            if (!submodules.add(submodule)) {
                throw new IllegalArgumentException(String.format("Type '%s' with @Module annotation has duplicate submodule '%s'", moduleClass, submodule));
            }
        }
        for (Class<?> submodule : currentSubmodules) {
            getSubmodules(submodule, submodules);
        }
    }

    static List<Class<?>> getImplementationsFromModule(Class<?> moduleClass) {
        List<Class<?>> implementations = new ArrayList<>(
                Arrays.asList(moduleClass.getAnnotation(Module.class).implementations()));
        if (moduleClass.isAnnotationPresent(Scan.class)) {
            implementations.addAll(getImplementationsFromModuleScan(moduleClass));
        }
        return implementations;
    }

    private static List<Class<?>> getImplementationsFromModuleScan(Class<?> moduleClass) {
        Scan scan = moduleClass.getAnnotation(Scan.class);
        Set<String> packages = new HashSet<>();
        for (String packageName : scan.packages()) {
            if (!packages.add(packageName)) {
                throw new IllegalArgumentException(String.format("Module '%s' with annotation @Scan '%s' contains duplicate packages '%s'", moduleClass, scan, packageName));
            }
        }
        List<Class<?>> classes = new ArrayList<>();
        for (String packageName : packages) {
            classes.addAll(getClassesFromPackage(moduleClass, packageName, scan.recursively()));
        }
        return filterImplementationsFromPackage(classes, scan.inner());
    }

    private static List<Class<?>> filterImplementationsFromPackage(List<Class<?>> classes, boolean inner) {
        return classes.stream()
                .filter((cls) -> {
                    boolean isSuitable = Modifier.isPublic(cls.getModifiers())
                            && !cls.isInterface()
                            && !Modifier.isAbstract(cls.getModifiers())
                            && !cls.isEnum()
                            && !cls.isAnnotationPresent(Module.class)
                            && !cls.isAnnotationPresent(Component.class);
                    if (isSuitable) {
                        if (cls.isMemberClass()) {
                            isSuitable = inner;
                        }
                    }
                    return isSuitable;
                })
                .collect(Collectors.toList());
    }

    private static List<Class<?>> getClassesFromPackage(Class<?> moduleClass, String packageName,
                                                        boolean recursively) {
        URL packageUrl = Thread.currentThread().getContextClassLoader()
                .getResource(packageName.replace(".", "/"));
        if (packageUrl == null) {
            throw new IllegalArgumentException(String.format("Module '%s' with annotation @Scan not found package '%s'", moduleClass, packageName));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File file : getFilesFromPackage(packageUrl.getFile(), recursively)) {
            String qualifiedClassName = file.getPath()
                    .replace("\\", ".")
                    .replaceAll(".*(?=" + packageName + ")", "")
                    .replaceAll(".class$", "");
            try {
                classes.add(Class.forName(qualifiedClassName));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(String.format("Package '%s' contains not only classes", packageName), e);
            }
        }
        return classes;
    }

    private static List<File> getFilesFromPackage(String packagePath, boolean recursively) {
        File[] listFiles = new File(packagePath).listFiles();
        if (listFiles == null) {
            return Collections.emptyList();
        }
        List<File> packageFiles = new ArrayList<>();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (recursively) {
                    packageFiles.addAll(getFilesFromPackage(file.getPath(), true));
                }
            } else {
                packageFiles.add(file);
            }
        }
        return packageFiles;
    }

    static List<Method> getProvidesMethodsFromModule(Class<?> moduleClass) {
        if (!moduleClass.isAnnotationPresent(Module.class)) {
            throw new IllegalArgumentException(String.format("Type '%s' doesn't have a @Module annotation. Need type with @Module annotation", moduleClass));
        }
        List<Method> providesMethods = Arrays.stream(moduleClass.getDeclaredMethods())
                .filter((method) -> method.isAnnotationPresent(Provides.class))
                .collect(Collectors.toList());
        for (Method method : providesMethods) {
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalArgumentException(String.format("Method '%s' with annotation @Provides must be public", method));
            }
            if (method.getReturnType().equals(void.class)) {
                throw new IllegalArgumentException(String.format("Method '%s' with annotation @Provides must have return type (not void)", method));
            }
        }
        return providesMethods;
    }

    static String getNameFromAnnotationElement(AnnotatedElement annotatedElement) {
        if (annotatedElement.isAnnotationPresent(Named.class)) {
            return annotatedElement.getAnnotation(Named.class).value();
        }
        return Named.DEFAULT_NAME;
    }

    static Method getPostConstructMethod(Class<?> cls) {
        List<Method> postConstructMethods = Arrays.stream(cls.getDeclaredMethods())
                .filter((method) -> method.isAnnotationPresent(PostConstruct.class))
                .collect(Collectors.toList());
        if (postConstructMethods.size() > 1) {
            throw new IllegalArgumentException(String.format("Class '%s' must not contain more than one method with the @PostConstruct annotation", cls));
        } else if (postConstructMethods.size() == 1) {
            Method method = postConstructMethods.get(0);
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalArgumentException(String.format("Method '%s' with @PostConstruct annotation must be public", method));
            }
            if (method.getParameterCount() != 0) {
                throw new IllegalArgumentException(String.format("Method '%s' with @PostConstruct annotation cannot have parameters", method));
            }
            if (!method.getReturnType().equals(void.class)) {
                throw new IllegalArgumentException(String.format("Method '%s' with @Inject annotation cannot have return type", method));
            }
            return method;
        } else {
            return null;
        }
    }

    static boolean isSingleton(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(Singleton.class);
    }

    static List<Field> getAllFieldsForInject(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        while (!cls.equals(Object.class)) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    fields.add(field);
                }
            }
            cls = cls.getSuperclass();
        }
        return fields.stream().distinct().collect(Collectors.toList());
    }

    static Constructor<?> getPublicConstructorForInject(Class<?> cls) {
        List<Constructor<?>> injectConstructors = Arrays.stream(cls.getDeclaredConstructors())
                .filter((constructor) -> constructor.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
        if (injectConstructors.size() > 1) {
            throw new IllegalArgumentException(String.format("Class '%s' must not contain more than one constructor with the @Inject annotation", cls));
        } else if (injectConstructors.size() == 1) {
            Constructor<?> constructor = injectConstructors.get(0);
            if (!Modifier.isPublic(constructor.getModifiers())) {
                throw new IllegalArgumentException(String.format("Constructor '%s' with @Inject annotation must be public", constructor));
            }
            if (constructor.getParameterCount() == 0) {
                throw new IllegalArgumentException(String.format("Constructor '%s' with @Inject annotation cannot have zero parameters", constructor));
            }
            return constructor;
        } else {
            return null;
        }
    }

    static List<Method> getPublicMethodsForInject(Class<?> cls) {
        List<Method> methods = Arrays.stream(cls.getDeclaredMethods())
                .filter((method) -> method.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalArgumentException(String.format("Method '%s' with @Inject annotation must be public", method));
            }
            if (method.getParameterCount() == 0) {
                throw new IllegalArgumentException(String.format("Method '%s' with @Inject annotation cannot have zero parameters", method));
            }
            if (!method.getReturnType().equals(void.class)) {
                throw new IllegalArgumentException(String.format("Method '%s' with @Inject annotation cannot have return type", method));
            }
        }
        return methods;
    }
}
