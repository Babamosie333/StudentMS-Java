import { useState } from "react";
import { useRouter } from "next/router";

export default function Connect() {
  const router = useRouter();
  const [uri, setUri] = useState(
    "mongodb+srv://sudhanshu:sudhanshu@minorproject.ya4tkqy.mongodb.net/?appName=minorproject"
  );
  const [dbName, setDbName] = useState("student_management");
  const [error, setError] = useState("");
  const [connecting, setConnecting] = useState(false);

  async function handleConnect(e) {
    e.preventDefault();
    setError("");

    if (!uri || !dbName) {
      setError("Both fields are required");
      return;
    }

    setConnecting(true);
    try {
      const res = await fetch("/api/connect", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ uri, dbName }),
      });
      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Connection failed");
        setConnecting(false);
        return;
      }

      router.push("/");
    } catch {
      setError("Something went wrong. Try again.");
      setConnecting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#2b2b2b] p-4">
      <div className="w-full max-w-lg overflow-hidden rounded-2xl bg-white shadow-2xl">
        {/* Title bar, mirroring the desktop dialog */}
        <div className="flex items-center gap-2 border-b border-border bg-[#EDEDED] px-4 py-3">
          <span className="h-3 w-3 rounded-full bg-[#FF5F57]" />
          <span className="h-3 w-3 rounded-full bg-[#FEBC2E]" />
          <span className="h-3 w-3 rounded-full bg-[#28C840]" />
          <span className="mx-auto -ml-8 text-sm font-medium text-[#4a4a4a]">
            Student Management System — Connect to Database
          </span>
        </div>

        <form onSubmit={handleConnect} className="px-8 py-8">
          <h1 className="text-2xl font-bold text-text">Connect to MongoDB Atlas</h1>
          <p className="mt-1 text-sm text-subtext">
            Enter your Atlas connection string to continue
          </p>

          <div className="mt-6">
            <label className="mb-1 block text-sm font-bold text-text">
              Connection String (URI)
            </label>
            <input
              type="text"
              value={uri}
              onChange={(e) => setUri(e.target.value)}
              placeholder="mongodb+srv://user:pass@cluster.mongodb.net/?appName=..."
              className="w-full rounded-lg border border-border px-3 py-2.5 text-sm outline-none focus:border-primary"
              autoComplete="off"
              spellCheck={false}
            />
          </div>

          <div className="mt-5">
            <label className="mb-1 block text-sm font-bold text-text">Database Name</label>
            <input
              type="text"
              value={dbName}
              onChange={(e) => setDbName(e.target.value)}
              placeholder="student_management"
              className="w-full rounded-lg border border-border px-3 py-2.5 text-sm outline-none focus:border-primary"
              autoComplete="off"
              spellCheck={false}
            />
          </div>

          {error && <p className="mt-4 text-sm font-medium text-danger">{error}</p>}

          <button
            type="submit"
            disabled={connecting}
            className="mt-6 w-full rounded-lg bg-primary py-3 text-sm font-bold text-white hover:bg-primaryDark disabled:opacity-60"
          >
            {connecting ? "Connecting..." : "Connect"}
          </button>
        </form>
      </div>
    </div>
  );
}
