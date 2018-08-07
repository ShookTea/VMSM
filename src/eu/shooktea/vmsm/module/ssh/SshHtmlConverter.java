package eu.shooktea.vmsm.module.ssh;

public class SshHtmlConverter {
    private SshHtmlConverter() {}

    public static String sshToHtml(String ssh) {
        return HTML_OPEN + ssh + HTML_CLOSE;
    }

    private static final String JS_SCROLL
            = "window.scrollTo(0, " + Integer.MAX_VALUE + ");";
    private static final String HTML_SCROLL
            = "<script type='text/javascript'>" + JS_SCROLL + "</script>";
    private static final String HTML_STYLE =
            "background-color: black; color: white; font-family: monospaced; font-size: 13px;";
    private static final String HTML_HEAD =
            "<meta charset='UTF-8'/><style>" + SshConnection.styles + "</style>";
    private static final String HTML_OPEN =
            "<!DOCTYPE html><html><head>" + HTML_HEAD + "</head><body style='" + HTML_STYLE + "'>";
    private static final String HTML_CLOSE = HTML_SCROLL + "</body></html>";
}
