package com.ustc.hewei.sellticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/21 15:25
 */

@TableName("ticket_stock")
public class TicketStock {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
