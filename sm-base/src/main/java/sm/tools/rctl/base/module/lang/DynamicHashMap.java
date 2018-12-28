package sm.tools.rctl.base.module.lang;

import sm.tools.rctl.base.utils.ObjectUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DynamicHashMap<K, V> extends HashMap<K, V> {

    public DynamicHashMap() {
        super();
    }

    public DynamicHashMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    public <T> T getObject(K key) {
        return (T) this.get(key);
    }

    public String getString(K key) {
        return ObjectUtil.getString(this.get(key));
    }

    public Character getChar(K key) {
        return ObjectUtil.getChar(this.get(key));
    }

    public Byte getByte(K key) {
        return ObjectUtil.getByte(this.get(key));
    }

    public Integer getInteger(K key) {
        return ObjectUtil.getInteger(this.get(key));
    }

    public Short getShort(K key) {
        return ObjectUtil.getShort(this.get(key));
    }

    public Long getLong(K key) {
        return ObjectUtil.getLong(this.get(key));
    }

    public Float getFloat(K key) {
        return ObjectUtil.getFloat(this.get(key));
    }

    public Double getDouble(K key) {
        return ObjectUtil.getDouble(this.get(key));
    }

    public BigDecimal getDecimal(K key) {
        return ObjectUtil.getDecimal(this.get(key));
    }

    public Boolean getBoolean(K key) {
        return ObjectUtil.getBoolean(this.get(key));
    }

}
