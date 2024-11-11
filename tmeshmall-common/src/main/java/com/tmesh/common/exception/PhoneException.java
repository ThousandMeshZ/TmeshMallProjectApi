package com.tmesh.common.exception;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
