import { ObjectId } from "mongodb";
import { getDb, parseConnectionCookie } from "../../../lib/mongodb";

export default async function handler(req, res) {
  const { id } = req.query;

  if (!ObjectId.isValid(id)) {
    return res.status(400).json({ error: "Invalid student id" });
  }

  const conn = parseConnectionCookie(req);
  if (!conn) {
    return res.status(401).json({ error: "Not connected to a database" });
  }

  let db;
  try {
    db = await getDb(conn.uri, conn.dbName);
  } catch {
    return res.status(500).json({ error: "Database connection failed" });
  }

  const collection = db.collection("students");
  const _id = new ObjectId(id);

  if (req.method === "PUT") {
    const { name, rollNo, course, semester, marks } = req.body;

    await collection.updateOne(
      { _id },
      { $set: { name, rollNo, course, semester, marks } }
    );

    return res.status(200).json({ message: "Student updated" });
  }

  if (req.method === "DELETE") {
    await collection.deleteOne({ _id });
    return res.status(200).json({ message: "Student deleted" });
  }

  res.setHeader("Allow", ["PUT", "DELETE"]);
  return res.status(405).end(`Method ${req.method} Not Allowed`);
}
