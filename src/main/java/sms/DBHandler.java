package sms;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all communication with MongoDB Atlas.
 * Every student is stored as one Document (like a JSON object) inside
 * the "students" collection. No SQL, no tables to design.
 */
public class DBHandler {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Connects to MongoDB Atlas using the connection string.
     * Throws an exception if the URI/credentials are wrong.
     */
    public void connect(String connectionString, String databaseName) {
        client = MongoClients.create(connectionString);
        database = client.getDatabase(databaseName);
        collection = database.getCollection("students");

        // Force a real round-trip to the server so bad credentials fail immediately.
        database.runCommand(new Document("ping", 1));
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }

    public boolean isConnected() {
        return collection != null;
    }

    /** Inserts a new student document. */
    public void addStudent(Student s) {
        Document doc = new Document("name", s.getName())
                .append("rollNo", s.getRollNo())
                .append("course", s.getCourse())
                .append("semester", s.getSemester())
                .append("marks", s.getMarks());
        collection.insertOne(doc);
    }

    /** Returns every student in the collection, sorted by Roll Number (ascending). */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().sort(Sorts.ascending("rollNo")).iterator()) {
            while (cursor.hasNext()) {
                list.add(documentToStudent(cursor.next()));
            }
        }
        return list;
    }

    /** Updates an existing student, found by its MongoDB _id. */
    public void updateStudent(Student s) {
        Document filter = new Document("_id", new ObjectId(s.getId()));
        Document update = new Document("$set", new Document("name", s.getName())
                .append("rollNo", s.getRollNo())
                .append("course", s.getCourse())
                .append("semester", s.getSemester())
                .append("marks", s.getMarks()));
        collection.updateOne(filter, update);
    }

    /** Deletes a student by its MongoDB _id. */
    public void deleteStudent(String id) {
        collection.deleteOne(new Document("_id", new ObjectId(id)));
    }

    /** Simple search by name or roll number (case-insensitive, partial match). */
    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        Document regex = new Document("$regex", keyword).append("$options", "i");
        Document filter = new Document("$or", List.of(
                new Document("name", regex),
                new Document("rollNo", regex)
        ));
        try (MongoCursor<Document> cursor = collection.find(filter).sort(Sorts.ascending("rollNo")).iterator()) {
            while (cursor.hasNext()) {
                list.add(documentToStudent(cursor.next()));
            }
        }
        return list;
    }

    private Student documentToStudent(Document doc) {
        return new Student(
                doc.getObjectId("_id").toString(),
                doc.getString("name"),
                doc.getString("rollNo"),
                doc.getString("course"),
                doc.getString("semester"),
                doc.getString("marks")
        );
    }
}
