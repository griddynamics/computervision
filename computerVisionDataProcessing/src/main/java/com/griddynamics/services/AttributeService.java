package com.griddynamics.services;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.griddynamics.pojo.starsDomain.AttributeType;
import com.griddynamics.pojo.starsDomain.Result;
import com.griddynamics.pojo.starsDomain.StarsAttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author npakhomova on 1/7/16.
 */
public class AttributeService implements Serializable {

    public static Logger logger = LoggerFactory.getLogger(AttributeService.class);

    public static final String STARS_ATTRIBUTE_SERVICE_ATTRIBUTE_TYPES = "/StarsAttributeService/attributeTypes";
    public static final String STARS_ATTRIBUTE_SERVICE_ATTRIBUTE_TYPE = "/StarsAttributeService/attributeType/";
    public static final Map<String, String> COLOR_NORMAL_ATTR_FOR_UPC = new HashMap<>();

    //todo think about more efficient colorNormalCache
    private final Map<String, Map<BigDecimal, StarsAttributeValue>> colorNormalCache = new HashMap<>();
    private final Map<Long, AttributeType> attributeTypesCache = new HashMap<>();
    private final String serviceUrl;

    private Client client;

    static {
        COLOR_NORMAL_ATTR_FOR_UPC.put("contentTypeId", "105");
        COLOR_NORMAL_ATTR_FOR_UPC.put("refTag", AttributeTypeConstants.colorNormal.toString());
    }

    public AttributeService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Return normalized value of color
     *
     * @param upcFeatureNormalColorId
     * @return
     */
    public String getNormalColorById(BigDecimal upcFeatureNormalColorId) {
        // try get Attribute from colorNormalCache
        String key = AttributeTypeConstants.colorNormal +"upc";
        Map<BigDecimal, StarsAttributeValue> attributeTypeMap = colorNormalCache.get(key);

        if (attributeTypeMap == null) {
            // if not - try to get from service
            attributeTypeMap = getAttributeTypeFromService();
            if (attributeTypeMap != null) {
                colorNormalCache.put(key, attributeTypeMap);
            }
        }

        StarsAttributeValue starsAttributeValue = attributeTypeMap.get(upcFeatureNormalColorId);
        return starsAttributeValue != null ? starsAttributeValue.getDisplayValue() : null;
    }

    public AttributeType getStarsAttributeType(long attributeId) {
        AttributeType attributeType = attributeTypesCache.get(attributeId);
        if (attributeType == null) {
            Result result =
                    executeGetMethod(Result.class, STARS_ATTRIBUTE_SERVICE_ATTRIBUTE_TYPE + attributeId);
            attributeType = result.getData().getAttributeType();
            attributeTypesCache.put(attributeId, attributeType);
        }
        return attributeType;
    }

    private Map<BigDecimal, StarsAttributeValue> getAttributeTypeFromService() {
        Result result = executeGetMethod(
                Result.class,
                STARS_ATTRIBUTE_SERVICE_ATTRIBUTE_TYPES,
                COLOR_NORMAL_ATTR_FOR_UPC
        );
        if (result == null) {
            return null;
        }
        AttributeType attributeType = result.getData().getAttributeType();
        return Maps.uniqueIndex(attributeType.getAttributeValues().getAttributeValues(),
                new Function<StarsAttributeValue, BigDecimal>() {
                    @Nullable
                    public BigDecimal apply(@Nullable StarsAttributeValue input) {
                        return input != null ? BigDecimal.valueOf(input.getId()) : null;
                    }
                });
    }

    private <P> P executeGetMethod(Class<P> clazz, String basePath) {
        return executeGetMethod(clazz, basePath, Collections.<String, String>emptyMap());
    }


    private <P> P executeGetMethod(Class<P> clazz, String basePath, Map<String, String> params) {
        P result = null;
        try {
            // lazy client initialization
            if (client == null) {
                client = Client.create();
            }

            WebResource webResource = client.resource(serviceUrl).path(basePath);

            for (String key : params.keySet()) {
                webResource = webResource.queryParam(key, params.get(key));
            }
            // todo check on status
            result = webResource.get(clazz);

        } catch (Exception e) {
            logger.error("Problems with attribute service", e);
        }
        return result;
    }

    public enum AttributeTypeConstants {
        colorNormal,
        sizeNormal,
        haltCode,
        projectDivision,
        projectGmm,
        imageType,
        braBand
    }
}
