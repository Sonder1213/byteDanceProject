package warp;

/**
 * @author hewei
 * @version 1.0
 * @description: response包装类
 * @date 2022/10/21 10:54
 */

public class Response {
    private String id;
    private Long orderId;
    private Boolean isSuccess;
    private String exception;

    public static Response ok(Request request, Long orderId) {
        Response response = new Response();
        response.setOrderId(orderId);
        response.setId(request.getId());
        response.setSuccess(true);
        return response;
    }

    public static Response fail(Request request, String exception) {
        Response response = new Response();
        response.setSuccess(false);
        response.setException(exception);
        response.setId(request.getId());
        return response;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", orderId=" + orderId +
                ", isSuccess=" + isSuccess +
                ", exception='" + exception + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
