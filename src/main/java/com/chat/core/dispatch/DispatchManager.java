package com.chat.core.dispatch;

import com.chat.common.Cmd;
import com.chat.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DispatchManager {
    private static Map<Cmd, DispatchInfo> dispatchInfoMap = new HashMap<>();

    public static void init() {
        Set<Class<?>> classes = ClassUtil.getClassSet("com.chat", ApiController.class);
        for (Class<?> clazz : classes) {
            try {
                Set<Method> methods = ClassUtil.getMethodSet(clazz, ApiMapping.class);
                Object controller = clazz.getDeclaredConstructor().newInstance();
                for (Method method : methods) {
                    ApiMapping apiMapping = method.getDeclaredAnnotation(ApiMapping.class);
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length < 2) {
                        continue;
                    }
                    if (parameters[0].getType() != int.class && parameters[0].getType() != Integer.class) {
                        continue;
                    }

                    Class reqClass = parameters.length == 1 ? parameters[0].getType() : parameters[1].getType();
                    DispatchInfo info = new DispatchInfo(apiMapping.login(), controller, method, reqClass);
                    dispatchInfoMap.put(apiMapping.value(), info);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}
