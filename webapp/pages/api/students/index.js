import { getDb, parseConnectionCookie } from "../../../lib/mongodb";

export default async function handler(req, res) {
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

  if (req.method === "GET") {
    const { search } = req.query;
    let filter = {};

    if (search) {
      filter = {
        $or: [
          { name: { $regex: search, $options: "i" } },
          { rollNo: { $regex: search, $options: "i" } },
        ],
      };
    }

    const students = await collection.find(filter).sort({ rollNo: 1 }).toArray();
    const mapped = students.map((s) => ({
      id: s._id.toString(),
      name: s.name,
      rollNo: s.rollNo,
      course: s.course,
      semester: s.semester,
      marks: s.marks,
    }));

    return res.status(200).json(mapped);
  }

  if (req.method === "POST") {
    const { name, rollNo, course, semester, marks } = req.body;

    if (!name || !rollNo) {
      return res.status(400).json({ error: "Name and Roll No. are required" });
    }

    const result = await collection.insertOne({
      name,
      rollNo,
      course,
      semester,
      marks,
    });

    return res.status(201).json({ id: result.insertedId.toString() });
  }

  res.setHeader("Allow", ["GET", "POST"]);
  return res.status(405).end(`Method ${req.method} Not Allowed`);
}
