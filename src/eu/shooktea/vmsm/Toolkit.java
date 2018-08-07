package eu.shooktea.vmsm;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
     * Creates {@link ImageView} node with icon resized to 15 pixels.
     * @param resourceFileName name of file in {@code /eu/shooktea/vmsm/resources/} directory
     * @return created node with icon from resources directory
     */
    public static ImageView createMenuImage(String resourceFileName) {
        return createImageView(resourceFileName, 15);
    }

    /**
     * Creates {@link ImageView} node with icon resized to 32 pixels and some tooltip.
     * @param resourceFileName name of file in {@code /eu/shooktea/vmsm/resources/} directory
     * @param tooltipText string containing tooltip to be displayed when hovering over button.
     * @return created node with icon from resources directory
     */
    public static ImageView createQuickGuiButton(String resourceFileName, String tooltipText) {
        ImageView iv = createImageView(resourceFileName, 32);
        if (tooltipText == null) return iv;
        Tooltip tip = new Tooltip(tooltipText);
        Tooltip.install(iv, tip);
        return iv;
    }

    /**
     * Creates {@link ImageView} node from given resource and resized to given size
     * @param resourceFileName name of file in {@code /eu/shooktea/vmsm/resources/} directory
     * @param width width of icon in pixels
     * @return created node with icon from resources directory
     */
    public static ImageView createImageView(String resourceFileName, double width) {
        resourceFileName = "/eu/shooktea/vmsm/resources/" + resourceFileName;
        Image image = new Image(Toolkit.class.getResourceAsStream(resourceFileName));
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setPickOnBounds(true);
        iv.setFitWidth(width);
        iv.setFitHeight(width);
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
