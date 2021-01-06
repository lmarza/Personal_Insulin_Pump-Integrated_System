package LD_InsulinPump;

import LD_InsulinPump_Mock.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@ComponentScan("LD_InsulinPump")
public class Config {

    @Autowired
    private SimpMessagingTemplate template;

    @Bean
    public ControllerState controllerState(){
        return ControllerState.RUNNING;
    }

    @Bean
    public Sensor sensor(){
        return new SensorRandomImpl(new Random(), new Random());
    }

    @Bean
    public Pump pump(){
        return new PumpRandomImpl(new Random());
    }

    @Bean
    public NeedleAssembly needle(){
        return new NeedleRandomImpl(new Random());
    }

    @Bean
    public List<Measurement> measurementsList()
    {
        return new ArrayList<>();
    }

    @Bean
    public SimpMessagingTemplate template()
    {
        return template;
    }
}
