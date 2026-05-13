package lostandfound.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All dropdown data for the BISU Candijay Campus Lost & Found System.
 * Edit this file to add/remove colleges, programs, or categories.
 */
public class DropdownData {

    // ── BISU Candijay Colleges ────────────────────────────────────────────────
    public static final String[] COLLEGES = {
        "-- Select College --",
        "CBM (College of Business & Management)",
        "CFMS (College of Fisheries & Marine Sciences)",
        "CoS (College of Sciences)",
        "CTE (College of Teacher Education)"
    };

    // ── Programs per College ──────────────────────────────────────────────────
    public static final Map<String, String[]> PROGRAMS_BY_COLLEGE = new LinkedHashMap<>();
    static {
        PROGRAMS_BY_COLLEGE.put("-- Select College --",
            new String[]{"-- Select Program --"});

        PROGRAMS_BY_COLLEGE.put("CBM (College of Business & Management)",
            new String[]{"-- Select Program --", "BSHM", "BSOAD"});

        PROGRAMS_BY_COLLEGE.put("CFMS (College of Fisheries & Marine Sciences)",
            new String[]{"-- Select Program --", "BSF", "BSMB"});

        PROGRAMS_BY_COLLEGE.put("CoS (College of Sciences)",
            new String[]{"-- Select Program --", "BSCS", "BSES"});

        PROGRAMS_BY_COLLEGE.put("CTE (College of Teacher Education)",
            new String[]{"-- Select Program --", "BEED", "BSED English",
                "BSED Filipino", "BSED Mathematics", "BSED Science"});
    }

    // ── Year Levels ───────────────────────────────────────────────────────────
    public static final String[] YEAR_LEVELS = {
        "-- Select Year --", "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
    };

    public static int yearToInt(String label) {
        return switch (label) {
            case "1st Year" -> 1; case "2nd Year" -> 2; case "3rd Year" -> 3;
            case "4th Year" -> 4; case "5th Year" -> 5; default -> 0;
        };
    }

    public static String intToYear(int y) {
        return switch (y) {
            case 1 -> "1st Year"; case 2 -> "2nd Year"; case 3 -> "3rd Year";
            case 4 -> "4th Year"; case 5 -> "5th Year"; default -> "N/A";
        };
    }

    // ── Item Categories ───────────────────────────────────────────────────────
    public static final String[] ITEM_CATEGORIES = {
        "Electronics", "Clothing", "Books/Notes", "ID/Cards", "Keys",
        "Bags", "Jewelry", "Sports Equipment", "Documents",
        "Personal Accessories", "Marine/Fishing Equipment", "Other"
    };
}