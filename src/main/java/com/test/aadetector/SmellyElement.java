package com.test.aadetector;

import java.util.Map;

public abstract class SmellyElement {
    public abstract String getElementName();

    public abstract boolean getHasSmell();

    public abstract Map<String, String> getData();
}
