import { MongoClient } from "mongodb";
import { encodeConnectionCookie } from "../../lib/mongodb";

export default async function handler(req, res) {
  if (req.method !== "POST") {
    res.setHeader("Allow", ["POST"]);
    return res.status(405).end(`Method ${req.method} Not Allowed`);
  }

  const { uri, dbName } = req.body;

  if (!uri || !dbName) {
    return res.status(400).json({ error: "Connection string and database name are required" });
  }

  // Test the connection before saving it, so bad credentials fail fast
  // with a clear message instead of breaking every page later.
  let client;
  try {
    client = new MongoClient(uri, { serverSelectionTimeoutMS: 8000 });
    await client.connect();
    await client.db(dbName).command({ ping: 1 });
  } catch (err) {
    return res.status(400).json({ error: "Could not connect. Check your URI and database name." });
  } finally {
    if (client) await client.close();
  }

  const cookieValue = encodeConnectionCookie(uri, dbName);
  res.setHeader(
    "Set-Cookie",
    `sms_conn=${cookieValue}; Path=/; HttpOnly; SameSite=Lax; Max-Age=${60 * 60 * 24 * 30}`
  );

  return res.status(200).json({ message: "Connected" });
}
