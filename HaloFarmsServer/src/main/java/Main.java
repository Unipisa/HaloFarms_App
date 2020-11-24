import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/*
public class Main {

public static String OPERATIONS = "| 1: list users |\n" + "| 2: select user 'username' |\n" + "--------------------------";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileInputStream serviceAccount =
                new FileInputStream("primahalofarms-5024a-firebase-adminsdk-g77kd-54392e9291.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://primahalofarms-5024a.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        Firestore firestore = FirestoreClient.getFirestore();
        // list collection path (username - email)
        Iterable<CollectionReference> collections = firestore.listCollections();
        ArrayList<String> users = new ArrayList<>();
        for (CollectionReference c : collections) {
            users.add(c.getPath());
        }

        System.out.println("--------------------------\n" + "| WELCOME TO HALO SERVER |\n" +  "| SELECT AN OPERATION |");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(OPERATIONS);
        String input;
        // textual menu
        for (;;) {
            input = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(input, "'");
            input = tokenizer.nextToken();
            switch (input) {
                case "list users":
                    System.out.println(users);
                    break;

                case "select user ":
                    String username = tokenizer.nextToken();
                    if (users.contains(username)) {
                         Iterable<DocumentReference> docs = firestore.collection(username).listDocuments();
                         for (DocumentReference d : docs) {
                             System.out.println(d.getPath());
                         }
                         System.out.print("select a area to show or analyse: ");
                         input = reader.readLine();
                         DocumentReference docRef = firestore.collection(username).document(input);
                         // asynchronously retrieve the document
                         ApiFuture<DocumentSnapshot> future = docRef.get();
                         // ...
                         // future.get() blocks on response
                         DocumentSnapshot document = future.get();
                         if (document.exists()) {
                             List<Point> points = document.toObject(PointList.class).getPoints();
                             System.out.println("the following points need to be analyzed:");
                             for (Point p : points) {
                                 if (p.analyze) {
                                     System.out.println(p.idZone);
                                 }
                             }
                             System.out.println("select id of point");
                             input = reader.readLine();
                             for (Point p : points) {
                                 if (Integer.parseInt(input) == p.idZone && p.analyze) {

                                     // Atomically remove a region from the "regions" array field.
                                     ApiFuture<WriteResult> arrayRm = docRef.update("points",
                                             FieldValue.arrayRemove(p));
                                     System.out.println("Update time : " + arrayRm.get());

                                     System.out.print("Date (dd/MM/yyyy): ");
                                     input = reader.readLine();
                                     p.date = input;

                                     System.out.print("EC: ");
                                     input = reader.readLine();
                                     p.ec = Float.parseFloat(input);

                                     System.out.print("SAR: ");
                                     input = reader.readLine();
                                     p.sar = Float.parseFloat(input);

                                     System.out.print("pH: ");
                                     input = reader.readLine();
                                     p.ph = Float.parseFloat(input);


                                     System.out.print("CEC: ");
                                     input = reader.readLine();
                                     p.cec = Float.parseFloat(input);

                                     // Atomically add a new region to the "regions" array field.
                                     ApiFuture<WriteResult> arrayUnion = docRef.update("points",
                                             FieldValue.arrayUnion(p));
                                     System.out.println("Update time : " + arrayUnion.get());
                                     break;
                                 }
                             }
                        //System.out.println("wrong point");
                    } else {
                             System.out.println("No such document!");
                         }
                         break;
                    }
                    break;
                default:
                    System.out.println("wrong operation, try again");
                    break;
            }

        }



    }
}


 */