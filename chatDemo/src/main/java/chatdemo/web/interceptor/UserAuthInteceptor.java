package chatdemo.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import chatdemo.util.Constant;

public class UserAuthInteceptor implements HandlerInterceptor {

    /**
     * 拦截前处理
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @return boolean
     * @throws Exception 异常
     */

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //从请求会话中获取用户令牌，如果没有令牌则跳转到登录页
        HttpSession session = request.getSession();
        Object userToken = session.getAttribute(Constant.USER_TOKEN);
        if (userToken == null) {
            /*JsonMsgHelper.writeJson(response, new ResponseJson(HttpStatus.FORBIDDEN).setMsg("请登录"),
                    HttpStatus.FORBIDDEN);*/
            response.sendRedirect("login");
            return false;
        }
        return true;
    }

    /**
     * 拦截后处理
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @param modelAndView ModelAndView
     * @throws Exception 异常
     */

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 在响应中添加头部信息，解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials","true");
    }

    /**
     * 完成后的处理
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @throws Exception 异常
     */

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}
