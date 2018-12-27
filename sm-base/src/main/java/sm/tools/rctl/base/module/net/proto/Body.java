package sm.tools.rctl.base.module.net.proto;

public class Body<T> {
    private String bodyClass;
    private T object;

    public Body() {
    }

    public Body(T object) {
        this.object = object;
        this.bodyClass = object.getClass().getName();
    }

    public String getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
