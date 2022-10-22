package com.optimagrowth.licensingservice.models;

import com.optimagrowth.licensingservice.models.UserContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContextHolder {
    public static final ThreadLocal<UserContext> userContext
            = new ThreadLocal<UserContext>();

    public static final UserContext getContext() {
        UserContext context = userContext.get();
        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);
        }
        return userContext.get();
    }

    public static final UserContext createEmptyContext() {
        return new UserContext();
    }
}
