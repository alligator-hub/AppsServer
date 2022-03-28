package appsserver.enums;

import java.util.ArrayList;
import java.util.List;

public enum Roles {
    ADMIN,
    ROOT;

    public static List<Roles> getRolesByNames(List<String> roleNameList) {
        List<Roles> res = new ArrayList<>();

        for (String s : roleNameList) {
            for (Roles value : Roles.values()) {
                if (s.equals(value.name())) {
                    res.add(value);
                    break;
                }
            }
        }
        return res;
    }
}
