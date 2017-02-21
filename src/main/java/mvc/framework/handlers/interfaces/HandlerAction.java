package mvc.framework.handlers.interfaces;

import mvc.framework.controllers.ControllerActionPair;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

public interface HandlerAction {

    String executeControllerAction(HttpServletRequest request, ControllerActionPair controllerActionPair) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException;
}
