package LD_InsulinPump;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
@ComponentScan("LD_InsulinPump")
public class Config {
    @Bean
    public ControllerState prova(){
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
}
