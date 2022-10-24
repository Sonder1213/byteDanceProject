package enumeration;

/**
 * @author hewei
 * @version 1.0
 * @description: 包的类型
 * @date 2022/10/21 11:05
 */

public enum PackageType {
    REQUEST(0),
    RESPONSE(1);

    private final int code;

    public int getCode() {
        return code;
    }

    PackageType(int code) {
        this.code = code;
    }
}
