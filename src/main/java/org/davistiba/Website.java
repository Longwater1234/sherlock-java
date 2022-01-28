package org.davistiba;

import java.util.Objects;

/**
 * For JSON conversion
 */
public class Website {
    private String url;

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Website website = (Website) o;

        return Objects.equals(url, website.url);
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
