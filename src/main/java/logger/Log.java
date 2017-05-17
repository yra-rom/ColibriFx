package logger;

public final class Log {
    private static boolean ignore = true;
    public void ignore(){
        ignore = true;
    }
    public void noIgnore(){
        ignore = false;
    }

    public static void d(final String tag, final Object text){
        System.out.println(tag + ": " + text);
    }

    public static void e(final String tag, final Object text){
        System.err.println(tag + ": " + text);
    }

    public static void l(final Object text) {
        System.out.println("\t\t" + "|" + "----> " + text);
    }

    public static void i(final String tag, final Object text){
        if(!ignore) {
            System.out.println(tag + ": " + text);
        }
    }

}
