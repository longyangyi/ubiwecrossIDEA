public class Test {
    public void testThread() {
        new Thread(new Runnable() {
            public void run() {
                System.out.println("in thread");
            }
        }).start();
    }

    public void testArray() {
        int[] sum = new int[10];
        for (int i=0;i<sum.length;i++) {
            System.out.println(sum[i]);
        }
    }
}
