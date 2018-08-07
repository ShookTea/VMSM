package eu.shooktea.vmsm.module.ssh;

class SshHtmlConverter {
    private SshHtmlConverter() {}

    static String sshToHtml(String ssh) {
        String styles = parseStyles(ssh);
        String result = HTML_OPEN + styles + HTML_CLOSE;
        return result;
    }

    private static String parseStyles(String ssh) {
        String styled = ssh
                .replaceAll("\\e\\[([0-9]+)m", "</span><span class='bash_$1'>")
                .replaceAll("\\e\\[([0-9]+);([0-9]+)m", "</span><span class='bash_$1 bash_$2'>")
                .replaceAll("\\n", "<br/>")
                ;
        return "<span>" + styled + "</span>";
    }

    private static final String CSS_STYLES
            = ".bash_0 {}"
            + ".bash_1, .bash_01 {font-weight: bold;}"
            + ".bash_4, .bash_04 {text-decoration: underline;}"
            + ".bash_8, .bash_08 {display: none;}"
            + color(30, "black")
            + color(31, "red")
            + color(32, "green")
            + color(33, "yellow")
            + color(34, "blue")
            + color(35, "magenta")
            + color(36, "cyan")
            + color(37, "lightgrey")
            + color(90, "darkgrey")
            + color(91, "red")
            + color(92, "green")
            + color(93, "yellow")
            + color(94, "blue")
            + color(95, "magenta")
            + color(96, "cyan")
            ;

    private static final String JS_SCROLL
            = "window.scrollTo(0, " + Integer.MAX_VALUE + ");";
    private static final String HTML_SCROLL
            = "<script type='text/javascript'>" + JS_SCROLL + "</script>";
    private static final String HTML_STYLE =
            "background-color: black; color: white; font-family: monospaced; font-size: 13px;";
    private static final String HTML_HEAD =
            "<meta charset='UTF-8'/><style>" + CSS_STYLES + "</style>";
    private static final String HTML_OPEN =
            "<!DOCTYPE html><html><head>" + HTML_HEAD + "</head><body style='" + HTML_STYLE + "'>";
    private static final String HTML_CLOSE = HTML_SCROLL + "</body></html>";

    private static String color(int code, String color) {
        return ".bash_" + code + " {color: " + color + ";}";
    }
}
