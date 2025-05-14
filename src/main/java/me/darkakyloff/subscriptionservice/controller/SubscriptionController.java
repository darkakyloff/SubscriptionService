package me.darkakyloff.subscriptionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.darkakyloff.subscriptionservice.dto.SubscriptionDto;
import me.darkakyloff.subscriptionservice.dto.TopSubscriptionDto;
import me.darkakyloff.subscriptionservice.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController
{
    private final SubscriptionService subscriptionService;

    @PostMapping("/users/{userId}/subscriptions")
    public ResponseEntity<SubscriptionDto> addSubscription(@PathVariable Long userId, @Valid @RequestBody SubscriptionDto subscriptionDto)
    {
        subscriptionDto.setUserId(userId);
        return new ResponseEntity<>(subscriptionService.createSubscription(subscriptionDto), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<List<SubscriptionDto>> getUserSubscriptions(@PathVariable Long userId)
    {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @DeleteMapping("/users/{userId}/subscriptions/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long userId, @PathVariable Long subscriptionId)
    {
        subscriptionService.deleteSubscription(userId, subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscriptions/top")
    public ResponseEntity<List<TopSubscriptionDto>> getTopSubscriptions()
    {
        return ResponseEntity.ok(subscriptionService.getTopSubscriptions());
    }
}