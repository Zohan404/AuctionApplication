package elte.icj06o.auction.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcx8m4jq0",
                "api_key", "723384269447156",
                "api_secret", "UO9BTRAuTCrqOghTTXNZ2g_9uiY"
        ));
    }
}
