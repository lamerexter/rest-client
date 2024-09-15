package org.orthodoxengineering.restclient;

import java.util.Objects;

public class SimpleBean {
    private String stringProperty;

    public SimpleBean() {
        this(null);
    }

    public SimpleBean(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBean that = (SimpleBean) o;
        return Objects.equals(stringProperty, that.stringProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringProperty);
    }
}
