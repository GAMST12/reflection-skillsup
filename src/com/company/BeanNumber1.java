package com.company;

import com.company.annotations.InjectBean;
import com.company.annotations.PrintBefore;
import com.company.annotations.SkillsUpBean;

/**
 * Created by gal on 19.12.16.
 */
@SkillsUpBean
public class BeanNumber1 implements IBeanNumber1 {

    @InjectBean
    private BeanNumber2 beanNumber2;

    @InjectBean
    private SampleBean sampleBean;

    private String name;

    @PrintBefore
    @Override
    public void printSomething(){
        System.out.println("Printing");
    }

    @Override
    public String toString() {
        return "BeanNumber1{" +
                "beanNumber2=" + beanNumber2 +
                ", sampleBean=" + sampleBean +
                ", name='" + name + '\'' +
                '}';
    }
}
