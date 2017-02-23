package mvc.framework.handlers;

import mvc.framework.annotations.parameters.ModelAttribute;
import mvc.framework.annotations.parameters.PathVariable;
import mvc.framework.annotations.parameters.RequestParam;
import mvc.framework.controllers.ControllerActionPair;
import mvc.framework.handlers.interfaces.HandlerAction;
import mvc.framework.models.Model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;


public class HandlerActionImpl implements HandlerAction {
    @Override
    public String executeControllerAction(HttpServletRequest request, HttpServletResponse response, ControllerActionPair controllerActionPair) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, NamingException {
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

            if(parameter.isAnnotationPresent(ModelAttribute.class)){
                argument = this.getModelAttributeValue(parameter, request);
            }

            if(parameter.getType().isAssignableFrom(HttpSession.class)){
                argument = request.getSession();
            }

            if(parameter.getType().isAssignableFrom(Cookie[].class)){
                argument = request.getCookies();
            }

            if(parameter.getType().isAssignableFrom(HttpServletResponse.class)){
                argument = response;
            }

            if(parameter.getType().isAssignableFrom(HttpServletRequest.class)){
                argument = request;
            }

            objects.add(argument);
        }

        Context context = new InitialContext();
        String controllerName = controller.getSimpleName();
        Object controllerObject = context.lookup("java:global/" + controllerName);

        return (String) actionMethod.invoke(controllerObject, (Object[]) objects.toArray());
    }

    private Object getModelAttributeValue(Parameter parameter, HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        Class bindingModelClass = parameter.getType();
        Object bindingModelObject = bindingModelClass.newInstance();
        Field[] fields = bindingModelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = request.getParameter(fieldName);
            if(fieldValue != null){
                field.set(bindingModelObject, fieldValue);
            }
        }
        return bindingModelObject;
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

    @SuppressWarnings("unchecked")
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
