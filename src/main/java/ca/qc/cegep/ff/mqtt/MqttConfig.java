package ca.qc.cegep.ff.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {
    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttClientConfig clientConfigs) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{clientConfigs.getBrokerProtocol() + "://" + clientConfigs.getBrokerHost() + ":" + clientConfigs.getBrokerPort()});
        options.setUserName(clientConfigs.getUsername());
        options.setPassword(clientConfigs.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(30);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoClientFactory mqttClientSubscriberFactory(MqttClientConfig clientConfigs) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{clientConfigs.getBrokerProtocol() + "://" + clientConfigs.getBrokerHost() + ":" + clientConfigs.getBrokerPort()});
        options.setUserName("flightSub");
        options.setPassword(clientConfigs.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(30);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutboundHandler(MqttPahoClientFactory mqttClientFactory, MqttClientConfig clientConfigs) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("flightPub", mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(clientConfigs.getFlightsTopic());
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(MqttClientConfig clientConfigs) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("flightSub", mqttClientSubscriberFactory(clientConfigs), clientConfigs.getFlightsTopic());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> System.out.println("Received MQTT message: " + message.getPayload());
    }

}
