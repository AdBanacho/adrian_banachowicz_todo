package ch.cern.todo.profile;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProfileService {

    private static final Map<String, String> MOCKED_FULL_NAMES = new HashMap<>();

    static {
        MOCKED_FULL_NAMES.put("adriBana", "Adrian Banachowicz");
        MOCKED_FULL_NAMES.put("testTest", "Test Test");
    }


    public String getFullName(String profileId){
        return MOCKED_FULL_NAMES.get(profileId);
    }
}
