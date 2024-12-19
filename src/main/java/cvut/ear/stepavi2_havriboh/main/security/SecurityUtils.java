package cvut.ear.stepavi2_havriboh.main.security;

import cvut.ear.stepavi2_havriboh.main.model.User;
import cvut.ear.stepavi2_havriboh.main.security.model.UserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Gets the currently authenticated user.
     *
     * @return Current user
     */
    public static User getCurrentUser() {
        final UserDetails ud = getCurrentUserDetails();
        return ud != null ? ud.getUser() : null;
    }

    /**
     * Gets details of the currently authenticated user.
     *
     * @return Currently authenticated user details or null, if no one is currently authenticated
     */
    public static UserDetails getCurrentUserDetails() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().getPrincipal() instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * Checks whether the current authentication token represents an anonymous user.
     *
     * @return Whether current authentication is anonymous
     */
    public static boolean isAuthenticatedAnonymously() {
        return getCurrentUserDetails() == null;
    }
}

