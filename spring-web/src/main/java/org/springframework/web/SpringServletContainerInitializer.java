/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Servlet 3.0 {@link ServletContainerInitializer} designed to support code-based
 * configuration of the servlet container using Spring's {@link WebApplicationInitializer}
 * SPI as opposed to (or possibly in combination with) the traditional
 * {@code web.xml}-based approach.
 *
 * <h2>Mechanism of Operation</h2>
 * This class will be loaded and instantiated and have its {@link #onStartup}
 * method invoked by any Servlet 3.0-compliant container during container startup assuming
 * that the {@code spring-web} module JAR is present on the classpath. This occurs through
 * the JAR Services API {@link ServiceLoader#load(Class)} method detecting the
 * {@code spring-web} module's {@code META-INF/services/javax.servlet.ServletContainerInitializer}
 * service provider configuration file. See the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">
 * JAR Services API documentation</a> as well as section <em>8.2.4</em> of the Servlet 3.0
 * Final Draft specification for complete details.
 *
 * <h3>In combination with {@code web.xml}</h3>
 * A web application can choose to limit the amount of classpath scanning the Servlet
 * container does at startup either through the {@code metadata-complete} attribute in
 * {@code web.xml}, which controls scanning for Servlet annotations or through an
 * {@code <absolute-ordering>} element also in {@code web.xml}, which controls which
 * web fragments (i.e. jars) are allowed to perform a {@code ServletContainerInitializer}
 * scan. When using this feature, the {@link SpringServletContainerInitializer}
 * can be enabled by adding "spring_web" to the list of named web fragments in
 * {@code web.xml} as follows:
 *
 * <pre class="code">
 * {@code
 * <absolute-ordering>
 *   <name>some_web_fragment</name>
 *   <name>spring_web</name>
 * </absolute-ordering>
 * }</pre>
 *
 * <h2>Relationship to Spring's {@code WebApplicationInitializer}</h2>
 * Spring's {@code WebApplicationInitializer} SPI consists of just one method:
 * {@link WebApplicationInitializer#onStartup(ServletContext)}. The signature is intentionally
 * quite similar to {@link ServletContainerInitializer#onStartup(Set, ServletContext)}:
 * simply put, {@code SpringServletContainerInitializer} is responsible for instantiating
 * and delegating the {@code ServletContext} to any user-defined
 * {@code WebApplicationInitializer} implementations. It is then the responsibility of
 * each {@code WebApplicationInitializer} to do the actual work of initializing the
 * {@code ServletContext}. The exact process of delegation is described in detail in the
 * {@link #onStartup onStartup} documentation below.
 *
 * <h2>General Notes</h2>
 * In general, this class should be viewed as <em>supporting infrastructure</em> for
 * the more important and user-facing {@code WebApplicationInitializer} SPI. Taking
 * advantage of this container initializer is also completely <em>optional</em>: while
 * it is true that this initializer will be loaded and invoked under all Servlet 3.0+
 * runtimes, it remains the user's choice whether to make any
 * {@code WebApplicationInitializer} implementations available on the classpath. If no
 * {@code WebApplicationInitializer} types are detected, this container initializer will
 * have no effect.
 *
 * <p>Note that use of this container initializer and of {@code WebApplicationInitializer}
 * is not in any way "tied" to Spring MVC other than the fact that the types are shipped
 * in the {@code spring-web} module JAR. Rather, they can be considered general-purpose
 * in their ability to facilitate convenient code-based configuration of the
 * {@code ServletContext}. In other words, any servlet, listener, or filter may be
 * registered within a {@code WebApplicationInitializer}, not just Spring MVC-specific
 * components.
 *
 * <p>This class is neither designed for extension nor intended to be extended.
 * It should be considered an internal type, with {@code WebApplicationInitializer}
 * being the public-facing SPI.
 *
 * <h2>See Also</h2>
 * See {@link WebApplicationInitializer} Javadoc for examples and detailed usage
 * recommendations.<p>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see #onStartup(Set, ServletContext)
 * @see WebApplicationInitializer
 */
@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * Delegate the {@code ServletContext} to any {@link WebApplicationInitializer}
	 * implementations present on the application classpath.
	 * <p>Because this class declares @{@code HandlesTypes(WebApplicationInitializer.class)},
	 * Servlet 3.0+ containers will automatically scan the classpath for implementations
	 * of Spring's {@code WebApplicationInitializer} interface and provide the set of all
	 * such types to the {@code webAppInitializerClasses} parameter of this method.
	 * <p>If no {@code WebApplicationInitializer} implementations are found on the classpath,
	 * this method is effectively a no-op. An INFO-level log message will be issued notifying
	 * the user that the {@code ServletContainerInitializer} has indeed been invoked but that
	 * no {@code WebApplicationInitializer} implementations were found.
	 * <p>Assuming that one or more {@code WebApplicationInitializer} types are detected,
	 * they will be instantiated (and <em>sorted</em> if the @{@link
	 * org.springframework.core.annotation.Order @Order} annotation is present or
	 * the {@link org.springframework.core.Ordered Ordered} interface has been
	 * implemented). Then the {@link WebApplicationInitializer#onStartup(ServletContext)}
	 * method will be invoked on each instance, delegating the {@code ServletContext} such
	 * that each instance may register and configure servlets such as Spring's
	 * {@code DispatcherServlet}, listeners such as Spring's {@code ContextLoaderListener},
	 * or any other Servlet API componentry such as filters.
	 * @param webAppInitializerClasses all implementations of
	 * {@link WebApplicationInitializer} found on the application classpath
	 * @param servletContext the servlet context to be initialized
	 * @see WebApplicationInitializer#onStartup(ServletContext)
	 * @see AnnotationAwareOrderComparator
	 *
	 *
	 * servlet3.0 首先提供了 @WebServlet，@WebFilter 等注解，这样便有了抛弃 web.xml 的第一个途径，凭借注解声明 servlet 和 filter 来做到这一点。
	 *
	 * 除了这种方式，servlet3.0 规范还提供了更强大的功能，可以在运行时动态注册 servlet ，filter，listener。以 servlet 为例，过滤器与监听器与之类似。ServletContext 为动态配置 Servlet 增加了如下方法：
	 *
	 * ServletRegistration.Dynamic addServlet(String servletName,Class<? extends Servlet> servletClass)
	 * ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet)
	 * ServletRegistration.Dynamic addServlet(String servletName, String className)
	 * T createServlet(Class clazz)
	 * ServletRegistration getServletRegistration(String servletName)
	 * Map<String,? extends ServletRegistration> getServletRegistrations()
	 * 其中前三个方法的作用是相同的，只是参数类型不同而已；通过 createServlet() 方法创建的 Servlet，通常需要做一些自定义的配置，然后使用 addServlet() 方法来将其动态注册为一个可以用于服务的 Servlet。
	 * 两个 getServletRegistration() 方法主要用于动态为 Servlet 增加映射信息，这等价于在 web.xml 中使用 标签为存在的 Servlet 增加映射信息。
	 *
	 * 以上 ServletContext 新增的方法要么是在 ServletContextListener 的 contexInitialized 方法中调用，要么是在 ServletContainerInitializer 的 onStartup() 方法中调用。
	 *
	 * ServletContainerInitializer 也是 Servlet 3.0 新增的一个接口，容器在启动时使用 JAR 服务 API(JAR Service API) 来发现 ServletContainerInitializer 的实现类，
	 * 并且容器将 WEB-INF/lib 目录下 JAR 包中的类都交给该类的 onStartup() 方法处理，我们通常需要在该实现类上使用 @HandlesTypes 注解来指定希望被处理的类，过滤掉不希望给 onStartup() 处理的类。
	 */
	@Override
	public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<>();

		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				//接口 抽象类  WebApplicationinitializer的父类不会处理
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						//调用webApplicationInitializer的子类进行初始化
						initializers.add((WebApplicationInitializer)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (WebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}

}
