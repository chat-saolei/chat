package com.chat.core.dispatch;

import com.chat.util.ClassUtil;
import com.landlord.common.api.domain.ApiRequest;
import com.landlord.common.api.domain.ApiResponse;
import com.landlord.common.common.Cmd;
import com.landlord.common.common.GameException;
import com.landlord.common.util.JsonUtil;
import com.landlord.game.common.worker.WorkerManager;
import com.landlord.game.game.room.Room;
import com.landlord.game.game.room.RoomManager;
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
        Set<Class<?>> classes = ClassUtil.getClassSet("com.landlord.game", ApiController.class);
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
                    if (parameters[1].getType() != Room.class) {
                        continue;
                    }
                    Class reqClass = null;
                    if (parameters.length == 3) {
                        reqClass = parameters[2].getType();
                    }
                    DispatchInfo info = new DispatchInfo(controller, method, reqClass);
                    dispatchInfoMap.put(apiMapping.value(), info);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static ApiResponse dispatch(ApiRequest request) {
        log.info("receive request:" + request);
        Cmd cmd = Cmd.get(request.getCmd());
        if (cmd == null) {
            log.error("cmd not found:" + request.getCmd());
            return ApiResponse.error(request, Error.CMD_NOT_FOUND.getCode());
        }
        DispatchInfo dispatchInfo = dispatchInfoMap.get(cmd);
        if (dispatchInfo == null) {
            log.error("DispatchInfo not found:" + request.getCmd());
            return ApiResponse.error(request, Error.CMD_NOT_FOUND.getCode());
        }
        int playerId = request.getPlayerId();
        Room room = RoomManager.getByPlayerId(playerId);
        if (room == null) {
            log.error("not in room:" + playerId);
            return ApiResponse.error(request, Error.NOT_IN_ROOM.getCode());
        }
        Object controller = dispatchInfo.getController();
        Method mapping = dispatchInfo.getMethod();
        Class paramClass = dispatchInfo.getReqParamClass();
        try {
            Object o = WorkerManager.runInRoom(playerId, room, mapping, controller, JsonUtil.toObject(request.getData(), paramClass));
            return ApiResponse.success(request, o == null ? null : JsonUtil.toString(o));
        } catch (GameException ge) {
            log.error("", ge);
            return ApiResponse.error(request, ge.getError(), ge.getData());
        } catch (Exception e) {
            log.error("", e);
        }
        return ApiResponse.error(request, Error.SERVER_ERROR.getCode());
    }

}
