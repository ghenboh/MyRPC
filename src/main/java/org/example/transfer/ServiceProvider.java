package org.example.transfer;

import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private Map<String, Object> services = new HashMap<>();

    public void addService(Object service) {
        Class<?>[] clazz = service.getClass().getInterfaces();
        for(Class single: clazz) {
            services.put(single.getName(), service);
        }
    }

    public Object getService(String serviceClass) {
        return services.get(serviceClass);
    }
}
