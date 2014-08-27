import ninjakat.DoNotRename;

/**
 * @author yawkat
 */
@DoNotRename
public class Test {
    public static String test = TI.impl.v(1).toString();

    @DoNotRename
    public static void main(String[] args) {
        Inher.test2();
    }

    protected void test() {
        System.out.println(test);
    }

    public static int a() { return 0; }

    public static int b() { return 0; }

    public static long c() { return 0; }

    public static long e() { return 0; }

    public static double f() { return 0; }

    public static double g() { return 0; }
}
