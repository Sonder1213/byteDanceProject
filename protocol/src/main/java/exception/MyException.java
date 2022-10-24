package exception;

import enumeration.MyError;

/**
 * @author hewei
 * @version 1.0
 * @description: 自定义异常
 * @date 2022/10/21 11:10
 */

public class MyException extends RuntimeException{
    public MyException(MyError error, String detail) {
        super(error.getMassage() + ":" + detail);
    }
}
