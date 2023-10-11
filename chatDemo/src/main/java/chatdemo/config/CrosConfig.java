package chatdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域访问设置，用于使用前后端分离的方式调试前端代码中
 */
@Configuration
public abstract class CrosConfig implements WebMvcConfigurer {
    /**
     * 配置跨域请求处理
     * @param registry 跨域请求注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域请求的路径。"/*"表示所有路径都允许跨域请求
        registry.addMapping("/*")
                // 设置允许跨域请求的来源模式，可以是具体的域名或者正则表达式。"*"表示允许所有来源的跨域请求
                .allowedOriginPatterns("*")
                // 允许发送Cookie
                .allowCredentials(true)
                // 允许的请求方法
                .allowedMethods("GET", "POST", "DELETE", "PUT","PATCH")
                // 预检请求的有效期，单位为秒
                .maxAge(3600);
    }
}
