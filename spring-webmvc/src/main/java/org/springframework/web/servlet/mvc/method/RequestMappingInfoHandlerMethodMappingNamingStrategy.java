/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

/**
 * A {@link org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
 * HandlerMethodMappingNamingStrategy} for {@code RequestMappingInfo}-based handler
 * method mappings.
 *
 * If the {@code RequestMappingInfo} name attribute is set, its value is used.
 * Otherwise the name is based on the capital letters of the class name,
 * followed by "#" as a separator, and the method name. For example "TC#getFoo"
 * for a class named TestController with method getFoo.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class RequestMappingInfoHandlerMethodMappingNamingStrategy
		implements HandlerMethodMappingNamingStrategy<RequestMappingInfo> {

	/** Separator between the type and method-level parts of a HandlerMethod mapping name */
	public static final String SEPARATOR = "#";


	/**
	 * 情况1： 如果Mapping已经配置好名字 直接返回  如 @RequestMapping(name="login",value="user/login") 则直接返回Mapping的名字  login
	 *
	 * 情况2： 如果mapping未配置名字 则使用 类名大写 + "#" + 方法名 @RequestMapping(value="user/login") 假设它虽在的类为UserController 对应的方法名为login  则它对应的Mapping
	 * 名字就是 USERCONTROLLER#login
	 *
	 * */
	@Override
	public String getName(HandlerMethod handlerMethod, RequestMappingInfo mapping) {
		//如果mapping的名字非空 则使用 mapping 的名字
		if (mapping.getName() != null) {
			return mapping.getName();
		}
		//
		StringBuilder sb = new StringBuilder();
		//使用类名 + "#" + 方法名
		String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
		for (int i = 0 ; i < simpleTypeName.length(); i++) {
			if (Character.isUpperCase(simpleTypeName.charAt(i))) {
				//从大写的字符开始
				sb.append(simpleTypeName.charAt(i));
			}
		}
		sb.append(SEPARATOR).append(handlerMethod.getMethod().getName());
		return sb.toString();
	}

}
