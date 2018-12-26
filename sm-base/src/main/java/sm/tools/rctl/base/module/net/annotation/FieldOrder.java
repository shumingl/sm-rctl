package sm.tools.rctl.base.module.net.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldOrder {
    int value();
}
