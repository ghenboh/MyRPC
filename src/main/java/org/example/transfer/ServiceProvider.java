package org.example.transfer;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.registration.RegisterService;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private String host;
    private int port;
    private Map<String, Object> services = new HashMap<>();
    private static RegisterService registerService = new RegisterService();

    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void addService(Object service) {
        Class<?>[] clazz = service.getClass().getInterfaces();
        for(Class single: clazz) {
            registerService.register(single.getName(), new InetSocketAddress(host, port));
            services.put(single.getName(), service);
        }
    }

    public Object getService(String serviceClass) {
        return services.get(serviceClass);
    }
}
