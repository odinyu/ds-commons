package org.ds.common;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Daniel
 */
public abstract class BeanUtils {

    /**
     * map转bean
     *
     * @param sourceMap   map
     * @param targetClass 目标class
     * @param <T>         目标类型
     * @return bean
     */
    public static <T> T map2Bean(LinkedHashMap sourceMap, Class<T> targetClass) {
        try {
            Map<String, Integer> sortMap = sortFieldsMap(targetClass);
            T instance = targetClass.newInstance();
            Arrays.asList(Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors()).stream()
                    .filter(descriptor -> descriptor.getWriteMethod() != null)
                    .sorted((d1, d2) -> compare(sortMap, d1.getName(), d2.getName()))
                    .forEach(descriptor -> {
                        try {
                            Class<?> tempClass;
                            if ((tempClass = descriptor.getWriteMethod().getParameterTypes()[0]) == null)
                                System.out.println("warn writeMethod has NULL ParameterType");
                            else if (sourceMap.get(descriptor.getName()) instanceof LinkedHashMap)
                                descriptor.getWriteMethod().invoke(instance, map2Bean((LinkedHashMap) sourceMap.get(descriptor.getName()), tempClass));
                            else if (sourceMap.get(descriptor.getName()) instanceof ArrayList) {
                                final Type type = ((ParameterizedTypeImpl) targetClass.getDeclaredField(descriptor.getName()).getGenericType()).getActualTypeArguments()[0];
                                if (type instanceof Class) {
                                    descriptor.getWriteMethod().invoke(instance,
                                            ((ArrayList) sourceMap.get(descriptor.getName())).stream()
                                                    .map(o -> map2Bean((LinkedHashMap) o, (Class) type))
                                                    .collect(Collectors.toList()));
                                }
                            } else
                                descriptor.getWriteMethod().invoke(instance, sourceMap.get(descriptor.getName()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            return instance;
        } catch (IllegalAccessException | InstantiationException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bean 转 map
     *
     * @param sourceBean 源bean
     * @return map
     */
    public static LinkedHashMap<String, Object> bean2Map(Object sourceBean) {
        try {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            Map<String, Integer> sortMap = sortFieldsMap(sourceBean.getClass());
            Arrays.asList(Introspector.getBeanInfo(sourceBean.getClass()).getPropertyDescriptors()).stream()
                    .filter(descriptor -> !descriptor.getName().equals("class") && descriptor.getReadMethod() != null)
                    .sorted((d1, d2) -> compare(sortMap, d1.getName(), d2.getName()))
                    .forEach(descriptor -> {
                        try {
                            Object tempObj = descriptor.getReadMethod().invoke(sourceBean);
                            if (tempObj == null)
                                return;
                            if (tempObj.getClass().isMemberClass())
                                map.put(descriptor.getName(), bean2Map(tempObj));
                            else if (tempObj instanceof Collection)
                                map.put(descriptor.getName(), ((Collection) tempObj).stream().map(obj -> bean2Map(obj)).collect(Collectors.toList()));
                            else
                                map.put(descriptor.getName(), tempObj);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            return map;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sortFieldsMap
     *
     * @param targetClass 目标class
     * @return sortMap
     */
    private static Map<String, Integer> sortFieldsMap(Class targetClass) {
        Map<String, Integer> sortMap = new HashMap<>();
        Arrays.asList(targetClass.getDeclaredFields()).stream().forEach(field -> sortMap.put(field.getName(), sortMap.size()));
        return sortMap;
    }

    /**
     * 比较
     *
     * @param sortMap    sortMap
     * @param firstName  第一个字段名称
     * @param secondName 第二个字段名称
     * @return 大小
     */
    private static int compare(Map<String, Integer> sortMap, String firstName, String secondName) {
        if (sortMap.containsKey(firstName) && sortMap.containsKey(secondName))
            return sortMap.get(firstName).compareTo(sortMap.get(secondName));
        return 0;
    }
}
