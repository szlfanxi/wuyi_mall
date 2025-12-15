package com.wuyimall.test;

import com.wuyimall.dto.CreateOrderRequest;
import com.wuyimall.dto.OrderItemDTO;
import com.wuyimall.entity.OrderItem;
import com.wuyimall.repository.OrderItemRepository;
import com.wuyimall.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderCreateTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void testCreateOrderWithReceiverInfo() {
        // 准备测试数据
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTotalAmount(new BigDecimal(200));
        request.setName("szl");
        request.setPhone("18267573025");
        request.setAddress("学政杰112号");
        request.setAddressId("1");
        request.setPaymentMethod("online");

        // 添加订单商品项
        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO item1 = new OrderItemDTO();
        item1.setProductId(15L);
        item1.setProductName("测试商品1");
        item1.setProductPrice(new BigDecimal(100));
        item1.setQuantity(2);
        item1.setTotalPrice(new BigDecimal(200));
        orderItems.add(item1);
        request.setOrderItems(orderItems);

        // 调用订单创建服务
        orderService.createOrder(request, 12L);

        // 查询最新的订单商品记录
        List<OrderItem> latestItems = orderItemRepository.findAll();
        OrderItem lastItem = latestItems.get(latestItems.size() - 1);

        // 验证收货人信息是否正确保存
        assertEquals("szl", lastItem.getReceiveName());
        assertEquals("18267573025", lastItem.getReceivePhone());
        assertEquals("学政杰112号", lastItem.getReceiveAddress());
        System.out.println("测试成功！收货人信息已正确保存到order_item表中。");
        System.out.println("最新订单商品记录：");
        System.out.println("ID: " + lastItem.getId());
        System.out.println("商品名称: " + lastItem.getProductName());
        System.out.println("收货人姓名: " + lastItem.getReceiveName());
        System.out.println("收货人电话: " + lastItem.getReceivePhone());
        System.out.println("收货人地址: " + lastItem.getReceiveAddress());
    }
}