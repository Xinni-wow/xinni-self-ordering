package com.xinni.exception;

public class DeleteNotAllowedException extends BaseException{

    public DeleteNotAllowedException(){}

    public DeleteNotAllowedException(String msg){
        super(msg);
    }
}
