package sm.tools.rctl.base.module.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import sm.tools.rctl.base.utils.ResourceUtil;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.util.List;

public class EntityScanner {
    private static final Logger logger = LoggerFactory.getLogger(EntityScanner.class);

    public EntityScanner(String... packages) {
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
                DynamicCache[] caches = clazz.getAnnotationsByType(DynamicCache.class);
                if (caches != null && caches.length > 0) {
                    DynamicEntityCache.register(clazz);
                    logger.info("register DynamicEntity: " + className);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("entity-scan error.", e);
        }
    }
}
