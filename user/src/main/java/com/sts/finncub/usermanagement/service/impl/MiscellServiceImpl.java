package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.entity.MiscellaneousService;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.repository.MiscellaneousServiceRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.service.MiscellService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.sts.finncub.core.constants.Constant.SUCCESS;

@Service
@Slf4j
@AllArgsConstructor
public class MiscellServiceImpl implements MiscellService {

    private final MiscellaneousServiceRepository miscellaneousServiceRepository;
    private final UserCredentialService userCredentialService;

    @Override
    public Response getMiscellaneousNames() {
        return new Response(SUCCESS, miscellaneousServiceRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public Response updateMiscellaneousNames(String key, String value) {
        UserSession userSession = userCredentialService.getUserSession();
        MiscellaneousService byKey = miscellaneousServiceRepository.findByKey(key);
        if(byKey == null) {
            return new Response("Provided key is not present", HttpStatus.BAD_REQUEST);
        }
        byKey.setValue(value);
        byKey.setUpdatedBy(userSession.getUserId());
        byKey.setUpdatedOn(LocalDateTime.now());
        miscellaneousServiceRepository.save(byKey);
        log.info("data updated successfully for key {}", key);
        return new Response(SUCCESS, HttpStatus.OK);
    }

    @Override
    public Response getKeyValue(String key) {
        MiscellaneousService byKey = miscellaneousServiceRepository.findByKey(key);
        if(byKey == null) {
            return new Response("Provided key is not present", HttpStatus.BAD_REQUEST);
        }
        return new Response(SUCCESS, byKey, HttpStatus.OK);
    }
}
