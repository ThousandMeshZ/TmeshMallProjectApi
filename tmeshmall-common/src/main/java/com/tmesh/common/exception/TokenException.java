package com.tmesh.common.exception;

/**
 * 令牌处理异常
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 */
public class TokenException extends RuntimeException {

    public TokenException() {
        super("处理token，返回错误信息时异常");
    }
}
