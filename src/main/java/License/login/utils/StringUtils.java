package License.login.utils;

import java.util.Collection;

public class StringUtils {
    /**
     * string에 내용이 있는지 확인
     *
     * @param str
     * @return
     */
    public static boolean notEmpty(String str) {
        if (str == null) {
            return false;
        } else return !str.isEmpty();
    }

    public static boolean isEmpty(String str) {

        if (str == null) {
            return true;
        } else return str.trim().isEmpty();
    }

    public static String getOrDefault(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public static String[] array(String... strArray) {
        return strArray;
    }

    public static String[] toArray(Collection<String> list) {
        String[] ret = new String[list.size()];
        int idx = 0;
        for (String s : list) {
            ret[idx] = s;
            idx++;
        }
        return ret;
    }

    public static String select(String first, String second) {
        if (notEmpty(first)) {
            return first;
        } else if (notEmpty(second)) {
            return second;
        } else {
            return first;
        }
    }

    public static boolean match(String query, String reference) {


        if (reference == null || query == null) {
            return false;
        } else if (reference.isEmpty()) {
            return false;
        } else return query.equals(reference);
    }

    public static boolean matches(String query, String... referenceList) {
        if (referenceList == null) {
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.equals(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(String query, Collection<String> referenceList) {
        if (referenceList == null) {
            return false;
        }
        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.equals(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String query, String... referenceList) {
        if (referenceList == null) {
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.contains(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String query, Collection<String> referenceList) {
        if (referenceList == null) {
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.contains(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startWith(String query, String... referenceList) {
        if (referenceList == null) {
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.startsWith(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startWith(String query, Collection<String> referenceList) {
        if (referenceList==null){
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.startsWith(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endWith(String query, String... referenceList) {
        if (referenceList==null){
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.endsWith(reference)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endWith(String query, Collection<String> referenceList) {
        if (referenceList==null){
            return false;
        }

        for (String reference : referenceList) {
            if (reference == null) {
                continue;
            } else if (reference.isEmpty()) {
                continue;
            } else if (query.endsWith(reference)) {
                return true;
            }
        }
        return false;
    }
}
