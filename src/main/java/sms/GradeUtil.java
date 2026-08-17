package sms;

/**
 * Turns a raw "marks" string into a Grade letter.
 * Kept as plain logic (no DB field needed) so it's easy to explain:
 * "we calculate the grade on the fly from marks, we don't store it."
 */
public class GradeUtil {

    public static String gradeFor(String marksText) {
        if (marksText == null || marksText.isBlank()) return "-";
        try {
            double marks = Double.parseDouble(marksText.trim());
            if (marks >= 90) return "A+";
            if (marks >= 75) return "A";
            if (marks >= 60) return "B";
            if (marks >= 40) return "C";
            return "Fail";
        } catch (NumberFormatException e) {
            return "-"; // marks wasn't a plain number, skip grading
        }
    }

    public static java.awt.Color colorFor(String grade) {
        return switch (grade) {
            case "A+", "A" -> Theme.SUCCESS;
            case "B" -> Theme.PRIMARY;
            case "C" -> Theme.WARNING;
            case "Fail" -> Theme.DANGER;
            default -> Theme.subtext();
        };
    }
}
