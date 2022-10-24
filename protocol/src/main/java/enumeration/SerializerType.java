package enumeration;

/**
 * @author hewei
 * @version 1.0
 * @description: 序列化类型
 * @date 2022/10/21 11:02
 */

public enum SerializerType {
    KRYO(1, "KRYO"),
    PROTOSTUFF(2, "PROTOSTUFF");

    private final int code;
    private final String type;

    SerializerType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }
}
