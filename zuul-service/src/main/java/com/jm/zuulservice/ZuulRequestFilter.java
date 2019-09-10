package com.jm.zuulservice;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

/**
 * <h1>ZuulRequestFilter</h1>
 * <p>
 * <h3>ref='https://cloud.spring.io/spring-cloud-netflix/reference/html/#_custom_zuul_filter_examples'>Custom Zuul
 * Filter Examples</a>
 * </h3>
 * <p>Most of the following &quot;How to Write&quot; examples below are included
 * <a href='https://github.com/spring-cloud-samples/sample-zuul-filters'>Sample Zuul Filters</a>
 * project. There are also examples of manipulating the request or response body in that repository.</p>
 * <p>This section includes the following examples (inspired by):</p>
 * <ul>
 * <li><a href='https://cloud.spring.io/spring-cloud-netflix/reference/html/#zuul-developer-guide-sample-pre-filter'>How to Write a Pre Filter</a>
 * </li>
 * <li><a href='https://cloud.spring.io/spring-cloud-netflix/reference/html/#zuul-developer-guide-sample-route-filter'>How to Write a Route Filter</a>
 * </li>
 * <li><a href='https://cloud.spring.io/spring-cloud-netflix/reference/html/#zuul-developer-guide-sample-post-filter'>How to Write a Post Filter</a>
 * </li>
 * <li><a href='https://www.fangzhipeng.com/springcloud/2017/06/05/sc05-zuul.html'>Spring Cloud 教程第5篇：Zuul</a>
 * </li>
 * </ul>
 *
 * @author Johnny Miller (锺俊), email: johnnysviva@outlook.com
 * @date 9/10/19 4:48 PM
 **/
@Slf4j
@Component
public class ZuulRequestFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // run before PreDecoration
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // filter every request
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        log.info("Filtered request. Request URL: {}", ctx.getRequest().getRequestURL());
        return null;
    }
}
