package com.tmesh.common.exception;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/
public class UsernameException extends RuntimeException {


    public UsernameException() {
        super("存在相同的用户名");
    }
}
