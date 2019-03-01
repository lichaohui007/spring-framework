package org.mine;

public class OrderServiceImpl implements OrderService{
	private static Order order = null;
	@Override
	public Order createOrder(String userName, String product) {
		order = new Order();
		order.setUserName(userName);
		order.setProduct(product);
		return order;
	}

	@Override
	public Order queryOrder(String userName) {
		return order;
	}
}
