import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;

import java.math.BigInteger;
import java.util.*;

public class Wecross {

    WeCrossRPCService weCrossRPCService;
    WeCrossRPC weCrossRPC;
    Resource group1, group2;

    String group1path = "payment.group1.AssurerInterchain";
    String group2path = "payment.group2.AssurerInterchain";


    public void init() {
        try {
            // 初始化 Service
            weCrossRPCService = new WeCrossRPCService();


            // 初始化Resource
            weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);

            weCrossRPC.login("org1-admin", "123456").send(); // 需要有登录态才能进一步操作
            group1 = ResourceFactory.build(weCrossRPC, group1path);
            group2 = ResourceFactory.build(weCrossRPC, group2path);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    public void testRegister() {
        init();
        final int id = 111;
        final int num = 500;
        setRegisterCount(group1, 0);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getRegisterCount(group1) >= num) {
                        System.out.print("count thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        System.out.println((end - start) / num);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    register(group1, id);
                }
            }).start();
        }

    }


    public void testDeposite() {
        init();
        final int id = 111;
        final int num = 500;

        register(group1, id);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getBalance(group1, id) >= num) {
                        System.out.print("count thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        System.out.println((end - start) / num);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    deposite(group1, id, 1);
                }
            }).start();
        }

    }

    public void testDeduct() {
        init();
        final int id = 111;
        final int num = 500;

        register(group1, id);
        deposite(group1, id, num + 100);
        setPremium(group1, id, 1);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getBalance(group1, id) < 100) {
                        System.out.print("count thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        System.out.println((end - start) / num);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    deduct(group1, id);
                }
            }).start();
        }

    }


    public void testRegisterInterchain() {
        init();
        final int id = 111;
        final int num = 500;
        setRegisterCount(group2, 0);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getRegisterCount(group2) >= num) {
                        System.out.print("count thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    registerInterchain(group1, group1path, group2path, id);
                }
            }).start();
        }
    }


    public void testDepositeInterchain() {
        init();
        final int id = 111;
        final int num = 500;

        register(group1, id);
        deposite(group1, id, num + 10);
        register(group2, id);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getBalance(group2, id) >= num) {
                        System.out.print("balance thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        System.out.println((end - start) / num);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    depositeInterchain(group1, group1path, group2path, id, 1);
                }
            }).start();
        }
    }

    public void testAddData() {
        init();

        final int id = 111;
        final int num = 100;

        setDataCount(group1, 0);

        BigInteger prime = new BigInteger("3625777959491383970316008656527161993117823115699210573271223304064230072126582678092278516820325823", 10);
        BigInteger generator = new BigInteger("5368599314701883481312149937713926484893700902887324948708440870835759175801427277521103738233511563", 10);
        final long timestamp = System.currentTimeMillis();

        int r = (int) (Math.random() * 100);

        final int velocity = (int) (Math.random() * 100);
        int acceleration = (int) (Math.random() * 20);
        int angle = (int) (Math.random() * 60);
        int over_speed = zeroORone();
        int rapid_acc = zeroORone();
        int rapid_turn = zeroORone();

        final BigInteger h_velocity = generator.pow(velocity + r).mod(prime);
        final BigInteger h_acceleration = generator.pow(acceleration + r).mod(prime);
        final BigInteger h_angle = generator.pow(angle + r).mod(prime);
        final BigInteger h_over_speed = generator.pow(over_speed + r).mod(prime);
        final BigInteger h_rapid_acc = generator.pow(rapid_acc + r).mod(prime);
        final BigInteger h_rapid_turn = generator.pow(rapid_turn + r).mod(prime);


        /*
        //单线程
        System.out.println("a=[");
        int repeate = 10;
        for (int i = 100; i <= 1000; i += 100) { // byte

            String dataByte = "";
            for (int j = 0; j < i; j++) {
                dataByte += "A";
            }
            System.out.println(dataByte);

            try{
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }


            long start = System.currentTimeMillis();
            for (int k = 0; k < repeate; k++) {
                addDataNoLog(group1, id, "", "", "", "", "", "", dataByte);
            }

            long end = System.currentTimeMillis();

            double avg = ((end - start) * 1.0 / repeate)/1000;

            System.out.printf("%d:%.3f\n\n", i, avg);

        }*/


        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (getDataCount(group1) >= num) {
                        System.out.print("data count thread over!\nconsumption: ");
                        long end = System.currentTimeMillis();
                        System.out.println(end - start);
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        // 多进程
        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                public void run() {
                    addData(group1, id, timestamp + "", h_velocity.toString(), h_acceleration.toString(), h_angle.toString(), h_over_speed.toString(), h_rapid_acc.toString(), h_rapid_turn.toString());

                }
            }).start();
        }

    }

    public void testGetData() {
        init();

        int num = 10;
        int buchang = 10;
        int repeate = 10;


        int readCount = 0;

        for (int i = 1; i <= num; i++) {
            long start = System.currentTimeMillis();

            for (int k = 0; k < repeate; k++) {
                for (int j = 0; j < buchang * i; j++) {
                    getData(group1, 111, "1638521860175");
                }
            }
            long end = System.currentTimeMillis();

            double consumption = (end - start) * 1.0 / 1000;
            System.out.printf("%.3f,", consumption / repeate);
        }
    }

    public void testUploadData() {
        final long timeSum = 0;
        int repeate = 10;
        int dataSize = 9000; //byte

        init();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < repeate; i++) {
            setDataCount(group1, 0);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final long start = System.currentTimeMillis();
            uploadData(group1, dataSize);
            new Thread(new Runnable() {
                public void run() {
                    while (true) {

                        if (getDataCount(group1) >= 1) {
                            //System.out.print("data count thread over!\nconsumption: ");
                            long end = System.currentTimeMillis();
                            System.out.print(end - start);
                            System.out.print("+");
                            break;
                        }

                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        }

    }

    public void run() {
        int id = 111;
        init();
        register(group1, id);
        int balance = getBalance(group1, id);
        deposite(group1, id, 100);
        balance = getBalance(group1, id);
        depositeInterchain(group1, group1path, group2path, id, 10);

        try {
            System.out.println("[Thread] wait 5s for depositeInterchain");
            Thread.sleep(5000);
            balance = getBalance(group1, id);
            balance = getBalance(group2, id);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> timestampList = new ArrayList<String>();


        int r_sum = 0;

        int velocity_sum = 0;
        int acceleration_sum = 0;
        int angle_sum = 0;
        int over_speed_sum = 0;
        int rapid_acc_sum = 0;
        int rapid_turn_sum = 0;

        BigInteger prime = new BigInteger("3625777959491383970316008656527161993117823115699210573271223304064230072126582678092278516820325823", 10);
        BigInteger generator = new BigInteger("5368599314701883481312149937713926484893700902887324948708440870835759175801427277521103738233511563", 10);

        for (int i = 0; i < 50; i++) {

            long timestamp = System.currentTimeMillis();

            int r = (int) (Math.random() * 100);
            r_sum += r;


            int velocity = (int) (Math.random() * 100);
            int acceleration = (int) (Math.random() * 20);
            int angle = (int) (Math.random() * 60);
            int over_speed = zeroORone();
            int rapid_acc = zeroORone();
            int rapid_turn = zeroORone();

            velocity_sum += velocity;
            acceleration_sum += acceleration;
            angle_sum += angle;
            over_speed_sum += over_speed;
            rapid_acc_sum += rapid_acc;
            rapid_turn_sum += rapid_turn;


            BigInteger h_velocity = generator.pow(velocity + r).mod(prime);
            BigInteger h_acceleration = generator.pow(acceleration + r).mod(prime);
            BigInteger h_angle = generator.pow(angle + r).mod(prime);
            BigInteger h_over_speed = generator.pow(over_speed + r).mod(prime);
            BigInteger h_rapid_acc = generator.pow(rapid_acc + r).mod(prime);
            BigInteger h_rapid_turn = generator.pow(rapid_turn + r).mod(prime);


            addData(group1, id, timestamp + "", h_velocity.toString(), h_acceleration.toString(), h_angle.toString(), h_over_speed.toString(), h_rapid_acc.toString(), h_rapid_turn.toString());

            timestampList.add(timestamp + "");
        }

        BigInteger h_velocity_all_mul = new BigInteger("1", 10);
        BigInteger h_acceleration_all_mul = new BigInteger("1", 10);
        BigInteger h_angle_all_mul = new BigInteger("1", 10);
        BigInteger h_over_speed_all_mul = new BigInteger("1", 10);
        BigInteger h_rapid_acc_all_mul = new BigInteger("1", 10);
        BigInteger h_rapid_turn_all_mul = new BigInteger("1", 10);


        for (int i = 0; i < timestampList.size(); i++) {
            String[] data = getData(group1, id, timestampList.get(i));

            BigInteger h_velocity = new BigInteger(data[1], 10);
            BigInteger h_acceleration = new BigInteger(data[2], 10);
            BigInteger h_angle = new BigInteger(data[3], 10);
            BigInteger h_over_speed = new BigInteger(data[4], 10);
            BigInteger h_rapid_acc = new BigInteger(data[5], 10);
            BigInteger h_rapid_turn = new BigInteger(data[6], 10);

            h_velocity_all_mul = h_velocity_all_mul.multiply(h_velocity).mod(prime);
            h_acceleration_all_mul = h_acceleration_all_mul.multiply(h_acceleration).mod(prime);
            h_angle_all_mul = h_angle_all_mul.multiply(h_angle).mod(prime);
            h_over_speed_all_mul = h_over_speed_all_mul.multiply(h_over_speed).mod(prime);
            h_rapid_acc_all_mul = h_rapid_acc_all_mul.multiply(h_rapid_acc).mod(prime);
            h_rapid_turn_all_mul = h_rapid_turn_all_mul.multiply(h_rapid_turn).mod(prime);


        }

        BigInteger g_velocity_r_sum = generator.pow(velocity_sum + r_sum).mod(prime);
        System.out.println(g_velocity_r_sum);
        System.out.println(h_velocity_all_mul);

        BigInteger g_acceleration_r_sum = generator.pow(acceleration_sum + r_sum).mod(prime);
        System.out.println(g_acceleration_r_sum);
        System.out.println(h_acceleration_all_mul);

        BigInteger g_angle_r_sum = generator.pow(angle_sum + r_sum).mod(prime);
        System.out.println(g_angle_r_sum);
        System.out.println(h_angle_all_mul);

        BigInteger g_over_speed_r_sum = generator.pow(over_speed_sum + r_sum).mod(prime);
        System.out.println(g_over_speed_r_sum);
        System.out.println(h_over_speed_all_mul);

        BigInteger g_rapid_acc_r_sum = generator.pow(rapid_acc_sum + r_sum).mod(prime);
        System.out.println(g_rapid_acc_r_sum);
        System.out.println(h_rapid_acc_all_mul);

        BigInteger g_rapid_turn_r_sum = generator.pow(rapid_turn_sum + r_sum).mod(prime);
        System.out.println(g_rapid_turn_r_sum);
        System.out.println(h_rapid_turn_all_mul);

    }


    public void register(Resource r, int id) {
        try {
            String[] sendTransactionRet = r.sendTransaction("register", id + "");
            //System.out.println(r.getPath() + "[register: id=" + id + "] " + Arrays.toString(sendTransactionRet));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataCount(Resource r, int _count) {
        try {
            String[] sendTransactionRet = r.sendTransaction("setDataCount", _count + "");
            //System.out.println(r.getPath() + "[setDataCount: count=" + _count + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDataCount(Resource r) {
        try {
            String[] callRet = r.call("getDataCount");
            //System.out.println(r.getPath() + "[getDataCount] " + callRet[0]);
            return Integer.parseInt(callRet[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void setRegisterCount(Resource r, int _count) {
        try {
            String[] sendTransactionRet = r.sendTransaction("setRegisterCount", _count + "");
            System.out.println(r.getPath() + "[setRegisterCount: count=" + _count + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRegisterCount(Resource r) {
        try {
            String[] callRet = r.call("getRegisterCount");
            System.out.println(r.getPath() + "[getRegisterCount] " + callRet[0]);
            return Integer.parseInt(callRet[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBalance(Resource r, int id) {
        try {
            String[] callRet = r.call("getBalance", id + "");
            System.out.println(r.getPath() + "[getBalance: id=" + id + "] " + callRet[0]);
            return Integer.parseInt(callRet[0]);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return -1;
    }

    public void deposite(Resource r, int id, int amount) {
        try {
            String[] sendTransactionRet = r.sendTransaction("deposite", id + "", amount + "");
            System.out.println(r.getPath() + "[deposite: id=" + id + ", amount=" + amount + "] " + Arrays.toString(sendTransactionRet));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void addData(Resource r, int id, String timestamp, String velocity, String acceleration, String angle, String over_speed, String rapid_acc, String rapid_turn) {
        try {
            String[] sendTransactionRet = r.sendTransaction("addData", id + "", timestamp, velocity, acceleration, angle, over_speed, rapid_acc, rapid_turn);
            System.out.println(r.getPath() + "[addData: id=" + id + ", timestamp=" + timestamp + "] velocity=" + velocity + ", acceleration=" + acceleration + ", angle=" + angle + ", over_speed=" + over_speed + ", rapid_acc=" + rapid_acc + ", rapid_turn=" + rapid_turn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDataNoLog(Resource r, int id, String timestamp, String velocity, String acceleration, String angle, String over_speed, String rapid_acc, String rapid_turn) {
        try {
            String[] sendTransactionRet = r.sendTransaction("addData", id + "", timestamp, velocity, acceleration, angle, over_speed, rapid_acc, rapid_turn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getData(Resource r, int id, String timestamp) {
        try {
            String[] callRet = r.call("getData", id + "", timestamp + "");
            //System.out.println(r.getPath() + "[getData: id=" + id + ", timestamp=" + timestamp + "] " + Arrays.toString(callRet));
            return callRet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void uploadData(Resource r, int dataSize) {
        try {
            String data = "";
            for (int i = 0; i < dataSize; i++) {
                data += "A";
            }

            String[] sendTransactionRet = r.sendTransaction("addData", "1", "2", data, "", "", "", "", "");
            //System.out.println(r.getPath() + "[uploadData, data size: " + dataSize + ", " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void depositeInterchain(Resource r, String originChainPath, String targetChainPath, int id, int amount) {
        try {
            TransactionResponse transactionResponse = weCrossRPC.sendTransaction(originChainPath, "depositeInterchain", targetChainPath, "depositeFromInterchain", id + "", amount + "", originChainPath, "depositeCallback").send();
            System.out.println(r.getPath() + "[depositeInterchain] originChainPath=" + originChainPath + ", targetChainPath=" + targetChainPath + ", id=" + id + ", amount=" + amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerInterchain(Resource r, String originChainPath, String targetChainPath, int id) {
        try {
            TransactionResponse transactionResponse = weCrossRPC.sendTransaction(originChainPath, "registerInterchain", targetChainPath, "registerFromInterchain", id + "", originChainPath, "registerCallback").send();
            System.out.println(r.getPath() + "[depositeInterchain] originChainPath=" + originChainPath + ", targetChainPath=" + targetChainPath + ", id=" + id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int zeroORone() {
        double d = Math.random();
        if (d > 0.5) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setPremium(Resource r, int id, int amount) {
        try {
            String[] sendTransactionRet = r.sendTransaction("setPremium", id + "", amount + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deduct(Resource r, int id) {
        try {
            String[] sendTransactionRet = r.sendTransaction("deduct", id + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
