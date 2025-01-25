package com.example.kulvida.configurations;


import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class ImageKitConfiguration {

    //  Put essential values of keys [UrlEndpoint, PrivateKey, PublicKey]
    private String UrlEndpoint="https://ik.imagekit.io/Heisen/";
    private String PrivateKey="private_51yviXhQCiCpD+vE8YZlVrwEIZg=";
    private String PublicKey="public_zsXOu/spjladPgKyPZV51h6QV5E=";


    @Bean
    public ImageKit image(){
        ImageKit imageKit=null;
        try{
            imageKit = ImageKit.getInstance();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        Configuration config = new Configuration(PublicKey, PrivateKey, UrlEndpoint);
        if(imageKit != null)
            imageKit.setConfig(config);
        return imageKit;
    }

}
