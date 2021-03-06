package GUI;
import Ephemeral.OkHttpExample;

import javax.swing.*;

// Main class for the demo. Run this to start the demo.
public class Demo extends JFrame {
    private LoginDialog loginDialog;
    private static String authToken;
    private static OkHttpExample obj;
    private static String user;

    // Constructor for our Demo class
    public Demo() {
        loginDialog = new LoginDialog(this, true);
        loginDialog.setVisible(true);
        authToken = loginDialog.getAuthToken();
        obj = loginDialog.getObj();
        user = loginDialog.getUser();
        System.out.println(user);
    }

    public static void main(String[] args) throws Exception {
        /*
        LoginDemo LD = new LoginDemo();
        boolean check_auth = LD.getValue();
        System.out.println(check_auth);

        if (check_auth) {
            System.out.println("DO YOU SEE ME?");
            new ViewMessageList().main(null);
        }

         */

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new Demo();
                String[] args = {authToken};

                try {
                    new ViewMessageList().main(args, obj, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
