
package org.util;

import org.exceptions.ValidationException;

@FunctionalInterface // This interface receives a generic of type Validation and returns nothing
public interface Validation<T> {
    void validate(T value) throws ValidationException;
}
