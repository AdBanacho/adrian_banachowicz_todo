package ch.cern.todo.profile;

import ch.cern.todo.exceptions.NotPermissionException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfileService {

    private static final Map<String, String> MOCKED_FULL_NAMES = new HashMap<>();

    static {
        MOCKED_FULL_NAMES.put("adriBana", "Adrian Banachowicz");
        MOCKED_FULL_NAMES.put("mareNowa", "Marek Nowak");
    }


    public String getFullName(String profileId){
        return MOCKED_FULL_NAMES.get(profileId);
    }

    public static void validationSameUser(List<String> profileIds, String fieldName){
        String loggedInUsername = getLoggedInUsername();
        if (!profileIds.contains(loggedInUsername)){
             throw new NotPermissionException("User "+ loggedInUsername + " not allowed to change" + fieldName);
         };
    }

    public static String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            return authentication.getPrincipal().toString();
        }
    }

}
