package sm.tools.rctl.server.core.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.utils.ReflectUtil;
import sm.tools.rctl.base.utils.ResourceUtil;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.lang.reflect.Method;
import java.util.List;

public class ActionHandlerScanner {
    private static final Logger logger = LoggerFactory.getLogger(ActionHandlerScanner.class);

    public ActionHandlerScanner(String... packages) {
        scan(packages);
    }

    public void scan(String... packages) {
        if (packages != null && packages.length > 0)
            for (String base : packages)
                scan(base);
    }

    /**
     * 包扫描
     */
    private void scan(String patternPackage) {
        if (StringUtil.isNOE(patternPackage)) return;

        try {
            // 执行包扫描
            List<MetadataReader> metadataReaders = ResourceUtil.resourceScan(patternPackage);

            for (MetadataReader metadata : metadataReaders) {
                ClassMetadata classMetadata = metadata.getClassMetadata();
                String className = classMetadata.getClassName();
                Class<?> clazz = Class.forName(className);
                List<Method> methods = ReflectUtil.getAllMethods(clazz);
                if (methods != null && methods.size() > 0) {
                    for (Method method : methods) {
                        ActionHandler handler = method.getAnnotation(ActionHandler.class);
                        if (handler != null) {
                            String cacheKey = RctlConstants.CACHE_KEY_HANDLER;
                            logger.info("add cache : {}/{} = {}", cacheKey, handler.value(), method);
                            MemoryCache.put(cacheKey, handler.value(), method);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("包扫描异常", e);
        }
    }
}
