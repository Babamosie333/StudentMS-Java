# Student Management System (Java + MongoDB Atlas)

A simple, easy-to-explain Java Swing desktop app that manages student
records (Add / View / Update / Delete / Search), storing data in
MongoDB Atlas (cloud database) instead of a local SQL database.

## Why this is easy to explain in a viva
- Only 6 Java files, each with one clear job:
  - `Main.java` — starts the app
  - `ConnectionView.java` — screen to enter your MongoDB Atlas URI
  - `ManagementView.java` — the main screen (table + form + buttons)
  - `DBHandler.java` — the only file that talks to MongoDB (all CRUD)
  - `Student.java` — plain data holder for one student
  - `Theme.java` / `RoundedButton.java` — just UI styling
- No SQL, no complex joins — each student is one document, so it's
  simple to describe: "we save a JSON-like object per student."
- One collection (`students`) inside your database.

## How to get a free MongoDB Atlas connection string
1. Go to https://www.mongodb.com/cloud/atlas/register and make a free account.
2. Create a free (M0) cluster.
3. Under "Database Access", create a database user with a username/password.
4. Under "Network Access", add IP `0.0.0.0/0` (allow from anywhere) — fine for a college project.
5. Click "Connect" on your cluster → "Drivers" → copy the connection string. It looks like:
   ```
   mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/
   ```
6. Replace `<username>` and `<password>` with your real credentials.

## How to run
Requires Java 17+ and Maven installed.

```bash
cd StudentMS
mvn compile exec:java
```

On launch:
1. Paste your Atlas connection string and a database name (any name — it
   will be created automatically, e.g. `student_management`).
2. Click **Connect**.
3. Use the form on the left to add students, click any row to load it
   into the form for editing/deleting, and use the search box to filter
   by name or roll number.

## Building a runnable JAR (optional, for submission/demo)
```bash
mvn package
java -jar target/StudentMS-1.0.0.jar
```

## Features
- Add / View / Update / Delete student records
- Search by name or roll number
- **Students are always displayed sorted by Roll Number** (ascending) — handled in the database query itself (`Sorts.ascending("rollNo")`), so newly added students automatically appear in the right position.
- **Export Report** button — saves the currently displayed student list as a `.csv` file (opens in Excel/Sheets), useful for handing in a printed report or backup.
- **Dashboard summary cards** — Total Students, Average Marks, Highest Marks, Courses Offered, computed live from the data (no extra DB fields needed).
- **Auto-calculated Grade badges** — a colored pill (A+/A/B/C/Fail) computed on the fly from the Marks field, no schema change.
- **Course-wise bar chart** — click "View Chart" to see a simple bar chart of students per course, drawn with plain Java2D (no external chart library).
- **Toast notifications** — small auto-dismissing confirmation banners (instead of blocking popups) for Add/Update/Delete/Export actions.
- **Avatar initials** next to each student's name in the table, similar to Gmail-style contact avatars.

## Possible "future scope" additions (mention in your report, don't need to build)
- Login/authentication for admin
- Export student list to PDF/Excel
- Attendance tracking module
- Charts for marks/performance (JavaFX charts)
# StudentMS-Java
