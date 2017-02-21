package mvc.framework.dispatchers.interfaces;

import mvc.framework.controllers.ControllerActionPair;
import javax.servlet.http.HttpServletRequest;

public interface Dispatcher {

    ControllerActionPair dispatchRequest(HttpServletRequest request);

    String dispatchAction(HttpServletRequest request, ControllerActionPair controllerActionPair);

}
