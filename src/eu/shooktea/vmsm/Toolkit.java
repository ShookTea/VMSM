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
        return input.stream()
                .map(str -> str.split(":"))
                .flatMap(Arrays::stream)
                .distinct()
                .map(Version::new)
                .sorted()
                .map(Version::get)
                .collect(Collectors.toList());
    }

    private static boolean isSslOn = true;

    private static class Version implements Comparable<Version> {
        private final String version;

        public Version(String version) {
            this.version = version;
        }

        public String get() {
            return version;
        }

        @Override
        public int compareTo(Version that) {
            if(that == null)
                return 1;
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            int length = Math.max(thisParts.length, thatParts.length);
            for(int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if(thisPart < thatPart)
                    return -1;
                if(thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            return this.compareTo((Version)obj) == 0;
        }

        @Override
        public int hashCode() {
            return version.hashCode();
        }
    }
}
