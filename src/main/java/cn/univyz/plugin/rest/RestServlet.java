package cn.univyz.plugin.rest;

import cn.univyz.framework.helper.ClassHelper;
import cn.univyz.framework.util.CollectionUtil;
import cn.univyz.framework.util.StringUtil;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import java.util.Set;

/**
 * REST Servlet
 *
 * @author bfy
 * @version 1.0.0
 */
@WebServlet(urlPatterns = RestConstant.SERVLET_URL, loadOnStartup = 0)
public class RestServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig sc) {
        //初始化CXF总线
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        //发布REST 服务
        publishRestService();
    }

    private void publishRestService() {
        //遍历所有标注了REST注解的类
        Set<Class<?>> restClassSet=ClassHelper.getClassSetByAnnotation(Rest.class);
        if (CollectionUtil.isNotEmpty(restClassSet)){
            for (Class<?> restClass : restClassSet) {
                //获取REST地址
                String address = getAddress(restClass);
                //发布REST 服务
                RestHelper.publicshService(address, restClass);
            }
        }
    }

    private String getAddress(Class<?> restClass) {
        String address;
        //若REST注解的 value 属性不为空，则获取当前值，否则获取类名
        String value =restClass.getAnnotation(Rest.class).value();
        if (StringUtil.isNotEmpty(value)) {
            address = value;
        }else {
            address = restClass.getSimpleName();
        }
        //确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }
        address = address.replaceAll("\\/+","/");
        return address;
    }
}
