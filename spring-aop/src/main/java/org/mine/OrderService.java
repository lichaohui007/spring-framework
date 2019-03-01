package org.mine;

public interface OrderService {
	Order createOrder(String userName, String product);
	Order queryOrder(String userName);
}
