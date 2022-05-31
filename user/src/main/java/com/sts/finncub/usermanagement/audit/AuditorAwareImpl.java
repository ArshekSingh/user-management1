package com.sts.finncub.usermanagement.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sts.finncub.core.entity.UserSession;

/**
 * @author Shahzad Hussain
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserSession userSession = (UserSession) principal;
		return Optional.of(userSession.getUserId());
	}
}
