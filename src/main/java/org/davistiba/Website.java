package org.davistiba;

import com.google.gson.annotations.SerializedName;

/**
 * For JSON conversion
 */
public class Website {
    @SerializedName(value = "url")
    private String url;

    public String getUrl() {
        return url;
    }

}
