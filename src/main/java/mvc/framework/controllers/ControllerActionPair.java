package mvc.framework.controllers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerActionPair {

    private Class controllerClass;

    private Method method;

    private Map<String, String> pathVariables;

    public ControllerActionPair(Class controller, Method method) {
        this.controllerClass = controller;
        this.method = method;
        this.pathVariables = new HashMap<>();
    }

    public Class getController() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    public void addPathVariable(String key, String value){
        this.pathVariables.put(key, value);
    }

    public String getPathVariable(String key){
        return this.pathVariables.get(key);
    }
}