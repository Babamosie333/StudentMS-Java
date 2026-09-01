import { MongoClient } from "mongodb";

// Cache clients per connection string so we don't reopen a connection
// on every request, but still support different users/URIs at runtime.
const cache = global._mongoClients || (global._mongoClients = new Map());

export async function getDb(uri, dbName) {
  const key = `${uri}::${dbName}`;

  if (cache.has(key)) {
    return cache.get(key);
  }

  const client = new MongoClient(uri, { serverSelectionTimeoutMS: 8000 });
  await client.connect();
  const db = client.db(dbName);

  cache.set(key, db);
  return db;
}

export function parseConnectionCookie(req) {
  const raw = req.cookies?.sms_conn;
  if (!raw) return null;

  try {
    const decoded = Buffer.from(raw, "base64").toString("utf8");
    const parsed = JSON.parse(decoded);
    if (!parsed.uri || !parsed.dbName) return null;
    return parsed;
  } catch {
    return null;
  }
}

export function encodeConnectionCookie(uri, dbName) {
  return Buffer.from(JSON.stringify({ uri, dbName }), "utf8").toString("base64");
}
