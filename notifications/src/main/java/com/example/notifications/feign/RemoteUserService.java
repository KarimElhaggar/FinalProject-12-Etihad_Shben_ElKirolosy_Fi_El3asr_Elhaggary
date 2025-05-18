package com.example.notifications.feign;

import com.example.notifications.dto.UserEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface RemoteUserService {
    @GetMapping("/users/{id}/email")
    UserEmailDTO getUserEmailById(@PathVariable("id") Long id);
}
