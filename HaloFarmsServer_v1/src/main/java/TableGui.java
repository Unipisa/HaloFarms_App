import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TableGui {
    private final Firestore firestore;
    private JPanel panel1;

    private JLabel usersJLabel;
    private JList<String> usersList;
    private final DefaultListModel<String> usersListModel = new DefaultListModel<>();

    private JLabel fieldsJLabel;
    private JList<String> fieldsList;
    private final DefaultListModel<String> fieldsListModel = new DefaultListModel<>();

    private JLabel pointsToAnalyzeJLabel;
    private JList<Integer> pointsToAnalyzeJList;
    private final DefaultListModel<Integer> pointsToAnalyzeModel = new DefaultListModel<>();

    // user to analyze
    private String user;
    // field to analyze
    private Field field;
    // document to analyze
    private DocumentReference document;
    // to re-write to firestore
    private FieldWithDate fieldWithDate;
    // docs of user
    private final ArrayList<Point> points = new ArrayList<>();
    // points to analyze of user/docu
    private final ArrayList<DocumentReference> docs = new ArrayList<>();




    public TableGui(Firestore firestore) {
        this.firestore = firestore;
    }

    public void show() {
        JFrame frame = new JFrame("Table");
        frame.setContentPane(panel1);
        frame.setPreferredSize(new Dimension(700,700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        usersList.setModel(usersListModel);
        usersList.addListSelectionListener(e -> {
            if (!usersList.getValueIsAdjusting()) {
                points.clear();
                pointsToAnalyzeModel.clear();
                docs.clear();
                fieldsListModel.clear();
                // show in near jlist his fields!
                user = usersList.getSelectedValue();
                Iterable<DocumentReference> documents = firestore.collection(user).listDocuments();
                for (DocumentReference d : documents) {
                    docs.add(d);
                    fieldsListModel.addElement(d.getId());
                }
            }
        });

        fieldsList.setModel(fieldsListModel);
        fieldsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                points.clear();
                pointsToAnalyzeModel.clear();
                // take the correct document to show
                if (docs.size() == 0) return;
                document = docs.get(fieldsList.getSelectedIndex());
                // asynchronously retrieve the document
                ApiFuture<DocumentSnapshot> future = document.get();
                // ...
                // future.get() blocks on response
                try {
                    DocumentSnapshot document = future.get();
                    if (document.exists()) {
                        // get all fields with date analysis of THIS document
                        fieldWithDate = document.toObject(FieldWithDate.class);
                        List<Field> fields = fieldWithDate.getFields();
                        // show only points to analyze
                        for (Field f : fields) {
                            for (Point p : f.getPoints()) {
                                if (p.isAnalyze()) {
                                    field = f;
                                    pointsToAnalyzeModel.addElement(p.getZoneId());
                                    points.add(p);
                                }
                            }
                        }
                    }

                } catch (InterruptedException | ExecutionException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });


        pointsToAnalyzeJList.setModel(pointsToAnalyzeModel);
        pointsToAnalyzeJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // if list is empty, ignore mouse click
                if (points.size() == 0) return;
                // reference to element just clicked
                int i = pointsToAnalyzeJList.getSelectedIndex();
                // get the point at position
                Point p = points.get(i);
                // create the box containing date
                JTextField date = new JTextField();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String d = sdf.format(new Date());
                // set the date to the field in analysing
                field.setDate(d);
                // set date
                date.setText(d);
                // build boxes for insert results of analysis
                JTextField ec = new JTextField();
                JTextField cec = new JTextField();
                JTextField sar = new JTextField();
                JTextField ph = new JTextField();
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("Date"),
                        date,
                        new JLabel("Ec"),
                        ec,
                        new JLabel("Cec"),
                        cec,
                        new JLabel("Sar"),
                        sar,
                        new JLabel("pH"),
                        ph
                };
                // show the dialog to fill
                int result = JOptionPane.showConfirmDialog(
                        null, inputs, "Analysis of point " + p.getZoneId(), JOptionPane.DEFAULT_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    // update point
                    p.setAnalyze(false);
                    p.setEc(Float.parseFloat(ec.getText()));
                    p.setCec(Float.parseFloat(cec.getText()));
                    p.setSar(Float.parseFloat(sar.getText()));
                    p.setPh(Float.parseFloat(ph.getText()));
                    // save to database
                    ApiFuture<WriteResult> writeResult = document.set(fieldWithDate);
                    pointsToAnalyzeModel.remove(i);
                    points.remove(i);

                }
            }
        });

        readFromFirestore();
    }

    private void readFromFirestore() {
        // list collection path (username - email)
        Iterable<CollectionReference> collections = firestore.listCollections();
        ArrayList<String> users = new ArrayList<>();
        for (CollectionReference c : collections) {
            String user = c.getPath();
            usersListModel.addElement(user);
            users.add(user);
        }
    }



    public static void main(String[] args) throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream("primahalofarms-5024a-firebase-adminsdk-g77kd-54392e9291.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://primahalofarms-5024a.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        Firestore firestore = FirestoreClient.getFirestore();
        TableGui tableGui = new TableGui(firestore);
        tableGui.show();
    }

}
