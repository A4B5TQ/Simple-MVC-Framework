package mvc.framework.handlers;

import mvc.framework.annotations.controllers.Controller;
import mvc.framework.annotations.requests.GetMapping;
import mvc.framework.annotations.requests.PostMapping;
import mvc.framework.controllers.ControllerActionPair;
import mvc.framework.handlers.interfaces.HandlerMapping;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HandlerMappingImpl implements HandlerMapping {

    private List<Class> controllerClasses;

    public HandlerMappingImpl() {
        this.controllerClasses = new ArrayList<>();
    }

    @Override
    public ControllerActionPair findController(HttpServletRequest request) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String urlPath = request.getRequestURI();
        String absolutePath = request.getServletContext().getRealPath("/WEB-INF/classes");
        this.findAllControllers(absolutePath);
        for (Class controller : this.controllerClasses) {
            Method[] methods = controller.getDeclaredMethods();
            for (Method method : methods) {
                String methodPath = this.findMethodPath(request, method);
                if (methodPath == null) {
                    continue;
                }

                if (this.isPathMatching(urlPath, methodPath)) {
                    ControllerActionPair controllerActionPair = new ControllerActionPair(controller, method);
                    this.addPathVariables(controllerActionPair,urlPath, methodPath);
                    return controllerActionPair;
                }
            }
        }
        return null;
    }


    private void addPathVariables(ControllerActionPair controllerActionPair, String urlPath, String methodPath) {
        String[] uriTokens = urlPath.split("/");
        String[] methodTokens = methodPath.split("/");
        for (int i = 0; i < uriTokens.length; i++) {
            if (methodTokens[i].startsWith("{") && methodTokens[i].endsWith("}")) {
                String key = methodTokens[i].replace("{","").replace("}","");
                String value = uriTokens[i];
                controllerActionPair.addPathVariable(key, value);
            }
        }
    }


    private boolean isPathMatching(String urlPath, String methodPath) {
        boolean isPathMatching = true;
        String[] uriTokens = urlPath.split("/");
        String[] methodTokens = methodPath.split("/");
        if (uriTokens.length != methodTokens.length) {
            return false;
        }

        for (int i = 0; i < uriTokens.length; i++) {
            if (methodTokens[i].startsWith("{") && methodTokens[i].endsWith("}")) {
                continue;
            }

            if (!uriTokens[i].equals(methodTokens[i])) {
                isPathMatching = false;
                break;
            }
        }
        return isPathMatching;
    }

    private String findMethodPath(HttpServletRequest request, Method method) throws IllegalAccessException, InstantiationException {
        String methodType = request.getMethod();
        String path = null;
        switch (methodType) {
            case "GET":
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    path = getMapping.value();
                }
                break;
            case "POST":
                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping getMapping = method.getAnnotation(PostMapping.class);
                    path = getMapping.value();
                }
                break;
        }
        return path;
    }


    private void findAllControllers(String projectDirectory) throws ClassNotFoundException, IOException {
        File directory = new File(projectDirectory);
        File[] files = directory.listFiles();
        for (File file : files != null ? files : new File[0]) {
            if (file.isFile()) {
                Class controller = this.getClass(file);
                if (controller != null) {
                    if (controller.isAnnotationPresent(Controller.class)) {
                        this.controllerClasses.add(controller);
                        this.populateURI();
                    }
                }
            } else if (file.isDirectory()) {
                findAllControllers(file.getAbsolutePath());
            }
        }
    }

    private void populateURI() {

    }

    private Class getClass(File file) throws ClassNotFoundException {
        String absolutePath = file.getAbsolutePath();
        String packages = absolutePath.split("classes\\\\")[1].replaceAll("[/\\\\]", ".");
        String className = packages.replace(".class", "");
        Class currentClass = null;
        if (!className.endsWith("DispatcherServlet")) {
            currentClass = Class.forName(className);
        }
        return currentClass;
    }
}
