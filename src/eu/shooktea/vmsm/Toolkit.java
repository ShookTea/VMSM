package eu.shooktea.vmsm;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Changes list of Strings with {@code A:B} format to list of Strings sorted by their links, for example for list {@code ["A:B", "XYZ:D", "B:XYZ"]}
     * it will return {@code ["A", "B", "XYZ", "D"]}.
     * @param input line without parsing
     * @return parsed line
     */
    public static List<String> parseLine(List<String> input) {
        Set<String> versions = new HashSet<>();
        List<Pair<String,String>> links = input.stream()
                .map(str -> str.split(":"))
                .map(arr -> new Pair<>(arr[0], arr[1]))
                .collect(Collectors.toList());
        links.forEach(pair -> {versions.add(pair.getKey()); versions.add(pair.getValue());});
        List<String> result = new ArrayList<>();

        String lastString = null;
        do {
            if (lastString == null) {
                for (String version : versions) {
                    Optional<Pair<String, String>> any = links.stream().filter(pair -> pair.getValue().equals(version)).findAny();
                    if (!any.isPresent()) {
                        lastString = version;
                        break;
                    }
                }
            }
            else {
                final String test = lastString;
                Optional<Pair<String, String>> any = links.stream().filter(pair -> pair.getKey().equals(test)).findAny();
                lastString = any.map(Pair::getValue).orElse(null);
            }

            if (lastString != null) {
                result.add(lastString);
                versions.remove(lastString);
            }
        } while (!versions.isEmpty());
        return result;
    }

    private static boolean isSslOn = true;
}
