package com.brunodn.udemy.auth;

import com.brunodn.udemy.auth.persistence.UserEntity;
import com.brunodn.udemy.auth.persistence.UserRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

@Singleton
public class JDBCAuthenticationProvider implements AuthenticationProvider{

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthenticationProvider.class);
    final UserRepository users ;
    public JDBCAuthenticationProvider(final UserRepository users) {
        this.users = users;
    }


    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable final HttpRequest<?> httpRequest,
            final AuthenticationRequest<?, ?> authenticationRequest) {


        return Flowable.create(emitter -> {
            final String identity = (String) authenticationRequest.getIdentity();
            LOG.debug("User {} tries to login...", identity);

            final Optional<UserEntity> mayBeUser = users.findByEmail(identity);

            if(mayBeUser.isPresent()){
                LOG.debug("Found user: {}", mayBeUser.get().getEmail());
                final String secret = (String) authenticationRequest.getSecret();

                if(secret.equals(mayBeUser.get().getPassword())){
                    LOG.debug("User is logged in.");
                    final HashMap<String,Object> attributes = new HashMap<>();
                    attributes.put("hair_color", "brown");
                    attributes.put("language","en");
                    emitter.onNext(AuthenticationResponse.success((String)mayBeUser.get().getEmail(),attributes));
                    emitter.onComplete();
                }else{
                    LOG.debug("Password is incorrect");
                }
            }else{
                LOG.debug("No users found with e-mail {}",identity);
            }
            emitter.onError(new AuthenticationException(new AuthenticationFailed("Login or password are wrong")));
        }, BackpressureStrategy.ERROR);
    }
}
