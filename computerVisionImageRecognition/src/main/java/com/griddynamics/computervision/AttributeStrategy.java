package com.griddynamics.computervision;

import java.io.File;
import java.io.IOException;

/**
 * Created by npakhomova on 4/19/16.
 */
public interface AttributeStrategy {
    public boolean doesApply (File picture) throws IOException;
}
