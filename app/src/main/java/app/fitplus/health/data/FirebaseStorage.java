package app.fitplus.health.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static app.fitplus.health.system.Application.getUser;

public class FirebaseStorage {

    public static DatabaseReference usersReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(getUser().getUid());
    }

    public static DatabaseReference goalsReference() {
        return FirebaseDatabase.getInstance().getReference("goals").child(getUser().getUid());
    }

    public static DatabaseReference statsReference() {
        return FirebaseDatabase.getInstance().getReference("stats").child(getUser().getUid());
    }

    public static DatabaseReference nutrientsReference() {
        return FirebaseDatabase.getInstance().getReference("/nutrients");
    }
}
