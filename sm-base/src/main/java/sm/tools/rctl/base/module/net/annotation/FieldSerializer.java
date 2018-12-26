package sm.tools.rctl.base.module.net.annotation;

import sm.tools.rctl.base.module.net.serialize.Serializer;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldSerializer {
    Class<Serializer> value();
}
