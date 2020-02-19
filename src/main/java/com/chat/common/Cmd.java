package com.chat.common;

import java.util.HashMap;
import java.util.Map;

public enum Cmd {
    //************USER*************
    LOGIN(1001);
    //************MESSAGE*************
    //************DIALOG*************





    private int cmd;
    private static Map<Integer, Cmd> map = new HashMap<Integer, Cmd>();
    private Cmd(int cmd) {
        this.cmd = cmd;
    }

    static {
        for (Cmd cmd1 : Cmd.values()) {
            map.put(cmd1.cmd, cmd1);
        }
    }

    public static Cmd of(int cmd) {
        return map.get(cmd);
    }
}
