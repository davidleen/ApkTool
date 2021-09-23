package com.changdu.apkpackage;

/**
 * cmd 执行命令错误
 */
public class CmdExecuteException extends Exception {

        public   int code;
    public CmdExecuteException(int code, String message) {
        super(message);
        this.code = code;
    }
}
