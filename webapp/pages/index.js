import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/router";
import { gradeFor } from "../lib/gradeUtil";

const emptyForm = { id: null, name: "", rollNo: "", course: "", semester: "", marks: "" };

const AVATAR_COLORS = ["#2FA5A9", "#E0574F", "#4A6CF2", "#E0A02E", "#7C4FE0", "#2EA56A"];

function avatarColorFor(name) {
  const idx = (name || "").charCodeAt(0) % AVATAR_COLORS.length;
  return AVATAR_COLORS[Math.abs(idx) || 0];
}

function initialsFor(name) {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0][0].toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

function gradeBadgeColor(grade) {
  switch (grade) {
    case "A+":
    case "A":
      return "#2EA56A";
    case "B":
      return "#4A6CF2";
    case "C":
      return "#E09A2E";
    case "Fail":
      return "#E04F4F";
    default:
      return "#9AA1AC";
  }
}

export default function Home() {
  const router = useRouter();
  const [students, setStudents] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [search, setSearch] = useState("");
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);
  const [checkingConn, setCheckingConn] = useState(true);

  async function fetchStudents(query = "") {
    setLoading(true);
    const url = query ? `/api/students?search=${encodeURIComponent(query)}` : "/api/students";
    const res = await fetch(url);

    if (res.status === 401) {
      router.replace("/connect");
      return;
    }

    const data = await res.json();
    setStudents(data);
    setLoading(false);
  }

  useEffect(() => {
    fetchStudents().finally(() => setCheckingConn(false));
  }, []);

  async function handleDisconnect() {
    await fetch("/api/disconnect", { method: "POST" });
    router.push("/connect");
  }

  function showToast(message, type = "success") {
    setToast({ message, type });
    setTimeout(() => setToast(null), 2500);
  }

  async function handleSearch(e) {
    e.preventDefault();
    fetchStudents(search);
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function selectRow(student) {
    setSelectedId(student.id);
    setForm(student);
  }

  function clearForm() {
    setSelectedId(null);
    setForm(emptyForm);
  }

  async function handleAdd(e) {
    e.preventDefault();
    if (!form.name || !form.rollNo) {
      showToast("Name and Roll No. are required", "danger");
      return;
    }
    await fetch("/api/students", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });
    showToast("Student added");
    clearForm();
    fetchStudents(search);
  }

  async function handleUpdate() {
    if (!selectedId) {
      showToast("Select a student first", "danger");
      return;
    }
    await fetch(`/api/students/${selectedId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });
    showToast("Student updated");
    clearForm();
    fetchStudents(search);
  }

  async function handleDelete() {
    if (!selectedId) {
      showToast("Select a student first", "danger");
      return;
    }
    if (!confirm("Delete this student?")) return;
    await fetch(`/api/students/${selectedId}`, { method: "DELETE" });
    showToast("Student deleted", "danger");
    clearForm();
    fetchStudents(search);
  }

  const stats = useMemo(() => {
    const total = students.length;
    const marksArr = students
      .map((s) => parseFloat(s.marks))
      .filter((n) => !Number.isNaN(n));
    const avg = marksArr.length
      ? (marksArr.reduce((a, b) => a + b, 0) / marksArr.length).toFixed(1)
      : "0";
    const highest = marksArr.length ? Math.max(...marksArr) : 0;
    const courses = new Set(students.map((s) => s.course).filter(Boolean)).size;
    return { total, avg, highest, courses };
  }, [students]);

  if (checkingConn) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <p className="text-sm text-subtext">Checking database connection...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Top bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 border-b border-border bg-white px-6 py-4">
        <h1 className="text-2xl font-bold text-text">Student Management System</h1>
        <form onSubmit={handleSearch} className="flex flex-wrap items-center gap-2">
          <input
            className="w-56 rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
            placeholder=""
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button
            type="submit"
            className="rounded-lg bg-primary px-4 py-2 text-sm font-bold text-white hover:bg-primaryDark"
          >
            Search
          </button>
          <button
            type="button"
            onClick={() => fetchStudents("")}
            className="rounded-lg bg-[#6B7280] px-4 py-2 text-sm font-bold text-white hover:bg-[#5b6472]"
          >
            Show All
          </button>
          <button
            type="button"
            className="rounded-lg bg-[#7C4FE0] px-4 py-2 text-sm font-bold text-white hover:opacity-90"
          >
            View Chart
          </button>
          <button
            type="button"
            className="rounded-lg bg-success px-4 py-2 text-sm font-bold text-white hover:opacity-90"
          >
            Export Report
          </button>
          <a
            href="https://github.com/Babamosie333/StudentMS-Java.git"
            target="_blank"
            rel="noopener noreferrer"
            className="flex h-9 w-9 items-center justify-center rounded-full bg-[#181717] text-white hover:opacity-90"
            aria-label="StudentMS-Java on GitHub"
          >
            <svg viewBox="0 0 16 16" width="18" height="18" fill="currentColor">
              <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38
                0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13
                -.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66
                .07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15
                -.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27
                1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15
                0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48
                0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.01 8.01 0 0 0 16 8c0-4.42-3.58-8-8-8z"/>
            </svg>
          </a>
          <button
            type="button"
            onClick={handleDisconnect}
            className="rounded-lg border border-border px-4 py-2 text-sm font-bold text-subtext hover:bg-background"
          >
            Disconnect
          </button>
        </form>
      </div>

      <div className="flex flex-col gap-6 p-6 lg:flex-row">
        {/* Sidebar form */}
        <div className="w-full shrink-0 rounded-xl border border-border bg-white p-5 lg:w-72">
          <h2 className="mb-4 text-base font-bold text-text">Student Details</h2>

          <form onSubmit={handleAdd} className="flex flex-col gap-4">
            <div>
              <label className="mb-1 block text-sm text-subtext">Full Name</label>
              <input
                name="name"
                value={form.name}
                onChange={handleChange}
                className="w-full rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm text-subtext">Roll Number</label>
              <input
                name="rollNo"
                value={form.rollNo}
                onChange={handleChange}
                className="w-full rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm text-subtext">Course</label>
              <input
                name="course"
                value={form.course}
                onChange={handleChange}
                className="w-full rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm text-subtext">Semester</label>
              <input
                name="semester"
                value={form.semester}
                onChange={handleChange}
                className="w-full rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm text-subtext">Marks</label>
              <input
                name="marks"
                value={form.marks}
                onChange={handleChange}
                className="w-full rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-primary"
              />
            </div>

            <button
              type="submit"
              className="rounded-lg bg-primary py-2.5 text-sm font-bold text-white hover:bg-primaryDark"
            >
              Add Student
            </button>
          </form>

          <button
            type="button"
            onClick={handleUpdate}
            className="mt-3 w-full rounded-lg bg-success py-2.5 text-sm font-bold text-white hover:opacity-90"
          >
            Update Selected
          </button>
          <button
            type="button"
            onClick={handleDelete}
            className="mt-3 w-full rounded-lg bg-danger py-2.5 text-sm font-bold text-white hover:opacity-90"
          >
            Delete Selected
          </button>
          <button
            type="button"
            onClick={clearForm}
            className="mt-3 w-full rounded-lg bg-[#6B7280] py-2.5 text-sm font-bold text-white hover:opacity-90"
          >
            Clear
          </button>
        </div>

        {/* Right side */}
        <div className="flex-1">
          {/* Stat cards */}
          <div className="mb-6 grid grid-cols-2 gap-4 sm:grid-cols-4">
            <StatCard color="#4A6CF2" value={stats.total} label="Total Students" />
            <StatCard color="#2EA56A" value={stats.avg} label="Average Marks" />
            <StatCard color="#E09A2E" value={stats.highest} label="Highest Marks" />
            <StatCard color="#7C4FE0" value={stats.courses} label="Courses Offered" />
          </div>

          {/* Table */}
          <div className="overflow-hidden rounded-xl border border-border bg-white">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-border">
                <tr>
                  <th className="px-4 py-3 font-bold text-text">Name</th>
                  <th className="px-4 py-3 font-bold text-text">Roll No</th>
                  <th className="px-4 py-3 font-bold text-text">Course</th>
                  <th className="px-4 py-3 font-bold text-text">Semester</th>
                  <th className="px-4 py-3 font-bold text-text">Marks</th>
                  <th className="px-4 py-3 font-bold text-text">Grade</th>
                </tr>
              </thead>
              <tbody>
                {loading && (
                  <tr>
                    <td colSpan={6} className="px-4 py-6 text-center text-subtext">
                      Loading...
                    </td>
                  </tr>
                )}
                {!loading && students.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-6 text-center text-subtext">
                      No students found
                    </td>
                  </tr>
                )}
                {students.map((s) => {
                  const grade = gradeFor(s.marks);
                  const isSelected = s.id === selectedId;
                  return (
                    <tr
                      key={s.id}
                      onClick={() => selectRow(s)}
                      className={`cursor-pointer border-b border-border last:border-0 ${
                        isSelected ? "bg-background" : "hover:bg-background"
                      }`}
                    >
                      <td className="flex items-center gap-2 px-4 py-3 text-text">
                        <span
                          className="flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold text-white"
                          style={{ backgroundColor: avatarColorFor(s.name) }}
                        >
                          {initialsFor(s.name)}
                        </span>
                        {s.name}
                      </td>
                      <td className="px-4 py-3 text-text">{s.rollNo}</td>
                      <td className="px-4 py-3 text-text">{s.course}</td>
                      <td className="px-4 py-3 text-text">{s.semester}</td>
                      <td className="px-4 py-3 text-text">{s.marks}</td>
                      <td className="px-4 py-3">
                        <span
                          className="rounded-md px-2.5 py-1 text-xs font-bold text-white"
                          style={{ backgroundColor: gradeBadgeColor(grade) }}
                        >
                          {grade}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Toast */}
      {toast && (
        <div
          className={`fixed bottom-6 right-6 rounded-xl px-4 py-3 text-sm font-bold text-white shadow-lg ${
            toast.type === "danger" ? "bg-danger" : "bg-success"
          }`}
        >
          {toast.message}
        </div>
      )}
    </div>
  );
}

function StatCard({ color, value, label }) {
  return (
    <div className="flex rounded-xl border border-border bg-white p-4">
      <div className="mr-4 w-1 rounded-full" style={{ backgroundColor: color }} />
      <div>
        <div className="text-2xl font-bold text-text">{value}</div>
        <div className="text-sm text-subtext">{label}</div>
      </div>
    </div>
  );
}
