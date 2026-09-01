// Mirrors GradeUtil.java from the desktop app so both interfaces
// calculate grades the exact same way from the "marks" field.

export function gradeFor(marksText) {
  if (!marksText || marksText.trim() === "") return "-";
  const marks = parseFloat(marksText);
  if (Number.isNaN(marks)) return "-";
  if (marks >= 90) return "A+";
  if (marks >= 75) return "A";
  if (marks >= 60) return "B";
  if (marks >= 40) return "C";
  return "Fail";
}

export function colorForGrade(grade) {
  switch (grade) {
    case "A+":
    case "A":
      return "text-success";
    case "B":
      return "text-primary";
    case "C":
      return "text-warning";
    case "Fail":
      return "text-danger";
    default:
      return "text-subtext";
  }
}
