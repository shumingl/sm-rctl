package sm.tools.rctl.remote.core.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.utils.ResourceUtil;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.util.List;

public class ActionCallbackScanner {
    private static final Logger logger = LoggerFactory.getLogger(ActionCallbackScanner.class);

    public ActionCallbackScanner(String... packages) {
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

                ActionHandler[] actionHandlers = clazz.getAnnotationsByType(ActionHandler.class);
                if (actionHandlers != null && actionHandlers.length > 0) {
                    ActionHandler handler = actionHandlers[0];
                    String cacheKey = RctlConstants.CACHE_KEY_ACTION_CALLBACK;
                    logger.info("add cache : {}/{} = {}", cacheKey, handler.value(), clazz);
                    MemoryCache.put(cacheKey, handler.value(), clazz);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("包扫描异常", e);
        }
    }
}
