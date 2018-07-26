package eu.shooktea.vmsm;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;

/**
 * Container for utility methods.
 */
public class Toolkit {
    private Toolkit() {}

    /**
     * Creates {@link ImageView} node with icon resized to 20 pixels.
     * @param resourceFileName name of file in {@code /eu/shooktea/vmsm/resources/} directory
     * @return created nod with icon from resources directory
     */
    public static ImageView createToolbarImage(String resourceFileName) {
        resourceFileName = "/eu/shooktea/vmsm/resources/" + resourceFileName;
        ImageView iv = new ImageView(new Image(Toolkit.class.getResourceAsStream(resourceFileName)));
        iv.setPreserveRatio(true);
        iv.setFitWidth(20);
        iv.setPickOnBounds(true);
        return iv;
    }

    /**
     * Creates {@link ImageView} node with icon resized to 15 pixels.
     * @param resourceFileName name of file in {@code /eu/shooktea/vmsm/resources/} directory
     * @return created nod with icon from resources directory
     */
    public static ImageView createMenuImage(String resourceFileName) {
        ImageView iv = createToolbarImage(resourceFileName);
        iv.setFitWidth(15);
        return iv;
    }

    /**
     * Turns off locking invalid SSL certificates to allow easy work with servers on virtual machines.
     * It is called from {@link Start#main(String[])} and should not be called anymore.
     */
    public static void turnOffSSL() {
        if (!isSslOn) return;
        isSslOn = false;
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (s, sslSession) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean isSslOn = true;
}
