package com.ustc.hewei.sellticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustc.hewei.sellticket.entity.TicketOrder;
import com.ustc.hewei.sellticket.mapper.TicketOrderMapper;
import com.ustc.hewei.sellticket.service.ITicketOrderService;
import org.springframework.stereotype.Service;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/21 15:36
 */

@Service
public class TicketOrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder> implements ITicketOrderService {
}
