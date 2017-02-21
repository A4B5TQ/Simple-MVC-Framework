package mvc.framework.handlers;

import mvc.framework.annotations.parameters.PathVariable;
import mvc.framework.annotations.parameters.RequestParam;
import mvc.framework.controllers.ControllerActionPair;
import mvc.framework.handlers.interfaces.HandlerAction;

import javax.enterprise.inject.Model;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerActionImpl implements HandlerAction {

    @Override
    public String executeControllerAction(HttpServletRequest request, ControllerActionPair controllerActionPair) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Class controller = controllerActionPair.getController();
        Method actionMethod = controllerActionPair.getMethod();
        Parameter[] parameters = actionMethod.getParameters();
        Object argument = null;
        List<Object> objects = new ArrayList();
        for (Parameter parameter : parameters) {
            if(parameter.isAnnotationPresent(PathVariable.class)){
                PathVariable pathVariableAnnotation = parameter.getAnnotation(PathVariable.class);
                argument = this.getPathVariableValue(parameter, pathVariableAnnotation, controllerActionPair);
            }

            if(parameter.isAnnotationPresent(RequestParam.class)){
                RequestParam requestParamAnnotation = parameter.getAnnotation(RequestParam.class);
                argument = this.getParameterValue(parameter, requestParamAnnotation, request);
            }

            if(parameter.getType().isAssignableFrom(Model.class)){
                Constructor constructor = parameter.getType().getConstructor(HttpServletRequest.class);
                argument = constructor.newInstance(request);
            }

            objects.add(argument);

        }

        return (String) actionMethod.invoke(controller.newInstance(), (Object[]) objects.toArray());
    }

    private <T> T getPathVariableValue(Parameter parameter, PathVariable pathVariableAnnotation, ControllerActionPair controllerActionPair) {
        String value = pathVariableAnnotation.value();
        String pathVariable =  controllerActionPair.getPathVariable(value);
        return convertArgument(parameter, pathVariable);
    }

    private <T> T getParameterValue(Parameter parameter, RequestParam requestParamAnnotationClass, HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        String parameterName = requestParamAnnotationClass.value();
        String requestParameter = request.getParameter(parameterName);
        return convertArgument(parameter, requestParameter);
    }

    private <T> T convertArgument(Parameter parameter, String pathVariable){
        Object object = null;
        switch (parameter.getType().getSimpleName()){
            case "String":
                object = pathVariable;
                break;
            case "Integer":
                object = Integer.parseInt(pathVariable);
                break;
            case "int":
                object = Integer.parseInt(pathVariable);
                break;
            case "Long":
                object = Long.parseLong(pathVariable);
                break;
            case "long":
                object = Long.parseLong(pathVariable);
                break;
        }

        return (T) object;
    }

}
