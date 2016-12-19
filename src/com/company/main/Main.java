package com.company.main;

import com.company.*;
import com.company.annotations.InjectBean;
import com.company.annotations.PrintBefore;
import com.company.annotations.SkillsUpBean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

//    Список классов которые необходимо просканировать на наличие аннотаций
    private static List<Class> classesToScan = Arrays.asList(SampleBean.class, SamplePlainObj.class, BeanNumber1.class, BeanNumber2.class);

//    Контейнер который содержит пары Class<->Instance. По одному instance на каждый класс
    private static HashMap<Class, Object> container = new HashMap<>();

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
//    Проходим по сканируемым классам в первый раз и создаем instances (бины) тех классов, которые с аннотацией SkillsUpBean
//    Кладем все в container
        for (Class managedClass : classesToScan) {
            if (managedClass.getDeclaredAnnotation(SkillsUpBean.class) != null) {
                System.out.println("SkillsUpBean Exists in " + managedClass.getName() + ". Creating instance");
                container.put(managedClass, managedClass.newInstance());
            } else {
                System.out.println("SkillsUpBean Doesnt exist in " + managedClass.getName());
            }
        }

//     Проходим по созданному нами container
        for (Class managedClass : container.keySet()) {
//            Сканируем все fields каждого нашего "бина"
            for (Field field : managedClass.getDeclaredFields()) {
                InjectBean annotation = field.getAnnotation(InjectBean.class);
//                Если над field есть аннотация InjectBean - ставим в этот field "бин" требуемого типа. Сам "бин" берется из того же контейнера
                if (annotation != null) {
                    field.setAccessible(true);
                    Object objForInjecting = container.get(managedClass);
                    Object objToInject = container.get(field.getType());

                    field.set(objForInjecting, objToInject);
                }
            }

//            Проходим по методам наших бинов и делаем прокси если необходимо
            for (Method method : managedClass.getDeclaredMethods()) {
                PrintBefore annotation = method.getAnnotation(PrintBefore.class);
                if (annotation != null) {
//                    Если стоит аннотация PrintBefore - делаем прокси для "бина"
                    MyInvocationHandler myInvocationHandler = new MyInvocationHandler(container.get(managedClass));
                    Object proxyInstance = Proxy.newProxyInstance(
                            Main.class.getClassLoader(),
                            new Class[]{IBeanNumber1.class},
                            myInvocationHandler);
//                    Смотрим как работает прокси
                    ((IBeanNumber1) proxyInstance).printSomething();
                }
            }
        }
        System.out.println();
        System.out.println(container);

    }
}

class MyInvocationHandler implements InvocationHandler {

    private Object originalObj;

    MyInvocationHandler(Object originalObj) {
        this.originalObj = originalObj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method");
        return method.invoke(originalObj, args);
    }
}