import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

public class MyWaiter {

    private static EventWaiter waiter;

    public static void setDefaultWaiter(EventWaiter waiter) {
        MyWaiter.waiter = waiter;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }
}
