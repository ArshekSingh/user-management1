package com.sts.finncub.usermanagement.assembler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.util.JSONUtils;
import org.json.JSONObject;

public class UserSessionConverter {
    public static UserSession convert(Object object) throws JsonProcessingException {
        JSONObject jsonObject = JSONUtils.toJSON(object);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserSession userSession = mapper.readValue(jsonObject.toString(), UserSession.class);
        UserSession userSessionResponse = mapper.readValue(userSession.getUserSessionJSON(), UserSession.class);
        return userSessionResponse;

    }
}