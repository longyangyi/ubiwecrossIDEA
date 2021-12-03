import java.math.BigInteger;
import java.util.LinkedList;

public class ElGamal {


    int buchang = 100; //buchang*1 ~ buchang*10
    int repeate = 10;

    public void run() {
        System.out.println("run in ElGamal");
        //testGenerateProof();
        //testVerifySingleProof();
        //testProofAggregation();
        testVerifyAggregation();
    }

    public void testGenerateProof() {

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("a=[");
        for (int i = 1; i <= 10; i++) {
            int num = buchang * i;
            long consumption = generateProof(num);
            //System.out.println(num + ":" + consumption);
            System.out.printf("%.3f,", consumption * 1.0 / 1000);
        }
        System.out.print("];");
    }

    public long generateProof(int num) {
        BigInteger prime = new BigInteger("8125885523076110626831219183576792320445894569509003843136762143", 10); // 64
        BigInteger generator = new BigInteger("1663908243104990722381950197016221255429251949593246503305582477", 10);

        int r = 100, d = 100;

        long start = System.currentTimeMillis();
        for (int j = 0; j < repeate; j++) {
            for (int i = 0; i < num; i++) {
                BigInteger h = generator.pow(d + r).mod(prime);
            }
        }
        long end = System.currentTimeMillis();

        long consumption = end - start;

        return consumption / repeate;
    }

    public void testVerifySingleProof() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("b=[");
        for (int i = 1; i <= 10; i++) {
            int num = buchang * i;
            long consumption = verifySingleProof(num);
            //System.out.println(num + ":" + consumption);
            System.out.printf("%.3f,", consumption * 1.0 / 1000);
        }
        System.out.println("];");
    }

    public long verifySingleProof(int num) {
        BigInteger prime = new BigInteger("8125885523076110626831219183576792320445894569509003843136762143", 10); // 64
        BigInteger generator = new BigInteger("1663908243104990722381950197016221255429251949593246503305582477", 10);
        int r = 100, d = 100;

        BigInteger h = generator.pow(d + r).mod(prime);

        long start = System.currentTimeMillis();
        for (int i = 0; i < repeate; i++) {
            for (int j = 0; j < num; j++) {
                int res = h.compareTo(generator.pow(d + r).mod(prime));
                //System.out.println(res);
                if (res != 0) {
                    System.out.println("not equal");
                }
            }
        }
        long end = System.currentTimeMillis();

        long consumption = end - start;
        return consumption / repeate;
    }

    public void testProofAggregation() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("a=[");
        for (int i = 1; i <= 10; i++) {
            int num = buchang * i;
            long consumption = proofAggregation(num);
            System.out.printf("%.3f,", consumption * 1.0 / 1000);
        }
        System.out.println("];");
    }

    public long proofAggregation(int num) {
        BigInteger prime = new BigInteger("8125885523076110626831219183576792320445894569509003843136762143", 10); // 64
        BigInteger generator = new BigInteger("1663908243104990722381950197016221255429251949593246503305582477", 10);
        int r = 100, d = 100;

        LinkedList<BigInteger> h_list = new LinkedList<BigInteger>();
        int rSum = 0, dSum = 0;
        for (int i = 0; i < num; i++) {
            BigInteger h = generator.pow(d + r).mod(prime);
            h_list.add(h);
            rSum += r;
            dSum += d;
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < repeate; i++) {
            BigInteger h_mul = new BigInteger("1", 10);
            for (int j = 0; j < num; j++) {
                h_mul = h_mul.multiply(h_list.get(i)).mod(prime);
            }
            int res = h_mul.compareTo(generator.pow(rSum + dSum).mod(prime));
            if (res != 0) {
                System.out.println("not equal");
            }
        }
        long end = System.currentTimeMillis();
        long consumption = end - start;
        return consumption / repeate;
    }

    public void testVerifyAggregation() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("b=[");
        for (int i = 1; i <= 10; i++) {
            int num = buchang * i;
            long consumption = verifyAggregation(num);
            System.out.printf("%.3f,", consumption * 1.0 / 1000);
        }
        System.out.println("];");
    }


    public long verifyAggregation(int num) {
        BigInteger prime = new BigInteger("8125885523076110626831219183576792320445894569509003843136762143", 10); // 64
        BigInteger generator = new BigInteger("1663908243104990722381950197016221255429251949593246503305582477", 10);
        int r = 100, d = 100, rSum = 0, dSum = 0;

        BigInteger h = generator.pow(d + r).mod(prime);
        for (int i = 0; i < num; i++) {
            rSum += r;
            dSum += d;
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < repeate; i++) {
            BigInteger h_mul = new BigInteger("1", 10);
            for (int j = 0; j < num; j++) {
                h_mul = h_mul.multiply(h).mod(prime);
            }
            int res = h_mul.compareTo(generator.pow(rSum + dSum).mod(prime));
            if (res != 0) {
                System.out.println("not equal");
            }
        }
        long end = System.currentTimeMillis();
        long consumption = end - start;
        return consumption / repeate;
    }

}
