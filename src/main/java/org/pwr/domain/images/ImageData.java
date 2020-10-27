package org.pwr.domain.images;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.pwr.domain.buckets.MultipartBody;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class ImageData extends MultipartBody {

    @JsonProperty("name")
    @FormParam("name")
    @PartType(MediaType.TEXT_PLAIN)
    private String name;

    @JsonProperty("shortDescription")
    @FormParam("shortDescription")
    @PartType(MediaType.TEXT_PLAIN)
    private String shortDescription;

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }
}
