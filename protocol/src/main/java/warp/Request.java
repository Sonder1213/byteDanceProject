package warp;

/**
 * @author hewei
 * @version 1.0
 * @description: request包装类
 * @date 2022/10/21 10:52
 */

public class Request {
    private String id;
    private String token;
    private Long ticketId;

    public Request() {
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Request(String id, String token, Long ticketId) {
        this.id = id;
        this.token = token;
        this.ticketId = ticketId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
