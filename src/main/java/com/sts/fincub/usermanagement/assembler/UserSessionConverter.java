package com.sts.fincub.usermanagement.assembler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sts.fincub.usermanagement.entity.UserSession;
import com.sts.fincub.usermanagement.utils.JSONUtils;
import org.json.JSONObject;

public class UserSessionConverter {
    public static UserSession convert(Object object) throws JsonProcessingException {
        JSONObject jsonObject = JSONUtils.toJSON(object);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return  mapper.readValue(jsonObject.toString(), UserSession.class);

    }
}