package com.ustc.hewei.sellticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustc.hewei.sellticket.entity.TicketStock;
import com.ustc.hewei.sellticket.mapper.TicketStockMapper;
import com.ustc.hewei.sellticket.service.ITicketStockService;
import org.springframework.stereotype.Service;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/21 15:37
 */

@Service
public class TicketStockServiceImpl extends ServiceImpl<TicketStockMapper, TicketStock> implements ITicketStockService {
}
