package com.chat.core.dispatch;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class DispatchInfo {
    private Object controller;
    private Method method;
    private Class reqParamClass;
}
