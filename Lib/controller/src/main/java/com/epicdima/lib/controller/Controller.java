package com.epicdima.lib.controller;

import com.epicdima.lib.controller.annotations.RequestBody;
import com.epicdima.lib.controller.annotations.RequestMapping;
import com.epicdima.lib.controller.annotations.RequestParam;
import com.epicdima.lib.controller.annotations.ResponseBody;
import com.epicdima.lib.controller.exceptions.ControllerAnnotationException;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EpicDima
 */
public abstract class Controller extends HttpServlet {
    protected static Converter defaultConverter = null;

    public static void setDefaultConverter(Converter converter) {
        defaultConverter = Objects.requireNonNull(converter);
    }

    private String unnecessaryPartOfUri;
    private boolean async = false;

    private final Map<Class<Converter>, Converter> converters = new ConcurrentHashMap<>();
    private final Map<RequestMethod, Map<String, Map<RequestMappingSignature, Method>>> requestMappingTable = new ConcurrentHashMap<>();

    @Override
    public void init() {
        Class<?> cls = getClass();
        if (!cls.isAnnotationPresent(WebServlet.class)) {
            throw new ControllerAnnotationException("Controller must be annotated with @WebServlet");
        }
        if (defaultConverter == null) {
            throw new ControllerAnnotationException("Controller's default converter cannot be equals null");
        }
        WebServlet webServlet = cls.getAnnotation(WebServlet.class);
        if (webServlet.value().length > 0) {
            unnecessaryPartOfUri = webServlet.value()[0];
        } else if (webServlet.urlPatterns().length > 0) {
            unnecessaryPartOfUri = webServlet.urlPatterns()[0];
        } else {
            throw new ControllerAnnotationException("WebServlet must be specified with value or urlPatterns");
        }
        unnecessaryPartOfUri = cleanUri(unnecessaryPartOfUri.replace("*", ""));
        async = webServlet.asyncSupported();
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                validate(method);
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                Map<String, Map<RequestMappingSignature, Method>> methodUriMap = requestMappingTable
                        .computeIfAbsent(requestMapping.type(), requestMethod -> new HashMap<>());
                Map<RequestMappingSignature, Method> methodParamsMap = methodUriMap
                        .computeIfAbsent(cleanUri(requestMapping.value()), requestUri -> new HashMap<>());
                List<String> params = new ArrayList<>();
                String requestBody = null;
                for (Parameter parameter : method.getParameters()) {
                    if (parameter.isAnnotationPresent(RequestParam.class)) {
                        params.add(parameter.getAnnotation(RequestParam.class).value());
                    } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                        requestBody = parameter.getType().toString();
                    }
                }
                if (methodParamsMap.put(new RequestMappingSignature(params, requestBody), method) != null) {
                    throw new ControllerAnnotationException("RequestMapping Method signature must be unique");
                }
            }
        }
    }

    private static String cleanUri(String uri) {
        if (uri.isEmpty()) {
            return uri;
        }
        if (uri.charAt(0) == '/') {
            uri = uri.substring(1);
        }
        if (uri.isEmpty()) {
            return uri;
        }
        if (uri.charAt(uri.length() - 1) == '/') {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    private String cleanRequestUri(String requestUri) {
        requestUri = cleanUri(requestUri);
        requestUri = requestUri.replace(unnecessaryPartOfUri, "");
        return cleanUri(requestUri);
    }

    private void validate(Method method) {
        checkResponseBody(method);
        checkParameters(method);
    }

    private void checkResponseBody(Method method) {
        if (method.isAnnotationPresent(ResponseBody.class)) {
            if (method.getReturnType().equals(void.class)) {
                throw new ControllerAnnotationException("Method with annotation ResponseBody cannot be with void return type");
            }
            proccessAnnotationsWithConverter(method.getAnnotation(ResponseBody.class).converter());
        }
    }

    private void checkParameters(Method method) {
        boolean requestBody = false;
        List<String> params = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getType().equals(HttpServletRequest.class) || parameter.getType().equals(HttpServletResponse.class)) {
                if (parameter.isAnnotationPresent(RequestBody.class)) {
                    throw new ControllerAnnotationException("Parameter with annotation RequestBody cannot be HttpServletRequest or HttpServletResponse");
                }
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    throw new ControllerAnnotationException("Parameter with annotation Param cannot be HttpServletRequest or HttpServletResponse");
                }
            }
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                if (method.getAnnotation(RequestMapping.class).type().equals(RequestMethod.GET)) {
                    throw new ControllerAnnotationException("Parameter with annotation RequestBody cannot handle GET request");
                }
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    throw new ControllerAnnotationException("Parameter with annotation RequestBody cannot also annotate Param");
                }
                if (requestBody) {
                    throw new ControllerAnnotationException("Maybe only one parameter with RequestBody annotation");
                } else {
                    requestBody = true;
                    proccessAnnotationsWithConverter(parameter.getAnnotation(RequestBody.class).converter());
                }
            }
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String paramKey = parameter.getAnnotation(RequestParam.class).value();
                if (params.contains(paramKey)) {
                    throw new ControllerAnnotationException("Duplicates Param value");
                } else {
                    params.add(paramKey);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void proccessAnnotationsWithConverter(Class<? extends Converter> converterClass) {
        if (converterClass.equals(Converter.class)) {
            return;
        }
        if (!converters.containsKey(converterClass)) {
            try {
                converters.put((Class<Converter>) converterClass, converterClass.getConstructor().newInstance());
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new ControllerAnnotationException("Converter must have constructor without parameters");
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        if (async) {
            AsyncContext asyncContext = req.startAsync(req, resp);
            asyncContext.start(() -> {
                proccessRequest(req, resp);
                asyncContext.complete();
            });
        } else {
            proccessRequest(req, resp);
        }
    }

    private void proccessRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Method method = findMethodForRequest(req);
            if (method == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Object[] values = getParameterValuesForRequest(method, req, resp);
            if (values == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Object object = method.invoke(this, values);
            if (object != null) {
                ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
                if (responseBody != null && !resp.isCommitted()) {
                    Converter converter = getConverter(responseBody.converter());
                    resp.setContentType(converter.getContentType());
                    resp.getWriter().write(converter.to(object));
                }
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Method findMethodForRequest(HttpServletRequest request) {
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());
        Map<String, Map<RequestMappingSignature, Method>> methodUriMap = requestMappingTable.get(requestMethod);
        Map<RequestMappingSignature, Method> methodParamsMap = methodUriMap.get(cleanRequestUri(request.getRequestURI()));
        if (methodParamsMap == null) {
            return null;
        }
        Method method;
        if (methodParamsMap.size() == 1) {
            method = methodParamsMap.values().toArray(new Method[0])[0];
        } else {
            method = methodParamsMap.get(new RequestMappingSignature(Collections.list(request.getParameterNames())));
        }
        if (method == null) {
            method = methodParamsMap.get(new RequestMappingSignature(Collections.emptyList()));
        }
        return method;
    }

    private Converter getConverter(Class<? extends Converter> converterClass) {
        if (converterClass.equals(Converter.class)) {
            return defaultConverter;
        } else {
            return converters.get(converterClass);
        }
    }

    private Object[] getParameterValuesForRequest(Method method, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Parameter[] parameters = method.getParameters();
        Object[] parametersValues = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType().equals(HttpServletRequest.class)) {
                parametersValues[i] = req;
            } else if (parameter.getType().equals(HttpServletResponse.class)) {
                parametersValues[i] = resp;
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                Object value = proccessParameterOnRequestParamAnnotation(parameter, req);
                if (value == null && !parameter.getAnnotation(RequestParam.class).nullable()) {
                    return null;
                }
                parametersValues[i] = value;
            } else {
                parametersValues[i] = getConverter(parameter.getAnnotation(RequestBody.class).converter())
                        .from(req.getReader(), parameter.getType());
            }
        }
        return parametersValues;
    }

    private Object proccessParameterOnRequestParamAnnotation(Parameter parameter, HttpServletRequest req) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String requestValue = req.getParameter(requestParam.value());
        if (requestValue == null) {
            if (requestParam.nullable()) {
                return null;
            }
            requestValue = requestParam.defaultValue();
        }
        Object value = proccessObjectByType(requestValue, parameter.getType());
        if (value == null) {
            if (requestParam.nullable()) {
                return null;
            }
            value = proccessObjectByType(requestParam.defaultValue(), parameter.getType());
        }
        return value;
    }

    private Object proccessObjectByType(String value, Type type) {
        if (type.equals(int.class) || type.equals(long.class) || type.equals(Integer.class)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            if ("true".equals(value)) {
                return true;
            } else if ("false".equals(value)) {
                return false;
            } else {
                return null;
            }
        } else {
            return value;
        }
    }


    public enum RequestMethod {
        GET,
        POST,
        PUT,
        DELETE
    }


    private static final class RequestMappingSignature {
        public final List<String> params;
        public final String requestBody;
        public boolean considerRequestBody = true;

        private RequestMappingSignature(List<String> params, String requestBody) {
            this.params = params;
            this.requestBody = requestBody;
        }

        private RequestMappingSignature(List<String> params) {
            this(params, null);
            considerRequestBody = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RequestMappingSignature that = (RequestMappingSignature) o;
            boolean result = Objects.equals(params, that.params);
            if (considerRequestBody) {
                return result && Objects.equals(requestBody, that.requestBody);
            } else {
                return result;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(params);
        }
    }
}
