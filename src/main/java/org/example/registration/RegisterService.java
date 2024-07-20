package org.example.registration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RegisterService {
    private CuratorFramework client;
    private static final String ROOT = "MyRPC";
    private static Random random = new Random();

    public RegisterService() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().retryPolicy(retryPolicy)
                .sessionTimeoutMs(40000).namespace(ROOT).connectString("127.0.0.1:2181").build();
        client.start();
    }

    public void register(String serviceName, InetSocketAddress address) {
        try {
            if(client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            String newPath = "/" + serviceName + "/" + address.getHostName() + ":" + address.getPort();
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(newPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress serviceDiscover(String serviceName) {
        try {
            List<String> addresses = client.getChildren().forPath("/" + serviceName);
            String address = addresses.get(random.nextInt(addresses.size()));
            String ip = address.split(":")[0];
            int port = Integer.parseInt(address.substring(address.lastIndexOf(":") + 1));
            return new InetSocketAddress(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
