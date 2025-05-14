package me.darkakyloff.subscriptionservice.service;

import me.darkakyloff.subscriptionservice.dto.SubscriptionDto;
import me.darkakyloff.subscriptionservice.dto.TopSubscriptionDto;
import me.darkakyloff.subscriptionservice.exception.ResourceNotFoundException;
import me.darkakyloff.subscriptionservice.model.Subscription;
import me.darkakyloff.subscriptionservice.model.User;
import me.darkakyloff.subscriptionservice.repository.SubscriptionRepository;
import me.darkakyloff.subscriptionservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService
{

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionDto createSubscription(SubscriptionDto subscriptionDto)
    {
        log.info("Создание новой подписки для пользователя с ID: {}", subscriptionDto.getUserId());
        
        User user = userRepository.findById(subscriptionDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + subscriptionDto.getUserId() + " не найден"));

        Subscription subscription = Subscription.builder()
                .name(subscriptionDto.getName())
                .provider(subscriptionDto.getProvider())
                .startDate(subscriptionDto.getStartDate())
                .endDate(subscriptionDto.getEndDate())
                .price(subscriptionDto.getPrice())
                .user(user)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Подписка с ID {} успешно создана для пользователя с ID {}", 
                 savedSubscription.getId(), user.getId());

        return mapToDto(savedSubscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getUserSubscriptions(Long userId)
    {
        log.info("Получение всех подписок для пользователя с ID: {}", userId);

        if (!userRepository.existsById(userId))
        {
            throw new ResourceNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        return subscriptions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubscription(Long userId, Long subscriptionId)
    {
        log.info("Удаление подписки с ID {} для пользователя с ID {}", subscriptionId, userId);

        if (!userRepository.existsById(userId)) throw new ResourceNotFoundException("Пользователь с ID " + userId + " не найден");

        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new ResourceNotFoundException("Подписка с ID " + subscriptionId + " не найдена"));

        if (!subscription.getUser().getId().equals(userId))
        {
            log.error("Подписка с ID {} не принадлежит пользователю с ID {}", subscriptionId, userId);
            throw new ResourceNotFoundException("Подписка с ID " + subscriptionId + " не найдена у пользователя с ID " + userId);
        }
        
        subscriptionRepository.delete(subscription);
        log.info("Подписка с ID {} успешно удалена", subscriptionId);
    }
    
    @Transactional(readOnly = true)
    public List<TopSubscriptionDto> getTopSubscriptions()
    {
        log.info("Получение ТОП-3 популярных подписок");
        List<TopSubscriptionDto> topSubscriptions = subscriptionRepository.findTopSubscriptions();

        if (topSubscriptions.size() > 3) return topSubscriptions.subList(0, 3);

        return topSubscriptions;
    }

    private SubscriptionDto mapToDto(Subscription subscription)
    {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .name(subscription.getName())
                .provider(subscription.getProvider())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .price(subscription.getPrice())
                .userId(subscription.getUser().getId())
                .build();
    }
}