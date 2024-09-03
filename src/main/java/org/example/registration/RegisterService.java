package org.example.registration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RegisterService {
    private CuratorFramework client;
    private static final String ROOT = "MyRPC";
    private static Random random = new Random();
    private static RedisService redisService = new RedisService();

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
            redisService.sadd(serviceName, address.getHostName() + ":" + address.getPort());
            client.getData().usingWatcher(new Watcher() {
                public void process(WatchedEvent event) {
                    String[] transfer = event.getPath().split("/");
                    String serviceName = transfer[transfer.length - 2];
                    redisService.expire(serviceName, 0);
                    redisService.delete(serviceName);
                    try {
                        client.getData().usingWatcher(this).forPath(event.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).forPath(newPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress serviceDiscover(String serviceName) {
        try {
            if(redisService.exists(serviceName) != 1L) {
                return makeAddress(redisService.srandmember(serviceName));
            }
            List<String> addresses = client.getChildren().forPath("/" + serviceName);
            String address = addresses.get(random.nextInt(addresses.size()));
            for(String addr : addresses) {
                redisService.sadd(serviceName, addr);
            }
            return makeAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InetSocketAddress makeAddress(String address) {
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.substring(address.lastIndexOf(":") + 1));
        return new InetSocketAddress(ip, port);
    }
}
