package enumeration;

/**
 * @author hewei
 * @version 1.0
 * @description: 错误枚举
 * @date 2022/10/21 11:11
 */

public enum MyError {
    UNKNOWN_PROTOCOL("未知的协议包"),
    UNKNOWN_PACKAGE("未知的包的类型"),
    UNKNOWN_SERIALIZER("未知的序列化方式"),
    UNKNOWN_TOKEN("未知的token");

    private final String massage;

    public String getMassage() {
        return massage;
    }

    MyError(String massage) {
        this.massage = massage;
    }
}
