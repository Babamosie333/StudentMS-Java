package sms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Main screen: shows all students in a table and lets the user
 * add / update / delete / search them. All DB work goes through DBHandler.
 */
public class ManagementView extends JFrame {

    private final DBHandler dbHandler;

    private JTextField nameField, rollField, courseField, semField, marksField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private String selectedId = null; // set when a row is clicked, used for update/delete
    private List<Student> currentList;

    // Dashboard summary labels (need to be updated on refresh)
    private JLabel totalValueLabel, avgValueLabel, highValueLabel, courseValueLabel;

    public ManagementView(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        buildUI();
        refreshTable();
    }

    /** Builds (or rebuilds, e.g. after a theme toggle) the entire window content. */
    private void buildUI() {
        getContentPane().removeAll();
        setTitle("Student Management System");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.background());
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormCard(), BorderLayout.WEST);

        JPanel centerStack = new JPanel(new BorderLayout());
        centerStack.setBackground(Theme.background());
        centerStack.add(buildDashboardCards(), BorderLayout.NORTH);
        centerStack.add(buildTableCard(), BorderLayout.CENTER);
        add(centerStack, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // ---------- HEADER ----------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.card());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.border()),
                new EmptyBorder(18, 25, 18, 25)));

        JLabel title = new JLabel("Student Management System");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.text());
        header.add(title, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_LABEL);
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border(), 1, true),
                new EmptyBorder(4, 10, 4, 10)));

        RoundedButton searchBtn = new RoundedButton("Search", Theme.PRIMARY, Theme.PRIMARY_DARK);
        searchBtn.setPreferredSize(new Dimension(90, 34));
        searchBtn.addActionListener(e -> onSearch());

        RoundedButton clearBtn = new RoundedButton("Show All", Theme.subtext(), Theme.text());
        clearBtn.setPreferredSize(new Dimension(90, 34));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        RoundedButton exportBtn = new RoundedButton("Export Report", Theme.SUCCESS, new Color(0x25, 0x8A, 0x57));
        exportBtn.setPreferredSize(new Dimension(130, 34));
        exportBtn.addActionListener(e -> onExportCsv());

        RoundedButton chartBtn = new RoundedButton("View Chart", new Color(0x8E, 0x5A, 0xE0), new Color(0x6E, 0x3A, 0xC0));
        chartBtn.setPreferredSize(new Dimension(110, 34));
        chartBtn.addActionListener(e -> onViewChart());

        LinkIconButton githubBtn = new LinkIconButton("GH", "https://github.com/Babamosie333/StudentMS-Java.git", "View on GitHub", "/icons/github.png");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Theme.card());
        actionPanel.add(searchField);
        actionPanel.add(searchBtn);
        actionPanel.add(clearBtn);
        actionPanel.add(chartBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(githubBtn);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    // ---------- DASHBOARD SUMMARY CARDS ----------

    private JPanel buildDashboardCards() {
        JPanel wrapper = new JPanel(new GridLayout(1, 4, 15, 0));
        wrapper.setBackground(Theme.background());
        wrapper.setBorder(new EmptyBorder(20, 10, 0, 20));

        totalValueLabel = new JLabel("0");
        avgValueLabel = new JLabel("0");
        highValueLabel = new JLabel("0");
        courseValueLabel = new JLabel("0");

        wrapper.add(statCard("Total Students", totalValueLabel, Theme.PRIMARY));
        wrapper.add(statCard("Average Marks", avgValueLabel, Theme.SUCCESS));
        wrapper.add(statCard("Highest Marks", highValueLabel, Theme.WARNING));
        wrapper.add(statCard("Courses Offered", courseValueLabel, new Color(0x8E, 0x5A, 0xE0)));

        return wrapper;
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.card());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(14, 16, 14, 16)));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(Theme.text());
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel captionLabel = new JLabel(label);
        captionLabel.setFont(Theme.FONT_LABEL);
        captionLabel.setForeground(Theme.subtext());
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(captionLabel);
        return card;
    }

    private void updateDashboard(List<Student> students) {
        totalValueLabel.setText(String.valueOf(students.size()));

        double sum = 0;
        double max = 0;
        int numericCount = 0;
        java.util.Set<String> courses = new java.util.HashSet<>();

        for (Student s : students) {
            if (s.getCourse() != null && !s.getCourse().isBlank()) {
                courses.add(s.getCourse().trim());
            }
            try {
                double m = Double.parseDouble(s.getMarks().trim());
                sum += m;
                max = Math.max(max, m);
                numericCount++;
            } catch (Exception ignored) {
                // marks not numeric, skip it in the average/highest calculation
            }
        }

        avgValueLabel.setText(numericCount == 0 ? "-" : String.format("%.1f", sum / numericCount));
        highValueLabel.setText(numericCount == 0 ? "-" : String.format("%.0f", max));
        courseValueLabel.setText(String.valueOf(courses.size()));
    }

    // ---------- LEFT FORM CARD ----------

    private JPanel buildFormCard() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.background());
        outer.setBorder(new EmptyBorder(20, 20, 20, 10));
        outer.setPreferredSize(new Dimension(300, 0));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.card());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border(), 1, true),
                new EmptyBorder(22, 20, 22, 20)));

        JLabel formTitle = new JLabel("Student Details");
        formTitle.setFont(Theme.FONT_BOLD);
        formTitle.setForeground(Theme.text());
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formTitle);
        card.add(Box.createVerticalStrut(18));

        nameField = addFormField(card, "Full Name");
        rollField = addFormField(card, "Roll Number");
        courseField = addFormField(card, "Course");
        semField = addFormField(card, "Semester");
        marksField = addFormField(card, "Marks");

        card.add(Box.createVerticalStrut(10));

        RoundedButton addBtn = new RoundedButton("Add Student", Theme.PRIMARY, Theme.PRIMARY_DARK);
        styleFullWidthButton(addBtn);
        addBtn.addActionListener(e -> onAdd());

        RoundedButton updateBtn = new RoundedButton("Update Selected", Theme.SUCCESS, new Color(0x25, 0x8A, 0x57));
        styleFullWidthButton(updateBtn);
        updateBtn.addActionListener(e -> onUpdate());

        RoundedButton deleteBtn = new RoundedButton("Delete Selected", Theme.DANGER, new Color(0xC2, 0x3B, 0x3B));
        styleFullWidthButton(deleteBtn);
        deleteBtn.addActionListener(e -> onDelete());

        RoundedButton clearFormBtn = new RoundedButton("Clear Form", Theme.subtext(), Theme.text());
        styleFullWidthButton(clearFormBtn);
        clearFormBtn.addActionListener(e -> clearForm());

        card.add(addBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(updateBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(deleteBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(clearFormBtn);

        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private JTextField addFormField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_LABEL);
        label.setForeground(Theme.subtext());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(Theme.FONT_LABEL);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border(), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(3));
        parent.add(field);
        parent.add(Box.createVerticalStrut(12));
        return field;
    }

    private void styleFullWidthButton(RoundedButton btn) {
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(100, 40));
    }

    // ---------- CENTER TABLE CARD ----------

    private JPanel buildTableCard() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.background());
        outer.setBorder(new EmptyBorder(15, 10, 20, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.card());
        card.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));

        String[] columns = {"Name", "Roll No", "Course", "Semester", "Marks", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(Theme.FONT_LABEL);
        table.setRowHeight(36);
        table.setSelectionBackground(new Color(0xE8, 0xEE, 0xFF));
        table.setSelectionForeground(Theme.text());
        table.setGridColor(Theme.border());
        table.getTableHeader().setFont(Theme.FONT_BOLD);
        table.getTableHeader().setBackground(Theme.card());
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        table.getColumnModel().getColumn(0).setCellRenderer(new AvatarNameRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new GradeBadgeRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedRowIntoForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ---------- DATA / EVENTS ----------

    private void refreshTable() {
        currentList = dbHandler.getAllStudents();
        populateTable(currentList);
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            String grade = GradeUtil.gradeFor(s.getMarks());
            tableModel.addRow(new Object[]{s.getName(), s.getRollNo(), s.getCourse(), s.getSemester(), s.getMarks(), grade});
        }
        updateDashboard(students);
        clearForm();
    }

    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        Student s = currentList.get(row);
        selectedId = s.getId();
        nameField.setText(s.getName());
        rollField.setText(s.getRollNo());
        courseField.setText(s.getCourse());
        semField.setText(s.getSemester());
        marksField.setText(s.getMarks());
    }

    private void clearForm() {
        selectedId = null;
        nameField.setText("");
        rollField.setText("");
        courseField.setText("");
        semField.setText("");
        marksField.setText("");
        table.clearSelection();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty() || rollField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Roll Number are required.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Student buildStudentFromForm() {
        return new Student(
                selectedId,
                nameField.getText().trim(),
                rollField.getText().trim(),
                courseField.getText().trim(),
                semField.getText().trim(),
                marksField.getText().trim()
        );
    }

    private void onAdd() {
        if (!validateForm()) return;
        dbHandler.addStudent(buildStudentFromForm());
        refreshTable();
        Toast.success(this, "Student added successfully");
    }

    private void onUpdate() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a student row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        dbHandler.updateStudent(buildStudentFromForm());
        refreshTable();
        Toast.success(this, "Student updated successfully");
    }

    private void onDelete() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a student row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dbHandler.deleteStudent(selectedId);
            refreshTable();
            Toast.success(this, "Student deleted");
        }
    }

    /** Opens a small dialog showing a bar chart of students per course. */
    private void onViewChart() {
        JDialog dialog = new JDialog(this, "Students per Course", true);
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.card());
        dialog.add(new ChartPanel(currentList == null ? List.of() : currentList));
        dialog.setVisible(true);
    }

    /** Exports whatever is currently shown in the table to a CSV file, chosen by the user. */
    private void onExportCsv() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student data to export.", "Nothing to Export", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Student Report");
        chooser.setSelectedFile(new java.io.File("student_report.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".csv");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Name,Roll No,Course,Semester,Marks,Grade\n");
            for (Student s : currentList) {
                writer.append(csvSafe(s.getName())).append(",")
                        .append(csvSafe(s.getRollNo())).append(",")
                        .append(csvSafe(s.getCourse())).append(",")
                        .append(csvSafe(s.getSemester())).append(",")
                        .append(csvSafe(s.getMarks())).append(",")
                        .append(csvSafe(GradeUtil.gradeFor(s.getMarks()))).append("\n");
            }
            Toast.success(this, "Report exported successfully");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Wraps a value in quotes if it contains a comma, so the CSV stays valid. */
    private String csvSafe(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void onSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        currentList = dbHandler.searchStudents(keyword);
        populateTable(currentList);
    }
}